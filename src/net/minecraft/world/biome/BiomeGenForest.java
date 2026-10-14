package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;

public class BiomeGenForest extends BiomeGenBase {
   private int field_150632_aF;
   public static final WorldGenForest field_150629_aC = new WorldGenForest(false, true);
   public static final WorldGenForest field_150630_aD = new WorldGenForest(false, false);
   public static final WorldGenCanopyTree field_150631_aE = new WorldGenCanopyTree(false);

   public BiomeGenForest(int id, int i) {
      super(id);
      this.field_150632_aF = i;
      this.theBiomeDecorator.treesPerChunk = 10;
      this.theBiomeDecorator.grassPerChunk = 2;
      if (this.field_150632_aF == 1) {
         this.theBiomeDecorator.treesPerChunk = 6;
         this.theBiomeDecorator.flowersPerChunk = 100;
         this.theBiomeDecorator.grassPerChunk = 1;
      }

      this.setFillerBlockMetadata(5159473);
      this.setTemperatureRainfall(0.7F, 0.8F);
      if (this.field_150632_aF == 2) {
         this.field_150609_ah = 353825;
         this.color = 3175492;
         this.setTemperatureRainfall(0.6F, 0.6F);
      }

      if (this.field_150632_aF == 0) {
         this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 5, 4, 4));
      }

      if (this.field_150632_aF == 3) {
         this.theBiomeDecorator.treesPerChunk = -999;
      }
   }

   @Override
   public BiomeGenBase func_150557_a(int colorIn, boolean bl) {
      if (this.field_150632_aF == 2) {
         this.field_150609_ah = 353825;
         this.color = colorIn;
         if (bl) {
            this.field_150609_ah = (this.field_150609_ah & 16711422) >> 1;
         }

         return this;
      } else {
         return super.func_150557_a(colorIn, bl);
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return this.field_150632_aF == 3 && rand.nextInt(3) > 0
         ? field_150631_aE
         : (this.field_150632_aF != 2 && rand.nextInt(5) != 0 ? this.worldGeneratorTrees : field_150630_aD);
   }

   @Override
   public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      if (this.field_150632_aF == 1) {
         double d = MathHelper.clamp_double((1.0 + GRASS_COLOR_NOISE.func_151601_a(pos.getX() / 48.0, pos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
         BlockFlower.EnumFlowerType enumFlowerType = BlockFlower.EnumFlowerType.values()[(int)(d * BlockFlower.EnumFlowerType.values().length)];
         return enumFlowerType == BlockFlower.EnumFlowerType.BLUE_ORCHID ? BlockFlower.EnumFlowerType.POPPY : enumFlowerType;
      } else {
         return super.pickRandomFlower(rand, pos);
      }
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      if (this.field_150632_aF == 3) {
         for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
               int k = i * 4 + 1 + 8 + rand.nextInt(3);
               int l = j * 4 + 1 + 8 + rand.nextInt(3);
               BlockPos blockPos = worldIn.getHeight(pos.add(k, 0, l));
               if (rand.nextInt(20) == 0) {
                  WorldGenBigMushroom worldGenBigMushroom = new WorldGenBigMushroom();
                  worldGenBigMushroom.generate(worldIn, rand, blockPos);
               } else {
                  WorldGenAbstractTree worldGenAbstractTree = this.genBigTreeChance(rand);
                  worldGenAbstractTree.func_175904_e();
                  if (worldGenAbstractTree.generate(worldIn, rand, blockPos)) {
                     worldGenAbstractTree.func_180711_a(worldIn, rand, blockPos);
                  }
               }
            }
         }
      }

      int m = rand.nextInt(5) - 3;
      if (this.field_150632_aF == 1) {
         m += 2;
      }

      for (int n = 0; n < m; n++) {
         int i2 = rand.nextInt(3);
         if (i2 == 0) {
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SYRINGA);
         } else if (i2 == 1) {
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.ROSE);
         } else if (i2 == 2) {
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.PAEONIA);
         }

         for (int i3 = 0; i3 < 5; i3++) {
            int i4 = rand.nextInt(16) + 8;
            int i5 = rand.nextInt(16) + 8;
            int i6 = rand.nextInt(worldIn.getHeight(pos.add(i4, 0, i5)).getY() + 32);
            if (DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, new BlockPos(pos.getX() + i4, i6, pos.getZ() + i5))) {
               break;
            }
         }
      }

      super.decorate(worldIn, rand, pos);
   }

   @Override
   public int getGrassColorAtPos(BlockPos pos) {
      int i = super.getGrassColorAtPos(pos);
      return this.field_150632_aF == 3 ? (i & 16711422) + 2634762 >> 1 : i;
   }

   @Override
   public BiomeGenBase createMutatedBiome(int i) {
      if (this.biomeID == BiomeGenBase.forest.biomeID) {
         BiomeGenForest biomeGenForest = new BiomeGenForest(i, 1);
         biomeGenForest.setHeight(new BiomeGenBase.Height(this.minHeight, this.maxHeight + 0.2F));
         biomeGenForest.setBiomeName("Flower Forest");
         biomeGenForest.func_150557_a(6976549, true);
         biomeGenForest.setFillerBlockMetadata(8233509);
         return biomeGenForest;
      } else {
         return this.biomeID != BiomeGenBase.birchForest.biomeID && this.biomeID != BiomeGenBase.birchForestHills.biomeID ? new BiomeGenMutated(i, this) {
            @Override
            public void decorate(World worldIn, Random rand, BlockPos pos) {
               this.baseBiome.decorate(worldIn, rand, pos);
            }
         } : new BiomeGenMutated(i, this) {
            @Override
            public WorldGenAbstractTree genBigTreeChance(Random rand) {
               return rand.nextBoolean() ? BiomeGenForest.field_150629_aC : BiomeGenForest.field_150630_aD;
            }
         };
      }
   }
}
