package com.mrcrayfish.guns.util;

/**
 * Author: MrCrayfish
 *
 * Transformed and adapted as needed by: En0ri4n
 */
public abstract class SuperBuilder<R, T extends SuperBuilder<R, T>>
{
    public abstract R build();

    @SuppressWarnings("unchecked")
    protected final T self()
    {
        return (T) this;
    }
}
