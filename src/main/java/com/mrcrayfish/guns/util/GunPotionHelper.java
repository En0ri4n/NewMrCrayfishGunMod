package com.mrcrayfish.guns.util;

import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.init.ModEffects;
import com.mrcrayfish.guns.particles.TrailData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class GunPotionHelper
{
    public static ParticleOptions getParticle(ServerPlayer player, ItemStack weapon)
    {
        if(player.hasEffect(ModEffects.FIRE_STARTER.get()))
        {
            return ParticleTypes.LAVA;
        }
        else if(player.hasEffect(ModEffects.PUNCTURING.get()))
        {
            return ParticleTypes.ENCHANTED_HIT;
        }
        return new TrailData(weapon.isEnchanted());
    }

    public static int getReloadInterval(Player player, ItemStack weapon)
    {
        int interval = 10;
        if(player.hasEffect(ModEffects.QUICK_HANDS.get()))
        {
            int level = player.getEffect(ModEffects.QUICK_HANDS.get()).getAmplifier();
            if(level > 0)
            {
                interval -= 3 * level;
            }
        }
        return Math.max(interval, 1);
    }

    public static int getRate(Player player, ItemStack weapon, Gun modifiedGun)
    {
        int rate = modifiedGun.getGeneral().getRate();
        int level = GunPotionHelper.getEffectLevel(player, ModEffects.TRIGGER_FINGER.get());
        if(level > 0)
        {
            float newRate = rate * (0.25F * level);
            rate -= Mth.clamp(newRate, 0, rate);
        }
        return rate;
    }

    public static double getAimDownSightSpeed(Player player, ItemStack weapon)
    {
        int level = GunPotionHelper.getEffectLevel(player, ModEffects.LIGHTWEIGHT.get());
        return level > 0 ? 1.5 : 1.0;
    }

    public static int getAmmoCapacity(Player player, ItemStack weapon, Gun modifiedGun)
    {
        int capacity = modifiedGun.getGeneral().getMaxAmmo();
        int level = GunPotionHelper.getEffectLevel(player, ModEffects.OVER_CAPACITY.get());
        if(level > 0)
        {
            capacity += Math.max(level, (capacity / 2) * level);
        }
        return capacity;
    }

    public static double getProjectileSpeedModifier(LivingEntity shooter, ItemStack weapon)
    {
        int level = GunPotionHelper.getEffectLevel(shooter, ModEffects.ACCELERATOR.get());
        if(level > 0)
        {
            return 1.0 + 0.5 * level;
        }
        return 1.0;
    }

    public static float getAcceleratorDamage(LivingEntity shooter, ItemStack weapon, float damage)
    {
        int level = GunPotionHelper.getEffectLevel(shooter, ModEffects.ACCELERATOR.get());
        if(level > 0)
        {
            return damage + damage * (0.1F * level);
        }
        return damage;
    }

    public static float getPuncturingChance(LivingEntity shooter, ItemStack weapon)
    {
        int level = GunPotionHelper.getEffectLevel(shooter, ModEffects.PUNCTURING.get());
        return level * 0.05F;
    }

    public static int getEffectLevel(LivingEntity entity, MobEffect effect)
    {
        return entity != null && entity.hasEffect(effect) ? entity.getEffect(effect).getAmplifier() : 0;
    }
}
