package net.minecraft.item;

import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemEnderEye extends Item {
   private static final long a = 101523873154701L;

   public ItemEnderEye() {
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      long var9 = a ^ 92882501463191L;
      IBlockState var12 = var3.getBlockState(var4);
      if (!var2.canPlayerEdit(var4.offset(var5), var5, var1) || var12.getBlock() != Blocks.end_portal_frame || var12.getValue(BlockEndPortalFrame.EYE)) {
         return false;
      }

      if (var3.isRemote) {
         return true;
      }

      var3.setBlockState(var4, var12.withProperty(BlockEndPortalFrame.EYE, true), 2);
      var3.updateComparatorOutputLevel(var4, Blocks.end_portal_frame);
      var1.stackSize--;

      for (int var13 = 0; var13 < 16; var13++) {
         double var14 = var4.getX() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F;
         double var16 = var4.getY() + 0.8125F;
         double var18 = var4.getZ() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F;
         double var20 = 0.0;
         double var22 = 0.0;
         double var24 = 0.0;
         var3.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var14, var16, var18, var20, var22, var24);
      }

      EnumFacing var26 = var12.getValue(BlockEndPortalFrame.FACING);
      int var27 = 0;
      long var10000 = 4635024364000137736L ^ var9;
      int var15 = 0;
      boolean var28 = false;
      boolean var17 = true;
      EnumFacing var29 = var26.rotateY();

      for (int var19 = -2; var19 <= 2; var19++) {
         BlockPos var33 = var4.offset(var29, var19);
         IBlockState var21 = var3.getBlockState(var33);
         if (var21.getBlock() == Blocks.end_portal_frame) {
            if (!var21.getValue(BlockEndPortalFrame.EYE)) {
               var17 = false;
               break;
            }

            var15 = var19;
            if (!var28) {
               var27 = var19;
               var28 = true;
            }
         }
      }

      if (var17 && var15 == var27 + 2) {
         BlockPos var30 = var4.offset(var26, 4);

         for (int var34 = var27; var34 <= var15; var34++) {
            BlockPos var37 = var30.offset(var29, var34);
            IBlockState var40 = var3.getBlockState(var37);
            if (var40.getBlock() != Blocks.end_portal_frame || !var40.getValue(BlockEndPortalFrame.EYE)) {
               var17 = false;
               break;
            }
         }

         for (int var35 = var27 - 1; var35 <= var15 + 1; var35 += 4) {
            var30 = var4.offset(var29, var35);

            for (int var38 = 1; var38 <= 3; var38++) {
               BlockPos var41 = var30.offset(var26, var38);
               IBlockState var23 = var3.getBlockState(var41);
               if (var23.getBlock() != Blocks.end_portal_frame || !var23.getValue(BlockEndPortalFrame.EYE)) {
                  var17 = false;
                  break;
               }
            }
         }

         if (var17) {
            for (int var36 = var27; var36 <= var15; var36++) {
               var30 = var4.offset(var29, var36);

               for (int var39 = 1; var39 <= 3; var39++) {
                  BlockPos var42 = var30.offset(var26, var39);
                  var3.setBlockState(var42, Blocks.end_portal.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      MovingObjectPosition var7 = this.getMovingObjectPositionFromPlayer(var2, var3, false);
      if (var7 != null
         && var7.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
         && var2.getBlockState(var7.getBlockPos()).getBlock() == Blocks.end_portal_frame) {
         return var1;
      }

      if (!var2.isRemote) {
         BlockPos var8 = var2.getStrongholdPos("Stronghold", new BlockPos(var3));
         if (var8 != null) {
            EntityEnderEye var9 = new EntityEnderEye(var2, var3.posX, var3.posY, var3.posZ);
            var9.moveTowards(var8);
            var2.spawnEntityInWorld(var9);
            var2.playSoundAtEntity(var3, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            var2.playAuxSFXAtEntity((EntityPlayer)null, 1002, new BlockPos(var3), 0);
            if (!var3.capabilities.isCreativeMode) {
               var1.stackSize--;
            }

            var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         }
      }

      return var1;
   }
}
