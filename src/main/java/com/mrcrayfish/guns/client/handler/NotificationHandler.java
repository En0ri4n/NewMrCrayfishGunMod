package com.mrcrayfish.guns.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: En0ri4n
 */
public class NotificationHandler
{
    private static NotificationHandler instance;

    public static NotificationHandler get()
    {
        if(instance == null)
        {
            instance = new NotificationHandler();
        }
        return instance;
    }

    private int showTime;
    private int timer;

    private NotificationHandler() {}

    @SubscribeEvent
    public void onNotification(RenderGameOverlayEvent e)
    {
        System.out.println("a");
        if(e.getType() == RenderGameOverlayEvent.ElementType.LAYER)
        {
            Font font = Minecraft.getInstance().font;
            PoseStack poseStack = e.getMatrixStack();
            int width = e.getWindow().getWidth();
            int height = e.getWindow().getHeight();

            Screen.drawCenteredString(poseStack, font, "^^", 10, 10, 0xFFFFFFFF);
        }
    }
}
