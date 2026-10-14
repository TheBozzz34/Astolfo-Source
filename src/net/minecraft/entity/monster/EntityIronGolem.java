package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityIronGolem extends EntityGolem {
   private int homeCheckTimer;
   public Village villageObj;
   private int attackTimer;
   private int holdRoseTick;

   public EntityIronGolem(World worldIn) {
      super(worldIn);
      this.setSize(1.4F, 2.9F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0, true));
      this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9, 32.0F));
      this.tasks.addTask(3, new EntityAIMoveThroughVillage(this, 0.6, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(5, new EntityAILookAtVillager(this));
      this.tasks.addTask(6, new EntityAIWander(this, 0.6));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIDefendVillage(this));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
      this.targetTasks
         .addTask(3, new EntityIronGolem.AINearestAttackableTargetNonCreeper<>(this, EntityLiving.class, 10, false, true, IMob.VISIBLE_MOB_SELECTOR));
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
   }

   @Override
   public void updateAITasks() {
      if (--this.homeCheckTimer <= 0) {
         this.homeCheckTimer = 70 + this.rand.nextInt(50);
         this.villageObj = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(this), 32);
         if (this.villageObj == null) {
            this.detachHome();
         } else {
            BlockPos blockPos = this.villageObj.getCenter();
            this.setHomePosAndDistance(blockPos, (int)(this.villageObj.getVillageRadius() * 0.6F));
         }
      }

      super.updateAITasks();
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
   }

   @Override
   public int decreaseAirSupply(int i) {
      return i;
   }

   @Override
   public void collideWithEntity(Entity entityIn) {
      if (entityIn instanceof IMob && !(entityIn instanceof EntityCreeper) && this.getRNG().nextInt(20) == 0) {
         this.setAttackTarget((EntityLivingBase)entityIn);
      }

      super.collideWithEntity(entityIn);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.attackTimer > 0) {
         this.attackTimer--;
      }

      if (this.holdRoseTick > 0) {
         this.holdRoseTick--;
      }

      if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.5000003E-7F && this.rand.nextInt(5) == 0) {
         int i = MathHelper.floor_double(this.posX);
         int j = MathHelper.floor_double(this.posY - 0.2F);
         int k = MathHelper.floor_double(this.posZ);
         IBlockState iBlockState = this.worldObj.getBlockState(new BlockPos(i, j, k));
         Block block = iBlockState.getBlock();
         if (block.getMaterial() != Material.air) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.BLOCK_CRACK,
                  this.posX + (this.rand.nextFloat() - 0.5) * this.width,
                  this.getEntityBoundingBox().minY + 0.1,
                  this.posZ + (this.rand.nextFloat() - 0.5) * this.width,
                  4.0 * (this.rand.nextFloat() - 0.5),
                  0.5,
                  (this.rand.nextFloat() - 0.5) * 4.0,
                  Block.getStateId(iBlockState)
               );
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("PlayerCreated", this.isPlayerCreated());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setPlayerCreated(tagCompund.getBoolean("PlayerCreated"));
   }

   @Override
   public boolean attackEntityAsMob(Entity entityIn) {
      this.attackTimer = 10;
      this.worldObj.setEntityState(this, (byte)4);
      boolean bl = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 7 + this.rand.nextInt(15));
      if (bl) {
         entityIn.motionY += 0.4F;
         this.applyEnchantments(this, entityIn);
      }

      this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
      return bl;
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 4) {
         this.attackTimer = 10;
         this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
      } else if (id == 11) {
         this.holdRoseTick = 400;
      } else {
         super.handleStatusUpdate(id);
      }
   }

   public Village getVillage() {
      return this.villageObj;
   }

   public int getAttackTimer() {
      return this.attackTimer;
   }

   public void setHoldingRose(boolean bl) {
      this.holdRoseTick = bl ? 400 : 0;
      this.worldObj.setEntityState(this, (byte)11);
   }

   @Override
   public String getHurtSound() {
      return "mob.irongolem.hit";
   }

   @Override
   public String getDeathSound() {
      return "mob.irongolem.death";
   }

   @Override
   public void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.irongolem.walk", 1.0F, 1.0F);
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int i = this.rand.nextInt(3);

      for (int j = 0; j < i; j++) {
         this.dropItemWithOffset(Item.getItemFromBlock(Blocks.red_flower), 1, BlockFlower.EnumFlowerType.POPPY.getMeta());
      }

      int k = 3 + this.rand.nextInt(3);

      for (int l = 0; l < k; l++) {
         this.dropItem(Items.iron_ingot, 1);
      }
   }

   public int getHoldRoseTick() {
      return this.holdRoseTick;
   }

   public boolean isPlayerCreated() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setPlayerCreated(boolean bl) {
      byte b = this.dataWatcher.getWatchableObjectByte(16);
      if (bl) {
         this.dataWatcher.updateObject(16, (byte)(b | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(b & -2));
      }
   }

   @Override
   public void onDeath(DamageSource cause) {
      if (!this.isPlayerCreated() && this.attackingPlayer != null && this.villageObj != null) {
         this.villageObj.setReputationForPlayer(this.attackingPlayer.getName(), -5);
      }

      super.onDeath(cause);
   }

   public static class AINearestAttackableTargetNonCreeper<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
      public AINearestAttackableTargetNonCreeper(
         final EntityCreature creature, Class<T> classTarget, int chance, boolean bl, boolean bl2, final Predicate<? super T> predicate
      ) {
         super(creature, classTarget, chance, bl, bl2, predicate);
         this.targetEntitySelector = new Predicate<T>() {
            public boolean apply(T entityLivingBase) {
               if (predicate != null && !predicate.apply(entityLivingBase)) {
                  return false;
               }

               if (entityLivingBase instanceof EntityCreeper) {
                  return false;
               }

               if (entityLivingBase instanceof EntityPlayer) {
                  double d = AINearestAttackableTargetNonCreeper.this.getTargetDistance();
                  if (entityLivingBase.isSneaking()) {
                     d *= 0.8F;
                  }

                  if (entityLivingBase.isInvisible()) {
                     float f = ((EntityPlayer)entityLivingBase).getArmorVisibility();
                     if (f < 0.1F) {
                        f = 0.1F;
                     }

                     d *= 0.7F * f;
                  }

                  if (entityLivingBase.getDistanceToEntity(creature) > d) {
                     return false;
                  }
               }

               return AINearestAttackableTargetNonCreeper.this.isSuitableTarget(entityLivingBase, false);
            }
         };
      }
   }
}
