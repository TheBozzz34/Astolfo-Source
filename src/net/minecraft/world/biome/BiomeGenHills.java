package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenHills extends BiomeGenBase {
   private WorldGenerator theWorldGenerator = new WorldGenMinable(
      Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.STONE), 9
   );
   private WorldGenTaiga2 field_150634_aD = new WorldGenTaiga2(false);
   private int field_150635_aE = 0;
   private int field_150636_aF = 1;
   private int field_150637_aG = 2;
   private int field_150638_aH = this.field_150635_aE;
   private static final long bb = 36559613349613L;

   public BiomeGenHills(int var1, boolean var2) {
      super(var1);
      if (var2) {
         this.theBiomeDecorator.treesPerChunk = 3;
         this.field_150638_aH = this.field_150636_aF;
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random var1) {
      return var1.nextInt(3) > 0 ? this.field_150634_aD : super.genBigTreeChance(var1);
   }

   @Override
   public void decorate(World var1, Random var2, BlockPos var3) {
      super.decorate(var1, var2, var3);
      int var7 = 3 + var2.nextInt(6);

      for (int var8 = 0; var8 < var7; var8++) {
         int var9 = var2.nextInt(16);
         int var10 = var2.nextInt(28) + 4;
         int var11 = var2.nextInt(16);
         BlockPos var12 = var3.add(var9, var10, var11);
         if (var1.getBlockState(var12).getBlock() == Blocks.stone) {
            var1.setBlockState(var12, Blocks.emerald_ore.getDefaultState(), 2);
         }
      }

      for (int var13 = 0; var13 < 7; var13++) {
         int var14 = var2.nextInt(16);
         int var15 = var2.nextInt(64);
         int var16 = var2.nextInt(16);
         this.theWorldGenerator.generate(var1, var2, var3.add(var14, var15, var16));
      }
   }

   @Override
   public void genTerrainBlocks(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      this.topBlock = Blocks.grass.getDefaultState();
      this.fillerBlock = Blocks.dirt.getDefaultState();
      if ((var6 < -1.0 || var6 > 2.0) && this.field_150638_aH == this.field_150637_aG) {
         this.topBlock = Blocks.gravel.getDefaultState();
         this.fillerBlock = Blocks.gravel.getDefaultState();
      } else if (var6 > 1.0 && this.field_150638_aH != this.field_150636_aF) {
         this.topBlock = Blocks.stone.getDefaultState();
         this.fillerBlock = Blocks.stone.getDefaultState();
      }

      this.generateBiomeTerrain(var1, var2, var3, var4, var5, var6);
   }

   private BiomeGenHills mutateHills(BiomeGenBase var1) {
      this.field_150638_aH = this.field_150637_aG;
      this.func_150557_a(var1.color, true);
      this.setBiomeName(var1.biomeName + " M");
      this.setHeight(new BiomeGenBase.Height(var1.minHeight, var1.maxHeight));
      this.setTemperatureRainfall(var1.temperature, var1.rainfall);
      return this;
   }

   @Override
   public BiomeGenBase createMutatedBiome(int var1) {
      return new BiomeGenHills(var1, false).mutateHills(this);
   }
}
