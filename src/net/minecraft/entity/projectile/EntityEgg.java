package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEgg extends EntityThrowable {
   private static final long ab = 19204106887080L;

   public EntityEgg(World var1) {
      super(var1);
   }

   public EntityEgg(World var1, EntityLivingBase var2) {
      super(var1, var2);
   }

   public EntityEgg(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public void onImpact(MovingObjectPosition var1) {
      if (var1.entityHit != null) {
         var1.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
      }

      if (!this.worldObj.isRemote && this.rand.nextInt(8) == 0) {
         byte var5 = 1;
         if (this.rand.nextInt(32) == 0) {
            var5 = 4;
         }

         for (int var6 = 0; var6 < var5; var6++) {
            EntityChicken var7 = new EntityChicken(this.worldObj);
            var7.setGrowingAge(-24000);
            var7.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            this.worldObj.spawnEntityInWorld(var7);
         }
      }

      double var8 = 0.08;

      for (int var9 = 0; var9 < 8; var9++) {
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.ITEM_CRACK,
               this.posX,
               this.posY,
               this.posZ,
               (this.rand.nextFloat() - 0.5) * 0.08,
               (this.rand.nextFloat() - 0.5) * 0.08,
               (this.rand.nextFloat() - 0.5) * 0.08,
               Item.getIdFromItem(Items.egg)
            );
      }

      if (!this.worldObj.isRemote) {
         this.setDead();
      }
   }
}
