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
   private static final long bb = 99500803454080L;

   public BiomeGenForest(int var1, int var2) {
      super(var1);
      this.field_150632_aF = var2;
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
   public BiomeGenBase func_150557_a(int var1, boolean var2) {
      if (this.field_150632_aF == 2) {
         this.field_150609_ah = 353825;
         this.color = var1;
         if (var2) {
            this.field_150609_ah = (this.field_150609_ah & 16711422) >> 1;
         }

         return this;
      } else {
         return super.func_150557_a(var1, var2);
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random var1) {
      return this.field_150632_aF == 3 && var1.nextInt(3) > 0
         ? field_150631_aE
         : (this.field_150632_aF != 2 && var1.nextInt(5) != 0 ? this.worldGeneratorTrees : field_150630_aD);
   }

   @Override
   public BlockFlower.EnumFlowerType pickRandomFlower(Random var1, BlockPos var2) {
      if (this.field_150632_aF == 1) {
         double var6 = MathHelper.clamp_double((1.0 + GRASS_COLOR_NOISE.func_151601_a(var2.getX() / 48.0, var2.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
         BlockFlower.EnumFlowerType var8 = BlockFlower.EnumFlowerType.values()[(int)(var6 * BlockFlower.EnumFlowerType.values().length)];
         return var8 == BlockFlower.EnumFlowerType.BLUE_ORCHID ? BlockFlower.EnumFlowerType.POPPY : var8;
      } else {
         return super.pickRandomFlower(var1, var2);
      }
   }

   @Override
   public void decorate(World var1, Random var2, BlockPos var3) {
      if (this.field_150632_aF == 3) {
         for (int var7 = 0; var7 < 4; var7++) {
            for (int var8 = 0; var8 < 4; var8++) {
               int var9 = var7 * 4 + 1 + 8 + var2.nextInt(3);
               int var10 = var8 * 4 + 1 + 8 + var2.nextInt(3);
               BlockPos var11 = var1.getHeight(var3.add(var9, 0, var10));
               if (var2.nextInt(20) == 0) {
                  WorldGenBigMushroom var12 = new WorldGenBigMushroom();
                  var12.generate(var1, var2, var11);
               } else {
                  WorldGenAbstractTree var19 = this.genBigTreeChance(var2);
                  var19.func_175904_e();
                  if (var19.generate(var1, var2, var11)) {
                     var19.func_180711_a(var1, var2, var11);
                  }
               }
            }
         }
      }

      int var14 = var2.nextInt(5) - 3;
      if (this.field_150632_aF == 1) {
         var14 += 2;
      }

      for (int var15 = 0; var15 < var14; var15++) {
         int var16 = var2.nextInt(3);
         if (var16 == 0) {
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SYRINGA);
         } else if (var16 == 1) {
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.ROSE);
         } else if (var16 == 2) {
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.PAEONIA);
         }

         for (int var17 = 0; var17 < 5; var17++) {
            int var18 = var2.nextInt(16) + 8;
            int var20 = var2.nextInt(16) + 8;
            int var13 = var2.nextInt(var1.getHeight(var3.add(var18, 0, var20)).getY() + 32);
            if (DOUBLE_PLANT_GENERATOR.generate(var1, var2, new BlockPos(var3.getX() + var18, var13, var3.getZ() + var20))) {
               break;
            }
         }
      }

      super.decorate(var1, var2, var3);
   }

   @Override
   public int getGrassColorAtPos(BlockPos var1) {
      int var5 = super.getGrassColorAtPos(var1);
      return this.field_150632_aF == 3 ? (var5 & 16711422) + 2634762 >> 1 : var5;
   }

   @Override
   public BiomeGenBase createMutatedBiome(int var1) {
      if (this.biomeID == BiomeGenBase.forest.biomeID) {
         BiomeGenForest var5 = new BiomeGenForest(var1, 1);
         var5.setHeight(new BiomeGenBase.Height(this.minHeight, this.maxHeight + 0.2F));
         var5.setBiomeName("Flower Forest");
         var5.func_150557_a(6976549, true);
         var5.setFillerBlockMetadata(8233509);
         return var5;
      } else {
         return this.biomeID != BiomeGenBase.birchForest.biomeID && this.biomeID != BiomeGenBase.birchForestHills.biomeID ? new BiomeGenMutated(var1, this) {
            @Override
            public void decorate(World var1, Random var2, BlockPos var3) {
               this.baseBiome.decorate(var1, var2, var3);
            }
         } : new BiomeGenMutated(var1, this) {
            private static final long bb = 30336142775135L;

            @Override
            public WorldGenAbstractTree genBigTreeChance(Random var1) {
               return var1.nextBoolean() ? BiomeGenForest.field_150629_aC : BiomeGenForest.field_150630_aD;
            }
         };
      }
   }
}
