package com.mrcrayfish.guns.item;

import com.mrcrayfish.guns.GunMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class CustomPotionItem extends net.minecraft.world.item.PotionItem
{
    public CustomPotionItem()
    {
        super(new Item.Properties().stacksTo(1).tab(GunMod.GROUP));
    }

    @Override
    public int getUseDuration(ItemStack itemStack)
    {
        return 32; // Default use duration of a potion
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.DRINK;
    }
}
