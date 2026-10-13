package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class ComponentScatteredFeaturePieces {
   private static final long a = 47928846511316L;

   public static void registerScatteredFeaturePieces() {
      MapGenStructureIO.registerStructureComponent(ComponentScatteredFeaturePieces.DesertPyramid.class, "TeDP");
      MapGenStructureIO.registerStructureComponent(ComponentScatteredFeaturePieces.JunglePyramid.class, "TeJP");
      MapGenStructureIO.registerStructureComponent(ComponentScatteredFeaturePieces.SwampHut.class, "TeSH");
   }

   public static class DesertPyramid extends ComponentScatteredFeaturePieces.Feature {
      private boolean[] hasPlacedChest = new boolean[4];
      private static final List<WeightedRandomChestContent> itemsToGenerateInTemple = Lists.newArrayList(
         new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
         new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
         new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15),
         new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2),
         new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20),
         new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16),
         new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
         new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
      );
      private static final long b = 73031503511605L;

      public DesertPyramid() {
      }

      public DesertPyramid(Random var1, int var2, int var3) {
         super(var1, var2, 64, var3, 21, 15, 21);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
         var1.setBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
         var1.setBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
         var1.setBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.hasPlacedChest[0] = var1.getBoolean("hasPlacedChest0");
         this.hasPlacedChest[1] = var1.getBoolean("hasPlacedChest1");
         this.hasPlacedChest[2] = var1.getBoolean("hasPlacedChest2");
         this.hasPlacedChest[3] = var1.getBoolean("hasPlacedChest3");
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         this.fillWithBlocks(
            var1,
            var3,
            0,
            -4,
            0,
            this.scatteredFeatureSizeX - 1,
            0,
            this.scatteredFeatureSizeZ - 1,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );

         for (int var8 = 1; var8 <= 9; var8++) {
            this.fillWithBlocks(
               var1,
               var3,
               var8,
               var8,
               var8,
               this.scatteredFeatureSizeX - 1 - var8,
               var8,
               this.scatteredFeatureSizeZ - 1 - var8,
               Blocks.sandstone.getDefaultState(),
               Blocks.sandstone.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               var1,
               var3,
               var8 + 1,
               var8,
               var8 + 1,
               this.scatteredFeatureSizeX - 2 - var8,
               var8,
               this.scatteredFeatureSizeZ - 2 - var8,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         for (int var19 = 0; var19 < this.scatteredFeatureSizeX; var19++) {
            for (int var9 = 0; var9 < this.scatteredFeatureSizeZ; var9++) {
               byte var10 = -5;
               this.replaceAirAndLiquidDownwards(var1, Blocks.sandstone.getDefaultState(), var19, var10, var9, var3);
            }
         }

         int var20 = this.getMetadataWithOffset(Blocks.sandstone_stairs, 3);
         int var21 = this.getMetadataWithOffset(Blocks.sandstone_stairs, 2);
         int var22 = this.getMetadataWithOffset(Blocks.sandstone_stairs, 0);
         int var11 = this.getMetadataWithOffset(Blocks.sandstone_stairs, 1);
         int var12 = ~EnumDyeColor.ORANGE.getDyeDamage() & 15;
         int var13 = ~EnumDyeColor.BLUE.getDyeDamage() & 15;
         this.fillWithBlocks(var1, var3, 0, 0, 0, 4, 9, 4, Blocks.sandstone.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 1, 10, 1, 3, 10, 3, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var20), 2, 10, 0, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var21), 2, 10, 4, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var22), 0, 10, 2, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var11), 4, 10, 2, var3);
         this.fillWithBlocks(
            var1,
            var3,
            this.scatteredFeatureSizeX - 5,
            0,
            0,
            this.scatteredFeatureSizeX - 1,
            9,
            4,
            Blocks.sandstone.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            this.scatteredFeatureSizeX - 4,
            10,
            1,
            this.scatteredFeatureSizeX - 2,
            10,
            3,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var20), this.scatteredFeatureSizeX - 3, 10, 0, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var21), this.scatteredFeatureSizeX - 3, 10, 4, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var22), this.scatteredFeatureSizeX - 5, 10, 2, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var11), this.scatteredFeatureSizeX - 1, 10, 2, var3);
         this.fillWithBlocks(var1, var3, 8, 0, 0, 12, 4, 4, Blocks.sandstone.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 9, 1, 0, 11, 3, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 9, 1, 1, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 9, 2, 1, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 9, 3, 1, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 10, 3, 1, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 11, 3, 1, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 11, 2, 1, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 11, 1, 1, var3);
         this.fillWithBlocks(var1, var3, 4, 1, 1, 8, 3, 3, Blocks.sandstone.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 4, 1, 2, 8, 2, 2, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 12, 1, 1, 16, 3, 3, Blocks.sandstone.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 12, 1, 2, 16, 2, 2, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            var1,
            var3,
            5,
            4,
            5,
            this.scatteredFeatureSizeX - 6,
            4,
            this.scatteredFeatureSizeZ - 6,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.fillWithBlocks(var1, var3, 9, 4, 9, 11, 4, 11, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            var1,
            var3,
            8,
            1,
            8,
            8,
            3,
            8,
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            12,
            1,
            8,
            12,
            3,
            8,
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            8,
            1,
            12,
            8,
            3,
            12,
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            12,
            1,
            12,
            12,
            3,
            12,
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(var1, var3, 1, 1, 5, 4, 4, 11, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.fillWithBlocks(
            var1,
            var3,
            this.scatteredFeatureSizeX - 5,
            1,
            5,
            this.scatteredFeatureSizeX - 2,
            4,
            11,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.fillWithBlocks(var1, var3, 6, 7, 9, 6, 7, 11, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.fillWithBlocks(
            var1,
            var3,
            this.scatteredFeatureSizeX - 7,
            7,
            9,
            this.scatteredFeatureSizeX - 7,
            7,
            11,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            5,
            5,
            9,
            5,
            7,
            11,
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            this.scatteredFeatureSizeX - 6,
            5,
            9,
            this.scatteredFeatureSizeX - 6,
            7,
            11,
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            false
         );
         this.setBlockState(var1, Blocks.air.getDefaultState(), 5, 5, 10, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 5, 6, 10, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 6, 6, 10, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), this.scatteredFeatureSizeX - 6, 5, 10, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), this.scatteredFeatureSizeX - 6, 6, 10, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), this.scatteredFeatureSizeX - 7, 6, 10, var3);
         this.fillWithBlocks(var1, var3, 2, 4, 4, 2, 6, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            var1,
            var3,
            this.scatteredFeatureSizeX - 3,
            4,
            4,
            this.scatteredFeatureSizeX - 3,
            6,
            4,
            Blocks.air.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var20), 2, 4, 5, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var20), 2, 3, 4, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var20), this.scatteredFeatureSizeX - 3, 4, 5, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var20), this.scatteredFeatureSizeX - 3, 3, 4, var3);
         this.fillWithBlocks(var1, var3, 1, 1, 3, 2, 2, 3, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.fillWithBlocks(
            var1,
            var3,
            this.scatteredFeatureSizeX - 3,
            1,
            3,
            this.scatteredFeatureSizeX - 2,
            2,
            3,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.setBlockState(var1, Blocks.sandstone_stairs.getDefaultState(), 1, 1, 2, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getDefaultState(), this.scatteredFeatureSizeX - 2, 1, 2, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SAND.getMetadata()), 1, 2, 2, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SAND.getMetadata()), this.scatteredFeatureSizeX - 2, 2, 2, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var11), 2, 1, 2, var3);
         this.setBlockState(var1, Blocks.sandstone_stairs.getStateFromMeta(var22), this.scatteredFeatureSizeX - 3, 1, 2, var3);
         this.fillWithBlocks(var1, var3, 4, 3, 5, 4, 3, 18, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.fillWithBlocks(
            var1,
            var3,
            this.scatteredFeatureSizeX - 5,
            3,
            5,
            this.scatteredFeatureSizeX - 5,
            3,
            17,
            Blocks.sandstone.getDefaultState(),
            Blocks.sandstone.getDefaultState(),
            false
         );
         this.fillWithBlocks(var1, var3, 3, 1, 5, 4, 2, 16, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            var1,
            var3,
            this.scatteredFeatureSizeX - 6,
            1,
            5,
            this.scatteredFeatureSizeX - 5,
            2,
            16,
            Blocks.air.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );

         for (int var14 = 5; var14 <= 17; var14 += 2) {
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 4, 1, var14, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 4, 2, var14, var3);
            this.setBlockState(
               var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), this.scatteredFeatureSizeX - 5, 1, var14, var3
            );
            this.setBlockState(
               var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), this.scatteredFeatureSizeX - 5, 2, var14, var3
            );
         }

         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 10, 0, 7, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 10, 0, 8, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 9, 0, 9, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 11, 0, 9, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 8, 0, 10, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 12, 0, 10, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 7, 0, 10, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 13, 0, 10, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 9, 0, 11, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 11, 0, 11, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 10, 0, 12, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 10, 0, 13, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var13), 10, 0, 10, var3);

         for (int var23 = 0; var23 <= this.scatteredFeatureSizeX - 1; var23 += this.scatteredFeatureSizeX - 1) {
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var23, 2, 1, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var23, 2, 2, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var23, 2, 3, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var23, 3, 1, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var23, 3, 2, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var23, 3, 3, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var23, 4, 1, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), var23, 4, 2, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var23, 4, 3, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var23, 5, 1, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var23, 5, 2, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var23, 5, 3, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var23, 6, 1, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), var23, 6, 2, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var23, 6, 3, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var23, 7, 1, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var23, 7, 2, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var23, 7, 3, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var23, 8, 1, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var23, 8, 2, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var23, 8, 3, var3);
         }

         for (int var24 = 2; var24 <= this.scatteredFeatureSizeX - 3; var24 += this.scatteredFeatureSizeX - 3 - 2) {
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var24 - 1, 2, 0, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var24, 2, 0, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var24 + 1, 2, 0, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var24 - 1, 3, 0, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var24, 3, 0, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var24 + 1, 3, 0, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var24 - 1, 4, 0, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), var24, 4, 0, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var24 + 1, 4, 0, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var24 - 1, 5, 0, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var24, 5, 0, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var24 + 1, 5, 0, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var24 - 1, 6, 0, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), var24, 6, 0, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var24 + 1, 6, 0, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var24 - 1, 7, 0, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var24, 7, 0, var3);
            this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), var24 + 1, 7, 0, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var24 - 1, 8, 0, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var24, 8, 0, var3);
            this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), var24 + 1, 8, 0, var3);
         }

         this.fillWithBlocks(
            var1,
            var3,
            8,
            4,
            0,
            12,
            6,
            0,
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            false
         );
         this.setBlockState(var1, Blocks.air.getDefaultState(), 8, 6, 0, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 12, 6, 0, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 9, 5, 0, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 10, 5, 0, var3);
         this.setBlockState(var1, Blocks.stained_hardened_clay.getStateFromMeta(var12), 11, 5, 0, var3);
         this.fillWithBlocks(
            var1,
            var3,
            8,
            -14,
            8,
            12,
            -11,
            12,
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            8,
            -10,
            8,
            12,
            -10,
            12,
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()),
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            8,
            -9,
            8,
            12,
            -9,
            12,
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()),
            false
         );
         this.fillWithBlocks(var1, var3, 8, -8, 8, 12, -1, 12, Blocks.sandstone.getDefaultState(), Blocks.sandstone.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 9, -11, 9, 11, -1, 11, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(var1, Blocks.stone_pressure_plate.getDefaultState(), 10, -11, 10, var3);
         this.fillWithBlocks(var1, var3, 9, -13, 9, 11, -13, 11, Blocks.tnt.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 8, -11, 10, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 8, -10, 10, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 7, -10, 10, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 7, -11, 10, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 12, -11, 10, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 12, -10, 10, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 13, -10, 10, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 13, -11, 10, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 10, -11, 8, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 10, -10, 8, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 10, -10, 7, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 10, -11, 7, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 10, -11, 12, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 10, -10, 12, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.CHISELED.getMetadata()), 10, -10, 13, var3);
         this.setBlockState(var1, Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.getMetadata()), 10, -11, 13, var3);

         for (Object var15 : EnumFacing.Plane.HORIZONTAL) {
            EnumFacing var16 = (EnumFacing)var15;
            if (!this.hasPlacedChest[var16.getHorizontalIndex()]) {
               int var17 = var16.getFrontOffsetX() * 2;
               int var18 = var16.getFrontOffsetZ() * 2;
               this.hasPlacedChest[var16.getHorizontalIndex()] = this.generateChestContents(
                  var1,
                  var3,
                  var2,
                  10 + var17,
                  -11,
                  10 + var18,
                  WeightedRandomChestContent.func_177629_a(itemsToGenerateInTemple, Items.enchanted_book.getRandom(var2)),
                  2 + var2.nextInt(5)
               );
            }
         }

         return true;
      }
   }

   public abstract static class Feature extends StructureComponent {
      public int scatteredFeatureSizeX;
      public int scatteredFeatureSizeY;
      public int scatteredFeatureSizeZ;
      public int field_74936_d = -1;
      private static final long c = 10975085608565L;
      private static final long q = -4726194850175647680L;

      public Feature() {
      }

      public Feature(Random var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         super(0);
         this.scatteredFeatureSizeX = var5;
         this.scatteredFeatureSizeY = var6;
         this.scatteredFeatureSizeZ = var7;
         this.coordBaseMode = EnumFacing.Plane.HORIZONTAL.random(var1);
         switch (this.coordBaseMode) {
            case NORTH:
            case SOUTH:
               this.boundingBox = new StructureBoundingBox(var2, var3, var4, var2 + var5 - 1, var3 + var6 - 1, var4 + var7 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(var2, var3, var4, var2 + var7 - 1, var3 + var6 - 1, var4 + var5 - 1);
         }
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         var1.setInteger("Width", this.scatteredFeatureSizeX);
         var1.setInteger("Height", this.scatteredFeatureSizeY);
         var1.setInteger("Depth", this.scatteredFeatureSizeZ);
         var1.setInteger("HPos", this.field_74936_d);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         this.scatteredFeatureSizeX = var1.getInteger("Width");
         this.scatteredFeatureSizeY = var1.getInteger("Height");
         this.scatteredFeatureSizeZ = var1.getInteger("Depth");
         this.field_74936_d = var1.getInteger("HPos");
      }

      public boolean func_74935_a(World var1, StructureBoundingBox var2, int var3) {
         if (this.field_74936_d >= 0) {
            return true;
         }

         int var7 = 0;
         int var8 = 0;
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

         for (int var10 = this.boundingBox.minZ; var10 <= this.boundingBox.maxZ; var10++) {
            for (int var11 = this.boundingBox.minX; var11 <= this.boundingBox.maxX; var11++) {
               var9.set(var11, (int)q, var10);
               if (var2.isVecInside(var9)) {
                  var7 += Math.max(var1.getTopSolidOrLiquidBlock(var9).getY(), var1.provider.getAverageGroundLevel());
                  var8++;
               }
            }
         }

         if (var8 == 0) {
            return false;
         }

         this.field_74936_d = var7 / var8;
         this.boundingBox.offset(0, this.field_74936_d - this.boundingBox.minY + var3, 0);
         return true;
      }
   }

   public static class JunglePyramid extends ComponentScatteredFeaturePieces.Feature {
      private boolean placedMainChest;
      private boolean placedHiddenChest;
      private boolean placedTrap1;
      private boolean placedTrap2;
      private static final List<WeightedRandomChestContent> field_175816_i = Lists.newArrayList(
         new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
         new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
         new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15),
         new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2),
         new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20),
         new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16),
         new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
         new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
      );
      private static final List<WeightedRandomChestContent> field_175815_j = Lists.newArrayList(new WeightedRandomChestContent(Items.arrow, 0, 2, 7, 30));
      private static ComponentScatteredFeaturePieces.JunglePyramid.Stones junglePyramidsRandomScatteredStones = new ComponentScatteredFeaturePieces.JunglePyramid.Stones();
      private static final long b = 89961959716631L;

      public JunglePyramid() {
      }

      public JunglePyramid(Random var1, int var2, int var3) {
         super(var1, var2, 64, var3, 12, 10, 15);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setBoolean("placedMainChest", this.placedMainChest);
         var1.setBoolean("placedHiddenChest", this.placedHiddenChest);
         var1.setBoolean("placedTrap1", this.placedTrap1);
         var1.setBoolean("placedTrap2", this.placedTrap2);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.placedMainChest = var1.getBoolean("placedMainChest");
         this.placedHiddenChest = var1.getBoolean("placedHiddenChest");
         this.placedTrap1 = var1.getBoolean("placedTrap1");
         this.placedTrap2 = var1.getBoolean("placedTrap2");
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (!this.func_74935_a(var1, var3, 0)) {
            return false;
         }

         int var7 = this.getMetadataWithOffset(Blocks.stone_stairs, 3);
         int var8 = this.getMetadataWithOffset(Blocks.stone_stairs, 2);
         int var9 = this.getMetadataWithOffset(Blocks.stone_stairs, 0);
         int var10 = this.getMetadataWithOffset(Blocks.stone_stairs, 1);
         this.fillWithRandomizedBlocks(
            var1, var3, 0, -4, 0, this.scatteredFeatureSizeX - 1, 0, this.scatteredFeatureSizeZ - 1, false, var2, junglePyramidsRandomScatteredStones
         );
         this.fillWithRandomizedBlocks(var1, var3, 2, 1, 2, 9, 2, 2, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 2, 1, 12, 9, 2, 12, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 2, 1, 3, 2, 2, 11, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 9, 1, 3, 9, 2, 11, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 1, 3, 1, 10, 6, 1, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 1, 3, 13, 10, 6, 13, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 1, 3, 2, 1, 6, 12, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 10, 3, 2, 10, 6, 12, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 2, 3, 2, 9, 3, 12, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 2, 6, 2, 9, 6, 12, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 3, 7, 3, 8, 7, 11, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 4, 8, 4, 7, 8, 10, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithAir(var1, var3, 3, 1, 3, 8, 2, 11);
         this.fillWithAir(var1, var3, 4, 3, 6, 7, 3, 9);
         this.fillWithAir(var1, var3, 2, 4, 2, 9, 5, 12);
         this.fillWithAir(var1, var3, 4, 6, 5, 7, 6, 9);
         this.fillWithAir(var1, var3, 5, 7, 6, 6, 7, 8);
         this.fillWithAir(var1, var3, 5, 1, 2, 6, 2, 2);
         this.fillWithAir(var1, var3, 5, 2, 12, 6, 2, 12);
         this.fillWithAir(var1, var3, 5, 5, 1, 6, 5, 1);
         this.fillWithAir(var1, var3, 5, 5, 13, 6, 5, 13);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 1, 5, 5, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 10, 5, 5, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 1, 5, 9, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 10, 5, 9, var3);

         for (int var11 = 0; var11 <= 14; var11 += 14) {
            this.fillWithRandomizedBlocks(var1, var3, 2, 4, var11, 2, 5, var11, false, var2, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(var1, var3, 4, 4, var11, 4, 5, var11, false, var2, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(var1, var3, 7, 4, var11, 7, 5, var11, false, var2, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(var1, var3, 9, 4, var11, 9, 5, var11, false, var2, junglePyramidsRandomScatteredStones);
         }

         this.fillWithRandomizedBlocks(var1, var3, 5, 6, 0, 6, 6, 0, false, var2, junglePyramidsRandomScatteredStones);

         for (int var13 = 0; var13 <= 11; var13 += 11) {
            for (int var12 = 2; var12 <= 12; var12 += 2) {
               this.fillWithRandomizedBlocks(var1, var3, var13, 4, var12, var13, 5, var12, false, var2, junglePyramidsRandomScatteredStones);
            }

            this.fillWithRandomizedBlocks(var1, var3, var13, 6, 5, var13, 6, 5, false, var2, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(var1, var3, var13, 6, 9, var13, 6, 9, false, var2, junglePyramidsRandomScatteredStones);
         }

         this.fillWithRandomizedBlocks(var1, var3, 2, 7, 2, 2, 9, 2, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 9, 7, 2, 9, 9, 2, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 2, 7, 12, 2, 9, 12, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 9, 7, 12, 9, 9, 12, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 4, 9, 4, 4, 9, 4, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 7, 9, 4, 7, 9, 4, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 4, 9, 10, 4, 9, 10, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 7, 9, 10, 7, 9, 10, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 5, 9, 7, 6, 9, 7, false, var2, junglePyramidsRandomScatteredStones);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 5, 9, 6, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 6, 9, 6, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var8), 5, 9, 8, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var8), 6, 9, 8, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 4, 0, 0, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 5, 0, 0, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 6, 0, 0, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 7, 0, 0, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 4, 1, 8, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 4, 2, 9, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 4, 3, 10, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 7, 1, 8, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 7, 2, 9, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 7, 3, 10, var3);
         this.fillWithRandomizedBlocks(var1, var3, 4, 1, 9, 4, 1, 9, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 7, 1, 9, 7, 1, 9, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 4, 1, 10, 7, 2, 10, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 5, 4, 5, 6, 4, 5, false, var2, junglePyramidsRandomScatteredStones);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var9), 4, 4, 5, var3);
         this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var10), 7, 4, 5, var3);

         for (int var14 = 0; var14 < 4; var14++) {
            this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var8), 5, 0 - var14, 6 + var14, var3);
            this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var8), 6, 0 - var14, 6 + var14, var3);
            this.fillWithAir(var1, var3, 5, 0 - var14, 7 + var14, 6, 0 - var14, 9 + var14);
         }

         this.fillWithAir(var1, var3, 1, -3, 12, 10, -1, 13);
         this.fillWithAir(var1, var3, 1, -3, 1, 3, -1, 13);
         this.fillWithAir(var1, var3, 1, -3, 1, 9, -1, 5);

         for (int var15 = 1; var15 <= 13; var15 += 2) {
            this.fillWithRandomizedBlocks(var1, var3, 1, -3, var15, 1, -2, var15, false, var2, junglePyramidsRandomScatteredStones);
         }

         for (int var16 = 2; var16 <= 12; var16 += 2) {
            this.fillWithRandomizedBlocks(var1, var3, 1, -1, var16, 3, -1, var16, false, var2, junglePyramidsRandomScatteredStones);
         }

         this.fillWithRandomizedBlocks(var1, var3, 2, -2, 1, 5, -2, 1, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 7, -2, 1, 9, -2, 1, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 6, -3, 1, 6, -3, 1, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 6, -1, 1, 6, -1, 1, false, var2, junglePyramidsRandomScatteredStones);
         this.setBlockState(
            var1,
            Blocks.tripwire_hook
               .getStateFromMeta(this.getMetadataWithOffset(Blocks.tripwire_hook, EnumFacing.EAST.getHorizontalIndex()))
               .withProperty(BlockTripWireHook.ATTACHED, true),
            1,
            -3,
            8,
            var3
         );
         this.setBlockState(
            var1,
            Blocks.tripwire_hook
               .getStateFromMeta(this.getMetadataWithOffset(Blocks.tripwire_hook, EnumFacing.WEST.getHorizontalIndex()))
               .withProperty(BlockTripWireHook.ATTACHED, true),
            4,
            -3,
            8,
            var3
         );
         this.setBlockState(var1, Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true), 2, -3, 8, var3);
         this.setBlockState(var1, Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true), 3, -3, 8, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 5, -3, 7, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 5, -3, 6, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 5, -3, 5, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 5, -3, 4, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 5, -3, 3, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 5, -3, 2, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 5, -3, 1, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 4, -3, 1, var3);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 3, -3, 1, var3);
         if (!this.placedTrap1) {
            this.placedTrap1 = this.generateDispenserContents(var1, var3, var2, 3, -2, 1, EnumFacing.NORTH.getIndex(), field_175815_j, 2);
         }

         this.setBlockState(var1, Blocks.vine.getStateFromMeta(15), 3, -2, 2, var3);
         this.setBlockState(
            var1,
            Blocks.tripwire_hook
               .getStateFromMeta(this.getMetadataWithOffset(Blocks.tripwire_hook, EnumFacing.NORTH.getHorizontalIndex()))
               .withProperty(BlockTripWireHook.ATTACHED, true),
            7,
            -3,
            1,
            var3
         );
         this.setBlockState(
            var1,
            Blocks.tripwire_hook
               .getStateFromMeta(this.getMetadataWithOffset(Blocks.tripwire_hook, EnumFacing.SOUTH.getHorizontalIndex()))
               .withProperty(BlockTripWireHook.ATTACHED, true),
            7,
            -3,
            5,
            var3
         );
         this.setBlockState(var1, Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true), 7, -3, 2, var3);
         this.setBlockState(var1, Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true), 7, -3, 3, var3);
         this.setBlockState(var1, Blocks.tripwire.getDefaultState().withProperty(BlockTripWire.ATTACHED, true), 7, -3, 4, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 8, -3, 6, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 9, -3, 6, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 9, -3, 5, var3);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 9, -3, 4, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 9, -2, 4, var3);
         if (!this.placedTrap2) {
            this.placedTrap2 = this.generateDispenserContents(var1, var3, var2, 9, -2, 3, EnumFacing.WEST.getIndex(), field_175815_j, 2);
         }

         this.setBlockState(var1, Blocks.vine.getStateFromMeta(15), 8, -1, 3, var3);
         this.setBlockState(var1, Blocks.vine.getStateFromMeta(15), 8, -2, 3, var3);
         if (!this.placedMainChest) {
            this.placedMainChest = this.generateChestContents(
               var1, var3, var2, 8, -3, 3, WeightedRandomChestContent.func_177629_a(field_175816_i, Items.enchanted_book.getRandom(var2)), 2 + var2.nextInt(5)
            );
         }

         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 9, -3, 2, var3);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 8, -3, 1, var3);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 4, -3, 5, var3);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 5, -2, 5, var3);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 5, -1, 5, var3);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 6, -3, 5, var3);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 7, -2, 5, var3);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 7, -1, 5, var3);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 8, -3, 5, var3);
         this.fillWithRandomizedBlocks(var1, var3, 9, -1, 1, 9, -1, 5, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithAir(var1, var3, 8, -3, 8, 10, -1, 10);
         this.setBlockState(var1, Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.CHISELED_META), 8, -2, 11, var3);
         this.setBlockState(var1, Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.CHISELED_META), 9, -2, 11, var3);
         this.setBlockState(var1, Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.CHISELED_META), 10, -2, 11, var3);
         this.setBlockState(
            var1,
            Blocks.lever
               .getStateFromMeta(BlockLever.getMetadataForFacing(EnumFacing.getFront(this.getMetadataWithOffset(Blocks.lever, EnumFacing.NORTH.getIndex())))),
            8,
            -2,
            12,
            var3
         );
         this.setBlockState(
            var1,
            Blocks.lever
               .getStateFromMeta(BlockLever.getMetadataForFacing(EnumFacing.getFront(this.getMetadataWithOffset(Blocks.lever, EnumFacing.NORTH.getIndex())))),
            9,
            -2,
            12,
            var3
         );
         this.setBlockState(
            var1,
            Blocks.lever
               .getStateFromMeta(BlockLever.getMetadataForFacing(EnumFacing.getFront(this.getMetadataWithOffset(Blocks.lever, EnumFacing.NORTH.getIndex())))),
            10,
            -2,
            12,
            var3
         );
         this.fillWithRandomizedBlocks(var1, var3, 8, -3, 8, 8, -3, 10, false, var2, junglePyramidsRandomScatteredStones);
         this.fillWithRandomizedBlocks(var1, var3, 10, -3, 8, 10, -3, 10, false, var2, junglePyramidsRandomScatteredStones);
         this.setBlockState(var1, Blocks.mossy_cobblestone.getDefaultState(), 10, -2, 9, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 8, -2, 9, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 8, -2, 10, var3);
         this.setBlockState(var1, Blocks.redstone_wire.getDefaultState(), 10, -1, 9, var3);
         this.setBlockState(var1, Blocks.sticky_piston.getStateFromMeta(EnumFacing.UP.getIndex()), 9, -2, 8, var3);
         this.setBlockState(
            var1, Blocks.sticky_piston.getStateFromMeta(this.getMetadataWithOffset(Blocks.sticky_piston, EnumFacing.WEST.getIndex())), 10, -2, 8, var3
         );
         this.setBlockState(
            var1, Blocks.sticky_piston.getStateFromMeta(this.getMetadataWithOffset(Blocks.sticky_piston, EnumFacing.WEST.getIndex())), 10, -1, 8, var3
         );
         this.setBlockState(
            var1,
            Blocks.unpowered_repeater.getStateFromMeta(this.getMetadataWithOffset(Blocks.unpowered_repeater, EnumFacing.NORTH.getHorizontalIndex())),
            10,
            -2,
            10,
            var3
         );
         if (!this.placedHiddenChest) {
            this.placedHiddenChest = this.generateChestContents(
               var1, var3, var2, 9, -3, 10, WeightedRandomChestContent.func_177629_a(field_175816_i, Items.enchanted_book.getRandom(var2)), 2 + var2.nextInt(5)
            );
         }

         return true;
      }

      public static class Stones extends StructureComponent.BlockSelector {
         private static final long a = 103318788662987L;

         public Stones() {
         }

         @Override
         public void selectBlocks(Random var1, int var2, int var3, int var4, boolean var5) {
            if (var1.nextFloat() < 0.4F) {
               this.blockstate = Blocks.cobblestone.getDefaultState();
            } else {
               this.blockstate = Blocks.mossy_cobblestone.getDefaultState();
            }
         }
      }
   }

   public static class SwampHut extends ComponentScatteredFeaturePieces.Feature {
      private boolean hasWitch;
      private static final long b = 6381828496560L;

      public SwampHut() {
      }

      public SwampHut(Random var1, int var2, int var3) {
         super(var1, var2, 64, var3, 7, 7, 9);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setBoolean("Witch", this.hasWitch);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.hasWitch = var1.getBoolean("Witch");
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (!this.func_74935_a(var1, var3, 0)) {
            return false;
         }

         this.fillWithBlocks(
            var1,
            var3,
            1,
            1,
            1,
            5,
            1,
            7,
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            1,
            4,
            2,
            5,
            4,
            7,
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            2,
            1,
            0,
            4,
            1,
            0,
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            2,
            2,
            2,
            3,
            3,
            2,
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            1,
            2,
            3,
            1,
            3,
            6,
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            5,
            2,
            3,
            5,
            3,
            6,
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(
            var1,
            var3,
            2,
            2,
            7,
            4,
            3,
            7,
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            Blocks.planks.getStateFromMeta(BlockPlanks.EnumType.SPRUCE.getMetadata()),
            false
         );
         this.fillWithBlocks(var1, var3, 1, 0, 2, 1, 3, 2, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 5, 0, 2, 5, 3, 2, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 1, 0, 7, 1, 3, 7, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 5, 0, 7, 5, 3, 7, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), 2, 3, 2, var3);
         this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), 3, 3, 7, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 1, 3, 4, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 5, 3, 4, var3);
         this.setBlockState(var1, Blocks.air.getDefaultState(), 5, 3, 5, var3);
         this.setBlockState(
            var1, Blocks.flower_pot.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, BlockFlowerPot.EnumFlowerType.MUSHROOM_RED), 1, 3, 5, var3
         );
         this.setBlockState(var1, Blocks.crafting_table.getDefaultState(), 3, 2, 6, var3);
         this.setBlockState(var1, Blocks.cauldron.getDefaultState(), 4, 2, 6, var3);
         this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), 1, 2, 1, var3);
         this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), 5, 2, 1, var3);
         int var7 = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
         int var8 = this.getMetadataWithOffset(Blocks.oak_stairs, 1);
         int var9 = this.getMetadataWithOffset(Blocks.oak_stairs, 0);
         int var10 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);
         this.fillWithBlocks(var1, var3, 0, 4, 1, 6, 4, 1, Blocks.spruce_stairs.getStateFromMeta(var7), Blocks.spruce_stairs.getStateFromMeta(var7), false);
         this.fillWithBlocks(var1, var3, 0, 4, 2, 0, 4, 7, Blocks.spruce_stairs.getStateFromMeta(var9), Blocks.spruce_stairs.getStateFromMeta(var9), false);
         this.fillWithBlocks(var1, var3, 6, 4, 2, 6, 4, 7, Blocks.spruce_stairs.getStateFromMeta(var8), Blocks.spruce_stairs.getStateFromMeta(var8), false);
         this.fillWithBlocks(var1, var3, 0, 4, 8, 6, 4, 8, Blocks.spruce_stairs.getStateFromMeta(var10), Blocks.spruce_stairs.getStateFromMeta(var10), false);

         for (int var11 = 2; var11 <= 7; var11 += 5) {
            for (int var12 = 1; var12 <= 5; var12 += 4) {
               this.replaceAirAndLiquidDownwards(var1, Blocks.log.getDefaultState(), var12, -1, var11, var3);
            }
         }

         if (!this.hasWitch) {
            int var15 = this.getXWithOffset(2, 5);
            int var16 = this.getYWithOffset(2);
            int var13 = this.getZWithOffset(2, 5);
            if (var3.isVecInside(new BlockPos(var15, var16, var13))) {
               this.hasWitch = true;
               EntityWitch var14 = new EntityWitch(var1);
               var14.setLocationAndAngles(var15 + 0.5, var16, var13 + 0.5, 0.0F, 0.0F);
               var14.onInitialSpawn(var1.getDifficultyForLocation(new BlockPos(var15, var16, var13)), (IEntityLivingData)null);
               var1.spawnEntityInWorld(var14);
            }
         }

         return true;
      }
   }
}
