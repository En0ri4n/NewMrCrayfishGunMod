package com.mrcrayfish.guns.client.screen.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.item.IHasAmmo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class GunInfosOverlay implements IIngameOverlay
{
    private static GunInfosOverlay instance;

    public static GunInfosOverlay get()
    {
        return instance == null ? instance = new GunInfosOverlay() : instance;
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

        ItemStack stack = mc.player.getItemInHand(InteractionHand.MAIN_HAND);

        if(stack.getItem() instanceof IHasAmmo iHasAmmo)
        {
            int ammoCount = iHasAmmo.getAmmoCount(stack);
            int displayHeight = height - (mc.player.isCreative() ? 33 : 48);

            String ammo = ChatFormatting.GRAY + "" + ammoCount + "/" + iHasAmmo.getMaxAmmo(stack);

            RenderSystem.setShaderTexture(0, Reference.getLoc("textures/bullet.png"));
            Screen.blit(poseStack, width / 2 + mc.font.width(ammo) / 2 - 2, displayHeight - 1, 10, 10, 0, 0,64, 64, 64, 64);

            Screen.drawString(poseStack, mc.font, ammo, width / 2 - mc.font.width(ammo) / 2, displayHeight, 0xFFFFFFFF);
        }
    }
}
