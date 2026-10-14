package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart {
   private static final long ab = 75805242391353L;
   private static final long cb = -6097683783027261430L;

   public EntityMinecartEmpty(World worldIn) {
      super(worldIn);
   }

   public EntityMinecartEmpty(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != playerIn) {
         return true;
      }

      if (this.riddenByEntity != null && this.riddenByEntity != playerIn) {
         return false;
      }

      if (!this.worldObj.isRemote) {
         playerIn.mountEntity(this);
      }

      return true;
   }

   @Override
   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
      if (receivingPower) {
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
