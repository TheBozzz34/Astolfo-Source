package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWolf extends EntityTameable {
   private float headRotationCourse;
   private float headRotationCourseOld;
   private boolean isWet;
   private boolean isShaking;
   private float timeWolfIsShaking;
   private float prevTimeWolfIsShaking;
   private static final long gb = 33326720328400L;

   public EntityWolf(World var1) {
      super(var1);
      this.setSize(0.6F, 0.8F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, this.aiSit);
      this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0, true));
      this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0, 10.0F, 2.0F));
      this.tasks.addTask(6, new EntityAIMate(this, 1.0));
      this.tasks.addTask(7, new EntityAIWander(this, 1.0));
      this.tasks.addTask(8, new EntityAIBeg(this, 8.0F));
      this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(9, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
      this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
      this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed<>(this, EntityAnimal.class, false, new Predicate<Entity>() {
         private static final long a = 13487023545426L;

         public boolean apply(Entity var1) {
            return var1 instanceof EntitySheep || var1 instanceof EntityRabbit;
         }
      }));
      this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntitySkeleton.class, false));
      this.setTamed(false);
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
      if (this.isTamed()) {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
      } else {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
      }

      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0);
   }

   @Override
   public void setAttackTarget(EntityLivingBase var1) {
      super.setAttackTarget(var1);
      if (var1 == null) {
         this.setAngry(false);
      } else if (!this.isTamed()) {
         this.setAngry(true);
      }
   }

   @Override
   public void updateAITasks() {
      this.dataWatcher.updateObject(18, this.getHealth());
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(18, new Float(this.getHealth()));
      this.dataWatcher.addObject(19, new Byte((byte)0));
      this.dataWatcher.addObject(20, new Byte((byte)EnumDyeColor.RED.getMetadata()));
   }

   @Override
   public void playStepSound(BlockPos var1, Block var2) {
      this.playSound("mob.wolf.step", 0.15F, 1.0F);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setBoolean("Angry", this.isAngry());
      var1.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setAngry(var1.getBoolean("Angry"));
      if (var1.hasKey("CollarColor", 99)) {
         this.setCollarColor(EnumDyeColor.byDyeDamage(var1.getByte("CollarColor")));
      }
   }

   @Override
   public String getLivingSound() {
      return this.isAngry()
         ? "mob.wolf.growl"
         : (
            this.rand.nextInt(3) == 0
               ? (this.isTamed() && this.dataWatcher.getWatchableObjectFloat(18) < 10.0F ? "mob.wolf.whine" : "mob.wolf.panting")
               : "mob.wolf.bark"
         );
   }

   @Override
   public String getHurtSound() {
      return "mob.wolf.hurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.wolf.death";
   }

   @Override
   public float getSoundVolume() {
      return 0.4F;
   }

   @Override
   public Item getDropItem() {
      return Item.getItemById(-1);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.worldObj.isRemote && this.isWet && !this.isShaking && !this.u() && this.onGround) {
         this.isShaking = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
         this.worldObj.setEntityState(this, (byte)8);
      }

      if (!this.worldObj.isRemote && this.getAttackTarget() == null && this.isAngry()) {
         this.setAngry(false);
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      this.headRotationCourseOld = this.headRotationCourse;
      if (this.isBegging()) {
         this.headRotationCourse = this.headRotationCourse + (1.0F - this.headRotationCourse) * 0.4F;
      } else {
         this.headRotationCourse = this.headRotationCourse + (0.0F - this.headRotationCourse) * 0.4F;
      }

      if (this.G()) {
         this.isWet = true;
         this.isShaking = false;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
      } else if ((this.isWet || this.isShaking) && this.isShaking) {
         if (this.timeWolfIsShaking == 0.0F) {
            this.playSound("mob.wolf.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
         this.timeWolfIsShaking += 0.05F;
         if (this.prevTimeWolfIsShaking >= 2.0F) {
            this.isWet = false;
            this.isShaking = false;
            this.prevTimeWolfIsShaking = 0.0F;
            this.timeWolfIsShaking = 0.0F;
         }

         if (this.timeWolfIsShaking > 0.4F) {
            float var4 = (float)this.getEntityBoundingBox().minY;
            int var5 = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * (float) Math.PI) * 7.0F);

            for (int var6 = 0; var6 < var5; var6++) {
               float var7 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
               float var8 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
               this.worldObj
                  .spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + var7, var4 + 0.8F, this.posZ + var8, this.motionX, this.motionY, this.motionZ);
            }
         }
      }
   }

   public boolean isWolfWet() {
      return this.isWet;
   }

   public float getShadingWhileWet(float var1) {
      return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * var1) / 2.0F * 0.25F;
   }

   public float getShakeAngle(float var1, float var2) {
      float var6 = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * var1 + var2) / 1.8F;
      if (var6 < 0.0F) {
         var6 = 0.0F;
      } else if (var6 > 1.0F) {
         var6 = 1.0F;
      }

      return MathHelper.sin(var6 * (float) Math.PI) * MathHelper.sin(var6 * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
   }

   public float getInterestedAngle(float var1) {
      return (this.headRotationCourseOld + (this.headRotationCourse - this.headRotationCourseOld) * var1) * 0.15F * (float) Math.PI;
   }

   @Override
   public float getEyeHeight() {
      return this.height * 0.8F;
   }

   @Override
   public int getVerticalFaceSpeed() {
      return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      Entity var6 = var1.getEntity();
      this.aiSit.setSitting(false);
      if (var6 != null && !(var6 instanceof EntityPlayer) && !(var6 instanceof EntityArrow)) {
         var2 = (var2 + 1.0F) / 2.0F;
      }

      return super.attackEntityFrom(var1, var2);
   }

   @Override
   public boolean attackEntityAsMob(Entity var1) {
      boolean var5 = var1.attackEntityFrom(
         DamageSource.causeMobDamage(this), (int)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()
      );
      if (var5) {
         this.applyEnchantments(this, var1);
      }

      return var5;
   }

   @Override
   public void setTamed(boolean var1) {
      super.setTamed(var1);
      if (var1) {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
      } else {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
      }

      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0);
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      ItemStack var5 = var1.inventory.getCurrentItem();
      if (this.isTamed()) {
         if (var5 != null) {
            if (var5.getItem() instanceof ItemFood) {
               ItemFood var6 = (ItemFood)var5.getItem();
               if (var6.isWolfsFavoriteMeat() && this.dataWatcher.getWatchableObjectFloat(18) < 20.0F) {
                  if (!var1.capabilities.isCreativeMode) {
                     var5.stackSize--;
                  }

                  this.heal(var6.getHealAmount(var5));
                  if (var5.stackSize <= 0) {
                     var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
                  }

                  return true;
               }
            } else if (var5.getItem() == Items.dye) {
               EnumDyeColor var7 = EnumDyeColor.byDyeDamage(var5.getMetadata());
               if (var7 != this.getCollarColor()) {
                  this.setCollarColor(var7);
                  if (!var1.capabilities.isCreativeMode && --var5.stackSize <= 0) {
                     var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
                  }

                  return true;
               }
            }
         }

         if (this.V(var1) && !this.worldObj.isRemote && !this.isBreedingItem(var5)) {
            this.aiSit.setSitting(!this.isSitting());
            this.Lr = false;
            this.navigator.clearPathEntity();
            this.setAttackTarget((EntityLivingBase)null);
         }
      } else if (var5 != null && var5.getItem() == Items.bone && !this.isAngry()) {
         if (!var1.capabilities.isCreativeMode) {
            var5.stackSize--;
         }

         if (var5.stackSize <= 0) {
            var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
         }

         if (!this.worldObj.isRemote) {
            if (this.rand.nextInt(3) == 0) {
               this.setTamed(true);
               this.navigator.clearPathEntity();
               this.setAttackTarget((EntityLivingBase)null);
               this.aiSit.setSitting(true);
               this.setHealth(20.0F);
               this.setOwnerId(var1.getUniqueID().toString());
               this.playTameEffect(true);
               this.worldObj.setEntityState(this, (byte)7);
            } else {
               this.playTameEffect(false);
               this.worldObj.setEntityState(this, (byte)6);
            }
         }

         return true;
      }

      return super.interact(var1);
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 8) {
         this.isShaking = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   public float getTailRotation() {
      return this.isAngry()
         ? 1.5393804F
         : (this.isTamed() ? (0.55F - (20.0F - this.dataWatcher.getWatchableObjectFloat(18)) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5));
   }

   @Override
   public boolean isBreedingItem(ItemStack var1) {
      return var1 == null ? false : (!(var1.getItem() instanceof ItemFood) ? false : ((ItemFood)var1.getItem()).isWolfsFavoriteMeat());
   }

   @Override
   public int getMaxSpawnedInChunk() {
      return 8;
   }

   public boolean isAngry() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
   }

   public void setAngry(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(16);
      if (var1) {
         this.dataWatcher.updateObject(16, (byte)(var5 | 2));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var5 & -3));
      }
   }

   public EnumDyeColor getCollarColor() {
      return EnumDyeColor.byDyeDamage(this.dataWatcher.getWatchableObjectByte(20) & 15);
   }

   public void setCollarColor(EnumDyeColor var1) {
      this.dataWatcher.updateObject(20, (byte)(var1.getDyeDamage() & 15));
   }

   public EntityWolf createChild(EntityAgeable var1) {
      EntityWolf var5 = new EntityWolf(this.worldObj);
      String var6 = this.getOwnerId();
      if (var6 != null && var6.trim().length() > 0) {
         var5.setOwnerId(var6);
         var5.setTamed(true);
      }

      return var5;
   }

   public void setBegging(boolean var1) {
      if (var1) {
         this.dataWatcher.updateObject(19, (byte)1);
      } else {
         this.dataWatcher.updateObject(19, (byte)0);
      }
   }

   @Override
   public boolean canMateWith(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      }

      if (!this.isTamed()) {
         return false;
      }

      if (!(var1 instanceof EntityWolf)) {
         return false;
      }

      EntityWolf var5 = (EntityWolf)var1;
      return !var5.isTamed() ? false : (var5.isSitting() ? false : this.isInLove() && var5.isInLove());
   }

   public boolean isBegging() {
      return this.dataWatcher.getWatchableObjectByte(19) == 1;
   }

   @Override
   public boolean canDespawn() {
      return !this.isTamed() && this.ticksExisted > 2400;
   }

   @Override
   public boolean shouldAttackEntity(EntityLivingBase var1, EntityLivingBase var2) {
      if (!(var1 instanceof EntityCreeper) && !(var1 instanceof EntityGhast)) {
         if (var1 instanceof EntityWolf) {
            EntityWolf var6 = (EntityWolf)var1;
            if (var6.isTamed() && var6.getOwner() == var2) {
               return false;
            }
         }

         return var1 instanceof EntityPlayer && var2 instanceof EntityPlayer && !((EntityPlayer)var2).canAttackPlayer((EntityPlayer)var1)
            ? false
            : !(var1 instanceof EntityHorse) || !((EntityHorse)var1).isTame();
      } else {
         return false;
      }
   }

   @Override
   public boolean allowLeashing() {
      return !this.isAngry() && super.allowLeashing();
   }
}
