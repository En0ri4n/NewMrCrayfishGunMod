package com.mrcrayfish.guns.init;

import com.mrcrayfish.guns.Reference;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Author: En0ri4n
 */
public class ModPotions
{
    public static final DeferredRegister<Potion> REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, Reference.MOD_ID);

    public static final List<Supplier<Potion>> POTIONS = new ArrayList<>();

    private static final int POTION_DURATION = 20 * 180; // 3 minutes

    public static final RegistryObject<Potion> QUICK_HANDS = register("quick_hands", () -> new Potion(new MobEffectInstance(ModEffects.QUICK_HANDS.get(), POTION_DURATION)));
    public static final RegistryObject<Potion> TRIGGER_FINGER = register("trigger_finger", () -> new Potion(new MobEffectInstance(ModEffects.TRIGGER_FINGER.get(), POTION_DURATION)));
    public static final RegistryObject<Potion> LIGHTWEIGHT = register("lightweight", () -> new Potion(new MobEffectInstance(ModEffects.LIGHTWEIGHT.get(), POTION_DURATION)));
    public static final RegistryObject<Potion> ACCELERATOR = register("accelerator", () -> new Potion(new MobEffectInstance(ModEffects.ACCELERATOR.get(), POTION_DURATION)));
    public static final RegistryObject<Potion> PUNCTURING = register("puncturing", () -> new Potion(new MobEffectInstance(ModEffects.PUNCTURING.get(), POTION_DURATION)));
    public static final RegistryObject<Potion> FIRE_STARTER = register("fire_starter", () -> new Potion(new MobEffectInstance(ModEffects.FIRE_STARTER.get(), POTION_DURATION)));
    public static final RegistryObject<Potion> COLLATERAL = register("collateral", () -> new Potion(new MobEffectInstance(ModEffects.COLLATERAL.get(), POTION_DURATION)));
    
    private static RegistryObject<Potion> register(String name, Supplier<Potion> potion)
    {
        POTIONS.add(potion);
        return REGISTER.register(name, potion);
    }
}
