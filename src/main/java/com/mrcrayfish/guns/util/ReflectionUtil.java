package com.mrcrayfish.guns.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Author: MrCrayfish
 */
public class ReflectionUtil
{
    private static final Method updateRedstoneOutputMethod = ObfuscationReflectionHelper.findMethod(TargetBlock.class, "m_57391_", LevelAccessor.class, BlockState.class, BlockHitResult.class, Entity.class);
    private static final Field maxDamageField = ObfuscationReflectionHelper.findField(Item.class, "f_41371_");

    public static int updateTargetBlock(TargetBlock block, LevelAccessor accessor, BlockState state, BlockHitResult result, Entity entity)
    {
        try
        {
            return (int) updateRedstoneOutputMethod.invoke(block, accessor, state, result, entity);
        }
        catch(IllegalAccessException | InvocationTargetException ignored)
        {
            return 0;
        }
    }

    public static void setItemMaxDurability(Item item, int maxDamage)
    {
        prepareField(maxDamageField);
        setFieldValue(maxDamageField, item, maxDamage);
    }

    private static void setFieldValue(Field field, Object instance, Object value)
    {
        prepareField(field);

        try
        {
            field.set(instance, value);
        }
        catch(IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void prepareField(Field field)
    {
        try
        {
            field.setAccessible(true);
            // Remove final modifier
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
