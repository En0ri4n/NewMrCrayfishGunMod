package com.mrcrayfish.guns.item;

import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.client.GunItemStackRenderer;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.common.NetworkGunManager;
import com.mrcrayfish.guns.debug.Debug;
import com.mrcrayfish.guns.util.GunHelper;
import com.mrcrayfish.guns.util.GunModifierHelper;
import com.mrcrayfish.guns.util.GunPotionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class GunItem extends Item implements IColored, IMeta, IHasAmmo
{
    private WeakHashMap<CompoundTag, Gun> modifiedGunCache = new WeakHashMap<>();

    private Gun gun = new Gun();

    public GunItem(Item.Properties properties)
    {
        super(properties.durability(1000));
    }

    public void setGun(NetworkGunManager.Supplier supplier)
    {
        this.gun = supplier.getGun();
    }

    public Gun getGun()
    {
        return this.gun;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag)
    {
        Gun modifiedGun = this.getModifiedGun(stack);

        Player player = Minecraft.getInstance().player;

        if(player == null) return;

        Item ammo = ForgeRegistries.ITEMS.getValue(modifiedGun.getProjectile().getItem());
        if(ammo != null)
        {
            tooltip.add(new TranslatableComponent("info.cgm.ammo_type", new TranslatableComponent(ammo.getDescriptionId()).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
        }

        String additionalDamageText = "";
        CompoundTag tagCompound = stack.getTag();
        if(tagCompound != null)
        {
            if(tagCompound.contains("AdditionalDamage", Tag.TAG_ANY_NUMERIC))
            {
                float additionalDamage = tagCompound.getFloat("AdditionalDamage");
                additionalDamage += GunModifierHelper.getAdditionalDamage(stack);

                if(additionalDamage > 0)
                {
                    additionalDamageText = ChatFormatting.GREEN + " +" + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage);
                }
                else if(additionalDamage < 0)
                {
                    additionalDamageText = ChatFormatting.RED + " " + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(additionalDamage);
                }
            }
        }

        float damage = modifiedGun.getProjectile().getDamage();
        damage = GunModifierHelper.getModifiedProjectileDamage(stack, damage);
        damage = GunPotionHelper.getAcceleratorDamage(player, stack, damage);
        tooltip.add(new TranslatableComponent("info.cgm.damage", ChatFormatting.WHITE + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damage) + additionalDamageText).withStyle(ChatFormatting.GRAY));

        if(tagCompound != null)
        {
            if(tagCompound.getBoolean("IgnoreAmmo"))
            {
                tooltip.add(new TranslatableComponent("info.cgm.ignore_ammo").withStyle(ChatFormatting.AQUA));
            }
            else
            {
                int ammoCount = getAmmoCount(stack);
                tooltip.add(new TranslatableComponent("info.cgm.ammo", ChatFormatting.WHITE.toString() + ammoCount + "/" + getMaxAmmo(stack)).withStyle(ChatFormatting.GRAY));
            }
        }

        tooltip.add(new TranslatableComponent("info.cgm.attachment_help", new KeybindComponent("key.cgm.attachments").getString().toUpperCase(Locale.ENGLISH)).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity)
    {
        return true;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> stacks)
    {
        if(this.allowdedIn(group))
        {
            ItemStack stack = new ItemStack(this);
            stacks.add(GunHelper.setWeaponFull(stack));
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }

    public Gun getModifiedGun(ItemStack stack)
    {
        CompoundTag tagCompound = stack.getTag();
        if(tagCompound != null && tagCompound.contains("Gun", Tag.TAG_COMPOUND))
        {
            return this.modifiedGunCache.computeIfAbsent(tagCompound, item ->
            {
                if(tagCompound.getBoolean("Custom"))
                {
                    return Gun.create(tagCompound.getCompound("Gun"));
                }
                else
                {
                    Gun gunCopy = this.gun.copy();
                    gunCopy.deserializeNBT(tagCompound.getCompound("Gun"));
                    return gunCopy;
                }
            });
        }
        if(GunMod.isDebugging())
        {
            return Debug.getGun(this);
        }
        return this.gun;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return false;
    }

    @Override
    public int getEnchantmentValue()
    {
        return 0;
    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        return this.gun.getGeneral().getDurability();
    }

    @Override
    public boolean canBeDepleted()
    {
        return super.canBeDepleted();
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer)
    {
        consumer.accept(new IItemRenderProperties()
        {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer()
            {
                return new GunItemStackRenderer();
            }
        });
    }

    @Override
    public int getAmmoCount(ItemStack stack)
    {
        return GunHelper.getAmmoCount(stack);
    }

    @Override
    public int getReloadAmount(ItemStack stack)
    {
        return hasAmmoMagazine(stack) ? 1 : this.gun.getGeneral().getReloadAmount();
    }

    @Override
    public boolean canUnload(ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean isGun(ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean ignoreAmmo(ItemStack stack)
    {
        return stack.getOrCreateTag().getBoolean("IgnoreAmmo");
    }

    @Override
    public void decreaseAmmo(ItemStack stack, int amount)
    {
        if(!(stack.getItem() instanceof GunItem) || ignoreAmmo(stack)) return;

        GunHelper.setAmmoCount(stack, GunHelper.getAmmoCount(stack) - 1);
    }

    @Override
    public ResourceLocation getAmmoType(ItemStack stack)
    {
        if(stack.getItem() instanceof GunItem)
        {
            Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
            return gun.getProjectile().getItem();
        }

        return new ResourceLocation("empty");
    }

    @Override
    public int getMaxAmmo(ItemStack stack)
    {
        if(!(stack.getItem() instanceof GunItem)) return 0;

        if(hasAmmoMagazine(stack))
        {
            if(GunHelper.hasMagazineLoaded(stack))
            {
                ItemStack magazineStack = GunHelper.getMagazine(stack);
                MagazineItem magazineItem = (MagazineItem) magazineStack.getItem();
                return magazineItem.getMaxAmmo(magazineStack);
            }
        }
        else
        {
            Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
            return gun.getGeneral().getMaxAmmo();
        }

        return 0;
    }

    @Override
    public boolean hasAmmoMagazine(ItemStack stack)
    {
        return GunHelper.getGunAmmo(stack) instanceof MagazineItem;
    }

    @Override
    public ResourceLocation getReloadSound(ItemStack stack)
    {
        if(stack.getItem() instanceof GunItem)
        {
            Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
            return gun.getSounds().getReload();
        }

        return new ResourceLocation("empty");
    }
}
