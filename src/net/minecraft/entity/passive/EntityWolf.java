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

   public EntityWolf(World worldIn) {
      super(worldIn);
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
         public boolean apply(Entity entity) {
            return entity instanceof EntitySheep || entity instanceof EntityRabbit;
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
   public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
      super.setAttackTarget(entitylivingbaseIn);
      if (entitylivingbaseIn == null) {
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
   public void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.wolf.step", 0.15F, 1.0F);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("Angry", this.isAngry());
      tagCompound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setAngry(tagCompund.getBoolean("Angry"));
      if (tagCompund.hasKey("CollarColor", 99)) {
         this.setCollarColor(EnumDyeColor.byDyeDamage(tagCompund.getByte("CollarColor")));
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
      if (!this.worldObj.isRemote && this.isWet && !this.isShaking && !this.hasPath() && this.onGround) {
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

      if (this.isWet()) {
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
            float f = (float)this.getEntityBoundingBox().minY;
            int i = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * (float) Math.PI) * 7.0F);

            for (int j = 0; j < i; j++) {
               float g = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
               float h = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
               this.worldObj
                  .spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + g, f + 0.8F, this.posZ + h, this.motionX, this.motionY, this.motionZ);
            }
         }
      }
   }

   public boolean isWolfWet() {
      return this.isWet;
   }

   public float getShadingWhileWet(float f) {
      return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * f) / 2.0F * 0.25F;
   }

   public float getShakeAngle(float f, float g) {
      float h = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * f + g) / 1.8F;
      if (h < 0.0F) {
         h = 0.0F;
      } else if (h > 1.0F) {
         h = 1.0F;
      }

      return MathHelper.sin(h * (float) Math.PI) * MathHelper.sin(h * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
   }

   public float getInterestedAngle(float f) {
      return (this.headRotationCourseOld + (this.headRotationCourse - this.headRotationCourseOld) * f) * 0.15F * (float) Math.PI;
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
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      Entity entity = source.getEntity();
      this.aiSit.setSitting(false);
      if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow)) {
         amount = (amount + 1.0F) / 2.0F;
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public boolean attackEntityAsMob(Entity entityIn) {
      boolean bl = entityIn.attackEntityFrom(
         DamageSource.causeMobDamage(this), (int)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()
      );
      if (bl) {
         this.applyEnchantments(this, entityIn);
      }

      return bl;
   }

   @Override
   public void setTamed(boolean tamed) {
      super.setTamed(tamed);
      if (tamed) {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
      } else {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
      }

      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0);
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack itemStack = player.inventory.getCurrentItem();
      if (this.isTamed()) {
         if (itemStack != null) {
            if (itemStack.getItem() instanceof ItemFood) {
               ItemFood itemFood = (ItemFood)itemStack.getItem();
               if (itemFood.isWolfsFavoriteMeat() && this.dataWatcher.getWatchableObjectFloat(18) < 20.0F) {
                  if (!player.capabilities.isCreativeMode) {
                     itemStack.stackSize--;
                  }

                  this.heal(itemFood.getHealAmount(itemStack));
                  if (itemStack.stackSize <= 0) {
                     player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                  }

                  return true;
               }
            } else if (itemStack.getItem() == Items.dye) {
               EnumDyeColor enumDyeColor = EnumDyeColor.byDyeDamage(itemStack.getMetadata());
               if (enumDyeColor != this.getCollarColor()) {
                  this.setCollarColor(enumDyeColor);
                  if (!player.capabilities.isCreativeMode && --itemStack.stackSize <= 0) {
                     player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                  }

                  return true;
               }
            }
         }

         if (this.isOwner(player) && !this.worldObj.isRemote && !this.isBreedingItem(itemStack)) {
            this.aiSit.setSitting(!this.isSitting());
            this.isJumping = false;
            this.navigator.clearPathEntity();
            this.setAttackTarget((EntityLivingBase)null);
         }
      } else if (itemStack != null && itemStack.getItem() == Items.bone && !this.isAngry()) {
         if (!player.capabilities.isCreativeMode) {
            itemStack.stackSize--;
         }

         if (itemStack.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
         }

         if (!this.worldObj.isRemote) {
            if (this.rand.nextInt(3) == 0) {
               this.setTamed(true);
               this.navigator.clearPathEntity();
               this.setAttackTarget((EntityLivingBase)null);
               this.aiSit.setSitting(true);
               this.setHealth(20.0F);
               this.setOwnerId(player.getUniqueID().toString());
               this.playTameEffect(true);
               this.worldObj.setEntityState(this, (byte)7);
            } else {
               this.playTameEffect(false);
               this.worldObj.setEntityState(this, (byte)6);
            }
         }

         return true;
      }

      return super.interact(player);
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 8) {
         this.isShaking = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
      } else {
         super.handleStatusUpdate(id);
      }
   }

   public float getTailRotation() {
      return this.isAngry()
         ? 1.5393804F
         : (this.isTamed() ? (0.55F - (20.0F - this.dataWatcher.getWatchableObjectFloat(18)) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5));
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return stack == null ? false : (!(stack.getItem() instanceof ItemFood) ? false : ((ItemFood)stack.getItem()).isWolfsFavoriteMeat());
   }

   @Override
   public int getMaxSpawnedInChunk() {
      return 8;
   }

   public boolean isAngry() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
   }

   public void setAngry(boolean angry) {
      byte b = this.dataWatcher.getWatchableObjectByte(16);
      if (angry) {
         this.dataWatcher.updateObject(16, (byte)(b | 2));
      } else {
         this.dataWatcher.updateObject(16, (byte)(b & -3));
      }
   }

   public EnumDyeColor getCollarColor() {
      return EnumDyeColor.byDyeDamage(this.dataWatcher.getWatchableObjectByte(20) & 15);
   }

   public void setCollarColor(EnumDyeColor collarcolor) {
      this.dataWatcher.updateObject(20, (byte)(collarcolor.getDyeDamage() & 15));
   }

   public EntityWolf createChild(EntityAgeable ageable) {
      EntityWolf entityWolf = new EntityWolf(this.worldObj);
      String string = this.getOwnerId();
      if (string != null && string.trim().length() > 0) {
         entityWolf.setOwnerId(string);
         entityWolf.setTamed(true);
      }

      return entityWolf;
   }

   public void setBegging(boolean beg) {
      if (beg) {
         this.dataWatcher.updateObject(19, (byte)1);
      } else {
         this.dataWatcher.updateObject(19, (byte)0);
      }
   }

   @Override
   public boolean canMateWith(EntityAnimal otherAnimal) {
      if (otherAnimal == this) {
         return false;
      }

      if (!this.isTamed()) {
         return false;
      }

      if (!(otherAnimal instanceof EntityWolf)) {
         return false;
      }

      EntityWolf entityWolf = (EntityWolf)otherAnimal;
      return !entityWolf.isTamed() ? false : (entityWolf.isSitting() ? false : this.isInLove() && entityWolf.isInLove());
   }

   public boolean isBegging() {
      return this.dataWatcher.getWatchableObjectByte(19) == 1;
   }

   @Override
   public boolean canDespawn() {
      return !this.isTamed() && this.ticksExisted > 2400;
   }

   @Override
   public boolean shouldAttackEntity(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
      if (!(entityLivingBase instanceof EntityCreeper) && !(entityLivingBase instanceof EntityGhast)) {
         if (entityLivingBase instanceof EntityWolf) {
            EntityWolf entityWolf = (EntityWolf)entityLivingBase;
            if (entityWolf.isTamed() && entityWolf.getOwner() == entityLivingBase2) {
               return false;
            }
         }

         return entityLivingBase instanceof EntityPlayer
               && entityLivingBase2 instanceof EntityPlayer
               && !((EntityPlayer)entityLivingBase2).canAttackPlayer((EntityPlayer)entityLivingBase)
            ? false
            : !(entityLivingBase instanceof EntityHorse) || !((EntityHorse)entityLivingBase).isTame();
      } else {
         return false;
      }
   }

   @Override
   public boolean allowLeashing() {
      return !this.isAngry() && super.allowLeashing();
   }
}
