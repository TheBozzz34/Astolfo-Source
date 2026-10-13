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

   public EntityTameable(World var1) {
      super(var1);
      this.setupTamedAI();
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
      this.dataWatcher.addObject(17, "");
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      if (this.getOwnerId() == null) {
         var1.setString("OwnerUUID", "");
      } else {
         var1.setString("OwnerUUID", this.getOwnerId());
      }

      var1.setBoolean("Sitting", this.isSitting());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      String var5 = "";
      if (var1.hasKey("OwnerUUID", 8)) {
         var5 = var1.getString("OwnerUUID");
      } else {
         String var6 = var1.getString("Owner");
         var5 = PreYggdrasilConverter.getStringUUIDFromName(var6);
      }

      if (var5.length() > 0) {
         this.setOwnerId(var5);
         this.setTamed(true);
      }

      this.aiSit.setSitting(var1.getBoolean("Sitting"));
      this.setSitting(var1.getBoolean("Sitting"));
   }

   public void playTameEffect(boolean var1) {
      EnumParticleTypes var5 = EnumParticleTypes.HEART;
      if (!var1) {
         var5 = EnumParticleTypes.SMOKE_NORMAL;
      }

      for (int var6 = 0; var6 < 7; var6++) {
         double var7 = this.rand.nextGaussian() * 0.02;
         double var9 = this.rand.nextGaussian() * 0.02;
         double var11 = this.rand.nextGaussian() * 0.02;
         this.worldObj
            .spawnParticle(
               var5,
               this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
               this.posY + 0.5 + this.rand.nextFloat() * this.height,
               this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
               var7,
               var9,
               var11
            );
      }
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 7) {
         this.playTameEffect(true);
      } else if (var1 == 6) {
         this.playTameEffect(false);
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   public boolean isTamed() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
   }

   public void setTamed(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(16);
      if (var1) {
         this.dataWatcher.updateObject(16, (byte)(var5 | 4));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var5 & -5));
      }

      this.setupTamedAI();
   }

   public void setupTamedAI() {
   }

   public boolean isSitting() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setSitting(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(16);
      if (var1) {
         this.dataWatcher.updateObject(16, (byte)(var5 | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var5 & -2));
      }
   }

   @Override
   public String getOwnerId() {
      return this.dataWatcher.getWatchableObjectString(17);
   }

   public void setOwnerId(String var1) {
      this.dataWatcher.updateObject(17, var1);
   }

   public EntityLivingBase getOwner() {
      try {
         UUID var3 = UUID.fromString(this.getOwnerId());
         return var3 == null ? null : this.worldObj.getPlayerEntityByUUID(var3);
      } catch (IllegalArgumentException var4) {
         return null;
      }
   }

   public boolean V(EntityLivingBase var1) {
      return var1 == this.getOwner();
   }

   public EntityAISit getAISit() {
      return this.aiSit;
   }

   public boolean shouldAttackEntity(EntityLivingBase var1, EntityLivingBase var2) {
      return true;
   }

   @Override
   public Team getTeam() {
      if (this.isTamed()) {
         EntityLivingBase var4 = this.getOwner();
         if (var4 != null) {
            return var4.getTeam();
         }
      }

      return super.getTeam();
   }

   @Override
   public boolean isOnSameTeam(EntityLivingBase var1) {
      if (this.isTamed()) {
         EntityLivingBase var5 = this.getOwner();
         if (var1 == var5) {
            return true;
         }

         if (var5 != null) {
            return var5.isOnSameTeam(var1);
         }
      }

      return super.isOnSameTeam(var1);
   }

   @Override
   public void onDeath(DamageSource var1) {
      if (!this.worldObj.isRemote
         && this.worldObj.getGameRules().getBoolean("showDeathMessages")
         && this.hasCustomName()
         && this.getOwner() instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.getOwner()).addChatMessage(this.getCombatTracker().getDeathMessage());
      }

      super.onDeath(var1);
   }
}
