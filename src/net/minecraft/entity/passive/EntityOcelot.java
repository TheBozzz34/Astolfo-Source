package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityOcelot extends EntityTameable {
   private EntityAIAvoidEntity<EntityPlayer> avoidEntity;
   private EntityAITempt aiTempt;
   private static final long gb = 32417958188157L;

   public EntityOcelot(World var1) {
      super(var1);
      this.setSize(0.6F, 0.7F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, this.aiSit);
      this.tasks.addTask(3, this.aiTempt = new EntityAITempt(this, 0.6, Items.fish, true));
      this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0, 10.0F, 5.0F));
      this.tasks.addTask(6, new EntityAIOcelotSit(this, 0.8));
      this.tasks.addTask(7, new EntityAILeapAtTarget(this, 0.3F));
      this.tasks.addTask(8, new EntityAIOcelotAttack(this));
      this.tasks.addTask(9, new EntityAIMate(this, 0.8));
      this.tasks.addTask(10, new EntityAIWander(this, 0.8));
      this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
      this.targetTasks.addTask(1, new EntityAITargetNonTamed<>(this, EntityChicken.class, false, (Predicate<? super EntityChicken>)null));
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(18, (byte)0);
   }

   @Override
   public void updateAITasks() {
      if (this.getMoveHelper().isUpdating()) {
         double var4 = this.getMoveHelper().getSpeed();
         if (var4 == 0.6) {
            this.v(true);
            this.setSprinting(false);
         } else if (var4 == 1.33) {
            this.v(false);
            this.setSprinting(true);
         } else {
            this.v(false);
            this.setSprinting(false);
         }
      } else {
         this.v(false);
         this.setSprinting(false);
      }
   }

   @Override
   public boolean canDespawn() {
      return !this.isTamed() && this.ticksExisted > 2400;
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
   }

   @Override
   public void fall(float var1, float var2) {
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setInteger("CatType", this.getTameSkin());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setTameSkin(var1.getInteger("CatType"));
   }

   @Override
   public String getLivingSound() {
      return this.isTamed() ? (this.isInLove() ? "mob.cat.purr" : (this.rand.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow")) : "";
   }

   @Override
   public String getHurtSound() {
      return "mob.cat.hitt";
   }

   @Override
   public String getDeathSound() {
      return "mob.cat.hitt";
   }

   @Override
   public float getSoundVolume() {
      return 0.4F;
   }

   @Override
   public Item getDropItem() {
      return Items.leather;
   }

   @Override
   public boolean attackEntityAsMob(Entity var1) {
      return var1.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      this.aiSit.setSitting(false);
      return super.attackEntityFrom(var1, var2);
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      ItemStack var5 = var1.inventory.getCurrentItem();
      if (this.isTamed()) {
         if (this.V(var1) && !this.worldObj.isRemote && !this.isBreedingItem(var5)) {
            this.aiSit.setSitting(!this.isSitting());
         }
      } else if (this.aiTempt.isRunning() && var5 != null && var5.getItem() == Items.fish && var1.getDistanceSqToEntity(this) < 9.0) {
         if (!var1.capabilities.isCreativeMode) {
            var5.stackSize--;
         }

         if (var5.stackSize <= 0) {
            var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
         }

         if (!this.worldObj.isRemote) {
            if (this.rand.nextInt(3) == 0) {
               this.setTamed(true);
               this.setTameSkin(1 + this.worldObj.rand.nextInt(3));
               this.setOwnerId(var1.getUniqueID().toString());
               this.playTameEffect(true);
               this.aiSit.setSitting(true);
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

   public EntityOcelot createChild(EntityAgeable var1) {
      EntityOcelot var5 = new EntityOcelot(this.worldObj);
      if (this.isTamed()) {
         var5.setOwnerId(this.getOwnerId());
         var5.setTamed(true);
         var5.setTameSkin(this.getTameSkin());
      }

      return var5;
   }

   @Override
   public boolean isBreedingItem(ItemStack var1) {
      return var1 != null && var1.getItem() == Items.fish;
   }

   @Override
   public boolean canMateWith(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      }

      if (!this.isTamed()) {
         return false;
      }

      if (!(var1 instanceof EntityOcelot)) {
         return false;
      }

      EntityOcelot var5 = (EntityOcelot)var1;
      return !var5.isTamed() ? false : this.isInLove() && var5.isInLove();
   }

   public int getTameSkin() {
      return this.dataWatcher.getWatchableObjectByte(18);
   }

   public void setTameSkin(int var1) {
      this.dataWatcher.updateObject(18, (byte)var1);
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.worldObj.rand.nextInt(3) != 0;
   }

   @Override
   public boolean isNotColliding() {
      if (this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
         && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox())) {
         BlockPos var4 = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
         if (var4.getY() < this.worldObj.getSeaLevel()) {
            return false;
         }

         Block var5 = this.worldObj.getBlockState(var4.down()).getBlock();
         if (var5 == Blocks.grass || var5.getMaterial() == Material.leaves) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.getCustomNameTag() : (this.isTamed() ? StatCollector.translateToLocal("entity.Cat.name") : super.getName());
   }

   @Override
   public void setTamed(boolean var1) {
      super.setTamed(var1);
   }

   @Override
   public void setupTamedAI() {
      if (this.avoidEntity == null) {
         this.avoidEntity = new EntityAIAvoidEntity<>(this, EntityPlayer.class, 16.0F, 0.8, 1.33);
      }

      this.tasks.removeTask(this.avoidEntity);
      if (!this.isTamed()) {
         this.tasks.addTask(4, this.avoidEntity);
      }
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      var2 = super.onInitialSpawn(var1, var2);
      if (this.worldObj.rand.nextInt(7) == 0) {
         for (int var6 = 0; var6 < 2; var6++) {
            EntityOcelot var7 = new EntityOcelot(this.worldObj);
            var7.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            var7.setGrowingAge(-24000);
            this.worldObj.spawnEntityInWorld(var7);
         }
      }

      return var2;
   }
}
