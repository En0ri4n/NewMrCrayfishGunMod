package com.mrcrayfish.guns.datagen;

import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.common.GripType;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.init.ModItems;
import com.mrcrayfish.guns.init.ModSounds;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class GunGen extends GunProvider
{
    public GunGen(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void registerGuns()
    {
        this.addGun(new ResourceLocation(Reference.MOD_ID, "assault_rifle"), Gun.Builder.create()
                .setAuto(true)
                .setFireRate(3)
                .setGripType(GripType.TWO_HANDED)
                .setMaxAmmo(40)
                .setReloadAmount(10)
                .setRecoilKick(0.25F)
                .setRecoilAngle(3.0F)
                .setSpread(4.0F)
                .setAmmo(ModItems.BASIC_AMMO.get())
                .setDamage(6.5F)
                .setProjectileSize(0.2F)
                .setProjectileSpeed(10F)
                .setProjectileLife(25)
                .setFireSound(ModSounds.ITEM_ASSAULT_RIFLE_FIRE.get())
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .setCockSound(ModSounds.ITEM_ASSAULT_RIFLE_COCK.get())
                .setSilencedFireSound(ModSounds.ITEM_ASSAULT_RIFLE_SILENCED_FIRE.get())
                .setEnchantedFireSound(ModSounds.ITEM_ASSAULT_RIFLE_ENCHANTED_FIRE.get())
                .setMuzzleFlash(0.5, 0.0, 3.6, -6.41)
                .setZoom(Gun.Modules.Zoom.builder()
                        .setFovModifier(0.6F)
                        .setOffset(0.0, 5.2, 3.2))
                .setScope(1.0F, 0.0, 4.4, 4.0)
                .setBarrel(0.5F, 0.0, 3.6, -5.9)
                .setStock(1.0F, 0.0, 3.6, 8.2)
                .setUnderBarrel(1.0F, 0.0, 2.799, -1.0)
                .build());

        this.addGun(new ResourceLocation(Reference.MOD_ID, "bazooka"), Gun.Builder.create()
                .setFireRate(80)
                .setGripType(GripType.BAZOOKA)
                .setMaxAmmo(1)
                .setRecoilAngle(10.0F)
                .setRecoilDurationOffset(0.25F)
                .setAmmo(ModItems.MISSILE_AMMO.get())
                .setDamage(25.0F)
                .setProjectileVisible(true)
                .setProjectileSize(0.2F)
                .setProjectileSpeed(2.0F)
                .setProjectileLife(150)
                .setFireSound(ModSounds.ITEM_BAZOOKA_FIRE.get())
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .setMuzzleFlash(2.0, 0.0, 4.3, -8.01)
                .setZoom(Gun.Modules.Zoom.builder()
                        .setFovModifier(0.5F)
                        .setOffset(3.5, 4.3, 16.0))
                .build());

        this.addGun(new ResourceLocation(Reference.MOD_ID, "grenade_launcher"), Gun.Builder.create()
                .setFireRate(20)
                .setGripType(GripType.TWO_HANDED)
                .setMaxAmmo(1)
                .setRecoilAngle(5.0F)
                .setRecoilKick(1.0F)
                .setRecoilDurationOffset(0.25F)
                .setAmmo(ModItems.GRENADE.get())
                .setDamage(15.0F)
                .setProjectileVisible(true)
                .setProjectileSize(0.2F)
                .setProjectileSpeed(1.5F)
                .setProjectileLife(50)
                .setProjectileAffectedByGravity(true)
                .setFireSound(ModSounds.ITEM_GRENADE_LAUNCHER_FIRE.get())
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .setMuzzleFlash(0.75, 0.0, 3.5, -3.8)
                .setZoom(Gun.Modules.Zoom.builder()
                        .setFovModifier(0.6F)
                        .setOffset(0.0, 5.7, 3.0))
                .setScope(1.0F, 0.0, 5.2, 2.7)
                .setStock(1.0F, 0.0, 3.6, 8.2)
                .build());

        this.addGun(new ResourceLocation(Reference.MOD_ID, "heavy_rifle"), Gun.Builder.create()
                .setFireRate(40)
                .setGripType(GripType.TWO_HANDED)
                .setMaxAmmo(4)
                .setReloadAmount(1)
                .setRecoilAngle(10.0F)
                .setRecoilKick(1.0F)
                .setRecoilDurationOffset(0.5F)
                .setRecoilAdsReduction(0.4F)
                .setAlwaysSpread(true)
                .setSpread(1.0F)
                .setAmmo(ModItems.ADVANCED_AMMO.get())
                .setDamage(18.0F)
                .setProjectileAffectedByGravity(true)
                .setProjectileSize(0.0625F)
                .setProjectileSpeed(25.0F)
                .setProjectileLife(30)
                .setFireSound(ModSounds.ITEM_HEAVY_RIFLE_FIRE.get())
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .setCockSound(ModSounds.ITEM_HEAVY_RIFLE_COCK.get())
                .setSilencedFireSound(ModSounds.ITEM_HEAVY_RIFLE_SILENCED_FIRE.get())
                .setEnchantedFireSound(ModSounds.ITEM_HEAVY_RIFLE_ENCHANTED_FIRE.get())
                .setMuzzleFlash(1.0, 0.0, 3.6, -9.41)
                .setZoom(Gun.Modules.Zoom.builder()
                        .setFovModifier(0.6F)
                        .setOffset(0.0, 5.1, 2.0))
                .setScope(1.0F, 0.0, 4.4, 4.0)
                .setBarrel(0.5F, 0.0, 3.6, -9.4)
                .setUnderBarrel(1.0F, 0.0, 3.0, -0.5)
                .build());

        this.addGun(new ResourceLocation(Reference.MOD_ID, "machine_pistol"), Gun.Builder.create()
                .setAuto(true)
                .setFireRate(2)
                .setGripType(GripType.ONE_HANDED)
                .setMaxAmmo(80)
                .setReloadAmount(20)
                .setRecoilAngle(4.0F)
                .setRecoilKick(0.25F)
                .setRecoilAdsReduction(0.5F)
                .setAlwaysSpread(true)
                .setSpread(6.0F)
                .setAmmo(ModItems.BASIC_AMMO.get())
                .setDamage(3.0F)
                .setProjectileSize(0.2F)
                .setProjectileSpeed(10.0)
                .setProjectileLife(20)
                .setFireSound(ModSounds.ITEM_MACHINE_PISTOL_FIRE.get())
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .setCockSound(ModSounds.ITEM_RIFLE_COCK.get())
                .setSilencedFireSound(ModSounds.ITEM_MACHINE_PISTOL_SILENCED_FIRE.get())
                .setEnchantedFireSound(ModSounds.ITEM_MACHINE_PISTOL_ENCHANTED_FIRE.get())
                .setMuzzleFlash(0.35, 0.0, 3.5, 1.79)
                .setZoom(Gun.Modules.Zoom.builder()
                        .setFovModifier(0.8F)
                        .setOffset(0.0, 4.8, -1.0))
                .setScope(1.0F, 0.0, 4.0, 5.5)
                .setBarrel(0.5F, 0.0, 3.2, 2.2)
                .setStock(1.0F, 0.0,3.2, 8.2)
                .build());

        this.addGun(new ResourceLocation(Reference.MOD_ID, "mini_gun"), Gun.Builder.create()
                .setAuto(true)
                .setFireRate(2)
                .setGripType(GripType.MINI_GUN)
                .setMaxAmmo(100)
                .setReloadAmount(10)
                .setRecoilAngle(1.0F)
                .setAlwaysSpread(true)
                .setSpread(7.0F)
                .setAmmo(ModItems.BASIC_AMMO.get())
                .setDamage(5.0F)
                .setProjectileSize(0.2F)
                .setProjectileSpeed(8.0F)
                .setProjectileLife(30)
                .setFireSound(ModSounds.ITEM_MINI_GUN_FIRE.get())
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .setEnchantedFireSound(ModSounds.ITEM_MINI_GUN_ENCHANTED_FIRE.get())
                .setMuzzleFlash(0.5, 0.0, 2.7, -11.51)
                .build());

        this.addGun(new ResourceLocation(Reference.MOD_ID, "pistol"), Gun.Builder.create()
                .setFireRate(4)
                .setGripType(GripType.ONE_HANDED)
                .setMaxAmmo(16)
                .setReloadAmount(4)
                .setRecoilAngle(10.0F)
                .setRecoilAdsReduction(0.5F)
                .setAlwaysSpread(true)
                .setSpread(1.0F)
                .setAmmo(ModItems.BASIC_AMMO.get())
                .setDamage(9.0F)
                .setProjectileSize(0.2F)
                .setProjectileSpeed(10.0)
                .setProjectileLife(25)
                .setFireSound(ModSounds.ITEM_PISTOL_FIRE.get())
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .setCockSound(ModSounds.ITEM_PISTOL_COCK.get())
                .setSilencedFireSound(ModSounds.ITEM_PISTOL_SILENCED_FIRE.get())
                .setEnchantedFireSound(ModSounds.ITEM_PISTOL_ENCHANTED_FIRE.get())
                .setMuzzleFlash(0.5, 0.0, 3.3, 2.64)
                .setZoom(Gun.Modules.Zoom.builder()
                        .setFovModifier(0.7F)
                        .setOffset(0.0, 4.5, -1.0))
                .setScope(0.75F, 0.0, 3.7, 6.0)
                .setBarrel(0.5F, 0.0, 3.3, 2.65)
                .setStock(1.0F, 0.0, 3.3, 7.95)
                .build());

        this.addGun(new ResourceLocation(Reference.MOD_ID, "rifle"), Gun.Builder.create()
                .setFireRate(8)
                .setGripType(GripType.TWO_HANDED)
                .setMaxAmmo(10)
                .setReloadAmount(2)
                .setRecoilAngle(10.0F)
                .setRecoilKick(0.5F)
                .setRecoilAdsReduction(0.5F)
                .setAlwaysSpread(true)
                .setSpread(1.0F)
                .setAmmo(ModItems.ADVANCED_AMMO.get())
                .setDamage(15.0F)
                .setProjectileAffectedByGravity(true)
                .setProjectileSize(0.0625F)
                .setProjectileSpeed(20.0F)
                .setProjectileLife(30)
                .setFireSound(ModSounds.ITEM_RIFLE_FIRE.get())
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .setCockSound(ModSounds.ITEM_RIFLE_COCK.get())
                .setSilencedFireSound(ModSounds.ITEM_RIFLE_SILENCED_FIRE.get())
                .setEnchantedFireSound(ModSounds.ITEM_RIFLE_ENCHANTED_FIRE.get())
                .setMuzzleFlash(0.5, 0.0, 3.8365, -10.21)
                .setZoom(Gun.Modules.Zoom.builder()
                        .setFovModifier(0.6F)
                        .setOffset(0.0, 4.6223, 6.0))
                .setScope(1.0F, 0.0, 4.3, 3.3)
                .setBarrel(0.45F, 0.0, 3.8365,-10.2)
                .setStock(1.0F, 0.0, 3.1294, 8.3)
                .setUnderBarrel(1.0F, 0.0, 2.63, -0.5)
                .build());

        this.addGun(new ResourceLocation(Reference.MOD_ID, "shotgun"), Gun.Builder.create()
                .setFireRate(8)
                .setGripType(GripType.TWO_HANDED)
                .setMaxAmmo(8)
                .setReloadAmount(2)
                .setRecoilKick(0.5F)
                .setRecoilAngle(10.0F)
                .setRecoilAdsReduction(0.4F)
                .setProjectileAmount(5)
                .setAlwaysSpread(true)
                .setSpread(20.0F)
                .setAmmo(ModItems.SHELL_AMMO.get())
                .setDamage(18.0F)
                .setProjectileSize(1.0F)
                .setProjectileSpeed(10.0)
                .setProjectileLife(5)
                .setFireSound(ModSounds.ITEM_SHOTGUN_FIRE.get())
                .setReloadSound(ModSounds.ITEM_PISTOL_RELOAD.get())
                .setCockSound(ModSounds.ITEM_SHOTGUN_COCK.get())
                .setSilencedFireSound(ModSounds.ITEM_SHOTGUN_SILENCED_FIRE.get())
                .setEnchantedFireSound(ModSounds.ITEM_SHOTGUN_ENCHANTED_FIRE.get())
                .setMuzzleFlash(0.5, 0.0, 3.6505, -3.81)
                .setZoom(Gun.Modules.Zoom.builder()
                        .setFovModifier(0.7F)
                        .setOffset(0.0, 5.1, 3.2))
                .setScope(1.0F, 0.0, 4.4, 4.0)
                .setBarrel(0.5F, 0.0, 3.6506, -3.8)
                .setStock(1.0F, 0.0, 3.6506, 8.4)
                .build());
    }
}
