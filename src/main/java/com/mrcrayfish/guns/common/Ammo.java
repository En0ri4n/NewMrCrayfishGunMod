package com.mrcrayfish.guns.common;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.annotation.Optional;
import com.mrcrayfish.guns.common.config.JsonSerializable;
import com.mrcrayfish.guns.compat.BackpackHelper;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.MagazineItem;
import com.mrcrayfish.guns.util.GunJsonUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * Simplified version of {@link Gun} to use with an {@link MagazineItem}
 */
public class Ammo implements INBTSerializable<CompoundTag>, JsonSerializable
{
    protected General general = new General();
    protected Projectile projectile = new Projectile();
    protected Sounds sounds = new Sounds();

    public General getGeneral()
    {
        return this.general;
    }

    public Projectile getProjectile()
    {
        return this.projectile;
    }

    public Sounds getSounds()
    {
        return this.sounds;
    }

    public static class General implements INBTSerializable<CompoundTag>, JsonSerializable
    {
        private int maxAmmo = 1;
        @Optional
        private int reloadAmount = 1;
        @Optional
        private int projectileAmount = 1;

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.putInt("MaxAmmo", this.maxAmmo);
            tag.putInt("ReloadSpeed", this.reloadAmount);
            tag.putInt("ProjectileAmount", this.projectileAmount);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("MaxAmmo", Tag.TAG_ANY_NUMERIC))
            {
                this.maxAmmo = tag.getInt("MaxAmmo");
            }
            if(tag.contains("ReloadSpeed", Tag.TAG_ANY_NUMERIC))
            {
                this.reloadAmount = tag.getInt("ReloadSpeed");
            }
            if(tag.contains("ProjectileAmount", Tag.TAG_ANY_NUMERIC))
            {
                this.projectileAmount = tag.getInt("ProjectileAmount");
            }
        }

        @Override
        public JsonObject toJsonObject()
        {
            Preconditions.checkArgument(this.maxAmmo > 0, "Max ammo must be more than zero");
            Preconditions.checkArgument(this.reloadAmount >= 1, "Reload angle must be more than or equal to zero");
            Preconditions.checkArgument(this.projectileAmount >= 1, "Projectile amount must be more than or equal to one");
            JsonObject object = new JsonObject();
            object.addProperty("auto", true);
            object.addProperty("maxAmmo", this.maxAmmo);
            object.addProperty("reloadAmount", this.reloadAmount);
            object.addProperty("projectileAmount", this.projectileAmount);
            object.addProperty("alwaysSpread", true);
            return object;
        }

        @Override
        public void loadConfig(JsonObject json)
        {
            if(json.has("maxAmmo"))
            {
                this.maxAmmo = json.get("maxAmmo").getAsInt();
            }
            if(json.has("reloadAmount"))
            {
                this.reloadAmount = json.get("reloadAmount").getAsInt();
            }
            if(json.has("projectileAmount"))
            {
                this.projectileAmount = json.get("projectileAmount").getAsInt();
            }
        }

        /**
         * @return A copy of the general get
         */
        public General copy()
        {
            General general = new General();
            general.maxAmmo = this.maxAmmo;
            general.reloadAmount = this.reloadAmount;
            general.projectileAmount = this.projectileAmount;
            return general;
        }

        /**
         * @return The maximum amount of ammo this ammo can hold
         */
        public int getMaxAmmo()
        {
            return this.maxAmmo;
        }

        /**
         * @return The amount of ammo to add to the ammo each reload cycle
         */
        public int getReloadAmount()
        {
            return this.reloadAmount;
        }

        /**
         * @return The amount of projectiles this ammo has
         */
        public int getProjectileAmount()
        {
            return this.projectileAmount;
        }
    }

    public static class Projectile implements INBTSerializable<CompoundTag>, JsonSerializable
    {
        private ResourceLocation item = new ResourceLocation(Reference.MOD_ID, "basic_ammo");

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.putString("Item", this.item.toString());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("Item", Tag.TAG_STRING))
            {
                this.item = new ResourceLocation(tag.getString("Item"));
            }
        }

        @Override
        public JsonObject toJsonObject()
        {
            JsonObject object = new JsonObject();
            object.addProperty("item", this.item.toString());
            return object;
        }

        @Override
        public void loadConfig(JsonObject json)
        {
            if(json.has("item"))
            {
                this.item = new ResourceLocation(json.get("item").getAsString());
            }
        }

        public Projectile copy()
        {
            Projectile projectile = new Projectile();
            projectile.item = this.item;
            return projectile;
        }

        /**
         * @return The registry id of the ammo item
         */
        public ResourceLocation getItem()
        {
            return this.item;
        }
    }

    public static class Sounds implements INBTSerializable<CompoundTag>, JsonSerializable
    {
        @Optional
        @Nullable
        private ResourceLocation reload = new ResourceLocation("silence");

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            if(this.reload != null)
            {
                tag.putString("Reload", this.reload.toString());
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            if(tag.contains("Reload", Tag.TAG_STRING))
            {
                this.reload = this.createSound(tag, "Reload");
            }
        }

        @Override
        public JsonObject toJsonObject()
        {
            JsonObject object = new JsonObject();
            if(this.reload != null)
            {
                object.addProperty("reload", this.reload.toString());
            }
            return object;
        }

        @Override
        public void loadConfig(JsonObject json)
        {
            if(json.has("reload"))
            {
                this.reload = ResourceLocation.tryParse(json.get("reload").getAsString());
            }
        }

        public Sounds copy()
        {
            Sounds sounds = new Sounds();
            sounds.reload = this.reload;
            return sounds;
        }

        @Nullable
        private ResourceLocation createSound(CompoundTag tag, String key)
        {
            String sound = tag.getString(key);
            return sound.isEmpty() ? null : new ResourceLocation(sound);
        }

        /**
         * @return The registry iid of the sound event when reloading this ammo
         */
        @Nullable
        public ResourceLocation getReload()
        {
            return this.reload;
        }
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.put("General", this.general.serializeNBT());
        tag.put("Projectile", this.projectile.serializeNBT());
        tag.put("Sounds", this.sounds.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag)
    {
        if(tag.contains("General", Tag.TAG_COMPOUND))
        {
            this.general.deserializeNBT(tag.getCompound("General"));
        }
        if(tag.contains("Projectile", Tag.TAG_COMPOUND))
        {
            this.projectile.deserializeNBT(tag.getCompound("Projectile"));
        }
        if(tag.contains("Sounds", Tag.TAG_COMPOUND))
        {
            this.sounds.deserializeNBT(tag.getCompound("Sounds"));
        }
    }

    @Override
    public JsonObject toJsonObject()
    {
        JsonObject object = new JsonObject();
        object.add("general", this.general.toJsonObject());
        object.add("projectile", this.projectile.toJsonObject());
        GunJsonUtil.addObjectIfNotEmpty(object, "sounds", this.sounds.toJsonObject());
        return object;
    }

    @Override
    public void loadConfig(JsonObject json)
    {
        if(json.has("general"))
        {
            this.general.loadConfig(json.getAsJsonObject("general"));
        }
        if(json.has("projectile"))
        {
            this.projectile.loadConfig(json.getAsJsonObject("projectile"));
        }
        if(json.has("sounds"))
        {
            this.sounds.loadConfig(json.getAsJsonObject("sounds"));
        }
    }

    public static Ammo create(CompoundTag tag)
    {
        Ammo gun = new Ammo();
        gun.deserializeNBT(tag);
        return gun;
    }

    public Ammo copy()
    {
        Ammo gun = new Ammo();
        gun.general = this.general.copy();
        gun.projectile = this.projectile.copy();
        gun.sounds = this.sounds.copy();
        return gun;
    }

    public static class Builder
    {
        private final Ammo ammo;

        private Builder()
        {
            this.ammo = new Ammo();
        }

        public static Builder create()
        {
            return new Builder();
        }

        public Ammo build()
        {
            return this.ammo.copy(); //Copy since the builder could be used again
        }

        public Builder setMaxAmmo(int maxAmmo)
        {
            this.ammo.general.maxAmmo = maxAmmo;
            return this;
        }

        public Builder setReloadAmount(int reloadAmount)
        {
            this.ammo.general.reloadAmount = reloadAmount;
            return this;
        }

        public Builder setProjectileAmount(int projectileAmount)
        {
            this.ammo.general.projectileAmount = projectileAmount;
            return this;
        }

        public Builder setAmmo(Item item)
        {
            this.ammo.projectile.item = item.getRegistryName();
            return this;
        }

        public Builder setReloadSound(SoundEvent sound)
        {
            this.ammo.sounds.reload = sound.getRegistryName();
            return this;
        }
    }
}