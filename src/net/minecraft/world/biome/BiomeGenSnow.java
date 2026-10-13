package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenIcePath;
import net.minecraft.world.gen.feature.WorldGenIceSpike;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

public class BiomeGenSnow extends BiomeGenBase {
   private boolean field_150615_aC;
   private WorldGenIceSpike field_150616_aD = new WorldGenIceSpike();
   private WorldGenIcePath field_150617_aE = new WorldGenIcePath(4);
   private static final long bb = 95500566700847L;

   public BiomeGenSnow(int var1, boolean var2) {
      super(var1);
      this.field_150615_aC = var2;
      if (var2) {
         this.topBlock = Blocks.snow.getDefaultState();
      }

      this.spawnableCreatureList.clear();
   }

   @Override
   public void decorate(World var1, Random var2, BlockPos var3) {
      if (this.field_150615_aC) {
         for (int var7 = 0; var7 < 3; var7++) {
            int var8 = var2.nextInt(16) + 8;
            int var9 = var2.nextInt(16) + 8;
            this.field_150616_aD.generate(var1, var2, var1.getHeight(var3.add(var8, 0, var9)));
         }

         for (int var10 = 0; var10 < 2; var10++) {
            int var11 = var2.nextInt(16) + 8;
            int var12 = var2.nextInt(16) + 8;
            this.field_150617_aE.generate(var1, var2, var1.getHeight(var3.add(var11, 0, var12)));
         }
      }

      super.decorate(var1, var2, var3);
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random var1) {
      return new WorldGenTaiga2(false);
   }

   @Override
   public BiomeGenBase createMutatedBiome(int var1) {
      BiomeGenBase var4 = new BiomeGenSnow(var1, true)
         .func_150557_a(13828095, true)
         .setBiomeName(this.biomeName + " Spikes")
         .setEnableSnow()
         .setTemperatureRainfall(0.0F, 0.5F)
         .setHeight(new BiomeGenBase.Height(this.minHeight + 0.1F, this.maxHeight + 0.1F));
      var4.minHeight = this.minHeight + 0.3F;
      var4.maxHeight = this.maxHeight + 0.4F;
      return var4;
   }
}
