package com.mrcrayfish.guns.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Just a marker interface to identify items that have ammo<br>
 */
public interface IHasAmmo
{
    int getAmmoCount(ItemStack stack);

    int getReloadAmount(ItemStack stack);

    boolean canUnload(ItemStack stack);

    boolean isGun(ItemStack stack);

    /**
     * Check if the gun (or ammo) has a magazine as ammo<br>
     * This is used to determine how the item should be reloaded
     */
    boolean hasAmmoMagazine(ItemStack stack);

    boolean ignoreAmmo(ItemStack stack);

    void decreaseAmmo(ItemStack stack, int amount);

    ResourceLocation getAmmoType(ItemStack stack);

    int getMaxAmmo(ItemStack stack);

    ResourceLocation getReloadSound(ItemStack stack);
}
