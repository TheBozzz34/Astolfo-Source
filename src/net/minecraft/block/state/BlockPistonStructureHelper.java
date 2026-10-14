package net.minecraft.block.state;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockPistonStructureHelper {
   private final World world;
   private final BlockPos pistonPos;
   private final BlockPos blockToMove;
   private final EnumFacing moveDirection;
   private final List<BlockPos> toMove = Lists.newArrayList();
   private final List<BlockPos> toDestroy = Lists.newArrayList();

   public BlockPistonStructureHelper(World worldIn, BlockPos posIn, EnumFacing pistonFacing, boolean extending) {
      this.world = worldIn;
      this.pistonPos = posIn;
      if (extending) {
         this.moveDirection = pistonFacing;
         this.blockToMove = posIn.offset(pistonFacing);
      } else {
         this.moveDirection = pistonFacing.getOpposite();
         this.blockToMove = posIn.offset(pistonFacing, 2);
      }
   }

   public boolean canMove() {
      this.toMove.clear();
      this.toDestroy.clear();
      Block block = this.world.getBlockState(this.blockToMove).getBlock();
      if (!BlockPistonBase.canPush(block, this.world, this.blockToMove, this.moveDirection, false)) {
         if (block.getMobilityFlag() != 1) {
            return false;
         }

         this.toDestroy.add(this.blockToMove);
         return true;
      } else {
         if (!this.func_177251_a(this.blockToMove)) {
            return false;
         }

         for (int i = 0; i < this.toMove.size(); i++) {
            BlockPos blockPos = this.toMove.get(i);
            if (this.world.getBlockState(blockPos).getBlock() == Blocks.slime_block && !this.func_177250_b(blockPos)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean func_177251_a(BlockPos origin) {
      Block block = this.world.getBlockState(origin).getBlock();
      if (block.getMaterial() == Material.air) {
         return true;
      }

      if (!BlockPistonBase.canPush(block, this.world, origin, this.moveDirection, false)) {
         return true;
      }

      if (origin.equals(this.pistonPos)) {
         return true;
      }

      if (this.toMove.contains(origin)) {
         return true;
      }

      int i = 1;
      if (i + this.toMove.size() > 12) {
         return false;
      }

      while (block == Blocks.slime_block) {
         BlockPos blockPos = origin.offset(this.moveDirection.getOpposite(), i);
         block = this.world.getBlockState(blockPos).getBlock();
         if (block.getMaterial() == Material.air || !BlockPistonBase.canPush(block, this.world, blockPos, this.moveDirection, false) || blockPos.equals(this.pistonPos)) {
            break;
         }

         if (++i + this.toMove.size() > 12) {
            return false;
         }
      }

      int j = 0;

      for (int k = i - 1; k >= 0; k--) {
         this.toMove.add(origin.offset(this.moveDirection.getOpposite(), k));
         j++;
      }

      int l = 1;

      while (true) {
         BlockPos blockPos2 = origin.offset(this.moveDirection, l);
         int m = this.toMove.indexOf(blockPos2);
         if (m > -1) {
            this.func_177255_a(j, m);

            for (int n = 0; n <= m + j; n++) {
               BlockPos blockPos3 = this.toMove.get(n);
               if (this.world.getBlockState(blockPos3).getBlock() == Blocks.slime_block && !this.func_177250_b(blockPos3)) {
                  return false;
               }
            }

            return true;
         }

         block = this.world.getBlockState(blockPos2).getBlock();
         if (block.getMaterial() == Material.air) {
            return true;
         }

         if (!BlockPistonBase.canPush(block, this.world, blockPos2, this.moveDirection, true) || blockPos2.equals(this.pistonPos)) {
            return false;
         }

         if (block.getMobilityFlag() == 1) {
            this.toDestroy.add(blockPos2);
            return true;
         }

         if (this.toMove.size() >= 12) {
            return false;
         }

         this.toMove.add(blockPos2);
         j++;
         l++;
      }
   }

   private void func_177255_a(int i, int j) {
      ArrayList arrayList = Lists.newArrayList();
      ArrayList arrayList2 = Lists.newArrayList();
      ArrayList arrayList3 = Lists.newArrayList();
      arrayList.addAll(this.toMove.subList(0, j));
      arrayList2.addAll(this.toMove.subList(this.toMove.size() - i, this.toMove.size()));
      arrayList3.addAll(this.toMove.subList(j, this.toMove.size() - i));
      this.toMove.clear();
      this.toMove.addAll(arrayList);
      this.toMove.addAll(arrayList2);
      this.toMove.addAll(arrayList3);
   }

   private boolean func_177250_b(BlockPos blockPos) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (enumFacing.getAxis() != this.moveDirection.getAxis() && !this.func_177251_a(blockPos.offset(enumFacing))) {
            return false;
         }
      }

      return true;
   }

   public List<BlockPos> getBlocksToMove() {
      return this.toMove;
   }

   public List<BlockPos> getBlocksToDestroy() {
      return this.toDestroy;
   }
}
