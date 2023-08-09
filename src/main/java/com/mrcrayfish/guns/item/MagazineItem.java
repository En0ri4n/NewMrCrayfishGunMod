package com.mrcrayfish.guns.item;

import com.mrcrayfish.guns.common.Ammo;
import com.mrcrayfish.guns.common.NetworkAmmoManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A basic item class that implements {@link IAmmo} to indicate this item is ammunition
 * <p>
 * Author: MrCrayfish
 *
 * Transformed by En0ri4n
 */
public class MagazineItem extends Item implements IAmmo, IHasAmmo
{
    private Ammo ammo = new Ammo();

    public MagazineItem(Properties properties)
    {
        super(properties.durability(100));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag)
    {
        // TODO: check if this is the right way
        Player player = Minecraft.getInstance().player;

        if(player == null) return;

        CompoundTag tagCompound = stack.getTag();

        if(tagCompound != null && getMaxAmmo(stack) > 1)
        {
            tooltip.add(new TranslatableComponent("info.cgm.ammo", ChatFormatting.WHITE.toString() + getAmmoCount(stack) + "/" + getMaxAmmo(stack)).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack pStack)
    {
        return getAmmoCount(pStack) < getMaxAmmo(pStack);
    }

    @Override
    public int getBarColor(ItemStack pStack)
    {
        return pStack.getOrCreateTag().getInt("AmmoCount") < this.ammo.getGeneral().getMaxAmmo() ? 0xECC906 : 0xFF0000;
    }

    public void setAmmo(NetworkAmmoManager.Supplier supplier)
    {
        this.ammo = supplier.getAmmo();
    }

    public Ammo getAmmo()
    {
        return this.ammo;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> stacks)
    {
        if(this.allowdedIn(group))
        {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putInt("AmmoCount", this.ammo.getGeneral().getMaxAmmo());
            stacks.add(stack);
        }
    }

    @Override
    public int getAmmoCount(ItemStack stack)
    {
        if(stack.getItem() instanceof MagazineItem && stack.getOrCreateTag().contains("AmmoCount"))
        {
            return stack.getOrCreateTag().getInt("AmmoCount");
        }

        return 0;
    }

    @Override
    public int getReloadAmount(ItemStack stack)
    {
        return this.ammo.getGeneral().getReloadAmount();
    }

    @Override
    public boolean canUnload(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean ignoreAmmo(ItemStack stack)
    {
        return stack.getItem() instanceof MagazineItem && stack.getOrCreateTag().getBoolean("IgnoreAmmo");
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        if(stack.getItem() instanceof MagazineItem)
        {
            return this.ammo.getProjectile().getItem().equals(stack.getItem().getRegistryName());
        }

        return false;
    }

    @Override
    public int getAmmoCapacity(Player player, ItemStack stack)
    {
        return this.ammo.getGeneral().getMaxAmmo();
    }

    @Override
    public ResourceLocation getAmmoType(ItemStack stack)
    {
        return this.ammo.getProjectile().getItem();
    }

    @Override
    public int getMaxAmmo(ItemStack stack)
    {
        return this.ammo.getGeneral().getMaxAmmo();
    }

    @Override
    public ResourceLocation getReloadSound(ItemStack stack)
    {
        return this.ammo.getSounds().getReload();
    }
}
