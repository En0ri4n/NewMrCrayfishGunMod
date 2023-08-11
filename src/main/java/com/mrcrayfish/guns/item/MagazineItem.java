package com.mrcrayfish.guns.item;

import com.mrcrayfish.guns.common.Magazine;
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
 * A item class that implements {@link IAmmo} and {@link IHasAmmo} to indicate this item is loadable ammunition
 * <p>
 * Author: En0ri4n
 */
public class MagazineItem extends Item implements IAmmo, IHasAmmo
{
    private Magazine magazine = new Magazine();

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

        if(tagCompound != null && getMaxAmmo(stack) > 0)
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
        float percent = (float) getAmmoCount(pStack) / (float) getMaxAmmo(pStack);

        int result = 255 << 8;
        result += (int) (percent * 255);
        result = result << 8;

        return result;

    }

    public void setAmmo(NetworkAmmoManager.Supplier supplier)
    {
        this.magazine = supplier.getAmmo();
    }

    public Magazine getAmmo()
    {
        return this.magazine;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> stacks)
    {
        if(this.allowdedIn(group))
        {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putInt("AmmoCount", this.magazine.getGeneral().getMaxAmmo());
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
    public boolean hasAmmoMagazine(ItemStack stack)
    {
        return false;
    }

    @Override
    public int getReloadAmount(ItemStack stack)
    {
        return this.magazine.getGeneral().getReloadAmount();
    }

    @Override
    public boolean canUnload(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean isGun(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean ignoreAmmo(ItemStack stack)
    {
        return stack.getItem() instanceof MagazineItem && stack.getOrCreateTag().getBoolean("IgnoreAmmo");
    }

    @Override
    public void decreaseAmmo(ItemStack stack, int amount)
    {
        if(stack.getItem() instanceof MagazineItem)
        {
            int ammoCount = getAmmoCount(stack);
            stack.getOrCreateTag().putInt("AmmoCount", Math.max(0, ammoCount - amount));
        }
    }

    @Override
    public ResourceLocation getAmmoType(ItemStack stack)
    {
        return this.magazine.getProjectile().getItem();
    }

    @Override
    public int getMaxAmmo(ItemStack stack)
    {
        return this.magazine.getGeneral().getMaxAmmo();
    }

    @Override
    public ResourceLocation getReloadSound(ItemStack stack)
    {
        return this.magazine.getSounds().getReload();
    }
}
