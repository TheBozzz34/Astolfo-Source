package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySmallFireball extends EntityFireball {
   private static final long bb = 113055726730359L;

   public EntitySmallFireball(World var1) {
      super(var1);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World var1, EntityLivingBase var2, double var3, double var5, double var7) {
      super(var1, var2, var3, var5, var7);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.setSize(0.3125F, 0.3125F);
   }

   @Override
   public void onImpact(MovingObjectPosition var1) {
      if (!this.worldObj.isRemote) {
         if (var1.entityHit != null) {
            boolean var5 = var1.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F);
            if (var5) {
               this.applyEnchantments(this.shootingEntity, var1.entityHit);
               if (!var1.entityHit.isImmuneToFire()) {
                  var1.entityHit.setFire(5);
               }
            }
         } else {
            boolean var7 = true;
            if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving) {
               var7 = this.worldObj.getGameRules().getBoolean("mobGriefing");
            }

            if (var7) {
               BlockPos var6 = var1.getBlockPos().offset(var1.sideHit);
               if (this.worldObj.isAirBlock(var6)) {
                  this.worldObj.setBlockState(var6, Blocks.fire.getDefaultState());
               }
            }
         }

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
}
