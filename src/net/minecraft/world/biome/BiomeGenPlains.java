package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BiomeGenPlains extends BiomeGenBase {
   public boolean field_150628_aC;

   public BiomeGenPlains(int id) {
      super(id);
      this.setTemperatureRainfall(0.8F, 0.4F);
      this.setHeight(height_LowPlains);
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 5, 2, 6));
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.flowersPerChunk = 4;
      this.theBiomeDecorator.grassPerChunk = 10;
   }

   @Override
   public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      double d = GRASS_COLOR_NOISE.func_151601_a(pos.getX() / 200.0, pos.getZ() / 200.0);
      if (d < -0.8) {
         int i = rand.nextInt(4);
         switch (i) {
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
      } else if (rand.nextInt(3) > 0) {
         int j = rand.nextInt(3);
         return j == 0 ? BlockFlower.EnumFlowerType.POPPY : (j == 1 ? BlockFlower.EnumFlowerType.HOUSTONIA : BlockFlower.EnumFlowerType.OXEYE_DAISY);
      } else {
         return BlockFlower.EnumFlowerType.DANDELION;
      }
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      double d = GRASS_COLOR_NOISE.func_151601_a((pos.getX() + 8) / 200.0, (pos.getZ() + 8) / 200.0);
      if (d < -0.8) {
         this.theBiomeDecorator.flowersPerChunk = 15;
         this.theBiomeDecorator.grassPerChunk = 5;
      } else {
         this.theBiomeDecorator.flowersPerChunk = 4;
         this.theBiomeDecorator.grassPerChunk = 10;
         DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.GRASS);

         for (int i = 0; i < 7; i++) {
            int j = rand.nextInt(16) + 8;
            int k = rand.nextInt(16) + 8;
            int l = rand.nextInt(worldIn.getHeight(pos.add(j, 0, k)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j, l, k));
         }
      }

      if (this.field_150628_aC) {
         DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SUNFLOWER);

         for (int m = 0; m < 10; m++) {
            int n = rand.nextInt(16) + 8;
            int i2 = rand.nextInt(16) + 8;
            int i3 = rand.nextInt(worldIn.getHeight(pos.add(n, 0, i2)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(n, i3, i2));
         }
      }

      super.decorate(worldIn, rand, pos);
   }

   @Override
   public BiomeGenBase createMutatedBiome(int i) {
      BiomeGenPlains biomeGenPlains = new BiomeGenPlains(i);
      biomeGenPlains.setBiomeName("Sunflower Plains");
      biomeGenPlains.field_150628_aC = true;
      biomeGenPlains.setColor(9286496);
      biomeGenPlains.field_150609_ah = 14273354;
      return biomeGenPlains;
   }
}
