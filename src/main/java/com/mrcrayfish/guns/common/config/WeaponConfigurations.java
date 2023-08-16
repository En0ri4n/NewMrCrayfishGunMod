package com.mrcrayfish.guns.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.common.Magazine;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.MagazineItem;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.S2CMessageUpdateGunsAndAmmos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: En0ri4n
 */
public class WeaponConfigurations
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private static int errorCount = 0;

    private static final Map<ResourceLocation, Gun> guns = new HashMap<>();
    private static final Map<ResourceLocation, Magazine> ammos = new HashMap<>();

    private static void addGun(GunItem item)
    {
        guns.put(item.getRegistryName(), item.getGun());
    }

    private static void addAmmo(MagazineItem item)
    {
        ammos.put(item.getRegistryName(), item.getAmmo());
    }

    /**
     * Called when the server is loaded, fill the guns and ammos maps
     */
    public static void onServerLoad(MinecraftServer server)
    {
        GunMod.LOGGER.info("Loading server configs...");

        guns.clear();
        ammos.clear();

        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof GunItem)
                .map(item -> ((GunItem) item))
                .forEach(WeaponConfigurations::addGun);

        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof MagazineItem)
                .map(item -> ((MagazineItem) item))
                .forEach(WeaponConfigurations::addAmmo);

        reloadConfiguration(server);
    }

    /**
     * Reload configurations for all guns and ammos
     *
     * @return an array of 3 integers :<br>Number of guns<br>Number of ammos<br>Number of errors (can't load config)
     */
    public static int[] reloadConfiguration(MinecraftServer server)
    {
        GunMod.LOGGER.info("Reloading server configs...");

        errorCount = 0;

        guns.forEach((id, gun) ->
        {
            JsonObject config = getConfig(server, id, gun, false);
            GunMod.LOGGER.debug("Loading config for gun " + id);
            gun.loadConfig(config);
        });

        ammos.forEach((id, ammo) ->
        {
            JsonObject config = getConfig(server, id, ammo, true);
            GunMod.LOGGER.debug("Loading config for ammo " + id);
            ammo.loadConfig(config);
        });

        // Ensure all clients have synced with the server
        sendChanges();

        GunMod.LOGGER.info("Loaded " + guns.size() + " guns and " + ammos.size() + " ammos (" + errorCount + " errors)");

        return new int[]{guns.size(), ammos.size(), errorCount};
    }

    /**
     * Sends a packet to all clients to update their guns and ammos to the server's config
     */
    public static void sendChanges()
    {
        PacketHandler.getPlayChannel().send(PacketDistributor.ALL.noArg(), new S2CMessageUpdateGunsAndAmmos());
    }

    private static JsonObject getConfig(MinecraftServer server, ResourceLocation registryName, JsonSerializable serializable, boolean isAmmo)
    {
        File gunConfig = new File(getConfigFolder(server, isAmmo), registryName.getPath() + ".json");

        if(!gunConfig.exists())
        {
            try
            {
                gunConfig.createNewFile();
            }
            catch(IOException e)
            {
                GunMod.LOGGER.debug("Failed to create gun config file for " + registryName, e);
                GunMod.LOGGER.debug("Using default config for " + registryName);
                remove(isAmmo, registryName);
            }

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(gunConfig)))
            {
                GSON.toJson(serializable.toJsonObject(), writer);
            }
            catch(IOException e)
            {
                GunMod.LOGGER.debug("Failed to create gun config file for " + registryName, e);
                GunMod.LOGGER.debug("Using default config for " + registryName);
                remove(isAmmo, registryName);
            }
        }

        JsonObject jsonObject = new JsonObject();

        try(BufferedReader reader = new BufferedReader(new FileReader(gunConfig)))
        {
            return GSON.fromJson(reader, JsonObject.class);
        }
        catch(IOException e)
        {
            GunMod.LOGGER.debug("Failed to load gun config file for " + registryName, e);
            GunMod.LOGGER.debug("Using default config for " + registryName);
            remove(isAmmo, registryName);
        }

        return jsonObject;
    }

    private static void remove(boolean isAmmo, ResourceLocation registryName)
    {
        if(isAmmo) ammos.remove(registryName);
        else guns.remove(registryName);

        errorCount++;
    }

    private static File getConfigFolder(MinecraftServer server, boolean isAmmo)
    {
        File folder = new File(server.getServerDirectory(), "config/cgm/" + (isAmmo ? "ammo" : "gun"));
        if(!folder.exists()) folder.mkdirs();
        return folder;
    }
}
