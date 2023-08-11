package com.mrcrayfish.guns.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.common.Ammo;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.MagazineItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: En0ri4n
 */
public class GunConfigs
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private static final Map<ResourceLocation, Gun> guns = new HashMap<>();
    private static final Map<ResourceLocation, Ammo> ammos = new HashMap<>();

    private static void addGun(GunItem item)
    {
        guns.put(item.getRegistryName(), item.getGun());
    }

    private static void addAmmo(MagazineItem item)
    {
        ammos.put(item.getRegistryName(), item.getAmmo());
    }

    public static void load(MinecraftServer server)
    {
        ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof GunItem).forEach(item -> addGun((GunItem) item));
        ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof MagazineItem).forEach(item -> addAmmo((MagazineItem) item));

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
                guns.remove(registryName);
            }

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(gunConfig)))
            {
                GSON.toJson(serializable.toJsonObject(), writer);
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

    private static File getConfigFolder(MinecraftServer server, boolean isAmmo)
    {
        File folder = new File(server.getServerDirectory(), "config/cgm/" + (isAmmo ? "ammo" : "gun"));
        if(!folder.exists())
            folder.mkdirs();
        return folder;
    }
}
