package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenSwamp extends BiomeGenBase {
   private static final long bb = 50904527362148L;

   public BiomeGenSwamp(int var1) {
      super(var1);
      this.theBiomeDecorator.treesPerChunk = 2;
      this.theBiomeDecorator.flowersPerChunk = 1;
      this.theBiomeDecorator.deadBushPerChunk = 1;
      this.theBiomeDecorator.mushroomsPerChunk = 8;
      this.theBiomeDecorator.reedsPerChunk = 10;
      this.theBiomeDecorator.clayPerChunk = 1;
      this.theBiomeDecorator.waterlilyPerChunk = 4;
      this.theBiomeDecorator.sandPerChunk2 = 0;
      this.theBiomeDecorator.sandPerChunk = 0;
      this.theBiomeDecorator.grassPerChunk = 5;
      this.waterColorMultiplier = 14745518;
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 1, 1, 1));
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random var1) {
      return this.worldGeneratorSwamp;
   }

   @Override
   public int getGrassColorAtPos(BlockPos var1) {
      double var5 = GRASS_COLOR_NOISE.func_151601_a(var1.getX() * 0.0225, var1.getZ() * 0.0225);
      return var5 < -0.1 ? 5011004 : 6975545;
   }

   @Override
   public int getFoliageColorAtPos(BlockPos var1) {
      return 6975545;
   }

   @Override
   public BlockFlower.EnumFlowerType pickRandomFlower(Random var1, BlockPos var2) {
      return BlockFlower.EnumFlowerType.BLUE_ORCHID;
   }

   @Override
   public void genTerrainBlocks(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      double var11 = GRASS_COLOR_NOISE.func_151601_a(var4 * 0.25, var5 * 0.25);
      if (var11 > 0.0) {
         int var13 = var4 & 15;
         int var14 = var5 & 15;

         for (int var15 = 255; var15 >= 0; var15--) {
            if (var3.getBlockState(var14, var15, var13).getBlock().getMaterial() != Material.air) {
               if (var15 == 62 && var3.getBlockState(var14, var15, var13).getBlock() != Blocks.water) {
                  var3.setBlockState(var14, var15, var13, Blocks.water.getDefaultState());
                  if (var11 < 0.12) {
                     var3.setBlockState(var14, var15 + 1, var13, Blocks.waterlily.getDefaultState());
                  }
               }
               break;
            }
         }
      }

      this.generateBiomeTerrain(var1, var2, var3, var4, var5, var6);
   }
}
