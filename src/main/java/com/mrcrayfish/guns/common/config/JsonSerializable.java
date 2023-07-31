package com.mrcrayfish.guns.common.config;

import com.google.gson.JsonObject;

/**
 * Author: En0ri4n
 */
public interface JsonSerializable
{
    JsonObject toJsonObject();

    void loadConfig(JsonObject json);
}
