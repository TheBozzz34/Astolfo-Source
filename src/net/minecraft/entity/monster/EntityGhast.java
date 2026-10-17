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

   public EntityGhast(World worldIn) {
      super(worldIn);
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

   public void setAttacking(boolean attacking) {
      this.dataWatcher.updateObject(16, (byte)(attacking ? 1 : 0));
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
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      } else if ("fireball".equals(source.getDamageType()) && source.getEntity() instanceof EntityPlayer) {
         super.attackEntityFrom(source, 1000.0F);
         ((EntityPlayer)source.getEntity()).triggerAchievement(AchievementList.ghast);
         return true;
      } else {
         return super.attackEntityFrom(source, amount);
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
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int i = this.rand.nextInt(2) + this.rand.nextInt(1 + lootingModifier);

      for (int j = 0; j < i; j++) {
         this.dropItem(Items.ghast_tear, 1);
      }

      i = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingModifier);

      for (int k = 0; k < i; k++) {
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
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("ExplosionPower", this.explosionStrength);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("ExplosionPower", 99)) {
         this.explosionStrength = tagCompund.getInteger("ExplosionPower");
      }
   }

   @Override
   public float getEyeHeight() {
      return 2.6F;
   }

   public static class AIFireballAttack extends EntityAIBase {
      private EntityGhast parentEntity;
      public int attackTimer;

      public AIFireballAttack(EntityGhast ghast) {
         this.parentEntity = ghast;
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
         EntityLivingBase entityLivingBase = this.parentEntity.getAttackTarget();
         double d = 64.0;
         if (entityLivingBase.getDistanceSqToEntity(this.parentEntity) < d * d && this.parentEntity.canEntityBeSeen(entityLivingBase)) {
            World world = this.parentEntity.worldObj;
            this.attackTimer++;
            if (this.attackTimer == 10) {
               world.playAuxSFXAtEntity((EntityPlayer)null, 1007, new BlockPos(this.parentEntity), 0);
            }

            if (this.attackTimer == 20) {
               double e = 4.0;
               Vec3 vec3 = this.parentEntity.getLook(1.0F);
               double d2 = entityLivingBase.posX - (this.parentEntity.posX + vec3.xCoord * e);
               double d3 = entityLivingBase.getEntityBoundingBox().minY + entityLivingBase.height / 2.0F - (0.5 + this.parentEntity.posY + this.parentEntity.height / 2.0F);
               double d4 = entityLivingBase.posZ - (this.parentEntity.posZ + vec3.zCoord * e);
               world.playAuxSFXAtEntity((EntityPlayer)null, 1008, new BlockPos(this.parentEntity), 0);
               EntityLargeFireball entityLargeFireball = new EntityLargeFireball(world, this.parentEntity, d2, d3, d4);
               entityLargeFireball.explosionPower = this.parentEntity.getFireballStrength();
               entityLargeFireball.posX = this.parentEntity.posX + vec3.xCoord * e;
               entityLargeFireball.posY = this.parentEntity.posY + this.parentEntity.height / 2.0F + 0.5;
               entityLargeFireball.posZ = this.parentEntity.posZ + vec3.zCoord * e;
               world.spawnEntityInWorld(entityLargeFireball);
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

      public AILookAround(EntityGhast ghast) {
         this.parentEntity = ghast;
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
            EntityLivingBase entityLivingBase = this.parentEntity.getAttackTarget();
            double d = 64.0;
            if (entityLivingBase.getDistanceSqToEntity(this.parentEntity) < d * d) {
               double e = entityLivingBase.posX - this.parentEntity.posX;
               double d2 = entityLivingBase.posZ - this.parentEntity.posZ;
               this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw = -((float)MathHelper.atan2(e, d2)) * 180.0F / (float) Math.PI;
            }
         }
      }
   }

   public static class AIRandomFly extends EntityAIBase {
      private EntityGhast parentEntity;

      public AIRandomFly(EntityGhast ghast) {
         this.parentEntity = ghast;
         this.setMutexBits(1);
      }

      @Override
      public boolean shouldExecute() {
         EntityMoveHelper entityMoveHelper = this.parentEntity.getMoveHelper();
         if (!entityMoveHelper.isUpdating()) {
            return true;
         }

         double d = entityMoveHelper.getX() - this.parentEntity.posX;
         double e = entityMoveHelper.getY() - this.parentEntity.posY;
         double d2 = entityMoveHelper.getZ() - this.parentEntity.posZ;
         double d3 = d * d + e * e + d2 * d2;
         return d3 < 1.0 || d3 > 3600.0;
      }

      @Override
      public boolean continueExecuting() {
         return false;
      }

      @Override
      public void startExecuting() {
         Random random = this.parentEntity.getRNG();
         double d = this.parentEntity.posX + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         double e = this.parentEntity.posY + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         double d2 = this.parentEntity.posZ + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         this.parentEntity.getMoveHelper().setMoveTo(d, e, d2, 1.0);
      }
   }

   public static class GhastMoveHelper extends EntityMoveHelper {
      private EntityGhast parentEntity;
      private int courseChangeCooldown;

      public GhastMoveHelper(EntityGhast ghast) {
         super(ghast);
         this.parentEntity = ghast;
      }

      @Override
      public void onUpdateMoveHelper() {
         if (this.update) {
            double d = this.posX - this.parentEntity.posX;
            double e = this.posY - this.parentEntity.posY;
            double d2 = this.posZ - this.parentEntity.posZ;
            double d3 = d * d + e * e + d2 * d2;
            if (this.courseChangeCooldown-- <= 0) {
               this.courseChangeCooldown = this.courseChangeCooldown + this.parentEntity.getRNG().nextInt(5) + 2;
               d3 = MathHelper.sqrt_double(d3);
               if (this.isNotColliding(this.posX, this.posY, this.posZ, d3)) {
                  this.parentEntity.motionX += d / d3 * 0.1;
                  this.parentEntity.motionY += e / d3 * 0.1;
                  this.parentEntity.motionZ += d2 / d3 * 0.1;
               } else {
                  this.update = false;
               }
            }
         }
      }

      private boolean isNotColliding(double x, double y, double z, double d) {
         double e = (x - this.parentEntity.posX) / d;
         double d2 = (y - this.parentEntity.posY) / d;
         double d3 = (z - this.parentEntity.posZ) / d;
         AxisAlignedBB axisAlignedBB = this.parentEntity.getEntityBoundingBox();

         for (int i = 1; i < d; i++) {
            axisAlignedBB = axisAlignedBB.offset(e, d2, d3);
            if (!this.parentEntity.worldObj.getCollidingBoundingBoxes(this.parentEntity, axisAlignedBB).isEmpty()) {
               return false;
            }
         }

         return true;
      }
   }
}
