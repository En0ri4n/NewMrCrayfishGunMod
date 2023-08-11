package com.mrcrayfish.guns.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.common.Magazine;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Author: En0ri4n
 */
public abstract class MagazineProvider implements DataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    private final DataGenerator generator;
    private final Map<ResourceLocation, Magazine> ammoMap = new HashMap<>();

    protected MagazineProvider(DataGenerator generator)
    {
        this.generator = generator;
    }

    protected abstract void registerMagazines();

    protected final void addMagazine(ResourceLocation id, Magazine magazine)
    {
        this.ammoMap.put(id, magazine);
    }

    @Override
    public void run(HashCache cache)
    {
        this.ammoMap.clear();
        this.registerMagazines();
        this.ammoMap.forEach((id, ammo) ->
        {
            Path path = this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/ammos/" + id.getPath() + ".json");
            try
            {
                JsonObject object = ammo.toJsonObject();
                String rawJson = GSON.toJson(object);
                String hash = SHA1.hashUnencodedChars(rawJson).toString();
                if(!Objects.equals(cache.getHash(path), hash) || !Files.exists(path))
                {
                    Files.createDirectories(path.getParent());
                    try(BufferedWriter writer = Files.newBufferedWriter(path))
                    {
                        writer.write(rawJson);
                    }
                }
                cache.putNew(path, hash);
            }
            catch(IOException e)
            {
                LOGGER.error("Couldn't save trades to {}", path, e);
            }
        });
    }

    @Override
    public String getName()
    {
        return "Guns: " + Reference.MOD_ID;
    }
}
