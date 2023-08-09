package com.mrcrayfish.guns.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrcrayfish.framework.api.data.login.ILoginData;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.annotation.Validator;
import com.mrcrayfish.guns.item.MagazineItem;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.S2CMessageUpdateGunsAndAmmos;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n<br>
 * From {@link NetworkGunManager}
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class NetworkAmmoManager extends SimplePreparableReloadListener<Map<MagazineItem, Ammo>>
{
    private static final int FILE_TYPE_LENGTH_VALUE = ".json".length();
    private static final Gson GSON_INSTANCE = Util.make(() -> {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ResourceLocation.class, JsonDeserializers.RESOURCE_LOCATION);
        builder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        return builder.create();
    });

    private static List<MagazineItem> clientRegisteredAmmos = new ArrayList<>();
    private static NetworkAmmoManager instance;

    private Map<ResourceLocation, Ammo> registeredAmmos = new HashMap<>();

    @Override
    protected Map<MagazineItem, Ammo> prepare(ResourceManager manager, ProfilerFiller profiler)
    {
        Map<MagazineItem, Ammo> map = new HashMap<>();
        ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof MagazineItem).forEach(item ->
        {
            ResourceLocation id = item.getRegistryName();
            if(id != null)
            {
                List<ResourceLocation> resources = new ArrayList<>(manager.listResources("ammos", (fileName) -> fileName.endsWith(id.getPath() + ".json")));
                resources.sort((r1, r2) -> {
                    if(r1.getNamespace().equals(r2.getNamespace())) return 0;
                    return r2.getNamespace().equals(Reference.MOD_ID) ? 1 : -1;
                });
                resources.forEach(resource ->
                {
                    String path = resource.getPath().substring(0, resource.getPath().length() - FILE_TYPE_LENGTH_VALUE);
                    String[] splitPath = path.split("/");

                    // Makes sure the file name matches exactly with the id of the ammo
                    if(!id.getPath().equals(splitPath[splitPath.length - 1]))
                        return;

                    // Also check if the mod id matches with the ammo's registered namespace
                    if (!id.getNamespace().equals(resource.getNamespace()))
                        return;

                    try(InputStream inputstream = manager.getResource(resource).getInputStream(); Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8)))
                    {
                        Ammo ammo = GsonHelper.fromJson(GSON_INSTANCE, reader, Ammo.class);
                        if(ammo != null && Validator.isValidObject(ammo))
                        {
                            
                            map.put((MagazineItem) item, ammo);
                        }
                        else
                        {
                            GunMod.LOGGER.error("Couldn't load data file {} as it is missing or malformed. Using default ammo data", resource);
                            map.putIfAbsent((MagazineItem) item, new Ammo());
                        }
                    }
                    catch(InvalidObjectException e)
                    {
                        GunMod.LOGGER.error("Missing required properties for {}", resource);
                        e.printStackTrace();
                    }
                    catch(IOException e)
                    {
                        GunMod.LOGGER.error("Couldn't parse data file {}", resource);
                    }
                    catch(IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                });
            }
        });
        
        return map;
    }

    @Override
    protected void apply(Map<MagazineItem, Ammo> objects, ResourceManager resourceManager, ProfilerFiller profiler)
    {
        ImmutableMap.Builder<ResourceLocation, Ammo> builder = ImmutableMap.builder();
        objects.forEach((item, ammo) -> {
            Validate.notNull(item.getRegistryName());
            builder.put(item.getRegistryName(), ammo);
            item.setAmmo(new Supplier(ammo));
        });
        this.registeredAmmos = builder.build();
    }

    /**
     * Writes all registered guns into the provided packet buffer
     *
     * @param buffer a packet buffer get
     */
    public void writeRegisteredAmmos(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(this.registeredAmmos.size());
        this.registeredAmmos.forEach((id, ammo) -> {
            buffer.writeResourceLocation(id);
            buffer.writeNbt(ammo.serializeNBT());
        });
}

    /**
     * Reads all registered guns from the provided packet buffer
     *
     * @param buffer a packet buffer get
     * @return a map of registered guns from the server
     */
    public static ImmutableMap<ResourceLocation, Ammo> readRegisteredAmmos(FriendlyByteBuf buffer)
    {
        int size = buffer.readVarInt();
        if(size > 0)
        {
            ImmutableMap.Builder<ResourceLocation, Ammo> builder = ImmutableMap.builder();
            for(int i = 0; i < size; i++)
            {
                ResourceLocation id = buffer.readResourceLocation();
                Ammo ammo = Ammo.create(buffer.readNbt());
                builder.put(id, ammo);
            }
            return builder.build();
        }
        return ImmutableMap.of();
    }

    public static boolean updateRegisteredAmmos(S2CMessageUpdateGunsAndAmmos message)
    {
        return updateRegisteredAmmos(message.getRegisteredAmmos());
    }

    /**
     * Updates registered guns from data provided by the server
     *
     * @return true if all registered guns were able to update their corresponding ammo item
     */
    private static boolean updateRegisteredAmmos(Map<ResourceLocation, Ammo> registeredGuns)
    {
        clientRegisteredAmmos.clear();
        if(registeredGuns != null)
        {
            for(Map.Entry<ResourceLocation, Ammo> entry : registeredGuns.entrySet())
            {
                Item item = ForgeRegistries.ITEMS.getValue(entry.getKey());
                if(!(item instanceof MagazineItem))
                {
                    return false;
                }
                ((MagazineItem) item).setAmmo(new Supplier(entry.getValue()));
                clientRegisteredAmmos.add((MagazineItem) item);
            }
            return true;
        }
        return false;
    }

    /**
     * Gets a map of all the registered guns objects. Note, this is an immutable map.
     *
     * @return a map of registered ammo objects
     */
    public Map<ResourceLocation, Ammo> getRegisteredAmmos()
    {
        return this.registeredAmmos;
    }

    /**
     * Gets a list of all the guns registered on the client side. Note, this is an immutable list.
     *
     * @return a map of guns registered on the client
     */
    public static List<MagazineItem> getClientRegisteredAmmos()
    {
        return ImmutableList.copyOf(clientRegisteredAmmos);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event)
    {
        NetworkAmmoManager.instance = null;
    }

    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event)
    {
        NetworkAmmoManager networkAmmoManager = new NetworkAmmoManager();
        event.addListener(networkAmmoManager);
        NetworkAmmoManager.instance = networkAmmoManager;
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event)
    {
        if(event.getPlayer() == null)
        {
            PacketHandler.getPlayChannel().send(PacketDistributor.ALL.noArg(), new S2CMessageUpdateGunsAndAmmos());
        }
    }

    /**
     * Gets the network ammo manager. This will be null if the client isn't running an integrated
     * server or the client is connected to a dedicated server.
     *
     * @return the network ammo manager
     */
    @Nullable
    public static NetworkAmmoManager get()
    {
        return instance;
    }

    /**
     * A simple wrapper for a ammo object to pass to GunItem. This is to indicate to developers that
     * Gun instances shouldn't be changed on GunItems as they are controlled by NetworkGunManager.
     * Changes to ammo properties should be made through the JSON file.
     */
    public static class Supplier
    {
        private Ammo ammo;

        private Supplier(Ammo ammo)
        {
            this.ammo = ammo;
        }

        public Ammo getAmmo()
        {
            return this.ammo;
        }
    }

    public static class LoginData implements ILoginData
    {
        @Override
        public void writeData(FriendlyByteBuf buffer)
        {
            Validate.notNull(NetworkAmmoManager.get());
            NetworkAmmoManager.get().writeRegisteredAmmos(buffer);
        }

        @Override
        public Optional<String> readData(FriendlyByteBuf buffer)
        {
            Map<ResourceLocation, Ammo> registeredGuns = NetworkAmmoManager.readRegisteredAmmos(buffer);
            NetworkAmmoManager.updateRegisteredAmmos(registeredGuns);
            return Optional.empty();
        }
    }
}
