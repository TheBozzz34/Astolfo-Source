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
   public ItemBoat() {
      this.maxStackSize = 1;
      this.setCreativeTab(CreativeTabs.tabTransport);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      float f = 1.0F;
      float g = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * f;
      float h = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * f;
      double d = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * f;
      double e = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * f + playerIn.getEyeHeight();
      double d2 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * f;
      Vec3 vec3 = new Vec3(d, e, d2);
      float f2 = MathHelper.cos(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = MathHelper.sin(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -MathHelper.cos(-g * (float) (Math.PI / 180.0));
      float f5 = MathHelper.sin(-g * (float) (Math.PI / 180.0));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d3 = 5.0;
      Vec3 vec32 = vec3.addVector(f6 * d3, f5 * d3, f7 * d3);
      MovingObjectPosition movingObjectPosition = worldIn.rayTraceBlocks(vec3, vec32, true);
      if (movingObjectPosition == null) {
         return itemStackIn;
      }

      Vec3 vec33 = playerIn.getLook(f);
      boolean bl = false;
      float f8 = 1.0F;
      List list = worldIn.getEntitiesWithinAABBExcludingEntity(
         playerIn, playerIn.getEntityBoundingBox().addCoord(vec33.xCoord * d3, vec33.yCoord * d3, vec33.zCoord * d3).expand(f8, f8, f8)
      );

      for (int i = 0; i < list.size(); i++) {
         Entity entity = (Entity)list.get(i);
         if (entity.canBeCollidedWith()) {
            float f9 = entity.getCollisionBorderSize();
            AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().expand(f9, f9, f9);
            if (axisAlignedBB.isVecInside(vec3)) {
               bl = true;
            }
         }
      }

      if (bl) {
         return itemStackIn;
      }

      if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         BlockPos blockPos = movingObjectPosition.getBlockPos();
         if (worldIn.getBlockState(blockPos).getBlock() == Blocks.snow_layer) {
            blockPos = blockPos.down();
         }

         EntityBoat entityBoat = new EntityBoat(worldIn, blockPos.getX() + 0.5F, blockPos.getY() + 1.0F, blockPos.getZ() + 0.5F);
         entityBoat.rotationYaw = ((MathHelper.floor_double(playerIn.rotationYaw * 4.0F / 360.0F + 0.5) & 3) - 1) * 90;
         if (!worldIn.getCollidingBoundingBoxes(entityBoat, entityBoat.getEntityBoundingBox().expand(-0.1, -0.1, -0.1)).isEmpty()) {
            return itemStackIn;
         }

         if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(entityBoat);
         }

         if (!playerIn.capabilities.isCreativeMode) {
            itemStackIn.stackSize--;
         }

         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      }

      return itemStackIn;
   }
}
