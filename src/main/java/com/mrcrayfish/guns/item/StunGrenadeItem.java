package com.mrcrayfish.guns.item;

import com.mrcrayfish.guns.entity.ThrowableGrenadeEntity;
import com.mrcrayfish.guns.entity.ThrowableStunGrenadeEntity;
import com.mrcrayfish.guns.init.ModSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
public class StunGrenadeItem extends GrenadeItem
{
    public StunGrenadeItem(Item.Properties properties, int maxCookTime)
    {
        super(properties, maxCookTime);
    }

    @Override
    public ThrowableGrenadeEntity create(Level world, LivingEntity entity, int timeLeft)
    {
        return new ThrowableStunGrenadeEntity(world, entity, 20 * 2);
    }

    @Override
    public boolean canCook()
    {
        return false;
    }

    @Override
    protected void onThrown(Level world, ThrowableGrenadeEntity entity)
    {
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.ITEM_GRENADE_PIN.get(), ModSounds.GUNS_SOURCE_SOUND, 1.0F, 1.0F);
    }
}
