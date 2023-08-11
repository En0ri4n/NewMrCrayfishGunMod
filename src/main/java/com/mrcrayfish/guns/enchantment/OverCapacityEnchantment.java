package com.mrcrayfish.guns.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
public class OverCapacityEnchantment extends GunEnchantment
{
    public OverCapacityEnchantment()
    {
        super(Rarity.RARE, EnchantmentTypes.GUN, new EquipmentSlot[]{EquipmentSlot.MAINHAND}, Type.WEAPON);
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    @Override
    public int getMinCost(int level)
    {
        return 5 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level)
    {
        return super.getMinCost(level) + 50;
    }
}
