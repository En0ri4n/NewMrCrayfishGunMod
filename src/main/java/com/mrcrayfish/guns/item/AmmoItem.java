package com.mrcrayfish.guns.item;

import net.minecraft.world.item.Item;

/**
 * A basic item class that implements {@link IAmmo} to indicate this item is ammunition
 * <p>
 * Author: MrCrayfish
 * <p>
 * Transformed by En0ri4n
 */
public class AmmoItem extends Item implements IAmmo
{
    public AmmoItem(Properties pProperties)
    {
        super(pProperties.stacksTo(64));
    }
}
