package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityWitherSkull extends EntityFireball {
   private static final long bb = 48499649661598L;

   public EntityWitherSkull(World var1) {
      super(var1);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntityWitherSkull(World var1, EntityLivingBase var2, double var3, double var5, double var7) {
      super(var1, var2, var3, var5, var7);
      this.setSize(0.3125F, 0.3125F);
   }

   @Override
   public float getMotionFactor() {
      return this.isInvulnerable() ? 0.73F : super.getMotionFactor();
   }

   public EntityWitherSkull(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.setSize(0.3125F, 0.3125F);
   }

   @Override
   public boolean isBurning() {
      return false;
   }

   @Override
   public float getExplosionResistance(Explosion var1, World var2, BlockPos var3, IBlockState var4) {
      float var8 = super.getExplosionResistance(var1, var2, var3, var4);
      Block var9 = var4.getBlock();
      if (this.isInvulnerable() && EntityWither.canDestroyBlock(var9)) {
         var8 = Math.min(0.8F, var8);
      }

      return var8;
   }

   @Override
   public void onImpact(MovingObjectPosition var1) {
      if (!this.worldObj.isRemote) {
         if (var1.entityHit != null) {
            if (this.shootingEntity != null) {
               if (var1.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8.0F)) {
                  if (!var1.entityHit.isEntityAlive()) {
                     this.shootingEntity.heal(5.0F);
                  } else {
                     this.applyEnchantments(this.shootingEntity, var1.entityHit);
                  }
               }
            } else {
               var1.entityHit.attackEntityFrom(DamageSource.magic, 5.0F);
            }

            if (var1.entityHit instanceof EntityLivingBase) {
               byte var5 = 0;
               if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL) {
                  var5 = 10;
               } else if (this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
                  var5 = 40;
               }

               if (var5 > 0) {
                  ((EntityLivingBase)var1.entityHit).addPotionEffect(new PotionEffect(Potion.wither.id, 20 * var5, 1));
               }
            }
         }

         this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, 1.0F, false, this.worldObj.getGameRules().getBoolean("mobGriefing"));
         this.setDead();
      }
   }

   @Override
   public boolean canBeCollidedWith() {
      return false;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      return false;
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(10, (byte)0);
   }

   public boolean isInvulnerable() {
      return this.dataWatcher.getWatchableObjectByte(10) == 1;
   }

   public void setInvulnerable(boolean var1) {
      this.dataWatcher.updateObject(10, (byte)(var1 ? 1 : 0));
   }
}
