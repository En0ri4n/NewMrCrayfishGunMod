package com.mrcrayfish.guns.item;

import net.minecraft.world.item.Item;

public class AmmoItem extends Item implements IAmmo
{
    public AmmoItem(Properties pProperties)
    {
        super(pProperties.stacksTo(64));
    }
}
