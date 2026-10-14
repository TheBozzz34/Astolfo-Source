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

   public EntitySmallFireball(World worldIn) {
      super(worldIn);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
      super(worldIn, x, y, z, accelX, accelY, accelZ);
      this.setSize(0.3125F, 0.3125F);
   }

   @Override
   public void onImpact(MovingObjectPosition movingObject) {
      if (!this.worldObj.isRemote) {
         if (movingObject.entityHit != null) {
            boolean bl = movingObject.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F);
            if (bl) {
               this.applyEnchantments(this.shootingEntity, movingObject.entityHit);
               if (!movingObject.entityHit.isImmuneToFire()) {
                  movingObject.entityHit.setFire(5);
               }
            }
         } else {
            boolean bl2 = true;
            if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving) {
               bl2 = this.worldObj.getGameRules().getBoolean("mobGriefing");
            }

            if (bl2) {
               BlockPos blockPos = movingObject.getBlockPos().offset(movingObject.sideHit);
               if (this.worldObj.isAirBlock(blockPos)) {
                  this.worldObj.setBlockState(blockPos, Blocks.fire.getDefaultState());
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
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }
}
