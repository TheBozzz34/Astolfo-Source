package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBoat extends Item {
   private static final long a = 129840843557085L;
   private static final long b = -5025391832612208550L;

   public ItemBoat() {
      this.maxStackSize = 1;
      this.setCreativeTab(CreativeTabs.tabTransport);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      float var7 = 1.0F;
      float var8 = var3.prevRotationPitch + (var3.rotationPitch - var3.prevRotationPitch) * var7;
      float var9 = var3.prevRotationYaw + (var3.rotationYaw - var3.prevRotationYaw) * var7;
      double var10 = var3.prevPosX + (var3.posX - var3.prevPosX) * var7;
      double var12 = var3.prevPosY + (var3.posY - var3.prevPosY) * var7 + var3.getEyeHeight();
      double var14 = var3.prevPosZ + (var3.posZ - var3.prevPosZ) * var7;
      Vec3 var16 = new Vec3(var10, var12, var14);
      float var17 = MathHelper.cos(-var9 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var18 = MathHelper.sin(-var9 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var19 = -MathHelper.cos(-var8 * (float) (Math.PI / 180.0));
      float var20 = MathHelper.sin(-var8 * (float) (Math.PI / 180.0));
      float var21 = var18 * var19;
      float var22 = var17 * var19;
      double var23 = 5.0;
      Vec3 var25 = var16.addVector(var21 * var23, var20 * var23, var22 * var23);
      MovingObjectPosition var26 = var2.rayTraceBlocks(var16, var25, true);
      if (var26 == null) {
         return var1;
      }

      Vec3 var27 = var3.getLook(var7);
      boolean var28 = false;
      float var29 = 1.0F;
      List var30 = var2.getEntitiesWithinAABBExcludingEntity(
         var3, var3.getEntityBoundingBox().addCoord(var27.xCoord * var23, var27.yCoord * var23, var27.zCoord * var23).expand(var29, var29, var29)
      );

      for (int var31 = 0; var31 < var30.size(); var31++) {
         Entity var32 = (Entity)var30.get(var31);
         if (var32.canBeCollidedWith()) {
            float var33 = var32.getCollisionBorderSize();
            AxisAlignedBB var34 = var32.getEntityBoundingBox().expand(var33, var33, var33);
            if (var34.isVecInside(var16)) {
               var28 = true;
            }
         }
      }

      if (var28) {
         return var1;
      }

      if (var26.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         BlockPos var35 = var26.getBlockPos();
         if (var2.getBlockState(var35).getBlock() == Blocks.snow_layer) {
            var35 = var35.down();
         }

         EntityBoat var36 = new EntityBoat(var2, var35.getX() + 0.5F, var35.getY() + 1.0F, var35.getZ() + 0.5F);
         var36.rotationYaw = ((MathHelper.floor_double(var3.rotationYaw * 4.0F / 360.0F + 0.5) & 3) - 1) * (int)b;
         if (!var2.getCollidingBoundingBoxes(var36, var36.getEntityBoundingBox().expand(-0.1, -0.1, -0.1)).isEmpty()) {
            return var1;
         }

         if (!var2.isRemote) {
            var2.spawnEntityInWorld(var36);
         }

         if (!var3.capabilities.isCreativeMode) {
            var1.stackSize--;
         }

         var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      }

      return var1;
   }
}
