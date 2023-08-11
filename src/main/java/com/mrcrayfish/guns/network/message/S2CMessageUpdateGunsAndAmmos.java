package com.mrcrayfish.guns.network.message;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.framework.api.network.PlayMessage;
import com.mrcrayfish.guns.client.network.ClientPlayHandler;
import com.mrcrayfish.guns.common.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.Validate;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
public class S2CMessageUpdateGunsAndAmmos extends PlayMessage<S2CMessageUpdateGunsAndAmmos>
{
    private ImmutableMap<ResourceLocation, Gun> registeredGuns;
    private ImmutableMap<ResourceLocation, CustomGun> customGuns;
    private ImmutableMap<ResourceLocation, Magazine> registeredAmmos;

    public S2CMessageUpdateGunsAndAmmos() {}

    @Override
    public void encode(S2CMessageUpdateGunsAndAmmos message, FriendlyByteBuf buffer)
    {
        Validate.notNull(NetworkGunManager.get());
        Validate.notNull(CustomGunLoader.get());
        NetworkGunManager.get().writeRegisteredGuns(buffer);
        CustomGunLoader.get().writeCustomGuns(buffer);
        NetworkAmmoManager.get().writeRegisteredAmmos(buffer);
    }

    @Override
    public S2CMessageUpdateGunsAndAmmos decode(FriendlyByteBuf buffer)
    {
        S2CMessageUpdateGunsAndAmmos message = new S2CMessageUpdateGunsAndAmmos();
        message.registeredGuns = NetworkGunManager.readRegisteredGuns(buffer);
        message.customGuns = CustomGunLoader.readCustomGuns(buffer);
        message.registeredAmmos = NetworkAmmoManager.readRegisteredAmmos(buffer);
        return message;
    }

    @Override
    public void handle(S2CMessageUpdateGunsAndAmmos message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleUpdateGunsAndAmmos(message));
        supplier.get().setPacketHandled(true);
    }

    public ImmutableMap<ResourceLocation, Gun> getRegisteredGuns()
    {
        return this.registeredGuns;
    }

    public ImmutableMap<ResourceLocation, CustomGun> getCustomGuns()
    {
        return this.customGuns;
    }

    public ImmutableMap<ResourceLocation, Magazine> getRegisteredAmmos()
    {
        return this.registeredAmmos;
    }
}
