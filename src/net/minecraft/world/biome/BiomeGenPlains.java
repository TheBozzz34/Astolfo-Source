package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BiomeGenPlains extends BiomeGenBase {
   public boolean field_150628_aC;
   private static final long bb = 5327718498573L;

   public BiomeGenPlains(int var1) {
      super(var1);
      this.setTemperatureRainfall(0.8F, 0.4F);
      this.setHeight(height_LowPlains);
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 5, 2, 6));
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.flowersPerChunk = 4;
      this.theBiomeDecorator.grassPerChunk = 10;
   }

   @Override
   public BlockFlower.EnumFlowerType pickRandomFlower(Random var1, BlockPos var2) {
      double var6 = GRASS_COLOR_NOISE.func_151601_a(var2.getX() / 200.0, var2.getZ() / 200.0);
      if (var6 < -0.8) {
         int var9 = var1.nextInt(4);
         switch (var9) {
            case 0:
               return BlockFlower.EnumFlowerType.ORANGE_TULIP;
            case 1:
               return BlockFlower.EnumFlowerType.RED_TULIP;
            case 2:
               return BlockFlower.EnumFlowerType.PINK_TULIP;
            case 3:
            default:
               return BlockFlower.EnumFlowerType.WHITE_TULIP;
         }
      } else if (var1.nextInt(3) > 0) {
         int var8 = var1.nextInt(3);
         return var8 == 0 ? BlockFlower.EnumFlowerType.POPPY : (var8 == 1 ? BlockFlower.EnumFlowerType.HOUSTONIA : BlockFlower.EnumFlowerType.OXEYE_DAISY);
      } else {
         return BlockFlower.EnumFlowerType.DANDELION;
      }
   }

   @Override
   public void decorate(World var1, Random var2, BlockPos var3) {
      double var7 = GRASS_COLOR_NOISE.func_151601_a((var3.getX() + 8) / 200.0, (var3.getZ() + 8) / 200.0);
      if (var7 < -0.8) {
         this.theBiomeDecorator.flowersPerChunk = 15;
         this.theBiomeDecorator.grassPerChunk = 5;
      } else {
         this.theBiomeDecorator.flowersPerChunk = 4;
         this.theBiomeDecorator.grassPerChunk = 10;
         DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.GRASS);

         for (int var9 = 0; var9 < 7; var9++) {
            int var10 = var2.nextInt(16) + 8;
            int var11 = var2.nextInt(16) + 8;
            int var12 = var2.nextInt(var1.getHeight(var3.add(var10, 0, var11)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(var1, var2, var3.add(var10, var12, var11));
         }
      }

      if (this.field_150628_aC) {
         DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SUNFLOWER);

         for (int var13 = 0; var13 < 10; var13++) {
            int var14 = var2.nextInt(16) + 8;
            int var15 = var2.nextInt(16) + 8;
            int var16 = var2.nextInt(var1.getHeight(var3.add(var14, 0, var15)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(var1, var2, var3.add(var14, var16, var15));
         }
      }

      super.decorate(var1, var2, var3);
   }

   @Override
   public BiomeGenBase createMutatedBiome(int var1) {
      BiomeGenPlains var4 = new BiomeGenPlains(var1);
      var4.setBiomeName("Sunflower Plains");
      var4.field_150628_aC = true;
      var4.e(9286496);
      var4.field_150609_ah = 14273354;
      return var4;
   }
}
