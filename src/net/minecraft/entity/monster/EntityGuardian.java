package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGuardian extends EntityMob {
   private float field_175482_b;
   private float field_175484_c;
   private float field_175483_bk;
   private float field_175485_bl;
   private float field_175486_bm;
   private EntityLivingBase targetedEntity;
   private int field_175479_bo;
   private boolean field_175480_bp;
   private EntityAIWander wander;
   private static final long ab = 30512125211793L;

   public EntityGuardian(World var1) {
      super(var1);
      this.experienceValue = 10;
      this.setSize(0.85F, 0.85F);
      this.tasks.addTask(4, new EntityGuardian.AIGuardianAttack(this));
      EntityAIMoveTowardsRestriction var4;
      this.tasks.addTask(5, var4 = new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(7, this.wander = new EntityAIWander(this, 1.0, 80));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityGuardian.class, 12.0F, 0.01F));
      this.tasks.addTask(9, new EntityAILookIdle(this));
      this.wander.setMutexBits(3);
      var4.setMutexBits(3);
      this.targetTasks
         .addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, new EntityGuardian.GuardianTargetSelector(this)));
      this.moveHelper = new EntityGuardian.GuardianMoveHelper(this);
      this.field_175484_c = this.field_175482_b = this.rand.nextFloat();
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setElder(var1.getBoolean("Elder"));
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setBoolean("Elder", this.isElder());
   }

   @Override
   public PathNavigate getNewNavigator(World var1) {
      return new PathNavigateSwimmer(this, var1);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, 0);
      this.dataWatcher.addObject(17, 0);
   }

   private boolean isSyncedFlagSet(int var1) {
      return (this.dataWatcher.getWatchableObjectInt(16) & var1) != 0;
   }

   private void setSyncedFlag(int var1, boolean var2) {
      int var6 = this.dataWatcher.getWatchableObjectInt(16);
      if (var2) {
         this.dataWatcher.updateObject(16, var6 | var1);
      } else {
         this.dataWatcher.updateObject(16, var6 & ~var1);
      }
   }

   public boolean func_175472_n() {
      return this.isSyncedFlagSet(2);
   }

   private void func_175476_l(boolean var1) {
      this.setSyncedFlag(2, var1);
   }

   public int func_175464_ck() {
      return this.isElder() ? 60 : 80;
   }

   public boolean isElder() {
      return this.isSyncedFlagSet(4);
   }

   public void setElder(boolean var1) {
      this.setSyncedFlag(4, var1);
      if (var1) {
         this.setSize(1.9975F, 1.9975F);
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
         this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0);
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(80.0);
         this.enablePersistence();
         this.wander.setExecutionChance(400);
      }
   }

   public void setElder() {
      this.setElder(true);
      this.field_175486_bm = this.field_175485_bl = 1.0F;
   }

   private void setTargetedEntity(int var1) {
      this.dataWatcher.updateObject(17, var1);
   }

   public boolean hasTargetedEntity() {
      return this.dataWatcher.getWatchableObjectInt(17) != 0;
   }

   public EntityLivingBase getTargetedEntity() {
      if (!this.hasTargetedEntity()) {
         return null;
      }

      if (this.worldObj.isRemote) {
         if (this.targetedEntity != null) {
            return this.targetedEntity;
         } else {
            Entity var4 = this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(17));
            if (var4 instanceof EntityLivingBase) {
               this.targetedEntity = (EntityLivingBase)var4;
               return this.targetedEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.getAttackTarget();
      }
   }

   @Override
   public void onDataWatcherUpdate(int var1) {
      super.onDataWatcherUpdate(var1);
      if (var1 == 16) {
         if (this.isElder() && this.width < 1.0F) {
            this.setSize(1.9975F, 1.9975F);
         }
      } else if (var1 == 17) {
         this.field_175479_bo = 0;
         this.targetedEntity = null;
      }
   }

   @Override
   public int getTalkInterval() {
      return 160;
   }

   @Override
   public String getLivingSound() {
      return !this.isInWater() ? "mob.guardian.land.idle" : (this.isElder() ? "mob.guardian.elder.idle" : "mob.guardian.idle");
   }

   @Override
   public String getHurtSound() {
      return !this.isInWater() ? "mob.guardian.land.hit" : (this.isElder() ? "mob.guardian.elder.hit" : "mob.guardian.hit");
   }

   @Override
   public String getDeathSound() {
      return !this.isInWater() ? "mob.guardian.land.death" : (this.isElder() ? "mob.guardian.elder.death" : "mob.guardian.death");
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public float getEyeHeight() {
      return this.height * 0.5F;
   }

   @Override
   public float getBlockPathWeight(BlockPos var1) {
      return this.worldObj.getBlockState(var1).getBlock().getMaterial() == Material.water
         ? 10.0F + this.worldObj.getLightBrightness(var1) - 0.5F
         : super.getBlockPathWeight(var1);
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         this.field_175484_c = this.field_175482_b;
         if (!this.isInWater()) {
            this.field_175483_bk = 2.0F;
            if (this.motionY > 0.0 && this.field_175480_bp && !this.isSilent()) {
               this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.guardian.flop", 1.0F, 1.0F, false);
            }

            this.field_175480_bp = this.motionY < 0.0 && this.worldObj.isBlockNormalCube(new BlockPos(this).down(), false);
         } else if (this.func_175472_n()) {
            if (this.field_175483_bk < 0.5F) {
               this.field_175483_bk = 4.0F;
            } else {
               this.field_175483_bk = this.field_175483_bk + (0.5F - this.field_175483_bk) * 0.1F;
            }
         } else {
            this.field_175483_bk = this.field_175483_bk + (0.125F - this.field_175483_bk) * 0.2F;
         }

         this.field_175482_b = this.field_175482_b + this.field_175483_bk;
         this.field_175486_bm = this.field_175485_bl;
         if (!this.isInWater()) {
            this.field_175485_bl = this.rand.nextFloat();
         } else if (this.func_175472_n()) {
            this.field_175485_bl = this.field_175485_bl + (0.0F - this.field_175485_bl) * 0.25F;
         } else {
            this.field_175485_bl = this.field_175485_bl + (1.0F - this.field_175485_bl) * 0.06F;
         }

         if (this.func_175472_n() && this.isInWater()) {
            Vec3 var4 = this.getLook(0.0F);

            for (int var5 = 0; var5 < 2; var5++) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.WATER_BUBBLE,
                     this.posX + (this.rand.nextDouble() - 0.5) * this.width - var4.xCoord * 1.5,
                     this.posY + this.rand.nextDouble() * this.height - var4.yCoord * 1.5,
                     this.posZ + (this.rand.nextDouble() - 0.5) * this.width - var4.zCoord * 1.5,
                     0.0,
                     0.0,
                     0.0
                  );
            }
         }

         if (this.hasTargetedEntity()) {
            if (this.field_175479_bo < this.func_175464_ck()) {
               this.field_175479_bo++;
            }

            EntityLivingBase var17 = this.getTargetedEntity();
            if (var17 != null) {
               this.getLookHelper().setLookPositionWithEntity(var17, 90.0F, 90.0F);
               this.getLookHelper().onUpdateLook();
               double var18 = this.func_175477_p(0.0F);
               double var7 = var17.posX - this.posX;
               double var9 = var17.posY + var17.height * 0.5F - (this.posY + this.getEyeHeight());
               double var11 = var17.posZ - this.posZ;
               double var13 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
               var7 /= var13;
               var9 /= var13;
               var11 /= var13;
               double var15 = this.rand.nextDouble();

               while (var15 < var13) {
                  var15 += 1.8 - var18 + this.rand.nextDouble() * (1.7 - var18);
                  this.worldObj
                     .spawnParticle(
                        EnumParticleTypes.WATER_BUBBLE,
                        this.posX + var7 * var15,
                        this.posY + var9 * var15 + this.getEyeHeight(),
                        this.posZ + var11 * var15,
                        0.0,
                        0.0,
                        0.0
                     );
               }
            }
         }
      }

      if (this.inWater) {
         this.setAir(300);
      } else if (this.onGround) {
         this.motionY += 0.5;
         this.motionX = this.motionX + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F;
         this.motionZ = this.motionZ + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F;
         this.rotationYaw = this.rand.nextFloat() * 360.0F;
         this.onGround = false;
         this.isAirBorne = true;
      }

      if (this.hasTargetedEntity()) {
         this.rotationYaw = this.rotationYawHead;
      }

      super.onLivingUpdate();
   }

   public float func_175471_a(float var1) {
      return this.field_175484_c + (this.field_175482_b - this.field_175484_c) * var1;
   }

   public float func_175469_o(float var1) {
      return this.field_175486_bm + (this.field_175485_bl - this.field_175486_bm) * var1;
   }

   public float func_175477_p(float var1) {
      return (this.field_175479_bo + var1) / this.func_175464_ck();
   }

   @Override
   public void updateAITasks() {
      super.updateAITasks();
      if (this.isElder()) {
         if ((this.ticksExisted + this.getEntityId()) % 1200 == 0) {
            Potion var8 = Potion.digSlowdown;

            for (EntityPlayerMP var10 : this.worldObj.getPlayers(EntityPlayerMP.class, new Predicate<EntityPlayerMP>() {
               private static final long a = 104387031494019L;

               public boolean apply(EntityPlayerMP var1) {
                  return EntityGuardian.this.getDistanceSqToEntity(var1) < 2500.0 && var1.theItemInWorldManager.survivalOrAdventure();
               }
            })) {
               if (!var10.isPotionActive(var8)
                  || var10.getActivePotionEffect(var8).getAmplifier() < 2
                  || var10.getActivePotionEffect(var8).getDuration() < 1200) {
                  var10.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(10, 0.0F));
                  var10.addPotionEffect(new PotionEffect(var8.id, 6000, 2));
               }
            }
         }

         if (!this.hasHome()) {
            this.setHomePosAndDistance(new BlockPos(this), 16);
         }
      }
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
      int var6 = this.rand.nextInt(3) + this.rand.nextInt(var2 + 1);
      if (var6 > 0) {
         this.entityDropItem(new ItemStack(Items.prismarine_shard, var6, 0), 1.0F);
      }

      if (this.rand.nextInt(3 + var2) > 1) {
         this.entityDropItem(new ItemStack(Items.fish, 1, ItemFishFood.FishType.COD.getMetadata()), 1.0F);
      } else if (this.rand.nextInt(3 + var2) > 1) {
         this.entityDropItem(new ItemStack(Items.prismarine_crystals, 1, 0), 1.0F);
      }

      if (var1 && this.isElder()) {
         this.entityDropItem(new ItemStack(Blocks.sponge, 1, 1), 1.0F);
      }
   }

   @Override
   public void addRandomDrop() {
      ItemStack var1 = WeightedRandom.getRandomItem(this.rand, EntityFishHook.func_174855_j()).getItemStack(this.rand);
      this.entityDropItem(var1, 1.0F);
   }

   @Override
   public boolean isValidLightLevel() {
      return true;
   }

   @Override
   public boolean isNotColliding() {
      return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty();
   }

   @Override
   public boolean getCanSpawnHere() {
      return (this.rand.nextInt(20) == 0 || !this.worldObj.canBlockSeeSky(new BlockPos(this))) && super.getCanSpawnHere();
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (!this.func_175472_n() && !var1.isMagicDamage() && var1.getSourceOfDamage() instanceof EntityLivingBase) {
         EntityLivingBase var6 = (EntityLivingBase)var1.getSourceOfDamage();
         if (!var1.isExplosion()) {
            var6.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0F);
            var6.playSound("damage.thorns", 0.5F, 1.0F);
         }
      }

      this.wander.makeUpdate();
      return super.attackEntityFrom(var1, var2);
   }

   @Override
   public int getVerticalFaceSpeed() {
      return 180;
   }

   @Override
   public void moveEntityWithHeading(float var1, float var2) {
      if (this.C()) {
         if (this.isInWater()) {
            this.moveFlying(var1, var2, 0.1F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9F;
            this.motionY *= 0.9F;
            this.motionZ *= 0.9F;
            if (!this.func_175472_n() && this.getAttackTarget() == null) {
               this.motionY -= 0.005;
            }
         } else {
            super.moveEntityWithHeading(var1, var2);
         }
      } else {
         super.moveEntityWithHeading(var1, var2);
      }
   }

   public static class AIGuardianAttack extends EntityAIBase {
      private EntityGuardian theEntity;
      private int tickCounter;
      private static final long a = 76258560849343L;

      public AIGuardianAttack(EntityGuardian var1) {
         this.theEntity = var1;
         this.setMutexBits(3);
      }

      @Override
      public boolean shouldExecute() {
         EntityLivingBase var4 = this.theEntity.getAttackTarget();
         return var4 != null && var4.isEntityAlive();
      }

      @Override
      public boolean continueExecuting() {
         return super.continueExecuting() && (this.theEntity.isElder() || this.theEntity.getDistanceSqToEntity(this.theEntity.getAttackTarget()) > 9.0);
      }

      @Override
      public void startExecuting() {
         this.tickCounter = -10;
         this.theEntity.getNavigator().clearPathEntity();
         this.theEntity.getLookHelper().setLookPositionWithEntity(this.theEntity.getAttackTarget(), 90.0F, 90.0F);
         this.theEntity.isAirBorne = true;
      }

      @Override
      public void resetTask() {
         this.theEntity.setTargetedEntity(0);
         this.theEntity.setAttackTarget((EntityLivingBase)null);
         this.theEntity.wander.makeUpdate();
      }

      @Override
      public void updateTask() {
         EntityLivingBase var4 = this.theEntity.getAttackTarget();
         this.theEntity.getNavigator().clearPathEntity();
         this.theEntity.getLookHelper().setLookPositionWithEntity(var4, 90.0F, 90.0F);
         if (!this.theEntity.C(var4)) {
            this.theEntity.setAttackTarget((EntityLivingBase)null);
         } else {
            this.tickCounter++;
            if (this.tickCounter == 0) {
               this.theEntity.setTargetedEntity(this.theEntity.getAttackTarget().getEntityId());
               this.theEntity.worldObj.setEntityState(this.theEntity, (byte)21);
            } else if (this.tickCounter >= this.theEntity.func_175464_ck()) {
               float var5 = 1.0F;
               if (this.theEntity.worldObj.getDifficulty() == EnumDifficulty.HARD) {
                  var5 += 2.0F;
               }

               if (this.theEntity.isElder()) {
                  var5 += 2.0F;
               }

               var4.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.theEntity, this.theEntity), var5);
               var4.attackEntityFrom(
                  DamageSource.causeMobDamage(this.theEntity),
                  (float)this.theEntity.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()
               );
               this.theEntity.setAttackTarget((EntityLivingBase)null);
            } else if (this.tickCounter >= 60 && this.tickCounter % 20 == 0) {
            }

            super.updateTask();
         }
      }
   }

   public static class GuardianMoveHelper extends EntityMoveHelper {
      private EntityGuardian entityGuardian;
      private static final long b = 42537469308209L;

      public GuardianMoveHelper(EntityGuardian var1) {
         super(var1);
         this.entityGuardian = var1;
      }

      @Override
      public void onUpdateMoveHelper() {
         if (this.update && !this.entityGuardian.getNavigator().noPath()) {
            double var4 = this.posX - this.entityGuardian.posX;
            double var6 = this.posY - this.entityGuardian.posY;
            double var8 = this.posZ - this.entityGuardian.posZ;
            double var10 = var4 * var4 + var6 * var6 + var8 * var8;
            var10 = MathHelper.sqrt_double(var10);
            var6 /= var10;
            float var12 = (float)(MathHelper.atan2(var8, var4) * 180.0 / Math.PI) - 90.0F;
            this.entityGuardian.rotationYaw = this.limitAngle(this.entityGuardian.rotationYaw, var12, 30.0F);
            this.entityGuardian.renderYawOffset = this.entityGuardian.rotationYaw;
            float var13 = (float)(this.speed * this.entityGuardian.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            this.entityGuardian.setAIMoveSpeed(this.entityGuardian.getAIMoveSpeed() + (var13 - this.entityGuardian.getAIMoveSpeed()) * 0.125F);
            double var14 = Math.sin((this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.5) * 0.05;
            double var16 = Math.cos(this.entityGuardian.rotationYaw * (float) Math.PI / 180.0F);
            double var18 = Math.sin(this.entityGuardian.rotationYaw * (float) Math.PI / 180.0F);
            this.entityGuardian.motionX += var14 * var16;
            this.entityGuardian.motionZ += var14 * var18;
            var14 = Math.sin((this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.75) * 0.05;
            this.entityGuardian.motionY += var14 * (var18 + var16) * 0.25;
            this.entityGuardian.motionY = this.entityGuardian.motionY + this.entityGuardian.getAIMoveSpeed() * var6 * 0.1;
            EntityLookHelper var20 = this.entityGuardian.getLookHelper();
            double var21 = this.entityGuardian.posX + var4 / var10 * 2.0;
            double var23 = this.entityGuardian.getEyeHeight() + this.entityGuardian.posY + var6 / var10 * 1.0;
            double var25 = this.entityGuardian.posZ + var8 / var10 * 2.0;
            double var27 = var20.s();
            double var29 = var20.t();
            double var31 = var20.i();
            if (!var20.getIsLooking()) {
               var27 = var21;
               var29 = var23;
               var31 = var25;
            }

            this.entityGuardian
               .getLookHelper()
               .setLookPosition(var27 + (var21 - var27) * 0.125, var29 + (var23 - var29) * 0.125, var31 + (var25 - var31) * 0.125, 10.0F, 40.0F);
            this.entityGuardian.func_175476_l(true);
         } else {
            this.entityGuardian.setAIMoveSpeed(0.0F);
            this.entityGuardian.func_175476_l(false);
         }
      }
   }

   public static class GuardianTargetSelector implements Predicate<EntityLivingBase> {
      private EntityGuardian parentEntity;
      private static final long a = 110705291230193L;

      public GuardianTargetSelector(EntityGuardian var1) {
         this.parentEntity = var1;
      }

      public boolean apply(EntityLivingBase var1) {
         return (var1 instanceof EntityPlayer || var1 instanceof EntitySquid) && var1.getDistanceSqToEntity(this.parentEntity) > 9.0;
      }
   }
}
