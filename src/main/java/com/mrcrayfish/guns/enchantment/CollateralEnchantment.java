package com.mrcrayfish.guns.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
public class CollateralEnchantment extends GunEnchantment
{
    public CollateralEnchantment()
    {
        super(Rarity.RARE, EnchantmentTypes.GUN, new EquipmentSlot[]{EquipmentSlot.MAINHAND}, Type.PROJECTILE);
    }

    @Override
    public int getMinCost(int level)
    {
        return 10;
    }

    @Override
    public int getMaxCost(int level)
    {
        return this.getMinCost(level) + 20;
    }
}
