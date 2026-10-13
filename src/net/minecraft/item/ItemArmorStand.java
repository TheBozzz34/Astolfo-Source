package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Rotations;
import net.minecraft.world.World;

public class ItemArmorStand extends Item {
   private static final long a = 58506812856611L;
   private static final long f = 8654054442229825546L;

   public ItemArmorStand() {
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 == EnumFacing.DOWN) {
         return false;
      }

      boolean var12 = var3.getBlockState(var4).getBlock().isReplaceable(var3, var4);
      BlockPos var13 = var12 ? var4 : var4.offset(var5);
      if (!var2.canPlayerEdit(var13, var5, var1)) {
         return false;
      }

      BlockPos var14 = var13.up();
      boolean var15 = !var3.isAirBlock(var13) && !var3.getBlockState(var13).getBlock().isReplaceable(var3, var13);
      var15 |= !var3.isAirBlock(var14) && !var3.getBlockState(var14).getBlock().isReplaceable(var3, var14);
      if (var15) {
         return false;
      }

      double var16 = var13.getX();
      double var18 = var13.getY();
      double var20 = var13.getZ();
      List var22 = var3.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.fromBounds(var16, var18, var20, var16 + 1.0, var18 + 2.0, var20 + 1.0));
      if (var22.size() > 0) {
         return false;
      }

      if (!var3.isRemote) {
         var3.setBlockToAir(var13);
         var3.setBlockToAir(var14);
         EntityArmorStand var23 = new EntityArmorStand(var3, var16 + 0.5, var18, var20 + 0.5);
         float var24 = MathHelper.floor_float((MathHelper.wrapAngleTo180_float(var2.rotationYaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
         var23.setLocationAndAngles(var16 + 0.5, var18, var20 + 0.5, var24, 0.0F);
         this.applyRandomRotations(var23, var3.rand);
         NBTTagCompound var25 = var1.getTagCompound();
         if (var25 != null && var25.hasKey("EntityTag", (int)f)) {
            NBTTagCompound var26 = new NBTTagCompound();
            var23.writeToNBTOptional(var26);
            var26.merge(var25.getCompoundTag("EntityTag"));
            var23.readFromNBT(var26);
         }

         var3.spawnEntityInWorld(var23);
      }

      var1.stackSize--;
      return true;
   }

   private void applyRandomRotations(EntityArmorStand var1, Random var2) {
      Rotations var3 = var1.getHeadRotation();
      float var4 = var2.nextFloat() * 5.0F;
      float var5 = var2.nextFloat() * 20.0F - 10.0F;
      Rotations var6 = new Rotations(var3.getX() + var4, var3.getY() + var5, var3.getZ());
      var1.setHeadRotation(var6);
      var3 = var1.getBodyRotation();
      var4 = var2.nextFloat() * 10.0F - 5.0F;
      var6 = new Rotations(var3.getX(), var3.getY() + var4, var3.getZ());
      var1.setBodyRotation(var6);
   }
}
