package com.mrcrayfish.guns.mixin.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionItemMixin
{
    @Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
    private void isFoilMixin(ItemStack stack, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(stack.isEnchanted());
    }
}
