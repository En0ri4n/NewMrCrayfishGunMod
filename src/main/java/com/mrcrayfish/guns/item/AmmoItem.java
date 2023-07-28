package com.mrcrayfish.guns.item;

import com.google.gson.JsonObject;
import com.mrcrayfish.guns.common.config.JsonSerializable;
import net.minecraft.world.item.Item;

/**
 * A basic item class that implements {@link IAmmo} to indicate this item is ammunition
 *
 * Author: MrCrayfish
 */
public class AmmoItem extends Item implements IAmmo, JsonSerializable
{
    private int maxAmmo;

    public AmmoItem(Properties properties)
    {
        super(properties.durability(100));
    }

    @Override
    public JsonObject toJsonObject()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("AmmoCount", getMaxAmmo());
        return jsonObject;
    }

    @Override
    public void loadConfig(JsonObject json)
    {
        setMaxAmmo(json.get("AmmoCount").getAsInt());
    }

    public int getMaxAmmo()
    {
        return maxAmmo;
    }

    public void setMaxAmmo(int maxAmmo)
    {
        this.maxAmmo = maxAmmo;
    }
}
