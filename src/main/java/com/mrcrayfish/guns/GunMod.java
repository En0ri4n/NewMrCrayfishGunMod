package com.mrcrayfish.guns;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.guns.client.ClientHandler;
import com.mrcrayfish.guns.client.CustomGunManager;
import com.mrcrayfish.guns.client.MetaLoader;
import com.mrcrayfish.guns.client.handler.CrosshairHandler;
import com.mrcrayfish.guns.commands.CreloadCommand;
import com.mrcrayfish.guns.common.BoundingBoxManager;
import com.mrcrayfish.guns.common.ProjectileManager;
import com.mrcrayfish.guns.common.config.GunConfigs;
import com.mrcrayfish.guns.crafting.ModRecipeType;
import com.mrcrayfish.guns.crafting.WorkbenchIngredient;
import com.mrcrayfish.guns.datagen.*;
import com.mrcrayfish.guns.enchantment.EnchantmentTypes;
import com.mrcrayfish.guns.entity.GrenadeEntity;
import com.mrcrayfish.guns.entity.MissileEntity;
import com.mrcrayfish.guns.init.*;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.util.GunHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(Reference.MOD_ID)
public class GunMod
{
    public static boolean debugging = false;
    public static boolean controllableLoaded = false;
    public static boolean backpackedLoaded = false;
    public static boolean playerReviveLoaded = false;
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
    public static final CreativeModeTab GROUP = new CreativeModeTab(Reference.MOD_ID)
    {
        @Override
        public ItemStack makeIcon()
        {
            ItemStack stack = new ItemStack(ModItems.PISTOL.get());
            GunHelper.setWeaponFull(stack);
            return stack;
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items)
        {
            super.fillItemList(items);
            CustomGunManager.fill(items);
            items.add(PotionUtils.setPotion(new ItemStack(ModItems.POTION.get()), ModPotions.QUICK_HANDS.get()));
            items.add(PotionUtils.setPotion(new ItemStack(ModItems.POTION.get()), ModPotions.TRIGGER_FINGER.get()));
            items.add(PotionUtils.setPotion(new ItemStack(ModItems.POTION.get()), ModPotions.LIGHTWEIGHT.get()));
            items.add(PotionUtils.setPotion(new ItemStack(ModItems.POTION.get()), ModPotions.ACCELERATOR.get()));
            items.add(PotionUtils.setPotion(new ItemStack(ModItems.POTION.get()), ModPotions.PUNCTURING.get()));
            items.add(PotionUtils.setPotion(new ItemStack(ModItems.POTION.get()), ModPotions.FIRE_STARTER.get()));
            items.add(PotionUtils.setPotion(new ItemStack(ModItems.POTION.get()), ModPotions.COLLATERAL.get()));
        }
    }.setEnchantmentCategories(EnchantmentTypes.GUN, EnchantmentTypes.SEMI_AUTO_GUN);

    public GunMod()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.REGISTER.register(bus);
        ModContainers.REGISTER.register(bus);
        ModEffects.REGISTER.register(bus);
        ModEntities.REGISTER.register(bus);
        ModItems.REGISTER.register(bus);
        ModParticleTypes.REGISTER.register(bus);
        ModRecipeSerializers.REGISTER.register(bus);
        ModSounds.REGISTER.register(bus);
        ModTileEntities.REGISTER.register(bus);
        ModPotions.REGISTER.register(bus);
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onGatherData);
        MinecraftForge.EVENT_BUS.addListener(this::onServerLaunch);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(CrosshairHandler::onConfigReload);
            bus.addListener(ClientHandler::onRegisterReloadListener);
            FrameworkClientAPI.registerDataLoader(MetaLoader.getInstance());
        });
        controllableLoaded = ModList.get().isLoaded("controllable");
        backpackedLoaded = ModList.get().isLoaded("backpacked");
        playerReviveLoaded = ModList.get().isLoaded("playerrevive");
    }

    private void onServerLaunch(ServerStartedEvent event)
    {
        LOGGER.info("Loading gun configs...");
        GunConfigs.load(event.getServer());
    }

    private void onServerStarting(RegisterCommandsEvent event)
    {
        CreloadCommand.register(event.getDispatcher());
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            ModRecipeType.init();
            ModSyncedDataKeys.register();
            CraftingHelper.register(Reference.getLoc("workbench_ingredient"), WorkbenchIngredient.Serializer.INSTANCE);
            ProjectileManager.getInstance().registerFactory(ModItems.GRENADE.get(), (worldIn, entity, weapon, item, modifiedGun) -> new GrenadeEntity(ModEntities.GRENADE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.MISSILE_AMMO.get(), (worldIn, entity, weapon, item, modifiedGun) -> new MissileEntity(ModEntities.MISSILE.get(), worldIn, entity, weapon, item, modifiedGun));
            PacketHandler.init();
            if(Config.COMMON.gameplay.improvedHitboxes.get())
            {
                MinecraftForge.EVENT_BUS.register(new BoundingBoxManager());
            }
        });
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(ClientHandler::setup);
    }

    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        BlockTagGen blockTagGen = new BlockTagGen(generator, existingFileHelper);
        generator.addProvider(new RecipeGen(generator));
        generator.addProvider(new LootTableGen(generator));
        generator.addProvider(blockTagGen);
        generator.addProvider(new ItemTagGen(generator, blockTagGen, existingFileHelper));
        generator.addProvider(new LanguageGen(generator));
        generator.addProvider(new GunGen(generator));
        generator.addProvider(new MagazineGen(generator));
    }

    public static boolean isDebugging()
    {
        return false; //!FMLEnvironment.production;
    }
}
