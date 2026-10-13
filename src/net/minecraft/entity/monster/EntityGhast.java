package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGhast extends EntityFlying implements IMob {
   private int explosionStrength = 1;
   private static final long bb = 99723844751675L;

   public EntityGhast(World var1) {
      super(var1);
      this.setSize(4.0F, 4.0F);
      this.isImmuneToFire = true;
      this.experienceValue = 5;
      this.moveHelper = new EntityGhast.GhastMoveHelper(this);
      this.tasks.addTask(5, new EntityGhast.AIRandomFly(this));
      this.tasks.addTask(7, new EntityGhast.AILookAround(this));
      this.tasks.addTask(7, new EntityGhast.AIFireballAttack(this));
      this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
   }

   public boolean isAttacking() {
      return this.dataWatcher.getWatchableObjectByte(16) != 0;
   }

   public void setAttacking(boolean var1) {
      this.dataWatcher.updateObject(16, (byte)(var1 ? 1 : 0));
   }

   public int getFireballStrength() {
      return this.explosionStrength;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.setDead();
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      } else if ("fireball".equals(var1.getDamageType()) && var1.getEntity() instanceof EntityPlayer) {
         super.attackEntityFrom(var1, 1000.0F);
         ((EntityPlayer)var1.getEntity()).triggerAchievement(AchievementList.ghast);
         return true;
      } else {
         return super.attackEntityFrom(var1, var2);
      }
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(100.0);
   }

   @Override
   public String getLivingSound() {
      return "mob.ghast.moan";
   }

   @Override
   public String getHurtSound() {
      return "mob.ghast.scream";
   }

   @Override
   public String getDeathSound() {
      return "mob.ghast.death";
   }

   @Override
   public Item getDropItem() {
      return Items.gunpowder;
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
      int var6 = this.rand.nextInt(2) + this.rand.nextInt(1 + var2);

      for (int var7 = 0; var7 < var6; var7++) {
         this.dropItem(Items.ghast_tear, 1);
      }

      var6 = this.rand.nextInt(3) + this.rand.nextInt(1 + var2);

      for (int var9 = 0; var9 < var6; var9++) {
         this.dropItem(Items.gunpowder, 1);
      }
   }

   @Override
   public float getSoundVolume() {
      return 10.0F;
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   @Override
   public int getMaxSpawnedInChunk() {
      return 1;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setInteger("ExplosionPower", this.explosionStrength);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      if (var1.hasKey("ExplosionPower", 99)) {
         this.explosionStrength = var1.getInteger("ExplosionPower");
      }
   }

   @Override
   public float getEyeHeight() {
      return 2.6F;
   }

   public static class AIFireballAttack extends EntityAIBase {
      private EntityGhast parentEntity;
      public int attackTimer;
      private static final long a = 7129824400833L;

      public AIFireballAttack(EntityGhast var1) {
         this.parentEntity = var1;
      }

      @Override
      public boolean shouldExecute() {
         return this.parentEntity.getAttackTarget() != null;
      }

      @Override
      public void startExecuting() {
         this.attackTimer = 0;
      }

      @Override
      public void resetTask() {
         this.parentEntity.setAttacking(false);
      }

      @Override
      public void updateTask() {
         EntityLivingBase var4 = this.parentEntity.getAttackTarget();
         double var5 = 64.0;
         if (var4.getDistanceSqToEntity(this.parentEntity) < var5 * var5 && this.parentEntity.C(var4)) {
            World var7 = this.parentEntity.worldObj;
            this.attackTimer++;
            if (this.attackTimer == 10) {
               var7.playAuxSFXAtEntity((EntityPlayer)null, 1007, new BlockPos(this.parentEntity), 0);
            }

            if (this.attackTimer == 20) {
               double var8 = 4.0;
               Vec3 var10 = this.parentEntity.getLook(1.0F);
               double var11 = var4.posX - (this.parentEntity.posX + var10.xCoord * var8);
               double var13 = var4.getEntityBoundingBox().minY + var4.height / 2.0F - (0.5 + this.parentEntity.posY + this.parentEntity.height / 2.0F);
               double var15 = var4.posZ - (this.parentEntity.posZ + var10.zCoord * var8);
               var7.playAuxSFXAtEntity((EntityPlayer)null, 1008, new BlockPos(this.parentEntity), 0);
               EntityLargeFireball var17 = new EntityLargeFireball(var7, this.parentEntity, var11, var13, var15);
               var17.explosionPower = this.parentEntity.getFireballStrength();
               var17.posX = this.parentEntity.posX + var10.xCoord * var8;
               var17.posY = this.parentEntity.posY + this.parentEntity.height / 2.0F + 0.5;
               var17.posZ = this.parentEntity.posZ + var10.zCoord * var8;
               var7.spawnEntityInWorld(var17);
               this.attackTimer = -40;
            }
         } else if (this.attackTimer > 0) {
            this.attackTimer--;
         }

         this.parentEntity.setAttacking(this.attackTimer > 10);
      }
   }

   public static class AILookAround extends EntityAIBase {
      private EntityGhast parentEntity;
      private static final long a = 79926368606003L;

      public AILookAround(EntityGhast var1) {
         this.parentEntity = var1;
         this.setMutexBits(2);
      }

      @Override
      public boolean shouldExecute() {
         return true;
      }

      @Override
      public void updateTask() {
         if (this.parentEntity.getAttackTarget() == null) {
            this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw = -((float)MathHelper.atan2(this.parentEntity.motionX, this.parentEntity.motionZ))
               * 180.0F
               / (float) Math.PI;
         } else {
            EntityLivingBase var4 = this.parentEntity.getAttackTarget();
            double var5 = 64.0;
            if (var4.getDistanceSqToEntity(this.parentEntity) < var5 * var5) {
               double var7 = var4.posX - this.parentEntity.posX;
               double var9 = var4.posZ - this.parentEntity.posZ;
               this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw = -((float)MathHelper.atan2(var7, var9)) * 180.0F / (float) Math.PI;
            }
         }
      }
   }

   public static class AIRandomFly extends EntityAIBase {
      private EntityGhast parentEntity;
      private static final long b = 127316666011090L;

      public AIRandomFly(EntityGhast var1) {
         this.parentEntity = var1;
         this.setMutexBits(1);
      }

      @Override
      public boolean shouldExecute() {
         EntityMoveHelper var4 = this.parentEntity.getMoveHelper();
         if (!var4.isUpdating()) {
            return true;
         }

         double var5 = var4.getX() - this.parentEntity.posX;
         double var7 = var4.getY() - this.parentEntity.posY;
         double var9 = var4.getZ() - this.parentEntity.posZ;
         double var11 = var5 * var5 + var7 * var7 + var9 * var9;
         return var11 < 1.0 || var11 > 3600.0;
      }

      @Override
      public boolean continueExecuting() {
         return false;
      }

      @Override
      public void startExecuting() {
         Random var1 = this.parentEntity.getRNG();
         double var2 = this.parentEntity.posX + (var1.nextFloat() * 2.0F - 1.0F) * 16.0F;
         double var4 = this.parentEntity.posY + (var1.nextFloat() * 2.0F - 1.0F) * 16.0F;
         double var6 = this.parentEntity.posZ + (var1.nextFloat() * 2.0F - 1.0F) * 16.0F;
         this.parentEntity.getMoveHelper().setMoveTo(var2, var4, var6, 1.0);
      }
   }

   public static class GhastMoveHelper extends EntityMoveHelper {
      private EntityGhast parentEntity;
      private int courseChangeCooldown;
      private static final long c = 32583265553584L;

      public GhastMoveHelper(EntityGhast var1) {
         super(var1);
         this.parentEntity = var1;
      }

      @Override
      public void onUpdateMoveHelper() {
         if (this.update) {
            double var4 = this.posX - this.parentEntity.posX;
            double var6 = this.posY - this.parentEntity.posY;
            double var8 = this.posZ - this.parentEntity.posZ;
            double var10 = var4 * var4 + var6 * var6 + var8 * var8;
            if (this.courseChangeCooldown-- <= 0) {
               this.courseChangeCooldown = this.courseChangeCooldown + this.parentEntity.getRNG().nextInt(5) + 2;
               var10 = MathHelper.sqrt_double(var10);
               if (this.isNotColliding(this.posX, this.posY, this.posZ, var10)) {
                  this.parentEntity.motionX += var4 / var10 * 0.1;
                  this.parentEntity.motionY += var6 / var10 * 0.1;
                  this.parentEntity.motionZ += var8 / var10 * 0.1;
               } else {
                  this.update = false;
               }
            }
         }
      }

      private boolean isNotColliding(double var1, double var3, double var5, double var7) {
         double var12 = (var1 - this.parentEntity.posX) / var7;
         double var14 = (var3 - this.parentEntity.posY) / var7;
         double var16 = (var5 - this.parentEntity.posZ) / var7;
         AxisAlignedBB var18 = this.parentEntity.getEntityBoundingBox();

         for (int var19 = 1; var19 < var7; var19++) {
            var18 = var18.offset(var12, var14, var16);
            if (!this.parentEntity.worldObj.getCollidingBoundingBoxes(this.parentEntity, var18).isEmpty()) {
               return false;
            }
         }

         return true;
      }
   }
}
