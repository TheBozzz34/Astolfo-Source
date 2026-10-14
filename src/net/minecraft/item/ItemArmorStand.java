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
   public ItemArmorStand() {
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side == EnumFacing.DOWN) {
         return false;
      }

      boolean bl = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
      BlockPos blockPos = bl ? pos : pos.offset(side);
      if (!playerIn.canPlayerEdit(blockPos, side, stack)) {
         return false;
      }

      BlockPos blockPos2 = blockPos.up();
      boolean bl2 = !worldIn.isAirBlock(blockPos) && !worldIn.getBlockState(blockPos).getBlock().isReplaceable(worldIn, blockPos);
      bl2 |= !worldIn.isAirBlock(blockPos2) && !worldIn.getBlockState(blockPos2).getBlock().isReplaceable(worldIn, blockPos2);
      if (bl2) {
         return false;
      }

      double d = blockPos.getX();
      double e = blockPos.getY();
      double d2 = blockPos.getZ();
      List list = worldIn.getEntitiesWithinAABBExcludingEntity(
         (Entity)null, AxisAlignedBB.fromBounds(d, e, d2, d + 1.0, e + 2.0, d2 + 1.0)
      );
      if (list.size() > 0) {
         return false;
      }

      if (!worldIn.isRemote) {
         worldIn.setBlockToAir(blockPos);
         worldIn.setBlockToAir(blockPos2);
         EntityArmorStand entityArmorStand = new EntityArmorStand(worldIn, d + 0.5, e, d2 + 0.5);
         float f = MathHelper.floor_float((MathHelper.wrapAngleTo180_float(playerIn.rotationYaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
         entityArmorStand.setLocationAndAngles(d + 0.5, e, d2 + 0.5, f, 0.0F);
         this.applyRandomRotations(entityArmorStand, worldIn.rand);
         NBTTagCompound nbtTagCompound = stack.getTagCompound();
         if (nbtTagCompound != null && nbtTagCompound.hasKey("EntityTag", 10)) {
            NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
            entityArmorStand.writeToNBTOptional(nbtTagCompound2);
            nbtTagCompound2.merge(nbtTagCompound.getCompoundTag("EntityTag"));
            entityArmorStand.readFromNBT(nbtTagCompound2);
         }

         worldIn.spawnEntityInWorld(entityArmorStand);
      }

      stack.stackSize--;
      return true;
   }

   private void applyRandomRotations(EntityArmorStand armorStand, Random rand) {
      Rotations rotations = armorStand.getHeadRotation();
      float f = rand.nextFloat() * 5.0F;
      float g = rand.nextFloat() * 20.0F - 10.0F;
      Rotations rotations2 = new Rotations(rotations.getX() + f, rotations.getY() + g, rotations.getZ());
      armorStand.setHeadRotation(rotations2);
      rotations = armorStand.getBodyRotation();
      f = rand.nextFloat() * 10.0F - 5.0F;
      rotations2 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
      armorStand.setBodyRotation(rotations2);
   }
}
