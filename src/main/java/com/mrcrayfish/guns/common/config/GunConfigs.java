package com.mrcrayfish.guns.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.item.AmmoItem;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.util.ReflectionUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GunConfigs
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private static final Map<ResourceLocation, Gun> guns = new HashMap<>();
    private static final Map<ResourceLocation, AmmoItem> ammos = new HashMap<>();

    public static void addGun(GunItem item)
    {
        guns.put(item.getRegistryName(), item.getGun());
    }

    public static void addAmmo(AmmoItem item)
    {
        ammos.put(item.getRegistryName(), item);
    }

    public static void load(MinecraftServer server)
    {
        ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof GunItem).forEach(item -> addGun((GunItem) item));
        ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof AmmoItem).forEach(item -> addAmmo((AmmoItem) item));

        guns.forEach((id, gun) ->
        {
            JsonObject config = getConfig(server, id, gun);
            GunMod.LOGGER.debug("Loading config for gun " + id);
            gun.loadConfig(config);
            setDurability(id, gun.getGeneral().getDurability()); // Special case for durability because we need to use Reflection
        });

        ammos.forEach((id, ammo) ->
        {
            JsonObject config = getConfig(server, id, ammo);
            GunMod.LOGGER.debug("Loading config for ammo " + id);
            ammo.loadConfig(config);
            setDurability(id, ammo.getMaxAmmo()); // Special case for durability because we need to use Reflection
        });
    }

    private static void setDurability(ResourceLocation id, int durability)
    {
        Item item = ForgeRegistries.ITEMS.getValue(id);
        ReflectionUtil.setItemMaxDurability(item, durability);
    }

    private static JsonObject getConfig(MinecraftServer server, ResourceLocation registryName, JsonSerializable gun)
    {
        File gunConfig = new File(getConfigFolder(server), registryName.getPath() + ".json");

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
                guns.remove(registryName);
            }

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(gunConfig)))
            {
                GSON.toJson(gun.toJsonObject(), writer);
            }
            catch(IOException e)
            {
                GunMod.LOGGER.debug("Failed to create gun config file for " + registryName, e);
                GunMod.LOGGER.debug("Using default config for " + registryName);
                guns.remove(registryName);
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
            guns.remove(registryName);
        }

        return jsonObject;
    }

    private static File getConfigFolder(MinecraftServer server)
    {
        File folder = new File(server.getServerDirectory(), "config/guns");
        if(!folder.exists())
            folder.mkdirs();
        return folder;
    }
}
