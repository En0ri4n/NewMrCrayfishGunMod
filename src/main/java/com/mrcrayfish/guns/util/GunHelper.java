package com.mrcrayfish.guns.util;

import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.IHasAmmo;
import com.mrcrayfish.guns.item.MagazineItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class GunHelper
{
    /**
     * Sets the ammo count of the magazine inside the gun
     */
    public static void setAmmoCount(ItemStack gunStack, int amount)
    {
        if(!(gunStack.getItem() instanceof GunItem gunItem)) return;

        if(gunItem.hasAmmoMagazine(gunStack))
        {
            if(!hasMagazineLoaded(gunStack)) return;

            ItemStack ammo = ItemStack.of(gunStack.getOrCreateTag().getCompound("Magazine"));

            if(ammo.getItem() instanceof MagazineItem)
            {
                ammo.getOrCreateTag().putInt("AmmoCount", amount);
                gunStack.getOrCreateTag().put("Magazine", ammo.save(new CompoundTag()));
            }
        }
        else
        {
            gunStack.getOrCreateTag().putInt("AmmoCount", amount);
        }
    }

    /**
     * Gets the ammo count from the magazine inside the gun
     *
     * @param gunStack the gun itemstack
     * @return The amount of ammo in the magazine
     */
    public static int getAmmoCount(ItemStack gunStack)
    {
        if(!(gunStack.getItem() instanceof GunItem gunItem)) return 0;

        if(gunItem.hasAmmoMagazine(gunStack))
        {
            if(!gunStack.getOrCreateTag().contains("Magazine")) return 0;

            ItemStack ammo = ItemStack.of(gunStack.getOrCreateTag().getCompound("Magazine"));

            if(ammo.getItem() instanceof MagazineItem magazine) return magazine.getAmmoCount(ammo);
        }
        else if(gunStack.getOrCreateTag().contains("AmmoCount"))
        {
            return gunStack.getOrCreateTag().getInt("AmmoCount");
        }

        return 0;
    }

    /**
     * Get a full magazine of the specified ammo
     */
    public static ItemStack getFullMagazine(ResourceLocation itemId)
    {
        if(!(ForgeRegistries.ITEMS.getValue(itemId) instanceof MagazineItem magazine)) return ItemStack.EMPTY;

        ItemStack stack = new ItemStack(magazine);
        stack.getOrCreateTag().putInt("AmmoCount", magazine.getMaxAmmo(stack));
        return stack;
    }

    /**
     * Gets the magazine from the gun
     */
    public static ItemStack getMagazine(ItemStack gunStack)
    {
        if(!(gunStack.getItem() instanceof GunItem)) return ItemStack.EMPTY;

        if(!gunStack.getOrCreateTag().contains("Magazine")) return ItemStack.EMPTY;

        return ItemStack.of(gunStack.getOrCreateTag().getCompound("Magazine"));
    }

    /**
     * Sets the gun to have a full magazine of the specified ammo (used in Creative Tabs)
     */
    public static ItemStack setWeaponFull(ItemStack gunStack)
    {
        if(!(gunStack.getItem() instanceof GunItem gunItem)) return gunStack;

        if(gunItem.hasAmmoMagazine(gunStack))
            gunStack.getOrCreateTag().put("Magazine", getFullMagazine(gunItem.getGun().getProjectile().getItem()).save(new CompoundTag()));
        else
            gunStack.getOrCreateTag().putInt("AmmoCount", gunItem.getGun().getGeneral().getMaxAmmo());

        return gunStack;
    }

    /**
     * Increases the ammo count of the item (no checks are done)
     */
    public static void increaseAmmo(IHasAmmo iHasAmmo, ItemStack stack, int amount)
    {
        int ammoCount = iHasAmmo.getAmmoCount(stack);
        stack.getOrCreateTag().putInt("AmmoCount", ammoCount + amount);
    }

    /**
     * Loads the magazine in the gun and shrink the magazine stack
     */
    public static void loadMagazine(ItemStack stack, ItemStack magazine)
    {
        if(!(stack.getItem() instanceof GunItem gun)) return;

        if(gun.getAmmoType(stack).equals(magazine.getItem().getRegistryName()))
        {
            stack.getOrCreateTag().put("Magazine", magazine.save(new CompoundTag()));
            magazine.shrink(1);
        }
    }

    public static ItemStack unloadMagazine(ItemStack gunStack)
    {
        if(!(gunStack.getItem() instanceof GunItem)) return ItemStack.EMPTY;

        if(!gunStack.getOrCreateTag().contains("Magazine")) return ItemStack.EMPTY;

        ItemStack magazine = ItemStack.of(gunStack.getOrCreateTag().getCompound("Magazine"));
        gunStack.getOrCreateTag().remove("Magazine");
        return magazine;
    }

    /**
     * Gets the ammo type of the gun (returns air if not found or not a gun)
     */
    public static Item getGunAmmo(ItemStack gunStack)
    {
        if(!(gunStack.getItem() instanceof GunItem gun)) return Items.AIR;

        Item ammoItem = ForgeRegistries.ITEMS.getValue(gun.getAmmoType(gunStack));

        return ammoItem == null ? Items.AIR : ammoItem;
    }

    public static boolean hasMagazineLoaded(ItemStack gunStack)
    {
        return gunStack.getOrCreateTag().contains("Magazine") && !ItemStack.of(gunStack.getOrCreateTag().getCompound("Magazine")).isEmpty();
    }
}
