package net.minecraft.entity.monster;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityPigZombie extends EntityZombie {
   private static final UUID ATTACK_SPEED_BOOST_MODIFIER_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier ATTACK_SPEED_BOOST_MODIFIER = new AttributeModifier(
         ATTACK_SPEED_BOOST_MODIFIER_UUID, "Attacking speed boost", 0.05, 0
      )
      .setSaved(false);
   private int angerLevel;
   private int randomSoundDelay;
   private UUID angerTargetUUID;
   private static final long ab = 113449227378542L;

   public EntityPigZombie(World var1) {
      super(var1);
      this.isImmuneToFire = true;
   }

   @Override
   public void setRevengeTarget(EntityLivingBase var1) {
      super.setRevengeTarget(var1);
      if (var1 != null) {
         this.angerTargetUUID = var1.getUniqueID();
      }
   }

   @Override
   public void applyEntityAI() {
      this.targetTasks.addTask(1, new EntityPigZombie.AIHurtByAggressor(this));
      this.targetTasks.addTask(2, new EntityPigZombie.AITargetAggressor(this));
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(reinforcementChance).setBaseValue(0.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23F);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0);
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
   }

   @Override
   public void updateAITasks() {
      IAttributeInstance var4 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (this.isAngry()) {
         if (!this.isChild() && !var4.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
            var4.applyModifier(ATTACK_SPEED_BOOST_MODIFIER);
         }

         this.angerLevel--;
      } else if (var4.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
         var4.removeModifier(ATTACK_SPEED_BOOST_MODIFIER);
      }

      if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
         this.playSound("mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
      }

      if (this.angerLevel > 0 && this.angerTargetUUID != null && this.getAITarget() == null) {
         EntityPlayer var5 = this.worldObj.getPlayerEntityByUUID(this.angerTargetUUID);
         this.setRevengeTarget(var5);
         this.attackingPlayer = var5;
         this.recentlyHit = this.getRevengeTimer();
      }

      super.updateAITasks();
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   @Override
   public boolean isNotColliding() {
      return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
         && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setShort("Anger", (short)this.angerLevel);
      if (this.angerTargetUUID != null) {
         var1.setString("HurtBy", this.angerTargetUUID.toString());
      } else {
         var1.setString("HurtBy", "");
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.angerLevel = var1.getShort("Anger");
      String var5 = var1.getString("HurtBy");
      if (var5.length() > 0) {
         this.angerTargetUUID = UUID.fromString(var5);
         EntityPlayer var6 = this.worldObj.getPlayerEntityByUUID(this.angerTargetUUID);
         this.setRevengeTarget(var6);
         if (var6 != null) {
            this.attackingPlayer = var6;
            this.recentlyHit = this.getRevengeTimer();
         }
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      Entity var6 = var1.getEntity();
      if (var6 instanceof EntityPlayer) {
         this.becomeAngryAt(var6);
      }

      return super.attackEntityFrom(var1, var2);
   }

   private void becomeAngryAt(Entity var1) {
      this.angerLevel = 400 + this.rand.nextInt(400);
      this.randomSoundDelay = this.rand.nextInt(40);
      if (var1 instanceof EntityLivingBase) {
         this.setRevengeTarget((EntityLivingBase)var1);
      }
   }

   public boolean isAngry() {
      return this.angerLevel > 0;
   }

   @Override
   public String getLivingSound() {
      return "mob.zombiepig.zpig";
   }

   @Override
   public String getHurtSound() {
      return "mob.zombiepig.zpighurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.zombiepig.zpigdeath";
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
      int var6 = this.rand.nextInt(2 + var2);

      for (int var7 = 0; var7 < var6; var7++) {
         this.dropItem(Items.rotten_flesh, 1);
      }

      var6 = this.rand.nextInt(2 + var2);

      for (int var9 = 0; var9 < var6; var9++) {
         this.dropItem(Items.gold_nugget, 1);
      }
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      return false;
   }

   @Override
   public void addRandomDrop() {
      this.dropItem(Items.gold_ingot, 1);
   }

   @Override
   public void setEquipmentBasedOnDifficulty(DifficultyInstance var1) {
      this.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      super.onInitialSpawn(var1, var2);
      this.setVillager(false);
      return var2;
   }

   public static class AIHurtByAggressor extends EntityAIHurtByTarget {
      private static final long c = 87469934673729L;

      public AIHurtByAggressor(EntityPigZombie var1) {
         super(var1, true);
      }

      @Override
      public void setEntityAttackTarget(EntityCreature var1, EntityLivingBase var2) {
         super.setEntityAttackTarget(var1, var2);
         if (var1 instanceof EntityPigZombie) {
            ((EntityPigZombie)var1).becomeAngryAt(var2);
         }
      }
   }

   public static class AITargetAggressor extends EntityAINearestAttackableTarget<EntityPlayer> {
      private static final long c = 24135392844148L;

      public AITargetAggressor(EntityPigZombie var1) {
         super(var1, EntityPlayer.class, true);
      }

      @Override
      public boolean shouldExecute() {
         return ((EntityPigZombie)this.taskOwner).isAngry() && super.shouldExecute();
      }
   }
}
