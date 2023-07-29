package com.mrcrayfish.guns.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 *
 * Transformed and adapted as needed by: En0ri4n
 */
public class BufferUtil
{
    public static void writeVec3(FriendlyByteBuf buffer, Vec3 vec)
    {
        buffer.writeDouble(vec.x);
        buffer.writeDouble(vec.y);
        buffer.writeDouble(vec.z);
    }

    public static Vec3 readVec3(FriendlyByteBuf buffer)
    {
        return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }
}
