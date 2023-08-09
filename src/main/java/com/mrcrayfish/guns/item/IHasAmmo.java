package com.mrcrayfish.guns.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Just a marker interface to identify items that have ammo<br>
 */
public interface IHasAmmo
{
    int getAmmoCount(ItemStack stack);

    int getReloadAmount(ItemStack stack);

    boolean canUnload(ItemStack stack);

    boolean ignoreAmmo(ItemStack stack);

    boolean isAmmo(ItemStack stack);

    int getAmmoCapacity(Player player, ItemStack stack);

    ResourceLocation getAmmoType(ItemStack stack);

    int getMaxAmmo(ItemStack stack);

    ResourceLocation getReloadSound(ItemStack stack);
}
