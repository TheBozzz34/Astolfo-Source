package net.minecraft.entity.ai;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityAIMate extends EntityAIBase {
   private EntityAnimal theAnimal;
   public World theWorld;
   private EntityAnimal targetMate;
   public int spawnBabyDelay;
   public double moveSpeed;
   private static final long a = 35174016240311L;

   public EntityAIMate(EntityAnimal var1, double var2) {
      this.theAnimal = var1;
      this.theWorld = var1.worldObj;
      this.moveSpeed = var2;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theAnimal.isInLove()) {
         return false;
      }

      this.targetMate = this.getNearbyMate();
      return this.targetMate != null;
   }

   @Override
   public boolean continueExecuting() {
      return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
   }

   @Override
   public void resetTask() {
      this.targetMate = null;
      this.spawnBabyDelay = 0;
   }

   @Override
   public void updateTask() {
      this.theAnimal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, this.theAnimal.getVerticalFaceSpeed());
      this.theAnimal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
      this.spawnBabyDelay++;
      if (this.spawnBabyDelay >= 60 && this.theAnimal.getDistanceSqToEntity(this.targetMate) < 9.0) {
         this.spawnBaby();
      }
   }

   private EntityAnimal getNearbyMate() {
      float var4 = 8.0F;
      List var5 = this.theWorld.getEntitiesWithinAABB(this.theAnimal.getClass(), this.theAnimal.getEntityBoundingBox().expand(var4, var4, var4));
      double var6 = Double.MAX_VALUE;
      EntityAnimal var8 = null;

      for (Object var10$raw : var5) {
         EntityAnimal var10 = (EntityAnimal)var10$raw;
         if (this.theAnimal.canMateWith(var10) && this.theAnimal.getDistanceSqToEntity(var10) < var6) {
            var8 = var10;
            var6 = this.theAnimal.getDistanceSqToEntity(var10);
         }
      }

      return var8;
   }

   private void spawnBaby() {
      EntityAgeable var4 = this.theAnimal.createChild(this.targetMate);
      if (var4 != null) {
         EntityPlayer var5 = this.theAnimal.getPlayerInLove();
         if (var5 == null && this.targetMate.getPlayerInLove() != null) {
            var5 = this.targetMate.getPlayerInLove();
         }

         if (var5 != null) {
            var5.triggerAchievement(StatList.animalsBredStat);
            if (this.theAnimal instanceof EntityCow) {
               var5.triggerAchievement(AchievementList.breedCow);
            }
         }

         this.theAnimal.setGrowingAge(6000);
         this.targetMate.setGrowingAge(6000);
         this.theAnimal.resetInLove();
         this.targetMate.resetInLove();
         var4.setGrowingAge(-24000);
         var4.setLocationAndAngles(this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, 0.0F, 0.0F);
         this.theWorld.spawnEntityInWorld(var4);
         Random var6 = this.theAnimal.getRNG();

         for (int var7 = 0; var7 < 7; var7++) {
            double var8 = var6.nextGaussian() * 0.02;
            double var10 = var6.nextGaussian() * 0.02;
            double var12 = var6.nextGaussian() * 0.02;
            double var14 = var6.nextDouble() * this.theAnimal.width * 2.0 - this.theAnimal.width;
            double var16 = 0.5 + var6.nextDouble() * this.theAnimal.height;
            double var18 = var6.nextDouble() * this.theAnimal.width * 2.0 - this.theAnimal.width;
            this.theWorld
               .spawnParticle(
                  EnumParticleTypes.HEART, this.theAnimal.posX + var14, this.theAnimal.posY + var16, this.theAnimal.posZ + var18, var8, var10, var12
               );
         }

         if (this.theWorld.getGameRules().getBoolean("doMobLoot")) {
            this.theWorld
               .spawnEntityInWorld(new EntityXPOrb(this.theWorld, this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, var6.nextInt(7) + 1));
         }
      }
   }
}
