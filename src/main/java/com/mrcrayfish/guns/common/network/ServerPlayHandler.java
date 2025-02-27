package com.mrcrayfish.guns.common.network;

import com.mrcrayfish.guns.Config;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.blockentity.WorkbenchBlockEntity;
import com.mrcrayfish.guns.common.*;
import com.mrcrayfish.guns.common.container.AttachmentContainer;
import com.mrcrayfish.guns.common.container.WorkbenchContainer;
import com.mrcrayfish.guns.crafting.WorkbenchRecipe;
import com.mrcrayfish.guns.crafting.WorkbenchRecipes;
import com.mrcrayfish.guns.entity.ProjectileEntity;
import com.mrcrayfish.guns.event.GunFireEvent;
import com.mrcrayfish.guns.init.ModSyncedDataKeys;
import com.mrcrayfish.guns.interfaces.IProjectileFactory;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.IColored;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.C2SMessageShoot;
import com.mrcrayfish.guns.network.message.S2CMessageBulletTrail;
import com.mrcrayfish.guns.network.message.S2CMessageGunSound;
import com.mrcrayfish.guns.network.message.S2CMessageNotification;
import com.mrcrayfish.guns.util.GunHelper;
import com.mrcrayfish.guns.util.GunModifierHelper;
import com.mrcrayfish.guns.util.GunPotionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Predicate;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
public class ServerPlayHandler
{
    private static final Predicate<LivingEntity> HOSTILE_ENTITIES = entity -> entity.getSoundSource() == SoundSource.HOSTILE && !(entity instanceof NeutralMob) && !Config.COMMON.aggroMobs.exemptEntities.get().contains(entity.getType().getRegistryName().toString());

    /**
     * Fires the weapon the player is currently holding.
     * This is only intended for use on the logical server.
     *
     * @param player the player for who's weapon to fire
     */
    public static void handleShoot(C2SMessageShoot message, ServerPlayer player)
    {
        if(player.isSpectator()) return;

        if(player.getUseItem().getItem() == Items.SHIELD) return;

        Level world = player.level;
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(heldItem.getItem() instanceof GunItem gunItem && (Gun.hasAmmo(heldItem) || player.isCreative()))
        {
            Gun modifiedGun = gunItem.getModifiedGun(heldItem);

            if(!player.isCreative())
            {
                heldItem.hurtAndBreak(1, player, broker -> broker.broadcastBreakEvent(InteractionHand.MAIN_HAND)); // damage the item

                gunItem.decreaseAmmo(heldItem, 1); // decrease ammo in magazine
            }

            if(modifiedGun != null)
            {
                if(MinecraftForge.EVENT_BUS.post(new GunFireEvent.Pre(player, heldItem))) return;

                /* Updates the yaw and pitch with the clients current yaw and pitch */
                player.setYRot(Mth.wrapDegrees(message.getRotationYaw()));
                player.setXRot(Mth.clamp(message.getRotationPitch(), -90F, 90F));

                ShootTracker tracker = ShootTracker.getShootTracker(player);
                if(tracker.hasCooldown(gunItem) && tracker.getRemaining(gunItem) > Config.SERVER.cooldownThreshold.get())
                {
                    GunMod.LOGGER.warn(player.getName().getContents() + "(" + player.getUUID() + ") tried to fire before cooldown finished or server is lagging? Remaining milliseconds: " + tracker.getRemaining(gunItem));
                    return;
                }
                tracker.putCooldown(heldItem, gunItem, modifiedGun);

                if(ModSyncedDataKeys.RELOADING.getValue(player))
                {
                    ModSyncedDataKeys.RELOADING.setValue(player, false);
                }

                if(!modifiedGun.getGeneral().isAlwaysSpread() && modifiedGun.getGeneral().getSpread() > 0.0F)
                {
                    SpreadTracker.get(player).update(player, gunItem);
                }

                int count = modifiedGun.getGeneral().getProjectileAmount();
                Gun.Projectile projectileProps = modifiedGun.getProjectile();
                ProjectileEntity[] spawnedProjectiles = new ProjectileEntity[count];
                for(int i = 0; i < count; i++)
                {
                    IProjectileFactory factory = ProjectileManager.getInstance().getFactory(projectileProps.getItem());
                    ProjectileEntity projectileEntity = factory.create(world, player, heldItem, gunItem, modifiedGun);
                    projectileEntity.setWeapon(heldItem);
                    projectileEntity.setAdditionalDamage(Gun.getAdditionalDamage(heldItem));
                    world.addFreshEntity(projectileEntity);
                    spawnedProjectiles[i] = projectileEntity;
                    projectileEntity.tick();
                }
                if(!projectileProps.isVisible())
                {
                    ParticleOptions data = GunPotionHelper.getParticle(player, heldItem);
                    S2CMessageBulletTrail messageBulletTrail = new S2CMessageBulletTrail(spawnedProjectiles, projectileProps, player.getId(), data);
                    PacketHandler.getPlayChannel().send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(), Config.COMMON.network.projectileTrackingRange.get(), player.level.dimension())), messageBulletTrail);
                }

                MinecraftForge.EVENT_BUS.post(new GunFireEvent.Post(player, heldItem));

                if(Config.COMMON.aggroMobs.enabled.get())
                {
                    double radius = GunModifierHelper.getModifiedFireSoundRadius(heldItem, Config.COMMON.aggroMobs.unsilencedRange.get());
                    double x = player.getX();
                    double y = player.getY() + 0.5;
                    double z = player.getZ();
                    AABB box = new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
                    radius *= radius;
                    double dx, dy, dz;
                    for(LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, box, HOSTILE_ENTITIES))
                    {
                        dx = x - entity.getX();
                        dy = y - entity.getY();
                        dz = z - entity.getZ();
                        if(dx * dx + dy * dy + dz * dz <= radius)
                        {
                            entity.setLastHurtByMob(Config.COMMON.aggroMobs.angerHostileMobs.get() ? player : entity);
                        }
                    }
                }

                ResourceLocation fireSound = getFireSound(heldItem, modifiedGun);
                if(fireSound != null)
                {
                    double posX = player.getX();
                    double posY = player.getY() + player.getEyeHeight();
                    double posZ = player.getZ();
                    float volume = GunModifierHelper.getFireSoundVolume(heldItem);
                    float pitch = 0.9F + world.random.nextFloat() * 0.2F;
                    double radius = GunModifierHelper.getModifiedFireSoundRadius(heldItem, Config.SERVER.gunShotMaxDistance.get());
                    boolean muzzle = modifiedGun.getDisplay().getFlash() != null;
                    S2CMessageGunSound messageSound = new S2CMessageGunSound(fireSound, SoundSource.PLAYERS, (float) posX, (float) posY, (float) posZ, volume, pitch, player.getId(), muzzle, false);
                    PacketDistributor.TargetPoint targetPoint = new PacketDistributor.TargetPoint(posX, posY, posZ, radius, player.level.dimension());
                    PacketHandler.getPlayChannel().send(PacketDistributor.NEAR.with(() -> targetPoint), messageSound);
                }

                player.awardStat(Stats.ITEM_USED.get(gunItem));
            }
        }
        else
        {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.8F);
            PacketHandler.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> player), new S2CMessageNotification(NotificationType.EMPTY_AMMO));
        }
    }

    private static ResourceLocation getFireSound(ItemStack stack, Gun modifiedGun)
    {
        ResourceLocation fireSound = null;
        if(GunModifierHelper.isSilencedFire(stack))
        {
            fireSound = modifiedGun.getSounds().getSilencedFire();
        }
        else if(stack.isEnchanted())
        {
            fireSound = modifiedGun.getSounds().getEnchantedFire();
        }

        if(fireSound != null)
        {
            return fireSound;
        }

        return modifiedGun.getSounds().getFire();
    }

    /**
     * Crafts the specified item at the workstation the player is currently using.
     * This is only intended for use on the logical server.
     *
     * @param player the player who is crafting
     * @param id     the id of an item which is registered as a valid workstation recipe
     * @param pos    the block position of the workstation the player is using
     */
    public static void handleCraft(ServerPlayer player, ResourceLocation id, BlockPos pos)
    {
        Level world = player.level;

        if(player.containerMenu instanceof WorkbenchContainer workbench)
        {
            if(workbench.getPos().equals(pos))
            {
                WorkbenchRecipe recipe = WorkbenchRecipes.getRecipeById(world, id);
                if(recipe == null || !recipe.hasMaterials(player)) return;

                recipe.consumeMaterials(player);

                WorkbenchBlockEntity workbenchBlockEntity = workbench.getWorkbench();

                /* Gets the color based on the dye */
                ItemStack stack = recipe.getItem();
                ItemStack dyeStack = workbenchBlockEntity.getInventory().get(0);
                if(dyeStack.getItem() instanceof DyeItem dyeItem)
                {
                    int color = dyeItem.getDyeColor().getTextColor();

                    if(IColored.isDyeable(stack))
                    {
                        IColored colored = (IColored) stack.getItem();
                        colored.setColor(stack, color);
                        workbenchBlockEntity.getInventory().set(0, ItemStack.EMPTY);
                    }
                }

                Containers.dropItemStack(world, pos.getX() + 0.5, pos.getY() + 1.125, pos.getZ() + 0.5, stack);
            }
        }
    }

    /**
     * Called when a player press the unload keybind.
     */
    public static void handleUnload(ServerPlayer player)
    {
        ItemStack heldItem = player.getMainHandItem();

        if(!(heldItem.getItem() instanceof GunItem gunItem))
            return;

        PacketHandler.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> player), new S2CMessageNotification(NotificationType.UNLOADING));

        if(gunItem.hasAmmoMagazine(heldItem))
        {
            if(heldItem.getItem() instanceof GunItem && GunHelper.hasMagazineLoaded(heldItem))
                spawnAmmo(player, GunHelper.unloadMagazine(heldItem));
        }
        else
        {
            GunHelper.setAmmoCount(heldItem, 0);
            spawnAmmo(player, new ItemStack(GunHelper.getGunAmmo(heldItem), gunItem.getAmmoCount(heldItem)));
        }
    }

    private static void spawnAmmo(ServerPlayer player, ItemStack stack)
    {
        player.getInventory().add(stack);
        if(stack.getCount() > 0)
        {
            player.level.addFreshEntity(new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack.copy()));
        }
    }

    public static void handleAttachments(ServerPlayer player)
    {
        ItemStack heldItem = player.getMainHandItem();
        if(heldItem.getItem() instanceof GunItem)
        {
            NetworkHooks.openGui(player, new SimpleMenuProvider((windowId, playerInventory, player1) -> new AttachmentContainer(windowId, playerInventory, heldItem), new TranslatableComponent("container.cgm.attachments")));
        }
    }
}
