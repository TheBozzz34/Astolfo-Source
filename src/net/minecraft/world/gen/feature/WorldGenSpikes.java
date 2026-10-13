package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator {
   private Block baseBlockRequired;
   private static final long a = 27259305234694L;

   public WorldGenSpikes(Block var1) {
      this.baseBlockRequired = var1;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      if (var1.isAirBlock(var3) && var1.getBlockState(var3.down()).getBlock() == this.baseBlockRequired) {
         int var7 = var2.nextInt(32) + 6;
         int var8 = var2.nextInt(4) + 1;
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

         for (int var10 = var3.getX() - var8; var10 <= var3.getX() + var8; var10++) {
            for (int var11 = var3.getZ() - var8; var11 <= var3.getZ() + var8; var11++) {
               int var12 = var10 - var3.getX();
               int var13 = var11 - var3.getZ();
               if (var12 * var12 + var13 * var13 <= var8 * var8 + 1
                  && var1.getBlockState(var9.set(var10, var3.getY() - 1, var11)).getBlock() != this.baseBlockRequired) {
                  return false;
               }
            }
         }

         for (int var15 = var3.getY(); var15 < var3.getY() + var7 && var15 < 256; var15++) {
            for (int var17 = var3.getX() - var8; var17 <= var3.getX() + var8; var17++) {
               for (int var18 = var3.getZ() - var8; var18 <= var3.getZ() + var8; var18++) {
                  int var19 = var17 - var3.getX();
                  int var14 = var18 - var3.getZ();
                  if (var19 * var19 + var14 * var14 <= var8 * var8 + 1) {
                     var1.setBlockState(new BlockPos(var17, var15, var18), Blocks.obsidian.getDefaultState(), 2);
                  }
               }
            }
         }

         EntityEnderCrystal var16 = new EntityEnderCrystal(var1);
         var16.setLocationAndAngles(var3.getX() + 0.5F, var3.getY() + var7, var3.getZ() + 0.5F, var2.nextFloat() * 360.0F, 0.0F);
         var1.spawnEntityInWorld(var16);
         var1.setBlockState(var3.up(var7), Blocks.bedrock.getDefaultState(), 2);
         return true;
      } else {
         return false;
      }
   }
}
