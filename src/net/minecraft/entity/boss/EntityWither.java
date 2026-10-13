package net.minecraft.entity.boss;

import bypass.Astolfo;
import bypass.module.combat.Hitboxes;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityWither extends EntityMob implements IBossDisplayData, IRangedAttackMob {
   private float[] field_82220_d = new float[2];
   private float[] field_82221_e = new float[2];
   private float[] field_82217_f = new float[2];
   private float[] field_82218_g = new float[2];
   private int[] field_82223_h = new int[2];
   private int[] field_82224_i = new int[2];
   private int blockBreakCounter;
   private static final Predicate<Entity> attackEntitySelector = new Predicate<Entity>() {
      private static final long a = 8453972094853L;

      public boolean apply(Entity var1) {
         return var1 instanceof EntityLivingBase && ((EntityLivingBase)var1).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
      }
   };
   private static final long ab = 105208707728065L;

   public EntityWither(World var1) {
      super(var1);
      this.setHealth(this.getMaxHealth());
      this.setSize(0.9F, 3.5F);
      this.isImmuneToFire = true;
      ((PathNavigateGround)this.getNavigator()).setCanSwim(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0, 40, 20.0F));
      this.tasks.addTask(5, new EntityAIWander(this, 1.0));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 0, false, false, attackEntitySelector));
      this.experienceValue = 50;
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(0));
      this.dataWatcher.addObject(19, new Integer(0));
      this.dataWatcher.addObject(20, new Integer(0));
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setInteger("Invul", this.getInvulTime());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setInvulTime(var1.getInteger("Invul"));
   }

   @Override
   public String getLivingSound() {
      return "mob.wither.idle";
   }

   @Override
   public String getHurtSound() {
      return "mob.wither.hurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.wither.death";
   }

   @Override
   public void onLivingUpdate() {
      this.motionY *= 0.6F;
      if (!this.worldObj.isRemote && this.getWatchedTargetId(0) > 0) {
         Entity var4 = this.worldObj.getEntityByID(this.getWatchedTargetId(0));
         if (var4 != null) {
            if (this.posY < var4.posY || !this.isArmored() && this.posY < var4.posY + 5.0) {
               if (this.motionY < 0.0) {
                  this.motionY = 0.0;
               }

               this.motionY = this.motionY + (0.5 - this.motionY) * 0.6F;
            }

            double var5 = var4.posX - this.posX;
            double var7 = var4.posZ - this.posZ;
            double var9 = var5 * var5 + var7 * var7;
            if (var9 > 9.0) {
               double var11 = MathHelper.sqrt_double(var9);
               this.motionX = this.motionX + (var5 / var11 * 0.5 - this.motionX) * 0.6F;
               this.motionZ = this.motionZ + (var7 / var11 * 0.5 - this.motionZ) * 0.6F;
            }
         }
      }

      if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05F) {
         this.rotationYaw = (float)MathHelper.atan2(this.motionZ, this.motionX) * (180.0F / (float)Math.PI) - 90.0F;
      }

      super.onLivingUpdate();

      for (int var23 = 0; var23 < 2; var23++) {
         this.field_82218_g[var23] = this.field_82221_e[var23];
         this.field_82217_f[var23] = this.field_82220_d[var23];
      }

      for (int var24 = 0; var24 < 2; var24++) {
         int var26 = this.getWatchedTargetId(var24 + 1);
         Entity var6 = null;
         if (var26 > 0) {
            var6 = this.worldObj.getEntityByID(var26);
         }

         if (var6 != null) {
            double var30 = this.func_82214_u(var24 + 1);
            double var31 = this.func_82208_v(var24 + 1);
            double var32 = this.func_82213_w(var24 + 1);
            double var13 = var6.posX - var30;
            double var15 = var6.posY + var6.getEyeHeight() - var31;
            double var17 = var6.posZ - var32;
            double var19 = MathHelper.sqrt_double(var13 * var13 + var17 * var17);
            float var21 = (float)(MathHelper.atan2(var17, var13) * 180.0 / Math.PI) - 90.0F;
            float var22 = (float)(-(MathHelper.atan2(var15, var19) * 180.0 / Math.PI));
            this.field_82220_d[var24] = this.func_82204_b(this.field_82220_d[var24], var22, 40.0F);
            this.field_82221_e[var24] = this.func_82204_b(this.field_82221_e[var24], var21, 10.0F);
         } else {
            this.field_82221_e[var24] = this.func_82204_b(this.field_82221_e[var24], this.renderYawOffset, 10.0F);
         }
      }

      boolean var25 = this.isArmored();

      for (int var27 = 0; var27 < 3; var27++) {
         double var29 = this.func_82214_u(var27);
         double var8 = this.func_82208_v(var27);
         double var10 = this.func_82213_w(var27);
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.SMOKE_NORMAL,
               var29 + this.rand.nextGaussian() * 0.3F,
               var8 + this.rand.nextGaussian() * 0.3F,
               var10 + this.rand.nextGaussian() * 0.3F,
               0.0,
               0.0,
               0.0
            );
         if (var25 && this.worldObj.rand.nextInt(4) == 0) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.SPELL_MOB,
                  var29 + this.rand.nextGaussian() * 0.3F,
                  var8 + this.rand.nextGaussian() * 0.3F,
                  var10 + this.rand.nextGaussian() * 0.3F,
                  0.7F,
                  0.7F,
                  0.5
               );
         }
      }

      if (this.getInvulTime() > 0) {
         for (int var28 = 0; var28 < 3; var28++) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.SPELL_MOB,
                  this.posX + this.rand.nextGaussian() * 1.0,
                  this.posY + this.rand.nextFloat() * 3.3F,
                  this.posZ + this.rand.nextGaussian() * 1.0,
                  0.7F,
                  0.7F,
                  0.9F
               );
         }
      }
   }

   @Override
   public void updateAITasks() {
      if (this.getInvulTime() > 0) {
         int var4 = this.getInvulTime() - 1;
         if (var4 <= 0) {
            this.worldObj
               .newExplosion(this, this.posX, this.posY + this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getBoolean("mobGriefing"));
            this.worldObj.playBroadcastSound(1013, new BlockPos(this), 0);
         }

         this.setInvulTime(var4);
         if (this.ticksExisted % 10 == 0) {
            this.heal(10.0F);
         }
      } else {
         super.updateAITasks();

         for (int var16 = 1; var16 < 3; var16++) {
            if (this.ticksExisted >= this.field_82223_h[var16 - 1]) {
               this.field_82223_h[var16 - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);
               if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL || this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
                  int var5 = var16 - 1;
                  int var6 = this.field_82224_i[var16 - 1];
                  this.field_82224_i[var5] = this.field_82224_i[var16 - 1] + 1;
                  if (var6 > 15) {
                     float var7 = 10.0F;
                     float var8 = 5.0F;
                     double var9 = MathHelper.getRandomDoubleInRange(this.rand, this.posX - var7, this.posX + var7);
                     double var11 = MathHelper.getRandomDoubleInRange(this.rand, this.posY - var8, this.posY + var8);
                     double var13 = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - var7, this.posZ + var7);
                     this.launchWitherSkullToCoords(var16 + 1, var9, var11, var13, true);
                     this.field_82224_i[var16 - 1] = 0;
                  }
               }

               int var18 = this.getWatchedTargetId(var16);
               if (var18 > 0) {
                  Entity var21 = this.worldObj.getEntityByID(var18);
                  if (var21 == null || !var21.isEntityAlive() || !(this.getDistanceSqToEntity(var21) <= 900.0) || !this.C(var21)) {
                     this.updateWatchedTargetId(var16, 0);
                  } else if (var21 instanceof EntityPlayer && ((EntityPlayer)var21).capabilities.disableDamage) {
                     this.updateWatchedTargetId(var16, 0);
                  } else {
                     this.launchWitherSkullToEntity(var16 + 1, (EntityLivingBase)var21);
                     this.field_82223_h[var16 - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                     this.field_82224_i[var16 - 1] = 0;
                  }
               } else {
                  List var20 = this.worldObj
                     .getEntitiesWithinAABB(
                        EntityLivingBase.class,
                        this.getEntityBoundingBox().expand(20.0, 8.0, 20.0),
                        Predicates.and(attackEntitySelector, EntitySelectors.NOT_SPECTATING)
                     );

                  for (int var23 = 0; var23 < 10 && !var20.isEmpty(); var23++) {
                     EntityLivingBase var25 = (EntityLivingBase)var20.get(this.rand.nextInt(var20.size()));
                     if (var25 != this && var25.isEntityAlive() && this.C(var25)) {
                        if (var25 instanceof EntityPlayer) {
                           if (!((EntityPlayer)var25).capabilities.disableDamage) {
                              this.updateWatchedTargetId(var16, var25.getEntityId());
                           }
                        } else {
                           this.updateWatchedTargetId(var16, var25.getEntityId());
                        }
                        break;
                     }

                     var20.remove(var25);
                  }
               }
            }
         }

         if (this.getAttackTarget() != null) {
            this.updateWatchedTargetId(0, this.getAttackTarget().getEntityId());
         } else {
            this.updateWatchedTargetId(0, 0);
         }

         if (this.blockBreakCounter > 0) {
            this.blockBreakCounter--;
            if (this.blockBreakCounter == 0 && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
               int var17 = MathHelper.floor_double(this.posY);
               int var19 = MathHelper.floor_double(this.posX);
               int var22 = MathHelper.floor_double(this.posZ);
               boolean var24 = false;

               for (int var26 = -1; var26 <= 1; var26++) {
                  for (int var27 = -1; var27 <= 1; var27++) {
                     for (int var10 = 0; var10 <= 3; var10++) {
                        int var28 = var19 + var26;
                        int var12 = var17 + var10;
                        int var29 = var22 + var27;
                        BlockPos var14 = new BlockPos(var28, var12, var29);
                        Block var15 = this.worldObj.getBlockState(var14).getBlock();
                        if (var15.getMaterial() != Material.air && canDestroyBlock(var15)) {
                           var24 = this.worldObj.destroyBlock(var14, true) || var24;
                        }
                     }
                  }
               }

               if (var24) {
                  this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1012, new BlockPos(this), 0);
               }
            }
         }

         if (this.ticksExisted % 20 == 0) {
            this.heal(1.0F);
         }
      }
   }

   public static boolean canDestroyBlock(Block var0) {
      return var0 != Blocks.bedrock && var0 != Blocks.end_portal && var0 != Blocks.end_portal_frame && var0 != Blocks.command_block && var0 != Blocks.barrier;
   }

   public void func_82206_m() {
      this.setInvulTime(220);
      this.setHealth(this.getMaxHealth() / 3.0F);
   }

   @Override
   public void setInWeb() {
   }

   @Override
   public int getTotalArmorValue() {
      return 4;
   }

   private double func_82214_u(int var1) {
      if (var1 <= 0) {
         return this.posX;
      }

      float var4 = (this.renderYawOffset + 180 * (var1 - 1)) / 180.0F * (float) Math.PI;
      float var5 = MathHelper.cos(var4);
      return this.posX + var5 * 1.3;
   }

   private double func_82208_v(int var1) {
      return var1 <= 0 ? this.posY + 3.0 : this.posY + 2.2;
   }

   private double func_82213_w(int var1) {
      if (var1 <= 0) {
         return this.posZ;
      }

      float var4 = (this.renderYawOffset + 180 * (var1 - 1)) / 180.0F * (float) Math.PI;
      float var5 = MathHelper.sin(var4);
      return this.posZ + var5 * 1.3;
   }

   private float func_82204_b(float var1, float var2, float var3) {
      float var7 = MathHelper.wrapAngleTo180_float(var2 - var1);
      if (var7 > var3) {
         var7 = var3;
      }

      if (var7 < -var3) {
         var7 = -var3;
      }

      return var1 + var7;
   }

   private void launchWitherSkullToEntity(int var1, EntityLivingBase var2) {
      this.launchWitherSkullToCoords(var1, var2.posX, var2.posY + var2.getEyeHeight() * 0.5, var2.posZ, var1 == 0 && this.rand.nextFloat() < 0.001F);
   }

   private void launchWitherSkullToCoords(int var1, double var2, double var4, double var6, boolean var8) {
      this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1014, new BlockPos(this), 0);
      double var12 = this.func_82214_u(var1);
      double var14 = this.func_82208_v(var1);
      double var16 = this.func_82213_w(var1);
      double var18 = var2 - var12;
      double var20 = var4 - var14;
      double var22 = var6 - var16;
      EntityWitherSkull var24 = new EntityWitherSkull(this.worldObj, this, var18, var20, var22);
      if (var8) {
         var24.setInvulnerable(true);
      }

      var24.posY = var14;
      var24.posX = var12;
      var24.posZ = var16;
      this.worldObj.spawnEntityInWorld(var24);
   }

   @Override
   public void attackEntityWithRangedAttack(EntityLivingBase var1, float var2) {
      this.launchWitherSkullToEntity(0, var1);
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      if (var1 == DamageSource.drown || var1.getEntity() instanceof EntityWither) {
         return false;
      }

      if (this.getInvulTime() > 0 && var1 != DamageSource.outOfWorld) {
         return false;
      }

      if (this.isArmored()) {
         Entity var6 = var1.getSourceOfDamage();
         if (var6 instanceof EntityArrow) {
            return false;
         }
      }

      Entity var8 = var1.getEntity();
      if (var8 != null
         && !(var8 instanceof EntityPlayer)
         && var8 instanceof EntityLivingBase
         && ((EntityLivingBase)var8).getCreatureAttribute() == this.getCreatureAttribute()) {
         return false;
      }

      if (this.blockBreakCounter <= 0) {
         this.blockBreakCounter = 20;
      }

      for (int var7 = 0; var7 < this.field_82224_i.length; var7++) {
         this.field_82224_i[var7] = this.field_82224_i[var7] + 3;
      }

      return super.attackEntityFrom(var1, var2);
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
      EntityItem var6 = this.dropItem(Items.nether_star, 1);
      if (var6 != null) {
         var6.setNoDespawn();
      }

      if (!this.worldObj.isRemote) {
         for (EntityPlayer var8 : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().expand(50.0, 100.0, 50.0))) {
            var8.triggerAchievement(AchievementList.killWither);
         }
      }
   }

   @Override
   public void despawnEntity() {
      this.entityAge = 0;
   }

   @Override
   public int getBrightnessForRender(float var1) {
      return 15728880;
   }

   @Override
   public void fall(float var1, float var2) {
   }

   @Override
   public void addPotionEffect(PotionEffect var1) {
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(300.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6F);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0);
   }

   public float func_82207_a(int var1) {
      return this.field_82221_e[var1];
   }

   public float func_82210_r(int var1) {
      return this.field_82220_d[var1];
   }

   public int getInvulTime() {
      return this.dataWatcher.getWatchableObjectInt(20);
   }

   public void setInvulTime(int var1) {
      this.dataWatcher.updateObject(20, var1);
   }

   public int getWatchedTargetId(int var1) {
      return this.dataWatcher.getWatchableObjectInt(17 + var1);
   }

   @Override
   public float getCollisionBorderSize() {
      long var1 = ab ^ 92179472927554L;
      long var3 = var1 ^ 64932267881520L;
      return Astolfo.INSTANCE.getModuleManager().get(Hitboxes.class).isEnabled() && Hitboxes.wither.getValue() && Hitboxes.G
         ? super.getCollisionBorderSize() + Hitboxes.size.getValue()
         : super.getCollisionBorderSize();
   }

   public void updateWatchedTargetId(int var1, int var2) {
      this.dataWatcher.updateObject(17 + var1, var2);
   }

   public boolean isArmored() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   @Override
   public void mountEntity(Entity var1) {
      this.ridingEntity = null;
   }
}
