package com.mrcrayfish.guns.init;

import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.effect.EmptyEffect;
import com.mrcrayfish.guns.effect.IncurableEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
public class ModEffects
{
    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Reference.MOD_ID);

    public static final RegistryObject<IncurableEffect> BLINDED = REGISTER.register("blinded", () -> new IncurableEffect(MobEffectCategory.HARMFUL, 0));
    public static final RegistryObject<IncurableEffect> DEAFENED = REGISTER.register("deafened", () -> new IncurableEffect(MobEffectCategory.HARMFUL, 0));

    public static final RegistryObject<MobEffect> QUICK_HANDS = REGISTER.register("quick_hands", () -> new EmptyEffect(MobEffectCategory.BENEFICIAL, 0xFAC8CD));
    public static final RegistryObject<MobEffect> TRIGGER_FINGER = REGISTER.register("trigger_finger", () -> new EmptyEffect(MobEffectCategory.BENEFICIAL, 0x629677));
    public static final RegistryObject<MobEffect> LIGHTWEIGHT = REGISTER.register("lightweight", () -> new EmptyEffect(MobEffectCategory.BENEFICIAL, 0x495D63));
    public static final RegistryObject<MobEffect> ACCELERATOR = REGISTER.register("accelerator", () -> new EmptyEffect(MobEffectCategory.BENEFICIAL, 0x34F6F2));
    public static final RegistryObject<MobEffect> PUNCTURING = REGISTER.register("puncturing", () -> new EmptyEffect(MobEffectCategory.BENEFICIAL, 0xD1F5FF));
    public static final RegistryObject<MobEffect> FIRE_STARTER = REGISTER.register("fire_starter", () -> new EmptyEffect(MobEffectCategory.BENEFICIAL, 0x41E2BA));
    public static final RegistryObject<MobEffect> COLLATERAL = REGISTER.register("collateral", () -> new EmptyEffect(MobEffectCategory.BENEFICIAL, 0x3C4F76));
}