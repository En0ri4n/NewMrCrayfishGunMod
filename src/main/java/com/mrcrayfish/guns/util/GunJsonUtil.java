package com.mrcrayfish.guns.util;

import com.google.gson.JsonObject;

/**
 * Author: MrCrayfish
 *
 * Transformed and adapted as needed by: En0ri4n
 */
public class GunJsonUtil
{
    public static void addObjectIfNotEmpty(JsonObject parent, String key, JsonObject child)
    {
        if(child.size() > 0)
        {
            parent.add(key, child);
        }
    }
}
