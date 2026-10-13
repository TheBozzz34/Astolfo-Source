package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart {
   private static final long ab = 75805242391353L;
   private static final long cb = -6097683783027261430L;

   public EntityMinecartEmpty(World var1) {
      super(var1);
   }

   public EntityMinecartEmpty(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public boolean interactFirst(EntityPlayer var1) {
      if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != var1) {
         return true;
      }

      if (this.riddenByEntity != null && this.riddenByEntity != var1) {
         return false;
      }

      if (!this.worldObj.isRemote) {
         var1.mountEntity(this);
      }

      return true;
   }

   @Override
   public void onActivatorRailPass(int var1, int var2, int var3, boolean var4) {
      if (var4) {
         if (this.riddenByEntity != null) {
            this.riddenByEntity.mountEntity((Entity)null);
         }

         if (this.getRollingAmplitude() == 0) {
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude((int)cb);
            this.setDamage(50.0F);
            this.setBeenAttacked();
         }
      }
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.RIDEABLE;
   }
}
