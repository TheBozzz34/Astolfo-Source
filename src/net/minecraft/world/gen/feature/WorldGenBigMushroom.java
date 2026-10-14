package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBigMushroom extends WorldGenerator {
   private Block mushroomType;
   private static final long a = 16283217815916L;

   public WorldGenBigMushroom(Block block) {
      super(true);
      this.mushroomType = block;
   }

   public WorldGenBigMushroom() {
      super(false);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (this.mushroomType == null) {
         this.mushroomType = rand.nextBoolean() ? Blocks.brown_mushroom_block : Blocks.red_mushroom_block;
      }

      int i = rand.nextInt(3) + 4;
      boolean bl = true;
      if (position.getY() >= 1 && position.getY() + i + 1 < 256) {
         for (int j = position.getY(); j <= position.getY() + 1 + i; j++) {
            byte b = 3;
            if (j <= position.getY() + 3) {
               b = 0;
            }

            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int k = position.getX() - b; k <= position.getX() + b && bl; k++) {
               for (int l = position.getZ() - b; l <= position.getZ() + b && bl; l++) {
                  if (j >= 0 && j < 256) {
                     Block block = worldIn.getBlockState(mutableBlockPos.set(k, j, l)).getBlock();
                     if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                        bl = false;
                     }
                  } else {
                     bl = false;
                  }
               }
            }
         }

         if (!bl) {
            return false;
         }

         Block block2 = worldIn.getBlockState(position.down()).getBlock();
         if (block2 != Blocks.dirt && block2 != Blocks.grass && block2 != Blocks.mycelium) {
            return false;
         }

         int m = position.getY() + i;
         if (this.mushroomType == Blocks.red_mushroom_block) {
            m = position.getY() + i - 3;
         }

         for (int n = m; n <= position.getY() + i; n++) {
            int i2 = 1;
            if (n < position.getY() + i) {
               i2++;
            }

            if (this.mushroomType == Blocks.brown_mushroom_block) {
               i2 = 3;
            }

            int i3 = position.getX() - i2;
            int i4 = position.getX() + i2;
            int i5 = position.getZ() - i2;
            int i6 = position.getZ() + i2;

            for (int i7 = i3; i7 <= i4; i7++) {
               for (int i8 = i5; i8 <= i6; i8++) {
                  int i9 = 5;
                  if (i7 == i3) {
                     i9--;
                  } else if (i7 == i4) {
                     i9++;
                  }

                  if (i8 == i5) {
                     i9 -= 3;
                  } else if (i8 == i6) {
                     i9 += 3;
                  }

                  BlockHugeMushroom.EnumType enumType = BlockHugeMushroom.EnumType.byMetadata(i9);
                  if (this.mushroomType == Blocks.brown_mushroom_block || n < position.getY() + i) {
                     if ((i7 == i3 || i7 == i4) && (i8 == i5 || i8 == i6)) {
                        continue;
                     }

                     if (i7 == position.getX() - (i2 - 1) && i8 == i5) {
                        enumType = BlockHugeMushroom.EnumType.NORTH_WEST;
                     }

                     if (i7 == i3 && i8 == position.getZ() - (i2 - 1)) {
                        enumType = BlockHugeMushroom.EnumType.NORTH_WEST;
                     }

                     if (i7 == position.getX() + (i2 - 1) && i8 == i5) {
                        enumType = BlockHugeMushroom.EnumType.NORTH_EAST;
                     }

                     if (i7 == i4 && i8 == position.getZ() - (i2 - 1)) {
                        enumType = BlockHugeMushroom.EnumType.NORTH_EAST;
                     }

                     if (i7 == position.getX() - (i2 - 1) && i8 == i6) {
                        enumType = BlockHugeMushroom.EnumType.SOUTH_WEST;
                     }

                     if (i7 == i3 && i8 == position.getZ() + (i2 - 1)) {
                        enumType = BlockHugeMushroom.EnumType.SOUTH_WEST;
                     }

                     if (i7 == position.getX() + (i2 - 1) && i8 == i6) {
                        enumType = BlockHugeMushroom.EnumType.SOUTH_EAST;
                     }

                     if (i7 == i4 && i8 == position.getZ() + (i2 - 1)) {
                        enumType = BlockHugeMushroom.EnumType.SOUTH_EAST;
                     }
                  }

                  if (enumType == BlockHugeMushroom.EnumType.CENTER && n < position.getY() + i) {
                     enumType = BlockHugeMushroom.EnumType.ALL_INSIDE;
                  }

                  if (position.getY() >= position.getY() + i - 1 || enumType != BlockHugeMushroom.EnumType.ALL_INSIDE) {
                     BlockPos blockPos = new BlockPos(i7, n, i8);
                     if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                        this.setBlockAndNotifyAdequately(worldIn, blockPos, this.mushroomType.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, enumType));
                     }
                  }
               }
            }
         }

         for (int i10 = 0; i10 < i; i10++) {
            Block block3 = worldIn.getBlockState(position.up(i10)).getBlock();
            if (!block3.isFullBlock()) {
               this.setBlockAndNotifyAdequately(
                  worldIn, position.up(i10), this.mushroomType.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.STEM)
               );
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
