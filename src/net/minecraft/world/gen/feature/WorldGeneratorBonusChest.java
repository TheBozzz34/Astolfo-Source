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

   public WorldGeneratorBonusChest(List<WeightedRandomChestContent> list, int i) {
      this.chestItems = list;
      this.itemsToGenerateInBonusChest = i;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      Block block;
      while (
         ((block = worldIn.getBlockState(position).getBlock()).getMaterial() == Material.air || block.getMaterial() == Material.leaves) && position.getY() > 1
      ) {
         position = position.down();
      }

      if (position.getY() < 1) {
         return false;
      }

      position = position.up();

      for (int i = 0; i < 4; i++) {
         BlockPos blockPos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(3) - rand.nextInt(3), rand.nextInt(4) - rand.nextInt(4));
         if (worldIn.isAirBlock(blockPos) && World.doesBlockHaveSolidTopSurface(worldIn, blockPos.down())) {
            worldIn.setBlockState(blockPos, Blocks.chest.getDefaultState(), 2);
            TileEntity tileEntity = worldIn.getTileEntity(blockPos);
            if (tileEntity instanceof TileEntityChest) {
               WeightedRandomChestContent.generateChestContents(rand, this.chestItems, (TileEntityChest)tileEntity, this.itemsToGenerateInBonusChest);
            }

            BlockPos blockPos2 = blockPos.east();
            BlockPos blockPos3 = blockPos.west();
            BlockPos blockPos4 = blockPos.north();
            BlockPos blockPos5 = blockPos.south();
            if (worldIn.isAirBlock(blockPos3) && World.doesBlockHaveSolidTopSurface(worldIn, blockPos3.down())) {
               worldIn.setBlockState(blockPos3, Blocks.torch.getDefaultState(), 2);
            }

            if (worldIn.isAirBlock(blockPos2) && World.doesBlockHaveSolidTopSurface(worldIn, blockPos2.down())) {
               worldIn.setBlockState(blockPos2, Blocks.torch.getDefaultState(), 2);
            }

            if (worldIn.isAirBlock(blockPos4) && World.doesBlockHaveSolidTopSurface(worldIn, blockPos4.down())) {
               worldIn.setBlockState(blockPos4, Blocks.torch.getDefaultState(), 2);
            }

            if (worldIn.isAirBlock(blockPos5) && World.doesBlockHaveSolidTopSurface(worldIn, blockPos5.down())) {
               worldIn.setBlockState(blockPos5, Blocks.torch.getDefaultState(), 2);
            }

            return true;
         }
      }

      return false;
   }
}
