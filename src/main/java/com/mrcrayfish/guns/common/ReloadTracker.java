package com.mrcrayfish.guns.common;

import com.mrcrayfish.guns.Config;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.common.network.ServerPlayHandler;
import com.mrcrayfish.guns.init.ModSyncedDataKeys;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.IHasAmmo;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.S2CMessageGunSound;
import com.mrcrayfish.guns.network.message.S2CMessageNotification;
import com.mrcrayfish.guns.util.GunHelper;
import com.mrcrayfish.guns.util.GunPotionHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ReloadTracker
{
    private static final Map<Player, ReloadTracker> RELOAD_TRACKER_MAP = new WeakHashMap<>();

    private final int startTick;
    private final int slot;
    private final ItemStack stack;
    private final IHasAmmo iHasAmmo;

    private ReloadTracker(Player player)
    {
        this.startTick = player.tickCount;
        this.slot = player.getInventory().selected;
        this.stack = player.getInventory().getSelected();
        this.iHasAmmo = ((IHasAmmo) stack.getItem());
    }

    /**
     * Tests if the current item the player is holding is the same as the one being reloaded
     *
     * @param player the player to check
     * @return True if it's the same weapon and slot
     */
    private boolean isSameWeapon(Player player)
    {
        return !this.stack.isEmpty() && player.getInventory().selected == this.slot && player.getInventory().getSelected() == this.stack;
    }

    /**
     * @return True if the weapon is full
     */
    private boolean isWeaponFull(Player player)
    {
        if(iHasAmmo.hasAmmoMagazine(stack))
            return GunHelper.hasMagazineLoaded(stack) && iHasAmmo.getAmmoCount(stack) > 0;

        return iHasAmmo.getAmmoCount(stack) >= iHasAmmo.getMaxAmmo(stack);
    }

    private boolean hasNoAmmo(Player player)
    {
        return Gun.findAmmo(player, iHasAmmo.getAmmoType(this.stack)).stack().isEmpty();
    }

    private boolean canReload(Player player)
    {
        int deltaTicks = player.tickCount - this.startTick;
        int interval = GunPotionHelper.getReloadInterval(player);
        return deltaTicks > 0 && deltaTicks % interval == 0;
    }

    private void increaseAmmo(Player player)
    {
        AmmoContext context = Gun.findAmmo(player, iHasAmmo.getAmmoType(this.stack));
        ItemStack ammo = context.stack();

        if(ammo.isEmpty()) return;

        int a = iHasAmmo.getAmmoCount(this.stack);
        int m = iHasAmmo.getMaxAmmo(this.stack);
        int r = iHasAmmo.getReloadAmount(this.stack);
        int amount = Math.min(m - a, r);

        if(stack.getItem() instanceof GunItem && iHasAmmo.hasAmmoMagazine(stack))
        {
            if(player instanceof ServerPlayer serverPlayer)
                ServerPlayHandler.handleUnload(serverPlayer);

            GunHelper.loadMagazine(stack, ammo);

            // Trigger that the container changed
            Container container = context.container();
            if(container != null)
            {
                container.setChanged();
            }
        }
        else
        {
            int maxAmmo = iHasAmmo.getMaxAmmo(stack);
            amount = Math.min(amount, ammo.getCount());
            GunHelper.increaseAmmo(iHasAmmo, stack, amount);
            ammo.shrink(amount);

            // Trigger that the container changed
            Container container = context.container();
            if(container != null)
            {
                container.setChanged();
            }
        }

        ResourceLocation reloadSound = iHasAmmo.getReloadSound(this.stack);
        if(reloadSound != null)
        {
            double radius = Config.SERVER.reloadMaxDistance.get();
            S2CMessageGunSound message = new S2CMessageGunSound(reloadSound, SoundSource.PLAYERS, (float) player.getX(), (float) player.getY() + 1.0F, (float) player.getZ(), 1.0F, 1.0F, player.getId(), false, true);
            PacketHandler.getPlayChannel().send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(player.getX(), (player.getY() + 1.0), player.getZ(), radius, player.level.dimension())), message);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START && !event.player.level.isClientSide)
        {
            Player player = event.player;
            if(ModSyncedDataKeys.RELOADING.getValue(player))
            {
                if(!RELOAD_TRACKER_MAP.containsKey(player))
                {
                    if(!(player.getInventory().getSelected().getItem() instanceof IHasAmmo))
                    {
                        ModSyncedDataKeys.RELOADING.setValue(player, false);
                        return;
                    }
                    RELOAD_TRACKER_MAP.put(player, new ReloadTracker(player));
                }

                ReloadTracker tracker = RELOAD_TRACKER_MAP.get(player);

                if(!tracker.isSameWeapon(player) || tracker.isWeaponFull(player) || tracker.hasNoAmmo(player))
                {
                    RELOAD_TRACKER_MAP.remove(player);
                    ModSyncedDataKeys.RELOADING.setValue(player, false);
                    return;
                }

                if(tracker.canReload(player))
                {
                    tracker.increaseAmmo(player);

                    PacketHandler.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new S2CMessageNotification(NotificationType.RELOADING));

                    if(tracker.isWeaponFull(player) || tracker.hasNoAmmo(player))
                    {
                        RELOAD_TRACKER_MAP.remove(player);
                        ModSyncedDataKeys.RELOADING.setValue(player, false);

                        if(!(tracker.stack.getItem() instanceof GunItem)) // Only guns
                            return;

                        final Player finalPlayer = player;
                        final Gun gun = ((GunItem) tracker.stack.getItem()).getModifiedGun(tracker.stack);
                        DelayedTask.runAfter(4, () ->
                        {
                            ResourceLocation cockSound = gun.getSounds().getCock();
                            if(cockSound != null && finalPlayer.isAlive())
                            {
                                double radius = Config.SERVER.reloadMaxDistance.get();
                                S2CMessageGunSound messageSound = new S2CMessageGunSound(cockSound, SoundSource.PLAYERS, (float) finalPlayer.getX(), (float) (finalPlayer.getY() + 1.0), (float) finalPlayer.getZ(), 1.0F, 1.0F, finalPlayer.getId(), false, true);
                                PacketHandler.getPlayChannel().send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(finalPlayer.getX(), (finalPlayer.getY() + 1.0), finalPlayer.getZ(), radius, finalPlayer.level.dimension())), messageSound);
                            }
                        });
                    }
                }
            }
            else
            {
                RELOAD_TRACKER_MAP.remove(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerEvent.PlayerLoggedOutEvent event)
    {
        MinecraftServer server = event.getPlayer().getServer();
        if(server != null)
        {
            server.execute(() -> RELOAD_TRACKER_MAP.remove(event.getPlayer()));
        }
    }
}
