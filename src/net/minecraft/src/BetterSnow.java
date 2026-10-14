package net.minecraft.src;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BetterSnow {
   private static IBakedModel u = null;
   private static final long a = 98126046977800L;

   public static void R() {
      u = Config.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(Blocks.snow_layer.getDefaultState());
   }

   public static IBakedModel r() {
      return u;
   }

   public static IBlockState V() {
      return Blocks.snow_layer.getDefaultState();
   }

   public static boolean V(IBlockAccess iBlockAccess, Block block, IBlockState iBlockState, BlockPos blockPos) {
      return !E(block, iBlockState) ? false : v(iBlockAccess, blockPos);
   }

   private static boolean v(IBlockAccess iBlockAccess, BlockPos blockPos) {
      Block block = Blocks.snow_layer;
      return iBlockAccess.getBlockState(blockPos.north()).getBlock() != block
            && iBlockAccess.getBlockState(blockPos.south()).getBlock() != block
            && iBlockAccess.getBlockState(blockPos.west()).getBlock() != block
            && iBlockAccess.getBlockState(blockPos.east()).getBlock() != block
         ? false
         : iBlockAccess.getBlockState(blockPos.down()).getBlock().isOpaqueCube();
   }

   private static boolean E(Block block, IBlockState iBlockState) {
      if (block.isFullCube()) {
         return false;
      }

      if (block.isOpaqueCube()) {
         return false;
      }

      if (block instanceof BlockSnow) {
         return false;
      }

      if (!(block instanceof BlockBush)
         || !(block instanceof BlockDoublePlant)
            && !(block instanceof BlockFlower)
            && !(block instanceof BlockMushroom)
            && !(block instanceof BlockSapling)
            && !(block instanceof BlockTallGrass)) {
         if (block instanceof BlockFence
            || block instanceof BlockFenceGate
            || block instanceof BlockFlowerPot
            || block instanceof BlockPane
            || block instanceof BlockReed
            || block instanceof BlockWall) {
            return true;
         }

         if (block instanceof BlockRedstoneTorch && iBlockState.getValue(BlockTorch.FACING) == EnumFacing.UP) {
            return true;
         }

         if (block instanceof BlockLever) {
            Comparable comparable = iBlockState.getValue(BlockLever.FACING);
            if (comparable == BlockLever.EnumOrientation.UP_X || comparable == BlockLever.EnumOrientation.UP_Z) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }
}
