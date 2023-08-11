package com.mrcrayfish.guns.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

/**
 * Author: MrCrayfish
 *
 * Transformed and adapted as needed by: En0ri4n
 */
public class TriggerFingerEnchantment extends GunEnchantment
{
    public TriggerFingerEnchantment()
    {
        super(Rarity.RARE, EnchantmentTypes.SEMI_AUTO_GUN, new EquipmentSlot[]{EquipmentSlot.MAINHAND}, Type.WEAPON);
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }

    @Override
    public int getMinCost(int level)
    {
        return 15 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level)
    {
        return this.getMinCost(level) + 40;
    }
}
