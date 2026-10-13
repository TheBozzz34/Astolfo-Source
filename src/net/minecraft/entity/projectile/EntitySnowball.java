package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySnowball extends EntityThrowable {
   private static final long ab = 47578136270699L;
   private static final long gb = 5204993041214996488L;

   public EntitySnowball(World var1) {
      super(var1);
   }

   public EntitySnowball(World var1, EntityLivingBase var2) {
      super(var1, var2);
   }

   public EntitySnowball(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public void onImpact(MovingObjectPosition var1) {
      if (var1.entityHit != null) {
         byte var5 = 0;
         if (var1.entityHit instanceof EntityBlaze) {
            var5 = 3;
         }

         var1.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), var5);
      }

      for (int var6 = 0; var6 < (int)gb; var6++) {
         this.worldObj.spawnParticle(EnumParticleTypes.SNOWBALL, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
      }

      if (!this.worldObj.isRemote) {
         this.setDead();
      }
   }
}
