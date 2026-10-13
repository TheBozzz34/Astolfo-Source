package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable {
   private static final long ab = 85090333958056L;
   private static final long gb = 4754797580020352978L;

   public EntityExpBottle(World var1) {
      super(var1);
   }

   public EntityExpBottle(World var1, EntityLivingBase var2) {
      super(var1, var2);
   }

   public EntityExpBottle(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public float getGravityVelocity() {
      return 0.07F;
   }

   @Override
   public float getVelocity() {
      return 0.7F;
   }

   @Override
   public float getInaccuracy() {
      return -20.0F;
   }

   @Override
   public void onImpact(MovingObjectPosition var1) {
      if (!this.worldObj.isRemote) {
         this.worldObj.playAuxSFX((int)gb, new BlockPos(this), 0);
         int var5 = 3 + this.worldObj.rand.nextInt(5) + this.worldObj.rand.nextInt(5);

         while (var5 > 0) {
            int var6 = EntityXPOrb.getXPSplit(var5);
            var5 -= var6;
            this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var6));
         }

         this.setDead();
      }
   }
}
