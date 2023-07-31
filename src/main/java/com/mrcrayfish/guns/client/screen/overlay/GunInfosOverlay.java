package com.mrcrayfish.guns.client.screen.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.item.GunItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class GunInfosOverlay implements IIngameOverlay
{
    private static GunInfosOverlay instance;

    public static GunInfosOverlay get()
    {
        if(instance == null)
            return instance = new GunInfosOverlay();

        return instance;
    }

    private final Minecraft mc;

    private GunInfosOverlay()
    {
        this.mc = Minecraft.getInstance();
    }

    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height)
    {
        if(mc.player == null)
            return;

        if(isCarriyngGun(mc.player))
        {
            ItemStack gunStack = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
            Gun gun = getGunInHand(mc.player).getGun();

            CompoundTag tagCompound = gunStack.getTag();

            if(tagCompound != null)
            {
                int ammoCount = tagCompound.getInt("AmmoCount");
                int displayHeight = height - (mc.player.isCreative() ? 33 : 48);

                String ammo = ChatFormatting.GRAY + "" + ammoCount + "/" + gun.getGeneral().getMaxAmmo();

                RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MOD_ID, "textures/bullet.png"));
                Screen.blit(poseStack, width / 2 + mc.font.width(ammo) / 2 - 2, displayHeight - 1, 10, 10, 0, 0,64, 64, 64, 64);

                Screen.drawString(poseStack, mc.font, ammo, width / 2 - mc.font.width(ammo) / 2, displayHeight, 0xFFFFFFFF);
            }
        }
    }

    private boolean isCarriyngGun(Player player)
    {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GunItem;
    }

    private GunItem getGunInHand(Player player)
    {
        return (GunItem) player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
    }
}
