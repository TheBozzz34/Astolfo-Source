package net.minecraft.entity.passive;

import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable {
   public EntityAISit aiSit = new EntityAISit(this);
   private static final long hb = 87179505664682L;

   public EntityTameable(World worldIn) {
      super(worldIn);
      this.setupTamedAI();
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
      this.dataWatcher.addObject(17, "");
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      if (this.getOwnerId() == null) {
         tagCompound.setString("OwnerUUID", "");
      } else {
         tagCompound.setString("OwnerUUID", this.getOwnerId());
      }

      tagCompound.setBoolean("Sitting", this.isSitting());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      String string = "";
      if (tagCompund.hasKey("OwnerUUID", 8)) {
         string = tagCompund.getString("OwnerUUID");
      } else {
         String string2 = tagCompund.getString("Owner");
         string = PreYggdrasilConverter.getStringUUIDFromName(string2);
      }

      if (string.length() > 0) {
         this.setOwnerId(string);
         this.setTamed(true);
      }

      this.aiSit.setSitting(tagCompund.getBoolean("Sitting"));
      this.setSitting(tagCompund.getBoolean("Sitting"));
   }

   public void playTameEffect(boolean play) {
      EnumParticleTypes enumParticleTypes = EnumParticleTypes.HEART;
      if (!play) {
         enumParticleTypes = EnumParticleTypes.SMOKE_NORMAL;
      }

      for (int i = 0; i < 7; i++) {
         double d = this.rand.nextGaussian() * 0.02;
         double e = this.rand.nextGaussian() * 0.02;
         double d2 = this.rand.nextGaussian() * 0.02;
         this.worldObj
            .spawnParticle(
               enumParticleTypes,
               this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
               this.posY + 0.5 + this.rand.nextFloat() * this.height,
               this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
               d,
               e,
               d2
            );
      }
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 7) {
         this.playTameEffect(true);
      } else if (id == 6) {
         this.playTameEffect(false);
      } else {
         super.handleStatusUpdate(id);
      }
   }

   public boolean isTamed() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
   }

   public void setTamed(boolean tamed) {
      byte b = this.dataWatcher.getWatchableObjectByte(16);
      if (tamed) {
         this.dataWatcher.updateObject(16, (byte)(b | 4));
      } else {
         this.dataWatcher.updateObject(16, (byte)(b & -5));
      }

      this.setupTamedAI();
   }

   public void setupTamedAI() {
   }

   public boolean isSitting() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setSitting(boolean sitting) {
      byte b = this.dataWatcher.getWatchableObjectByte(16);
      if (sitting) {
         this.dataWatcher.updateObject(16, (byte)(b | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(b & -2));
      }
   }

   @Override
   public String getOwnerId() {
      return this.dataWatcher.getWatchableObjectString(17);
   }

   public void setOwnerId(String ownerUuid) {
      this.dataWatcher.updateObject(17, ownerUuid);
   }

   public EntityLivingBase getOwner() {
      try {
         UUID uuid = UUID.fromString(this.getOwnerId());
         return uuid == null ? null : this.worldObj.getPlayerEntityByUUID(uuid);
      } catch (IllegalArgumentException illegalArgumentException) {
         return null;
      }
   }

   public boolean V(EntityLivingBase entityLivingBase) {
      return entityLivingBase == this.getOwner();
   }

   public EntityAISit getAISit() {
      return this.aiSit;
   }

   public boolean shouldAttackEntity(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
      return true;
   }

   @Override
   public Team getTeam() {
      if (this.isTamed()) {
         EntityLivingBase entityLivingBase = this.getOwner();
         if (entityLivingBase != null) {
            return entityLivingBase.getTeam();
         }
      }

      return super.getTeam();
   }

   @Override
   public boolean isOnSameTeam(EntityLivingBase otherEntity) {
      if (this.isTamed()) {
         EntityLivingBase entityLivingBase = this.getOwner();
         if (otherEntity == entityLivingBase) {
            return true;
         }

         if (entityLivingBase != null) {
            return entityLivingBase.isOnSameTeam(otherEntity);
         }
      }

      return super.isOnSameTeam(otherEntity);
   }

   @Override
   public void onDeath(DamageSource cause) {
      if (!this.worldObj.isRemote
         && this.worldObj.getGameRules().getBoolean("showDeathMessages")
         && this.hasCustomName()
         && this.getOwner() instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.getOwner()).addChatMessage(this.getCombatTracker().getDeathMessage());
      }

      super.onDeath(cause);
   }
}
