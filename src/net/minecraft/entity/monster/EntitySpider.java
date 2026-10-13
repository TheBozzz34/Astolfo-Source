package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntitySpider extends EntityMob {
   private static final long gb = 29997369721194L;

   public EntitySpider(World var1) {
      super(var1);
      this.setSize(1.4F, 0.9F);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(4, new EntitySpider.AISpiderAttack(this, EntityPlayer.class));
      this.tasks.addTask(4, new EntitySpider.AISpiderAttack(this, EntityIronGolem.class));
      this.tasks.addTask(5, new EntityAIWander(this, 0.8));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntitySpider.AISpiderTarget<>(this, EntityPlayer.class));
      this.targetTasks.addTask(3, new EntitySpider.AISpiderTarget<>(this, EntityIronGolem.class));
   }

   @Override
   public double getMountedYOffset() {
      return this.height * 0.5F;
   }

   @Override
   public PathNavigate getNewNavigator(World var1) {
      return new PathNavigateClimber(this, var1);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote) {
         this.setBesideClimbableBlock(this.isCollidedHorizontally);
      }
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
   }

   @Override
   public String getLivingSound() {
      return "mob.spider.say";
   }

   @Override
   public String getHurtSound() {
      return "mob.spider.say";
   }

   @Override
   public String getDeathSound() {
      return "mob.spider.death";
   }

   @Override
   public void playStepSound(BlockPos var1, Block var2) {
      this.playSound("mob.spider.step", 0.15F, 1.0F);
   }

   @Override
   public Item getDropItem() {
      return Items.string;
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
      super.dropFewItems(var1, var2);
      if (var1 && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + var2) > 0)) {
         this.dropItem(Items.spider_eye, 1);
      }
   }

   @Override
   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   @Override
   public void setInWeb() {
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   @Override
   public boolean isPotionApplicable(PotionEffect var1) {
      return var1.getPotionID() == Potion.poison.id ? false : super.isPotionApplicable(var1);
   }

   public boolean isBesideClimbableBlock() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(16);
      if (var1) {
         var5 = (byte)(var5 | 1);
      } else {
         var5 = (byte)(var5 & -2);
      }

      this.dataWatcher.updateObject(16, var5);
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      var2 = super.onInitialSpawn(var1, var2);
      if (this.worldObj.rand.nextInt(100) == 0) {
         EntitySkeleton var6 = new EntitySkeleton(this.worldObj);
         var6.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
         var6.onInitialSpawn(var1, (IEntityLivingData)null);
         this.worldObj.spawnEntityInWorld(var6);
         var6.mountEntity(this);
      }

      if (var2 == null) {
         var2 = new EntitySpider.GroupData();
         if (this.worldObj.getDifficulty() == EnumDifficulty.HARD && this.worldObj.rand.nextFloat() < 0.1F * var1.getClampedAdditionalDifficulty()) {
            ((EntitySpider.GroupData)var2).func_111104_a(this.worldObj.rand);
         }
      }

      if (var2 instanceof EntitySpider.GroupData) {
         int var8 = ((EntitySpider.GroupData)var2).potionEffectId;
         if (var8 > 0 && Potion.potionTypes[var8] != null) {
            this.addPotionEffect(new PotionEffect(var8, Integer.MAX_VALUE));
         }
      }

      return var2;
   }

   @Override
   public float getEyeHeight() {
      return 0.65F;
   }

   public static class AISpiderAttack extends EntityAIAttackOnCollide {
      private static final long c = 93754183958447L;
      private static final long h = 1339786868706771044L;

      public AISpiderAttack(EntitySpider var1, Class<? extends Entity> var2) {
         super(var1, var2, 1.0, true);
      }

      @Override
      public boolean continueExecuting() {
         float var4 = this.attacker.getBrightness(1.0F);
         if (var4 >= 0.5F && this.attacker.getRNG().nextInt((int)h) == 0) {
            this.attacker.setAttackTarget((EntityLivingBase)null);
            return false;
         } else {
            return super.continueExecuting();
         }
      }

      @Override
      public double func_179512_a(EntityLivingBase var1) {
         return 4.0F + var1.width;
      }
   }

   public static class AISpiderTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget {
      private static final long c = 71243420373749L;

      public AISpiderTarget(EntitySpider var1, Class<T> var2) {
         super(var1, var2, true);
      }

      @Override
      public boolean shouldExecute() {
         float var4 = this.taskOwner.getBrightness(1.0F);
         return var4 >= 0.5F ? false : super.shouldExecute();
      }
   }

   public static class GroupData implements IEntityLivingData {
      public int potionEffectId;
      private static final long a = 19328430659062L;

      public void func_111104_a(Random var1) {
         int var5 = var1.nextInt(5);
         if (var5 <= 1) {
            this.potionEffectId = Potion.moveSpeed.id;
         } else if (var5 <= 2) {
            this.potionEffectId = Potion.damageBoost.id;
         } else if (var5 <= 3) {
            this.potionEffectId = Potion.regeneration.id;
         } else if (var5 <= 4) {
            this.potionEffectId = Potion.invisibility.id;
         }
      }
   }
}
