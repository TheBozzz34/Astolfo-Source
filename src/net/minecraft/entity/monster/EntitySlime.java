package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class EntitySlime extends EntityLiving implements IMob {
   public float squishAmount;
   public float squishFactor;
   public float prevSquishFactor;
   private boolean wasOnGround;
   private static final long bb = 44610304793729L;

   public EntitySlime(World var1) {
      super(var1);
      this.moveHelper = new EntitySlime.SlimeMoveHelper(this);
      this.tasks.addTask(1, new EntitySlime.AISlimeFloat(this));
      this.tasks.addTask(2, new EntitySlime.AISlimeAttack(this));
      this.tasks.addTask(3, new EntitySlime.AISlimeFaceRandom(this));
      this.tasks.addTask(5, new EntitySlime.AISlimeHop(this));
      this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
      this.targetTasks.addTask(3, new EntityAIFindEntityNearest(this, EntityIronGolem.class));
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)1);
   }

   public void setSlimeSize(int var1) {
      this.dataWatcher.updateObject(16, (byte)var1);
      this.setSize(0.51000005F * var1, 0.51000005F * var1);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(var1 * var1);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F + 0.1F * var1);
      this.setHealth(this.getMaxHealth());
      this.experienceValue = var1;
   }

   public int getSlimeSize() {
      return this.dataWatcher.getWatchableObjectByte(16);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setInteger("Size", this.getSlimeSize() - 1);
      var1.setBoolean("wasOnGround", this.wasOnGround);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      int var5 = var1.getInteger("Size");
      if (var5 < 0) {
         var5 = 0;
      }

      this.setSlimeSize(var5 + 1);
      this.wasOnGround = var1.getBoolean("wasOnGround");
   }

   public EnumParticleTypes getParticleType() {
      return EnumParticleTypes.SLIME;
   }

   public String getJumpSound() {
      return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
   }

   @Override
   public void onUpdate() {
      if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL && this.getSlimeSize() > 0) {
         this.isDead = true;
      }

      this.squishFactor = this.squishFactor + (this.squishAmount - this.squishFactor) * 0.5F;
      this.prevSquishFactor = this.squishFactor;
      super.onUpdate();
      if (this.onGround && !this.wasOnGround) {
         int var4 = this.getSlimeSize();

         for (int var5 = 0; var5 < var4 * 8; var5++) {
            float var6 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float var7 = this.rand.nextFloat() * 0.5F + 0.5F;
            float var8 = MathHelper.sin(var6) * var4 * 0.5F * var7;
            float var9 = MathHelper.cos(var6) * var4 * 0.5F * var7;
            World var10 = this.worldObj;
            EnumParticleTypes var11 = this.getParticleType();
            double var12 = this.posX + var8;
            double var14 = this.posZ + var9;
            var10.spawnParticle(var11, var12, this.getEntityBoundingBox().minY, var14, 0.0, 0.0, 0.0);
         }

         if (this.makesSoundOnLand()) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         }

         this.squishAmount = -0.5F;
      } else if (!this.onGround && this.wasOnGround) {
         this.squishAmount = 1.0F;
      }

      this.wasOnGround = this.onGround;
      this.alterSquishAmount();
   }

   public void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   public int getJumpDelay() {
      return this.rand.nextInt(20) + 10;
   }

   public EntitySlime createInstance() {
      return new EntitySlime(this.worldObj);
   }

   @Override
   public void onDataWatcherUpdate(int var1) {
      if (var1 == 16) {
         int var5 = this.getSlimeSize();
         this.setSize(0.51000005F * var5, 0.51000005F * var5);
         this.rotationYaw = this.rotationYawHead;
         this.renderYawOffset = this.rotationYawHead;
         if (this.isInWater() && this.rand.nextInt(20) == 0) {
            this.resetHeight();
         }
      }

      super.onDataWatcherUpdate(var1);
   }

   @Override
   public void setDead() {
      int var4 = this.getSlimeSize();
      if (!this.worldObj.isRemote && var4 > 1 && this.getHealth() <= 0.0F) {
         int var5 = 2 + this.rand.nextInt(3);

         for (int var6 = 0; var6 < var5; var6++) {
            float var7 = (var6 % 2 - 0.5F) * var4 / 4.0F;
            float var8 = (var6 / 2 - 0.5F) * var4 / 4.0F;
            EntitySlime var9 = this.createInstance();
            if (this.hasCustomName()) {
               var9.setCustomNameTag(this.getCustomNameTag());
            }

            if (this.isNoDespawnRequired()) {
               var9.enablePersistence();
            }

            var9.setSlimeSize(var4 / 2);
            var9.setLocationAndAngles(this.posX + var7, this.posY + 0.5, this.posZ + var8, this.rand.nextFloat() * 360.0F, 0.0F);
            this.worldObj.spawnEntityInWorld(var9);
         }
      }

      super.setDead();
   }

   @Override
   public void applyEntityCollision(Entity var1) {
      super.applyEntityCollision(var1);
      if (var1 instanceof EntityIronGolem && this.canDamagePlayer()) {
         this.func_175451_e((EntityLivingBase)var1);
      }
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer var1) {
      if (this.canDamagePlayer()) {
         this.func_175451_e(var1);
      }
   }

   public void func_175451_e(EntityLivingBase var1) {
      int var5 = this.getSlimeSize();
      if (this.C(var1)
         && this.getDistanceSqToEntity(var1) < 0.6 * var5 * 0.6 * var5
         && var1.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackStrength())) {
         this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         this.applyEnchantments(this, var1);
      }
   }

   @Override
   public float getEyeHeight() {
      return 0.625F * this.height;
   }

   public boolean canDamagePlayer() {
      return this.getSlimeSize() > 1;
   }

   public int getAttackStrength() {
      return this.getSlimeSize();
   }

   @Override
   public String getHurtSound() {
      return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
   }

   @Override
   public String getDeathSound() {
      return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
   }

   @Override
   public Item getDropItem() {
      return this.getSlimeSize() == 1 ? Items.slime_ball : null;
   }

   @Override
   public boolean getCanSpawnHere() {
      BlockPos var4 = new BlockPos(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ));
      Chunk var5 = this.worldObj.getChunkFromBlockCoords(var4);
      if (this.worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT && this.rand.nextInt(4) != 1) {
         return false;
      }

      if (this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL) {
         BiomeGenBase var6 = this.worldObj.getBiomeGenForCoords(var4);
         if (var6 == BiomeGenBase.swampland
            && this.posY > 50.0
            && this.posY < 70.0
            && this.rand.nextFloat() < 0.5F
            && this.rand.nextFloat() < this.worldObj.getCurrentMoonPhaseFactor()
            && this.worldObj.getLightFromNeighbors(new BlockPos(this)) <= this.rand.nextInt(8)) {
            return super.getCanSpawnHere();
         }

         if (this.rand.nextInt(10) == 0 && var5.getRandomWithSeed(987234911L).nextInt(10) == 0 && this.posY < 40.0) {
            return super.getCanSpawnHere();
         }
      }

      return false;
   }

   @Override
   public float getSoundVolume() {
      return 0.4F * this.getSlimeSize();
   }

   @Override
   public int getVerticalFaceSpeed() {
      return 0;
   }

   public boolean makesSoundOnJump() {
      return this.getSlimeSize() > 0;
   }

   public boolean makesSoundOnLand() {
      return this.getSlimeSize() > 2;
   }

   @Override
   public void jump() {
      this.motionY = 0.42F;
      this.isAirBorne = true;
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      int var6 = this.rand.nextInt(3);
      if (var6 < 2 && this.rand.nextFloat() < 0.5F * var1.getClampedAdditionalDifficulty()) {
         var6++;
      }

      int var7 = 1 << var6;
      this.setSlimeSize(var7);
      return super.onInitialSpawn(var1, var2);
   }

   public static class AISlimeAttack extends EntityAIBase {
      private EntitySlime slime;
      private int field_179465_b;
      private static final long a = 31859129501063L;
      private static final long b = 5767236330782196012L;

      public AISlimeAttack(EntitySlime var1) {
         this.slime = var1;
         this.setMutexBits(2);
      }

      @Override
      public boolean shouldExecute() {
         EntityLivingBase var4 = this.slime.getAttackTarget();
         return var4 == null ? false : (!var4.isEntityAlive() ? false : !(var4 instanceof EntityPlayer) || !((EntityPlayer)var4).capabilities.disableDamage);
      }

      @Override
      public void startExecuting() {
         this.field_179465_b = (int)b;
         super.startExecuting();
      }

      @Override
      public boolean continueExecuting() {
         EntityLivingBase var4 = this.slime.getAttackTarget();
         return var4 == null
            ? false
            : (
               !var4.isEntityAlive()
                  ? false
                  : (var4 instanceof EntityPlayer && ((EntityPlayer)var4).capabilities.disableDamage ? false : --this.field_179465_b > 0)
            );
      }

      @Override
      public void updateTask() {
         this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).func_179920_a(this.slime.rotationYaw, this.slime.canDamagePlayer());
      }
   }

   public static class AISlimeFaceRandom extends EntityAIBase {
      private EntitySlime slime;
      private float field_179459_b;
      private int field_179460_c;
      private static final long a = 26953013393056L;

      public AISlimeFaceRandom(EntitySlime var1) {
         this.slime = var1;
         this.setMutexBits(2);
      }

      @Override
      public boolean shouldExecute() {
         return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava());
      }

      @Override
      public void updateTask() {
         if (--this.field_179460_c <= 0) {
            this.field_179460_c = 40 + this.slime.getRNG().nextInt(60);
            this.field_179459_b = this.slime.getRNG().nextInt(360);
         }

         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).func_179920_a(this.field_179459_b, false);
      }
   }

   public static class AISlimeFloat extends EntityAIBase {
      private EntitySlime slime;
      private static final long a = 115896661382096L;

      public AISlimeFloat(EntitySlime var1) {
         this.slime = var1;
         this.setMutexBits(5);
         ((PathNavigateGround)var1.getNavigator()).setCanSwim(true);
      }

      @Override
      public boolean shouldExecute() {
         return this.slime.isInWater() || this.slime.isInLava();
      }

      @Override
      public void updateTask() {
         if (this.slime.getRNG().nextFloat() < 0.8F) {
            this.slime.getJumpHelper().setJumping();
         }

         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.2);
      }
   }

   public static class AISlimeHop extends EntityAIBase {
      private EntitySlime slime;

      public AISlimeHop(EntitySlime var1) {
         this.slime = var1;
         this.setMutexBits(5);
      }

      @Override
      public boolean shouldExecute() {
         return true;
      }

      @Override
      public void updateTask() {
         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.0);
      }
   }

   public static class SlimeMoveHelper extends EntityMoveHelper {
      private float field_179922_g;
      private int field_179924_h;
      private EntitySlime slime;
      private boolean field_179923_j;
      private static final long b = 12949991853926L;

      public SlimeMoveHelper(EntitySlime var1) {
         super(var1);
         this.slime = var1;
      }

      public void func_179920_a(float var1, boolean var2) {
         this.field_179922_g = var1;
         this.field_179923_j = var2;
      }

      public void setSpeed(double var1) {
         this.speed = var1;
         this.update = true;
      }

      @Override
      public void onUpdateMoveHelper() {
         this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, this.field_179922_g, 30.0F);
         this.entity.rotationYawHead = this.entity.rotationYaw;
         this.entity.renderYawOffset = this.entity.rotationYaw;
         if (!this.update) {
            this.entity.setMoveForward(0.0F);
         } else {
            this.update = false;
            if (this.entity.onGround) {
               this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
               if (this.field_179924_h-- <= 0) {
                  this.field_179924_h = this.slime.getJumpDelay();
                  if (this.field_179923_j) {
                     this.field_179924_h /= 3;
                  }

                  this.slime.getJumpHelper().setJumping();
                  if (this.slime.makesSoundOnJump()) {
                     this.slime
                        .playSound(
                           this.slime.getJumpSound(),
                           this.slime.getSoundVolume(),
                           ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F
                        );
                  }
               } else {
                  this.slime.moveStrafing = this.slime.moveForward = 0.0F;
                  this.entity.setAIMoveSpeed(0.0F);
               }
            } else {
               this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
            }
         }
      }
   }
}
