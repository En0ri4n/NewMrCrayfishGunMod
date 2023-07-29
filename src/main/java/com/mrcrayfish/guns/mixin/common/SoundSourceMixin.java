package com.mrcrayfish.guns.mixin.common;

import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(SoundSource.class)
@Unique
public class SoundSourceMixin
{
    @Shadow
    @Final
    @Mutable
    private static SoundSource[] $VALUES;

    private static final SoundSource GUNS = soundSourceExpansion$addVariant("GUNS", "guns");

    @Invoker("<init>")
    public static SoundSource soundSourceExpansion$invokeInit(String internalName, int internalId, String name) {
        throw new AssertionError();
    }

    private static SoundSource soundSourceExpansion$addVariant(String internalName, String name) {
        ArrayList<SoundSource> variants = new ArrayList<>(Arrays.asList(SoundSourceMixin.$VALUES));
        SoundSource instrument = soundSourceExpansion$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, name);
        variants.add(instrument);
        SoundSourceMixin.$VALUES = variants.toArray(new SoundSource[0]);
        return instrument;
    }
}
