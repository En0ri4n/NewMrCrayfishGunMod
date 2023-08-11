package com.mrcrayfish.guns.datagen;

import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.common.Magazine;
import com.mrcrayfish.guns.init.ModItems;
import com.mrcrayfish.guns.init.ModSounds;
import net.minecraft.data.DataGenerator;

public class MagazineGen extends MagazineProvider
{
    public MagazineGen(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void registerMagazines()
    {
        addMagazine(Reference.getLoc("basic_magazine"), Magazine.Builder.create()
                .setStoredAmmo(ModItems.BASIC_AMMO.get())
                .setMaxAmmo(30)
                .setReloadAmount(10)
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .build());

        addMagazine(Reference.getLoc("advanced_magazine"), Magazine.Builder.create()
                .setStoredAmmo(ModItems.ADVANCED_AMMO.get())
                .setMaxAmmo(60)
                .setReloadAmount(20)
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .build());
    }
}
