package net.minecraft.entity.monster;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public abstract class EntityMob extends EntityCreature implements IMob {
   private static final long eb = 111634237501245L;

   public EntityMob(World var1) {
      super(var1);
      this.experienceValue = 5;
   }

   @Override
   public void onLivingUpdate() {
      this.L();
      float var4 = this.getBrightness(1.0F);
      if (var4 > 0.5F) {
         this.entityAge += 2;
      }

      super.onLivingUpdate();
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.setDead();
      }
   }

   @Override
   public String getSwimSound() {
      return "game.hostile.swim";
   }

   @Override
   public String getSplashSound() {
      return "game.hostile.swim.splash";
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      if (!super.attackEntityFrom(var1, var2)) {
         return false;
      }

      Entity var6 = var1.getEntity();
      return this.riddenByEntity != var6 && this.ridingEntity != var6 ? true : true;
   }

   @Override
   public String getHurtSound() {
      return "game.hostile.hurt";
   }

   @Override
   public String getDeathSound() {
      return "game.hostile.die";
   }

   @Override
   public String getFallSoundString(int var1) {
      return var1 > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
   }

   @Override
   public boolean attackEntityAsMob(Entity var1) {
      float var5 = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
      int var6 = 0;
      if (var1 instanceof EntityLivingBase) {
         var5 += EnchantmentHelper.getModifierForCreature(this.getHeldItem(), ((EntityLivingBase)var1).getCreatureAttribute());
         var6 += EnchantmentHelper.getKnockbackModifier(this);
      }

      boolean var7 = var1.attackEntityFrom(DamageSource.causeMobDamage(this), var5);
      if (var7) {
         if (var6 > 0) {
            var1.addVelocity(
               -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * var6 * 0.5F,
               0.1,
               MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * var6 * 0.5F
            );
            this.motionX *= 0.6;
            this.motionZ *= 0.6;
         }

         int var8 = EnchantmentHelper.getFireAspectModifier(this);
         if (var8 > 0) {
            var1.setFire(var8 * 4);
         }

         this.applyEnchantments(this, var1);
      }

      return var7;
   }

   @Override
   public float getBlockPathWeight(BlockPos var1) {
      return 0.5F - this.worldObj.getLightBrightness(var1);
   }

   public boolean isValidLightLevel() {
      BlockPos var4 = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      if (this.worldObj.getLightFor(EnumSkyBlock.SKY, var4) > this.rand.nextInt(32)) {
         return false;
      }

      int var5 = this.worldObj.getLightFromNeighbors(var4);
      if (this.worldObj.isThundering()) {
         int var6 = this.worldObj.getSkylightSubtracted();
         this.worldObj.setSkylightSubtracted(10);
         var5 = this.worldObj.getLightFromNeighbors(var4);
         this.worldObj.setSkylightSubtracted(var6);
      }

      return var5 <= this.rand.nextInt(8);
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevel() && super.getCanSpawnHere();
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
   }

   @Override
   public boolean canDropLoot() {
      return true;
   }
}
