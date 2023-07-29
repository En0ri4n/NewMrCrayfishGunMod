package com.mrcrayfish.guns.item;

import com.google.gson.JsonObject;
import com.mrcrayfish.guns.common.config.JsonSerializable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A basic item class that implements {@link IAmmo} to indicate this item is ammunition
 * <p>
 * Author: MrCrayfish
 */
public class AmmoItem extends Item implements IAmmo, JsonSerializable
{
    private int maxAmmo = 100;

    public AmmoItem(Properties properties)
    {
        super(properties.durability(100));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag)
    {
        // TODO: check if this is the right way
        Player player = Minecraft.getInstance().player;

        if(player == null) return;

        CompoundTag tagCompound = stack.getTag();

        if(tagCompound != null)
        {
            int ammoCount = stack.getMaxDamage() - stack.getDamageValue();
            tooltip.add(new TranslatableComponent("info.cgm.ammo", ChatFormatting.WHITE.toString() + ammoCount + "/" + stack.getMaxDamage()).withStyle(ChatFormatting.GRAY));
        }
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
