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

   public WorldGenBigMushroom(Block var1) {
      super(true);
      this.mushroomType = var1;
   }

   public WorldGenBigMushroom() {
      super(false);
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      if (this.mushroomType == null) {
         this.mushroomType = var2.nextBoolean() ? Blocks.brown_mushroom_block : Blocks.red_mushroom_block;
      }

      int var7 = var2.nextInt(3) + 4;
      boolean var8 = true;
      if (var3.getY() >= 1 && var3.getY() + var7 + 1 < 256) {
         for (int var9 = var3.getY(); var9 <= var3.getY() + 1 + var7; var9++) {
            byte var10 = 3;
            if (var9 <= var3.getY() + 3) {
               var10 = 0;
            }

            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

            for (int var12 = var3.getX() - var10; var12 <= var3.getX() + var10 && var8; var12++) {
               for (int var13 = var3.getZ() - var10; var13 <= var3.getZ() + var10 && var8; var13++) {
                  if (var9 >= 0 && var9 < 256) {
                     Block var14 = var1.getBlockState(var11.set(var12, var9, var13)).getBlock();
                     if (var14.getMaterial() != Material.air && var14.getMaterial() != Material.leaves) {
                        var8 = false;
                     }
                  } else {
                     var8 = false;
                  }
               }
            }
         }

         if (!var8) {
            return false;
         }

         Block var22 = var1.getBlockState(var3.down()).getBlock();
         if (var22 != Blocks.dirt && var22 != Blocks.grass && var22 != Blocks.mycelium) {
            return false;
         }

         int var23 = var3.getY() + var7;
         if (this.mushroomType == Blocks.red_mushroom_block) {
            var23 = var3.getY() + var7 - 3;
         }

         for (int var24 = var23; var24 <= var3.getY() + var7; var24++) {
            int var26 = 1;
            if (var24 < var3.getY() + var7) {
               var26++;
            }

            if (this.mushroomType == Blocks.brown_mushroom_block) {
               var26 = 3;
            }

            int var28 = var3.getX() - var26;
            int var29 = var3.getX() + var26;
            int var15 = var3.getZ() - var26;
            int var16 = var3.getZ() + var26;

            for (int var17 = var28; var17 <= var29; var17++) {
               for (int var18 = var15; var18 <= var16; var18++) {
                  int var19 = 5;
                  if (var17 == var28) {
                     var19--;
                  } else if (var17 == var29) {
                     var19++;
                  }

                  if (var18 == var15) {
                     var19 -= 3;
                  } else if (var18 == var16) {
                     var19 += 3;
                  }

                  BlockHugeMushroom.EnumType var20 = BlockHugeMushroom.EnumType.byMetadata(var19);
                  if (this.mushroomType == Blocks.brown_mushroom_block || var24 < var3.getY() + var7) {
                     if ((var17 == var28 || var17 == var29) && (var18 == var15 || var18 == var16)) {
                        continue;
                     }

                     if (var17 == var3.getX() - (var26 - 1) && var18 == var15) {
                        var20 = BlockHugeMushroom.EnumType.NORTH_WEST;
                     }

                     if (var17 == var28 && var18 == var3.getZ() - (var26 - 1)) {
                        var20 = BlockHugeMushroom.EnumType.NORTH_WEST;
                     }

                     if (var17 == var3.getX() + (var26 - 1) && var18 == var15) {
                        var20 = BlockHugeMushroom.EnumType.NORTH_EAST;
                     }

                     if (var17 == var29 && var18 == var3.getZ() - (var26 - 1)) {
                        var20 = BlockHugeMushroom.EnumType.NORTH_EAST;
                     }

                     if (var17 == var3.getX() - (var26 - 1) && var18 == var16) {
                        var20 = BlockHugeMushroom.EnumType.SOUTH_WEST;
                     }

                     if (var17 == var28 && var18 == var3.getZ() + (var26 - 1)) {
                        var20 = BlockHugeMushroom.EnumType.SOUTH_WEST;
                     }

                     if (var17 == var3.getX() + (var26 - 1) && var18 == var16) {
                        var20 = BlockHugeMushroom.EnumType.SOUTH_EAST;
                     }

                     if (var17 == var29 && var18 == var3.getZ() + (var26 - 1)) {
                        var20 = BlockHugeMushroom.EnumType.SOUTH_EAST;
                     }
                  }

                  if (var20 == BlockHugeMushroom.EnumType.CENTER && var24 < var3.getY() + var7) {
                     var20 = BlockHugeMushroom.EnumType.ALL_INSIDE;
                  }

                  if (var3.getY() >= var3.getY() + var7 - 1 || var20 != BlockHugeMushroom.EnumType.ALL_INSIDE) {
                     BlockPos var21 = new BlockPos(var17, var24, var18);
                     if (!var1.getBlockState(var21).getBlock().isFullBlock()) {
                        this.setBlockAndNotifyAdequately(var1, var21, this.mushroomType.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, var20));
                     }
                  }
               }
            }
         }

         for (int var25 = 0; var25 < var7; var25++) {
            Block var27 = var1.getBlockState(var3.up(var25)).getBlock();
            if (!var27.isFullBlock()) {
               this.setBlockAndNotifyAdequately(
                  var1, var3.up(var25), this.mushroomType.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.STEM)
               );
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
