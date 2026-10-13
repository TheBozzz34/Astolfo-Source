package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGeneratorBonusChest extends WorldGenerator {
   private final List<WeightedRandomChestContent> chestItems;
   private final int itemsToGenerateInBonusChest;
   private static final long a = 105766673501289L;

   public WorldGeneratorBonusChest(List<WeightedRandomChestContent> var1, int var2) {
      this.chestItems = var1;
      this.itemsToGenerateInBonusChest = var2;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      Block var7;
      while (((var7 = var1.getBlockState(var3).getBlock()).getMaterial() == Material.air || var7.getMaterial() == Material.leaves) && var3.getY() > 1) {
         var3 = var3.down();
      }

      if (var3.getY() < 1) {
         return false;
      }

      var3 = var3.up();

      for (int var8 = 0; var8 < 4; var8++) {
         BlockPos var9 = var3.add(var2.nextInt(4) - var2.nextInt(4), var2.nextInt(3) - var2.nextInt(3), var2.nextInt(4) - var2.nextInt(4));
         if (var1.isAirBlock(var9) && World.doesBlockHaveSolidTopSurface(var1, var9.down())) {
            var1.setBlockState(var9, Blocks.chest.getDefaultState(), 2);
            TileEntity var10 = var1.getTileEntity(var9);
            if (var10 instanceof TileEntityChest) {
               WeightedRandomChestContent.generateChestContents(var2, this.chestItems, (TileEntityChest)var10, this.itemsToGenerateInBonusChest);
            }

            BlockPos var11 = var9.east();
            BlockPos var12 = var9.west();
            BlockPos var13 = var9.north();
            BlockPos var14 = var9.south();
            if (var1.isAirBlock(var12) && World.doesBlockHaveSolidTopSurface(var1, var12.down())) {
               var1.setBlockState(var12, Blocks.torch.getDefaultState(), 2);
            }

            if (var1.isAirBlock(var11) && World.doesBlockHaveSolidTopSurface(var1, var11.down())) {
               var1.setBlockState(var11, Blocks.torch.getDefaultState(), 2);
            }

            if (var1.isAirBlock(var13) && World.doesBlockHaveSolidTopSurface(var1, var13.down())) {
               var1.setBlockState(var13, Blocks.torch.getDefaultState(), 2);
            }

            if (var1.isAirBlock(var14) && World.doesBlockHaveSolidTopSurface(var1, var14.down())) {
               var1.setBlockState(var14, Blocks.torch.getDefaultState(), 2);
            }

            return true;
         }
      }

      return false;
   }
}
