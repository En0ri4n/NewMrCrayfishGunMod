package com.mrcrayfish.guns.init;

import com.mrcrayfish.guns.Reference;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: En0ri4n
 */
public class ModPotions
{
    public static final DeferredRegister<Potion> REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, Reference.MOD_ID);

    public static final RegistryObject<Potion> QUICK_HANDS = REGISTER.register("quick_hands", () -> new Potion(new MobEffectInstance(ModEffects.QUICK_HANDS.get())));

}
