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

      public boolean apply(Entity entity) {
         return entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
      }
   };
   private static final long ab = 105208707728065L;

   public EntityWither(World worldIn) {
      super(worldIn);
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
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("Invul", this.getInvulTime());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setInvulTime(tagCompund.getInteger("Invul"));
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
         Entity entity = this.worldObj.getEntityByID(this.getWatchedTargetId(0));
         if (entity != null) {
            if (this.posY < entity.posY || !this.isArmored() && this.posY < entity.posY + 5.0) {
               if (this.motionY < 0.0) {
                  this.motionY = 0.0;
               }

               this.motionY = this.motionY + (0.5 - this.motionY) * 0.6F;
            }

            double d = entity.posX - this.posX;
            double e = entity.posZ - this.posZ;
            double d2 = d * d + e * e;
            if (d2 > 9.0) {
               double d3 = MathHelper.sqrt_double(d2);
               this.motionX = this.motionX + (d / d3 * 0.5 - this.motionX) * 0.6F;
               this.motionZ = this.motionZ + (e / d3 * 0.5 - this.motionZ) * 0.6F;
            }
         }
      }

      if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05F) {
         this.rotationYaw = (float)MathHelper.atan2(this.motionZ, this.motionX) * (180.0F / (float)Math.PI) - 90.0F;
      }

      super.onLivingUpdate();

      for (int i = 0; i < 2; i++) {
         this.field_82218_g[i] = this.field_82221_e[i];
         this.field_82217_f[i] = this.field_82220_d[i];
      }

      for (int j = 0; j < 2; j++) {
         int k = this.getWatchedTargetId(j + 1);
         Entity entity2 = null;
         if (k > 0) {
            entity2 = this.worldObj.getEntityByID(k);
         }

         if (entity2 != null) {
            double d4 = this.func_82214_u(j + 1);
            double d5 = this.func_82208_v(j + 1);
            double d6 = this.func_82213_w(j + 1);
            double d7 = entity2.posX - d4;
            double d8 = entity2.posY + entity2.getEyeHeight() - d5;
            double d9 = entity2.posZ - d6;
            double d10 = MathHelper.sqrt_double(d7 * d7 + d9 * d9);
            float f = (float)(MathHelper.atan2(d9, d7) * 180.0 / Math.PI) - 90.0F;
            float g = (float)(-(MathHelper.atan2(d8, d10) * 180.0 / Math.PI));
            this.field_82220_d[j] = this.func_82204_b(this.field_82220_d[j], g, 40.0F);
            this.field_82221_e[j] = this.func_82204_b(this.field_82221_e[j], f, 10.0F);
         } else {
            this.field_82221_e[j] = this.func_82204_b(this.field_82221_e[j], this.renderYawOffset, 10.0F);
         }
      }

      boolean bl = this.isArmored();

      for (int l = 0; l < 3; l++) {
         double d11 = this.func_82214_u(l);
         double d12 = this.func_82208_v(l);
         double d13 = this.func_82213_w(l);
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.SMOKE_NORMAL,
               d11 + this.rand.nextGaussian() * 0.3F,
               d12 + this.rand.nextGaussian() * 0.3F,
               d13 + this.rand.nextGaussian() * 0.3F,
               0.0,
               0.0,
               0.0
            );
         if (bl && this.worldObj.rand.nextInt(4) == 0) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.SPELL_MOB,
                  d11 + this.rand.nextGaussian() * 0.3F,
                  d12 + this.rand.nextGaussian() * 0.3F,
                  d13 + this.rand.nextGaussian() * 0.3F,
                  0.7F,
                  0.7F,
                  0.5
               );
         }
      }

      if (this.getInvulTime() > 0) {
         for (int m = 0; m < 3; m++) {
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
         int i = this.getInvulTime() - 1;
         if (i <= 0) {
            this.worldObj
               .newExplosion(this, this.posX, this.posY + this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getBoolean("mobGriefing"));
            this.worldObj.playBroadcastSound(1013, new BlockPos(this), 0);
         }

         this.setInvulTime(i);
         if (this.ticksExisted % 10 == 0) {
            this.heal(10.0F);
         }
      } else {
         super.updateAITasks();

         for (int j = 1; j < 3; j++) {
            if (this.ticksExisted >= this.field_82223_h[j - 1]) {
               this.field_82223_h[j - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);
               if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL || this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
                  int k = j - 1;
                  int l = this.field_82224_i[j - 1];
                  this.field_82224_i[k] = this.field_82224_i[j - 1] + 1;
                  if (l > 15) {
                     float f = 10.0F;
                     float g = 5.0F;
                     double d = MathHelper.getRandomDoubleInRange(this.rand, this.posX - f, this.posX + f);
                     double e = MathHelper.getRandomDoubleInRange(this.rand, this.posY - g, this.posY + g);
                     double d2 = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - f, this.posZ + f);
                     this.launchWitherSkullToCoords(j + 1, d, e, d2, true);
                     this.field_82224_i[j - 1] = 0;
                  }
               }

               int m = this.getWatchedTargetId(j);
               if (m > 0) {
                  Entity entity = this.worldObj.getEntityByID(m);
                  if (entity == null || !entity.isEntityAlive() || !(this.getDistanceSqToEntity(entity) <= 900.0) || !this.C(entity)) {
                     this.updateWatchedTargetId(j, 0);
                  } else if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.disableDamage) {
                     this.updateWatchedTargetId(j, 0);
                  } else {
                     this.launchWitherSkullToEntity(j + 1, (EntityLivingBase)entity);
                     this.field_82223_h[j - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                     this.field_82224_i[j - 1] = 0;
                  }
               } else {
                  List list = this.worldObj
                     .getEntitiesWithinAABB(
                        EntityLivingBase.class,
                        this.getEntityBoundingBox().expand(20.0, 8.0, 20.0),
                        Predicates.and(attackEntitySelector, EntitySelectors.NOT_SPECTATING)
                     );

                  for (int n = 0; n < 10 && !list.isEmpty(); n++) {
                     EntityLivingBase entityLivingBase = (EntityLivingBase)list.get(this.rand.nextInt(list.size()));
                     if (entityLivingBase != this && entityLivingBase.isEntityAlive() && this.C(entityLivingBase)) {
                        if (entityLivingBase instanceof EntityPlayer) {
                           if (!((EntityPlayer)entityLivingBase).capabilities.disableDamage) {
                              this.updateWatchedTargetId(j, entityLivingBase.getEntityId());
                           }
                        } else {
                           this.updateWatchedTargetId(j, entityLivingBase.getEntityId());
                        }
                        break;
                     }

                     list.remove(entityLivingBase);
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
               int i2 = MathHelper.floor_double(this.posY);
               int i3 = MathHelper.floor_double(this.posX);
               int i4 = MathHelper.floor_double(this.posZ);
               boolean bl = false;

               for (int i5 = -1; i5 <= 1; i5++) {
                  for (int i6 = -1; i6 <= 1; i6++) {
                     for (int i7 = 0; i7 <= 3; i7++) {
                        int i8 = i3 + i5;
                        int i9 = i2 + i7;
                        int i10 = i4 + i6;
                        BlockPos blockPos = new BlockPos(i8, i9, i10);
                        Block block = this.worldObj.getBlockState(blockPos).getBlock();
                        if (block.getMaterial() != Material.air && canDestroyBlock(block)) {
                           bl = this.worldObj.destroyBlock(blockPos, true) || bl;
                        }
                     }
                  }
               }

               if (bl) {
                  this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1012, new BlockPos(this), 0);
               }
            }
         }

         if (this.ticksExisted % 20 == 0) {
            this.heal(1.0F);
         }
      }
   }

   public static boolean canDestroyBlock(Block block) {
      return block != Blocks.bedrock
         && block != Blocks.end_portal
         && block != Blocks.end_portal_frame
         && block != Blocks.command_block
         && block != Blocks.barrier;
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

   private double func_82214_u(int i) {
      if (i <= 0) {
         return this.posX;
      }

      float f = (this.renderYawOffset + 180 * (i - 1)) / 180.0F * (float) Math.PI;
      float g = MathHelper.cos(f);
      return this.posX + g * 1.3;
   }

   private double func_82208_v(int i) {
      return i <= 0 ? this.posY + 3.0 : this.posY + 2.2;
   }

   private double func_82213_w(int i) {
      if (i <= 0) {
         return this.posZ;
      }

      float f = (this.renderYawOffset + 180 * (i - 1)) / 180.0F * (float) Math.PI;
      float g = MathHelper.sin(f);
      return this.posZ + g * 1.3;
   }

   private float func_82204_b(float f, float g, float h) {
      float f2 = MathHelper.wrapAngleTo180_float(g - f);
      if (f2 > h) {
         f2 = h;
      }

      if (f2 < -h) {
         f2 = -h;
      }

      return f + f2;
   }

   private void launchWitherSkullToEntity(int i, EntityLivingBase entityLivingBase) {
      this.launchWitherSkullToCoords(
         i,
         entityLivingBase.posX,
         entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.5,
         entityLivingBase.posZ,
         i == 0 && this.rand.nextFloat() < 0.001F
      );
   }

   private void launchWitherSkullToCoords(int i, double x, double y, double z, boolean invulnerable) {
      this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1014, new BlockPos(this), 0);
      double d = this.func_82214_u(i);
      double e = this.func_82208_v(i);
      double d2 = this.func_82213_w(i);
      double d3 = x - d;
      double d4 = y - e;
      double d5 = z - d2;
      EntityWitherSkull entityWitherSkull = new EntityWitherSkull(this.worldObj, this, d3, d4, d5);
      if (invulnerable) {
         entityWitherSkull.setInvulnerable(true);
      }

      entityWitherSkull.posY = e;
      entityWitherSkull.posX = d;
      entityWitherSkull.posZ = d2;
      this.worldObj.spawnEntityInWorld(entityWitherSkull);
   }

   @Override
   public void attackEntityWithRangedAttack(EntityLivingBase target, float f) {
      this.launchWitherSkullToEntity(0, target);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (source == DamageSource.drown || source.getEntity() instanceof EntityWither) {
         return false;
      }

      if (this.getInvulTime() > 0 && source != DamageSource.outOfWorld) {
         return false;
      }

      if (this.isArmored()) {
         Entity entity = source.getSourceOfDamage();
         if (entity instanceof EntityArrow) {
            return false;
         }
      }

      Entity entity2 = source.getEntity();
      if (entity2 != null
         && !(entity2 instanceof EntityPlayer)
         && entity2 instanceof EntityLivingBase
         && ((EntityLivingBase)entity2).getCreatureAttribute() == this.getCreatureAttribute()) {
         return false;
      }

      if (this.blockBreakCounter <= 0) {
         this.blockBreakCounter = 20;
      }

      for (int i = 0; i < this.field_82224_i.length; i++) {
         this.field_82224_i[i] = this.field_82224_i[i] + 3;
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      EntityItem entityItem = this.dropItem(Items.nether_star, 1);
      if (entityItem != null) {
         entityItem.setNoDespawn();
      }

      if (!this.worldObj.isRemote) {
         for (EntityPlayer entityPlayer : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().expand(50.0, 100.0, 50.0))) {
            entityPlayer.triggerAchievement(AchievementList.killWither);
         }
      }
   }

   @Override
   public void despawnEntity() {
      this.entityAge = 0;
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 15728880;
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
   }

   @Override
   public void addPotionEffect(PotionEffect potioneffectIn) {
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(300.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6F);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0);
   }

   public float func_82207_a(int i) {
      return this.field_82221_e[i];
   }

   public float func_82210_r(int i) {
      return this.field_82220_d[i];
   }

   public int getInvulTime() {
      return this.dataWatcher.getWatchableObjectInt(20);
   }

   public void setInvulTime(int i) {
      this.dataWatcher.updateObject(20, i);
   }

   public int getWatchedTargetId(int i) {
      return this.dataWatcher.getWatchableObjectInt(17 + i);
   }

   @Override
   public float getCollisionBorderSize() {
      long l = ab ^ 92179472927554L;
      long m = l ^ 64932267881520L;
      return Astolfo.INSTANCE.getModuleManager().get(Hitboxes.class).isEnabled() && Hitboxes.wither.getValue() && Hitboxes.G
         ? super.getCollisionBorderSize() + Hitboxes.size.getValue()
         : super.getCollisionBorderSize();
   }

   public void updateWatchedTargetId(int targetOffset, int newId) {
      this.dataWatcher.updateObject(17 + targetOffset, newId);
   }

   public boolean isArmored() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   @Override
   public void mountEntity(Entity entityIn) {
      this.ridingEntity = null;
   }
}
