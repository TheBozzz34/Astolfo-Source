package net.minecraft.entity.monster;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;

public class EntitySkeleton extends EntityMob implements IRangedAttackMob {
   private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.0, 20, 60, 15.0F);
   private EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.2, false);
   private static final long ab = 14345860322891L;

   public EntitySkeleton(World var1) {
      super(var1);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIRestrictSun(this));
      this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0));
      this.tasks.addTask(3, new EntityAIAvoidEntity<>(this, EntityWolf.class, 6.0F, 1.0, 1.2));
      this.tasks.addTask(4, new EntityAIWander(this, 1.0));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
      if (var1 != null && !var1.isRemote) {
         this.setCombatTask();
      }
   }

   public EntitySkeleton(int var1) {
      super(null);
      this.setEntityId(var1);
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(13, new Byte((byte)0));
   }

   @Override
   public String getLivingSound() {
      return "mob.skeleton.say";
   }

   @Override
   public String getHurtSound() {
      return "mob.skeleton.hurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.skeleton.death";
   }

   @Override
   public void playStepSound(BlockPos var1, Block var2) {
      this.playSound("mob.skeleton.step", 0.15F, 1.0F);
   }

   @Override
   public boolean attackEntityAsMob(Entity var1) {
      if (super.attackEntityAsMob(var1)) {
         if (this.getSkeletonType() == 1 && var1 instanceof EntityLivingBase) {
            ((EntityLivingBase)var1).addPotionEffect(new PotionEffect(Potion.wither.id, 200));
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isDaytime() && !this.worldObj.isRemote) {
         float var4 = this.getBrightness(1.0F);
         BlockPos var5 = new BlockPos(this.posX, Math.round(this.posY), this.posZ);
         if (var4 > 0.5F && this.rand.nextFloat() * 30.0F < (var4 - 0.4F) * 2.0F && this.worldObj.canSeeSky(var5)) {
            boolean var6 = true;
            ItemStack var7 = this.getEquipmentInSlot(4);
            if (var7 != null) {
               if (var7.isItemStackDamageable()) {
                  var7.setItemDamage(var7.getItemDamage() + this.rand.nextInt(2));
                  if (var7.getItemDamage() >= var7.getMaxDamage()) {
                     this.renderBrokenItemStack(var7);
                     this.setCurrentItemOrArmor(4, (ItemStack)null);
                  }
               }

               var6 = false;
            }

            if (var6) {
               this.setFire(8);
            }
         }
      }

      if (this.worldObj.isRemote && this.getSkeletonType() == 1) {
         this.setSize(0.72F, 2.535F);
      }

      super.onLivingUpdate();
   }

   @Override
   public void updateRidden() {
      super.updateRidden();
      if (this.ridingEntity instanceof EntityCreature) {
         EntityCreature var4 = (EntityCreature)this.ridingEntity;
         this.renderYawOffset = var4.renderYawOffset;
      }
   }

   @Override
   public void onDeath(DamageSource var1) {
      super.onDeath(var1);
      if (var1.getSourceOfDamage() instanceof EntityArrow && var1.getEntity() instanceof EntityPlayer) {
         EntityPlayer var5 = (EntityPlayer)var1.getEntity();
         double var6 = var5.posX - this.posX;
         double var8 = var5.posZ - this.posZ;
         if (var6 * var6 + var8 * var8 >= 2500.0) {
            var5.triggerAchievement(AchievementList.snipeSkeleton);
         }
      } else if (var1.getEntity() instanceof EntityCreeper && ((EntityCreeper)var1.getEntity()).getPowered() && ((EntityCreeper)var1.getEntity()).isAIEnabled()
         )
       {
         ((EntityCreeper)var1.getEntity()).func_175493_co();
         this.entityDropItem(new ItemStack(Items.skull, 1, this.getSkeletonType() == 1 ? 1 : 0), 0.0F);
      }
   }

   @Override
   public Item getDropItem() {
      return Items.arrow;
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
      if (this.getSkeletonType() == 1) {
         int var6 = this.rand.nextInt(3 + var2) - 1;

         for (int var7 = 0; var7 < var6; var7++) {
            this.dropItem(Items.coal, 1);
         }
      } else {
         int var8 = this.rand.nextInt(3 + var2);

         for (int var10 = 0; var10 < var8; var10++) {
            this.dropItem(Items.arrow, 1);
         }
      }

      int var9 = this.rand.nextInt(3 + var2);

      for (int var11 = 0; var11 < var9; var11++) {
         this.dropItem(Items.bone, 1);
      }
   }

   @Override
   public void addRandomDrop() {
      if (this.getSkeletonType() == 1) {
         this.entityDropItem(new ItemStack(Items.skull, 1, 1), 0.0F);
      }
   }

   @Override
   public void setEquipmentBasedOnDifficulty(DifficultyInstance var1) {
      super.setEquipmentBasedOnDifficulty(var1);
      this.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      var2 = super.onInitialSpawn(var1, var2);
      if (this.worldObj.provider instanceof WorldProviderHell && this.getRNG().nextInt(5) > 0) {
         this.tasks.addTask(4, this.aiAttackOnCollide);
         this.setSkeletonType(1);
         this.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
         this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0);
      } else {
         this.tasks.addTask(4, this.aiArrowAttack);
         this.setEquipmentBasedOnDifficulty(var1);
         this.setEnchantmentBasedOnDifficulty(var1);
      }

      this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * var1.getClampedAdditionalDifficulty());
      if (this.getEquipmentInSlot(4) == null) {
         Calendar var6 = this.worldObj.getCurrentDate();
         if (var6.get(2) + 1 == 10 && var6.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
            this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
            this.equipmentDropChances[4] = 0.0F;
         }
      }

      return var2;
   }

   public void setCombatTask() {
      this.tasks.removeTask(this.aiAttackOnCollide);
      this.tasks.removeTask(this.aiArrowAttack);
      ItemStack var4 = this.getHeldItem();
      if (var4 != null && var4.getItem() == Items.bow) {
         this.tasks.addTask(4, this.aiArrowAttack);
      } else {
         this.tasks.addTask(4, this.aiAttackOnCollide);
      }
   }

   @Override
   public void attackEntityWithRangedAttack(EntityLivingBase var1, float var2) {
      EntityArrow var6 = new EntityArrow(this.worldObj, this, var1, 1.6F, 14 - this.worldObj.getDifficulty().getDifficultyId() * 4);
      int var7 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
      int var8 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
      var6.setDamage(var2 * 2.0F + this.rand.nextGaussian() * 0.25 + this.worldObj.getDifficulty().getDifficultyId() * 0.11F);
      if (var7 > 0) {
         var6.setDamage(var6.getDamage() + var7 * 0.5 + 0.5);
      }

      if (var8 > 0) {
         var6.setKnockbackStrength(var8);
      }

      if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0 || this.getSkeletonType() == 1) {
         var6.setFire(100);
      }

      this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.worldObj.spawnEntityInWorld(var6);
   }

   public int getSkeletonType() {
      return this.dataWatcher.getWatchableObjectByte(13);
   }

   public void setSkeletonType(int var1) {
      this.dataWatcher.updateObject(13, (byte)var1);
      this.isImmuneToFire = var1 == 1;
      if (var1 == 1) {
         this.setSize(0.72F, 2.535F);
      } else {
         this.setSize(0.6F, 1.95F);
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      if (var1.hasKey("SkeletonType", 99)) {
         byte var5 = var1.getByte("SkeletonType");
         this.setSkeletonType(var5);
      }

      this.setCombatTask();
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setByte("SkeletonType", (byte)this.getSkeletonType());
   }

   @Override
   public void setCurrentItemOrArmor(int var1, ItemStack var2) {
      super.setCurrentItemOrArmor(var1, var2);
      if (!this.worldObj.isRemote && var1 == 0) {
         this.setCombatTask();
      }
   }

   @Override
   public float getEyeHeight() {
      return this.getSkeletonType() == 1 ? super.getEyeHeight() : 1.74F;
   }

   @Override
   public double getYOffset() {
      return this.isChild() ? 0.0 : -0.35;
   }
}
