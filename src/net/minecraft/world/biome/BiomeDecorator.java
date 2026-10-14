package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.GeneratorBushFeature;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeDecorator {
   public World currentWorld;
   public Random randomGenerator;
   public BlockPos field_180294_c;
   public ChunkProviderSettings chunkProviderSettings;
   public WorldGenerator clayGen = new WorldGenClay(4);
   public WorldGenerator sandGen = new WorldGenSand(Blocks.sand, 7);
   public WorldGenerator gravelAsSandGen = new WorldGenSand(Blocks.gravel, 6);
   public WorldGenerator dirtGen;
   public WorldGenerator gravelGen;
   public WorldGenerator graniteGen;
   public WorldGenerator dioriteGen;
   public WorldGenerator andesiteGen;
   public WorldGenerator coalGen;
   public WorldGenerator ironGen;
   public WorldGenerator goldGen;
   public WorldGenerator redstoneGen;
   public WorldGenerator diamondGen;
   public WorldGenerator lapisGen;
   public WorldGenFlowers yellowFlowerGen = new WorldGenFlowers(Blocks.yellow_flower, BlockFlower.EnumFlowerType.DANDELION);
   public WorldGenerator mushroomBrownGen = new GeneratorBushFeature(Blocks.brown_mushroom);
   public WorldGenerator mushroomRedGen = new GeneratorBushFeature(Blocks.red_mushroom);
   public WorldGenerator bigMushroomGen = new WorldGenBigMushroom();
   public WorldGenerator reedGen = new WorldGenReed();
   public WorldGenerator cactusGen = new WorldGenCactus();
   public WorldGenerator waterlilyGen = new WorldGenWaterlily();
   public int waterlilyPerChunk;
   public int treesPerChunk;
   public int flowersPerChunk = 2;
   public int grassPerChunk = 1;
   public int deadBushPerChunk;
   public int mushroomsPerChunk;
   public int reedsPerChunk;
   public int cactiPerChunk;
   public int sandPerChunk = 1;
   public int sandPerChunk2 = 3;
   public int clayPerChunk = 1;
   public int bigMushroomsPerChunk;
   public boolean generateLakes = true;
   private static final long c = 88404703069534L;

   public void decorate(World worldIn, Random randomIn, BiomeGenBase biome, BlockPos blockPos) {
      if (this.currentWorld != null) {
         throw new RuntimeException("Already decorating");
      }

      this.currentWorld = worldIn;
      String string = worldIn.getWorldInfo().getGeneratorOptions();
      if (string != null) {
         this.chunkProviderSettings = ChunkProviderSettings.Factory.jsonToFactory(string).func_177864_b();
      } else {
         this.chunkProviderSettings = ChunkProviderSettings.Factory.jsonToFactory("").func_177864_b();
      }

      this.randomGenerator = randomIn;
      this.field_180294_c = blockPos;
      this.dirtGen = new WorldGenMinable(Blocks.dirt.getDefaultState(), this.chunkProviderSettings.dirtSize);
      this.gravelGen = new WorldGenMinable(Blocks.gravel.getDefaultState(), this.chunkProviderSettings.gravelSize);
      this.graniteGen = new WorldGenMinable(
         Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE), this.chunkProviderSettings.graniteSize
      );
      this.dioriteGen = new WorldGenMinable(
         Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE), this.chunkProviderSettings.dioriteSize
      );
      this.andesiteGen = new WorldGenMinable(
         Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE), this.chunkProviderSettings.andesiteSize
      );
      this.coalGen = new WorldGenMinable(Blocks.coal_ore.getDefaultState(), this.chunkProviderSettings.coalSize);
      this.ironGen = new WorldGenMinable(Blocks.iron_ore.getDefaultState(), this.chunkProviderSettings.ironSize);
      this.goldGen = new WorldGenMinable(Blocks.gold_ore.getDefaultState(), this.chunkProviderSettings.goldSize);
      this.redstoneGen = new WorldGenMinable(Blocks.redstone_ore.getDefaultState(), this.chunkProviderSettings.redstoneSize);
      this.diamondGen = new WorldGenMinable(Blocks.diamond_ore.getDefaultState(), this.chunkProviderSettings.diamondSize);
      this.lapisGen = new WorldGenMinable(Blocks.lapis_ore.getDefaultState(), this.chunkProviderSettings.lapisSize);
      this.genDecorations(biome);
      this.currentWorld = null;
      this.randomGenerator = null;
   }

   public void genDecorations(BiomeGenBase biomeGenBaseIn) {
      this.generateOres();

      for (int i = 0; i < this.sandPerChunk2; i++) {
         int j = this.randomGenerator.nextInt(16) + 8;
         int k = this.randomGenerator.nextInt(16) + 8;
         this.sandGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(j, 0, k)));
      }

      for (int l = 0; l < this.clayPerChunk; l++) {
         int m = this.randomGenerator.nextInt(16) + 8;
         int n = this.randomGenerator.nextInt(16) + 8;
         this.clayGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(m, 0, n)));
      }

      for (int i2 = 0; i2 < this.sandPerChunk; i2++) {
         int i3 = this.randomGenerator.nextInt(16) + 8;
         int i4 = this.randomGenerator.nextInt(16) + 8;
         this.gravelAsSandGen
            .generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(i3, 0, i4)));
      }

      int i5 = this.treesPerChunk;
      if (this.randomGenerator.nextInt(10) == 0) {
         i5++;
      }

      for (int i6 = 0; i6 < i5; i6++) {
         int i7 = this.randomGenerator.nextInt(16) + 8;
         int i8 = this.randomGenerator.nextInt(16) + 8;
         WorldGenAbstractTree worldGenAbstractTree = biomeGenBaseIn.genBigTreeChance(this.randomGenerator);
         worldGenAbstractTree.func_175904_e();
         BlockPos blockPos = this.currentWorld.getHeight(this.field_180294_c.add(i7, 0, i8));
         if (worldGenAbstractTree.generate(this.currentWorld, this.randomGenerator, blockPos)) {
            worldGenAbstractTree.func_180711_a(this.currentWorld, this.randomGenerator, blockPos);
         }
      }

      for (int i9 = 0; i9 < this.bigMushroomsPerChunk; i9++) {
         int i10 = this.randomGenerator.nextInt(16) + 8;
         int i11 = this.randomGenerator.nextInt(16) + 8;
         this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getHeight(this.field_180294_c.add(i10, 0, i11)));
      }

      for (int i12 = 0; i12 < this.flowersPerChunk; i12++) {
         int i13 = this.randomGenerator.nextInt(16) + 8;
         int i14 = this.randomGenerator.nextInt(16) + 8;
         int i15 = this.currentWorld.getHeight(this.field_180294_c.add(i13, 0, i14)).getY() + 32;
         if (i15 > 0) {
            int i16 = this.randomGenerator.nextInt(i15);
            BlockPos blockPos2 = this.field_180294_c.add(i13, i16, i14);
            BlockFlower.EnumFlowerType enumFlowerType = biomeGenBaseIn.pickRandomFlower(this.randomGenerator, blockPos2);
            BlockFlower blockFlower = enumFlowerType.getBlockType().getBlock();
            if (blockFlower.getMaterial() != Material.air) {
               this.yellowFlowerGen.setGeneratedBlock(blockFlower, enumFlowerType);
               this.yellowFlowerGen.generate(this.currentWorld, this.randomGenerator, blockPos2);
            }
         }
      }

      for (int i17 = 0; i17 < this.grassPerChunk; i17++) {
         int i18 = this.randomGenerator.nextInt(16) + 8;
         int i19 = this.randomGenerator.nextInt(16) + 8;
         int i20 = this.currentWorld.getHeight(this.field_180294_c.add(i18, 0, i19)).getY() * 2;
         if (i20 > 0) {
            int i21 = this.randomGenerator.nextInt(i20);
            biomeGenBaseIn.getRandomWorldGenForGrass(this.randomGenerator)
               .generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(i18, i21, i19));
         }
      }

      for (int i22 = 0; i22 < this.deadBushPerChunk; i22++) {
         int i23 = this.randomGenerator.nextInt(16) + 8;
         int i24 = this.randomGenerator.nextInt(16) + 8;
         int i25 = this.currentWorld.getHeight(this.field_180294_c.add(i23, 0, i24)).getY() * 2;
         if (i25 > 0) {
            int i26 = this.randomGenerator.nextInt(i25);
            new WorldGenDeadBush().generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(i23, i26, i24));
         }
      }

      for (int i27 = 0; i27 < this.waterlilyPerChunk; i27++) {
         int i28 = this.randomGenerator.nextInt(16) + 8;
         int i29 = this.randomGenerator.nextInt(16) + 8;
         int i30 = this.currentWorld.getHeight(this.field_180294_c.add(i28, 0, i29)).getY() * 2;
         if (i30 > 0) {
            int i31 = this.randomGenerator.nextInt(i30);
            BlockPos blockPos3 = this.field_180294_c.add(i28, i31, i29);

            while (blockPos3.getY() > 0) {
               BlockPos blockPos4 = blockPos3.down();
               if (!this.currentWorld.isAirBlock(blockPos4)) {
                  break;
               }

               blockPos3 = blockPos4;
            }

            this.waterlilyGen.generate(this.currentWorld, this.randomGenerator, blockPos3);
         }
      }

      for (int i32 = 0; i32 < this.mushroomsPerChunk; i32++) {
         if (this.randomGenerator.nextInt(4) == 0) {
            int i33 = this.randomGenerator.nextInt(16) + 8;
            int i34 = this.randomGenerator.nextInt(16) + 8;
            BlockPos blockPos5 = this.currentWorld.getHeight(this.field_180294_c.add(i33, 0, i34));
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, blockPos5);
         }

         if (this.randomGenerator.nextInt(8) == 0) {
            int i35 = this.randomGenerator.nextInt(16) + 8;
            int i36 = this.randomGenerator.nextInt(16) + 8;
            int i37 = this.currentWorld.getHeight(this.field_180294_c.add(i35, 0, i36)).getY() * 2;
            if (i37 > 0) {
               int i38 = this.randomGenerator.nextInt(i37);
               BlockPos blockPos6 = this.field_180294_c.add(i35, i38, i36);
               this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, blockPos6);
            }
         }
      }

      if (this.randomGenerator.nextInt(4) == 0) {
         int i39 = this.randomGenerator.nextInt(16) + 8;
         int i40 = this.randomGenerator.nextInt(16) + 8;
         int i41 = this.currentWorld.getHeight(this.field_180294_c.add(i39, 0, i40)).getY() * 2;
         if (i41 > 0) {
            int i42 = this.randomGenerator.nextInt(i41);
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(i39, i42, i40));
         }
      }

      if (this.randomGenerator.nextInt(8) == 0) {
         int i43 = this.randomGenerator.nextInt(16) + 8;
         int i44 = this.randomGenerator.nextInt(16) + 8;
         int i45 = this.currentWorld.getHeight(this.field_180294_c.add(i43, 0, i44)).getY() * 2;
         if (i45 > 0) {
            int i46 = this.randomGenerator.nextInt(i45);
            this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(i43, i46, i44));
         }
      }

      for (int i47 = 0; i47 < this.reedsPerChunk; i47++) {
         int i48 = this.randomGenerator.nextInt(16) + 8;
         int i49 = this.randomGenerator.nextInt(16) + 8;
         int i50 = this.currentWorld.getHeight(this.field_180294_c.add(i48, 0, i49)).getY() * 2;
         if (i50 > 0) {
            int i51 = this.randomGenerator.nextInt(i50);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(i48, i51, i49));
         }
      }

      for (int i52 = 0; i52 < 10; i52++) {
         int i53 = this.randomGenerator.nextInt(16) + 8;
         int i54 = this.randomGenerator.nextInt(16) + 8;
         int i55 = this.currentWorld.getHeight(this.field_180294_c.add(i53, 0, i54)).getY() * 2;
         if (i55 > 0) {
            int i56 = this.randomGenerator.nextInt(i55);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(i53, i56, i54));
         }
      }

      if (this.randomGenerator.nextInt(32) == 0) {
         int i57 = this.randomGenerator.nextInt(16) + 8;
         int i58 = this.randomGenerator.nextInt(16) + 8;
         int i59 = this.currentWorld.getHeight(this.field_180294_c.add(i57, 0, i58)).getY() * 2;
         if (i59 > 0) {
            int i60 = this.randomGenerator.nextInt(i59);
            new WorldGenPumpkin().generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(i57, i60, i58));
         }
      }

      for (int i61 = 0; i61 < this.cactiPerChunk; i61++) {
         int i62 = this.randomGenerator.nextInt(16) + 8;
         int i63 = this.randomGenerator.nextInt(16) + 8;
         int i64 = this.currentWorld.getHeight(this.field_180294_c.add(i62, 0, i63)).getY() * 2;
         if (i64 > 0) {
            int i65 = this.randomGenerator.nextInt(i64);
            this.cactusGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(i62, i65, i63));
         }
      }

      if (this.generateLakes) {
         for (int i66 = 0; i66 < 50; i66++) {
            int i67 = this.randomGenerator.nextInt(16) + 8;
            int i68 = this.randomGenerator.nextInt(16) + 8;
            int i69 = this.randomGenerator.nextInt(248) + 8;
            if (i69 > 0) {
               int i70 = this.randomGenerator.nextInt(i69);
               BlockPos blockPos7 = this.field_180294_c.add(i67, i70, i68);
               new WorldGenLiquids(Blocks.flowing_water).generate(this.currentWorld, this.randomGenerator, blockPos7);
            }
         }

         for (int i71 = 0; i71 < 20; i71++) {
            int i72 = this.randomGenerator.nextInt(16) + 8;
            int i73 = this.randomGenerator.nextInt(16) + 8;
            int i74 = this.randomGenerator.nextInt(this.randomGenerator.nextInt(this.randomGenerator.nextInt(240) + 8) + 8);
            BlockPos blockPos8 = this.field_180294_c.add(i72, i74, i73);
            new WorldGenLiquids(Blocks.flowing_lava).generate(this.currentWorld, this.randomGenerator, blockPos8);
         }
      }
   }

   public void genStandardOre1(int blockCount, WorldGenerator generator, int minHeight, int maxHeight) {
      if (maxHeight < minHeight) {
         int i = minHeight;
         minHeight = maxHeight;
         maxHeight = i;
      } else if (maxHeight == minHeight) {
         if (minHeight < 255) {
            maxHeight++;
         } else {
            minHeight--;
         }
      }

      for (int j = 0; j < blockCount; j++) {
         BlockPos blockPos = this.field_180294_c
            .add(this.randomGenerator.nextInt(16), this.randomGenerator.nextInt(maxHeight - minHeight) + minHeight, this.randomGenerator.nextInt(16));
         generator.generate(this.currentWorld, this.randomGenerator, blockPos);
      }
   }

   public void genStandardOre2(int blockCount, WorldGenerator generator, int centerHeight, int spread) {
      for (int i = 0; i < blockCount; i++) {
         BlockPos blockPos = this.field_180294_c
            .add(
               this.randomGenerator.nextInt(16),
               this.randomGenerator.nextInt(spread) + this.randomGenerator.nextInt(spread) + centerHeight - spread,
               this.randomGenerator.nextInt(16)
            );
         generator.generate(this.currentWorld, this.randomGenerator, blockPos);
      }
   }

   public void generateOres() {
      this.genStandardOre1(
         this.chunkProviderSettings.dirtCount, this.dirtGen, this.chunkProviderSettings.dirtMinHeight, this.chunkProviderSettings.dirtMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.gravelCount, this.gravelGen, this.chunkProviderSettings.gravelMinHeight, this.chunkProviderSettings.gravelMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.dioriteCount, this.dioriteGen, this.chunkProviderSettings.dioriteMinHeight, this.chunkProviderSettings.dioriteMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.graniteCount, this.graniteGen, this.chunkProviderSettings.graniteMinHeight, this.chunkProviderSettings.graniteMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.andesiteCount, this.andesiteGen, this.chunkProviderSettings.andesiteMinHeight, this.chunkProviderSettings.andesiteMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.coalCount, this.coalGen, this.chunkProviderSettings.coalMinHeight, this.chunkProviderSettings.coalMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.ironCount, this.ironGen, this.chunkProviderSettings.ironMinHeight, this.chunkProviderSettings.ironMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.goldCount, this.goldGen, this.chunkProviderSettings.goldMinHeight, this.chunkProviderSettings.goldMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.redstoneCount, this.redstoneGen, this.chunkProviderSettings.redstoneMinHeight, this.chunkProviderSettings.redstoneMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.diamondCount, this.diamondGen, this.chunkProviderSettings.diamondMinHeight, this.chunkProviderSettings.diamondMaxHeight
      );
      this.genStandardOre2(
         this.chunkProviderSettings.lapisCount, this.lapisGen, this.chunkProviderSettings.lapisCenterHeight, this.chunkProviderSettings.lapisSpread
      );
   }
}
