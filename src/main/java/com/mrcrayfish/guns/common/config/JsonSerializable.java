package com.mrcrayfish.guns.common.config;

import com.google.gson.JsonObject;

public interface JsonSerializable
{
    JsonObject toJsonObject();

    void loadConfig(JsonObject json);
}
