package com.mrcrayfish.guns.util.math;

import com.mrcrayfish.guns.entity.ProjectileEntity;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
public class ExtendedEntityRayTraceResult extends EntityHitResult
{
    private final boolean headshot;

    public ExtendedEntityRayTraceResult(ProjectileEntity.EntityResult result)
    {
        super(result.getEntity(), result.getHitPos());
        this.headshot = result.isHeadshot();
    }

    public boolean isHeadshot()
    {
        return this.headshot;
    }
}
