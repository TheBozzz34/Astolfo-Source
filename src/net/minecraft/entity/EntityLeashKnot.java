package net.minecraft.entity;

import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging {
   private static final long bb = 94639768984417L;

   public EntityLeashKnot(World var1) {
      super(var1);
   }

   public EntityLeashKnot(World var1, BlockPos var2) {
      super(var1, var2);
      this.setPosition(var2.getX() + 0.5, var2.getY() + 0.5, var2.getZ() + 0.5);
      this.setEntityBoundingBox(
         new AxisAlignedBB(this.posX - 0.1875, this.posY - 0.25 + 0.125, this.posZ - 0.1875, this.posX + 0.1875, this.posY + 0.25 + 0.125, this.posZ + 0.1875)
      );
   }

   @Override
   public void entityInit() {
      super.entityInit();
   }

   @Override
   public void updateFacingWithBoundingBox(EnumFacing var1) {
   }

   @Override
   public int getWidthPixels() {
      return 9;
   }

   @Override
   public int getHeightPixels() {
      return 9;
   }

   @Override
   public float getEyeHeight() {
      return -0.0625F;
   }

   @Override
   public boolean isInRangeToRenderDist(double var1) {
      return var1 < 1024.0;
   }

   @Override
   public void onBroken(Entity var1) {
   }

   @Override
   public boolean writeToNBTOptional(NBTTagCompound var1) {
      return false;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
   }

   @Override
   public boolean interactFirst(EntityPlayer var1) {
      ItemStack var5 = var1.getHeldItem();
      boolean var6 = false;
      if (var5 != null && var5.getItem() == Items.lead && !this.worldObj.isRemote) {
         double var7 = 7.0;

         for (EntityLiving var10 : this.worldObj
            .getEntitiesWithinAABB(
               EntityLiving.class,
               new AxisAlignedBB(this.posX - var7, this.posY - var7, this.posZ - var7, this.posX + var7, this.posY + var7, this.posZ + var7)
            )) {
            if (var10.getLeashed() && var10.getLeashedToEntity() == var1) {
               var10.setLeashedToEntity(this, true);
               var6 = true;
            }
         }
      }

      if (!this.worldObj.isRemote && !var6) {
         this.setDead();
         if (var1.capabilities.isCreativeMode) {
            double var11 = 7.0;

            for (EntityLiving var13 : this.worldObj
               .getEntitiesWithinAABB(
                  EntityLiving.class,
                  new AxisAlignedBB(this.posX - var11, this.posY - var11, this.posZ - var11, this.posX + var11, this.posY + var11, this.posZ + var11)
               )) {
               if (var13.getLeashed() && var13.getLeashedToEntity() == this) {
                  var13.clearLeashed(true, false);
               }
            }
         }
      }

      return true;
   }

   @Override
   public boolean onValidSurface() {
      return this.worldObj.getBlockState(this.hangingPosition).getBlock() instanceof BlockFence;
   }

   public static EntityLeashKnot createKnot(World var0, BlockPos var1) {
      EntityLeashKnot var2 = new EntityLeashKnot(var0, var1);
      var2.forceSpawn = true;
      var0.spawnEntityInWorld(var2);
      return var2;
   }

   public static EntityLeashKnot getKnotForPosition(World var0, BlockPos var1) {
      int var5 = var1.getX();
      int var6 = var1.getY();
      int var7 = var1.getZ();

      for (EntityLeashKnot var9 : var0.getEntitiesWithinAABB(
         EntityLeashKnot.class, new AxisAlignedBB(var5 - 1.0, var6 - 1.0, var7 - 1.0, var5 + 1.0, var6 + 1.0, var7 + 1.0)
      )) {
         if (var9.getHangingPosition().equals(var1)) {
            return var9;
         }
      }

      return null;
   }
}
