package com.mrcrayfish.guns.network.message;

import com.mrcrayfish.framework.api.network.PlayMessage;
import com.mrcrayfish.guns.client.network.ClientPlayHandler;
import com.mrcrayfish.guns.common.NotificationType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CMessageNotification extends PlayMessage<S2CMessageNotification>
{
    private NotificationType notification;

    public S2CMessageNotification() {}

    public S2CMessageNotification(NotificationType notificationType)
    {
        this.notification = notificationType;
    }

    @Override
    public void encode(S2CMessageNotification message, FriendlyByteBuf friendlyByteBuf)
    {
        friendlyByteBuf.writeEnum(message.notification);
    }

    @Override
    public S2CMessageNotification decode(FriendlyByteBuf friendlyByteBuf)
    {
        return new S2CMessageNotification(friendlyByteBuf.readEnum(NotificationType.class));
    }

    @Override
    public void handle(S2CMessageNotification message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleNotification(message));
        supplier.get().setPacketHandled(true);
    }

    public NotificationType getNotification()
    {
        return this.notification;
    }
}
