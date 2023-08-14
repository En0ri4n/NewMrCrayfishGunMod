package com.mrcrayfish.guns.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomPotionItem extends net.minecraft.world.item.PotionItem
{
    public CustomPotionItem()
    {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag)
    {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);

        pTooltip.add(new TranslatableComponent("effect.cgm." + PotionUtils.getPotion(pStack).getName("") + ".desc").withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
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
