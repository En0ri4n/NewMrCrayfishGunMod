package com.mrcrayfish.guns.enchantment;

import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.item.GunItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
public class EnchantmentTypes
{
    public static final EnchantmentCategory GUN = EnchantmentCategory.create(Reference.MOD_ID + ":gun", item -> item instanceof GunItem);
    public static final EnchantmentCategory SEMI_AUTO_GUN = EnchantmentCategory.create(Reference.MOD_ID + ":semi_auto_gun", item -> item instanceof GunItem && !((GunItem) item).getGun().getGeneral().isAuto());
}
