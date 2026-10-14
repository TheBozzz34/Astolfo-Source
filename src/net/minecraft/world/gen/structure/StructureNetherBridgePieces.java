package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureNetherBridgePieces {
   private static final StructureNetherBridgePieces.PieceWeight[] primaryComponents = new StructureNetherBridgePieces.PieceWeight[]{
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Straight.class, 30, 0, true),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing3.class, 10, 4),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing.class, 10, 4),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Stairs.class, 10, 3),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Throne.class, 5, 2),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Entrance.class, 5, 1)
   };
   private static final StructureNetherBridgePieces.PieceWeight[] secondaryComponents = new StructureNetherBridgePieces.PieceWeight[]{
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor5.class, 25, 0, true),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing2.class, 15, 5),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor2.class, 5, 10),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor.class, 5, 10),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor3.class, 10, 3, true),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor4.class, 7, 2),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.NetherStalkRoom.class, 5, 2)
   };

   public static void registerNetherFortressPieces() {
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing3.class, "NeBCr");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.End.class, "NeBEF");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Straight.class, "NeBS");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor3.class, "NeCCS");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor4.class, "NeCTB");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Entrance.class, "NeCE");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing2.class, "NeSCSC");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor.class, "NeSCLT");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor5.class, "NeSC");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor2.class, "NeSCRT");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.NetherStalkRoom.class, "NeCSR");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Throne.class, "NeMT");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing.class, "NeRC");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Stairs.class, "NeSR");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Start.class, "NeStart");
   }

   private static StructureNetherBridgePieces.Piece func_175887_b(
      StructureNetherBridgePieces.PieceWeight pieceWeight, List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
   ) {
      Class class2 = pieceWeight.weightClass;
      StructureNetherBridgePieces.Piece piece = null;
      if (class2 == StructureNetherBridgePieces.Straight.class) {
         piece = StructureNetherBridgePieces.Straight.func_175882_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureNetherBridgePieces.Crossing3.class) {
         piece = StructureNetherBridgePieces.Crossing3.func_175885_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureNetherBridgePieces.Crossing.class) {
         piece = StructureNetherBridgePieces.Crossing.func_175873_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureNetherBridgePieces.Stairs.class) {
         piece = StructureNetherBridgePieces.Stairs.func_175872_a(list, random2, i, j, k, l, enumFacing);
      } else if (class2 == StructureNetherBridgePieces.Throne.class) {
         piece = StructureNetherBridgePieces.Throne.func_175874_a(list, random2, i, j, k, l, enumFacing);
      } else if (class2 == StructureNetherBridgePieces.Entrance.class) {
         piece = StructureNetherBridgePieces.Entrance.func_175881_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureNetherBridgePieces.Corridor5.class) {
         piece = StructureNetherBridgePieces.Corridor5.func_175877_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureNetherBridgePieces.Corridor2.class) {
         piece = StructureNetherBridgePieces.Corridor2.func_175876_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureNetherBridgePieces.Corridor.class) {
         piece = StructureNetherBridgePieces.Corridor.func_175879_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureNetherBridgePieces.Corridor3.class) {
         piece = StructureNetherBridgePieces.Corridor3.func_175883_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureNetherBridgePieces.Corridor4.class) {
         piece = StructureNetherBridgePieces.Corridor4.func_175880_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureNetherBridgePieces.Crossing2.class) {
         piece = StructureNetherBridgePieces.Crossing2.func_175878_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureNetherBridgePieces.NetherStalkRoom.class) {
         piece = StructureNetherBridgePieces.NetherStalkRoom.func_175875_a(list, random2, i, j, k, enumFacing, l);
      }

      return piece;
   }

   public static class Corridor extends StructureNetherBridgePieces.Piece {
      private boolean field_111021_b;

      public Corridor() {
      }

      public Corridor(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
         this.field_111021_b = random2.nextInt(3) == 0;
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.field_111021_b = tagCompound.getBoolean("Chest");
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Chest", this.field_111021_b);
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.S((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 1, true);
      }

      public static StructureNetherBridgePieces.Corridor func_175879_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, 0, 0, 5, 7, 5, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 1, 4, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 3, 4, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 3, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 4, 1, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 3, 4, 3, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         if (this.field_111021_b
            && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3)))) {
            this.field_111021_b = false;
            this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 3, 2, 3, field_111019_a, 2 + randomIn.nextInt(4));
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Corridor2 extends StructureNetherBridgePieces.Piece {
      private boolean field_111020_b;

      public Corridor2() {
      }

      public Corridor2(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
         this.field_111020_b = random2.nextInt(3) == 0;
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.field_111020_b = tagCompound.getBoolean("Chest");
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Chest", this.field_111020_b);
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.m((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 1, true);
      }

      public static StructureNetherBridgePieces.Corridor2 func_175876_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, 0, 0, 5, 7, 5, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor2(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 3, 0, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 4, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 4, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 4, 1, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 3, 4, 3, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         if (this.field_111020_b
            && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(1, 3), this.getYWithOffset(2), this.getZWithOffset(1, 3)))) {
            this.field_111020_b = false;
            this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 1, 2, 3, field_111019_a, 2 + randomIn.nextInt(4));
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Corridor3 extends StructureNetherBridgePieces.Piece {
      public Corridor3() {
      }

      public Corridor3(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.H((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 1, 0, true);
      }

      public static StructureNetherBridgePieces.Corridor3 func_175883_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -7, 0, 5, 14, 10, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor3(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         int i = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 2);

         for (int j = 0; j <= 9; j++) {
            int k = Math.max(1, 7 - j);
            int l = Math.min(Math.max(k + 5, 14 - j), 13);
            int m = j;
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 0, 0, j, 4, k, j, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 1, k + 1, j, 3, l - 1, j, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false
            );
            if (j <= 6) {
               this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i), 1, k + 1, j, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i), 2, k + 1, j, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i), 3, k + 1, j, structureBoundingBoxIn);
            }

            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               l,
               j,
               4,
               l,
               j,
               Blocks.nether_brick.getDefaultState(),
               Blocks.nether_brick.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               k + 1,
               j,
               0,
               l - 1,
               j,
               Blocks.nether_brick.getDefaultState(),
               Blocks.nether_brick.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               4,
               k + 1,
               j,
               4,
               l - 1,
               j,
               Blocks.nether_brick.getDefaultState(),
               Blocks.nether_brick.getDefaultState(),
               false
            );
            if ((j & 1) == 0) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  0,
                  k + 2,
                  j,
                  0,
                  k + 3,
                  j,
                  Blocks.nether_brick_fence.getDefaultState(),
                  Blocks.nether_brick_fence.getDefaultState(),
                  false
               );
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  4,
                  k + 2,
                  j,
                  4,
                  k + 3,
                  j,
                  Blocks.nether_brick_fence.getDefaultState(),
                  Blocks.nether_brick_fence.getDefaultState(),
                  false
               );
            }

            for (int n = 0; n <= 4; n++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), n, -1, m, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Corridor4 extends StructureNetherBridgePieces.Piece {
      public Corridor4() {
      }

      public Corridor4(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         byte b = 1;
         if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.NORTH) {
            b = 5;
         }

         this.S((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, b, rand.nextInt(8) > 0);
         this.m((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, b, rand.nextInt(8) > 0);
      }

      public static StructureNetherBridgePieces.Corridor4 func_175880_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -3, 0, 0, 9, 7, 9, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor4(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 8, 5, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 8, 6, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 2, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 2, 0, 8, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 0, 1, 4, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 4, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 8, 2, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 2, 2, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 4, 7, 2, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 8, 8, 3, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 6, 0, 3, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 3, 6, 8, 3, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 4, 0, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 3, 4, 8, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 5, 2, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 3, 5, 7, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 4, 5, 1, 5, 5, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 4, 5, 7, 5, 5, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );

         for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 8; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Corridor5 extends StructureNetherBridgePieces.Piece {
      public Corridor5() {
      }

      public Corridor5(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.H((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 1, 0, true);
      }

      public static StructureNetherBridgePieces.Corridor5 func_175877_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, 0, 0, 5, 7, 5, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor5(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 3, 0, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 1, 4, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 3, 4, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Crossing extends StructureNetherBridgePieces.Piece {
      public Crossing() {
      }

      public Crossing(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.H((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 2, 0, false);
         this.S((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 2, false);
         this.m((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 2, false);
      }

      public static StructureNetherBridgePieces.Crossing func_175873_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -2, 0, 0, 7, 9, 7, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Crossing(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 6, 1, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 6, 7, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 1, 6, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 6, 1, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 2, 0, 6, 6, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 2, 6, 6, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 6, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 5, 0, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 2, 0, 6, 6, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 2, 5, 6, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 6, 0, 4, 6, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 5, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 6, 6, 4, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 6, 4, 5, 6, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 2, 0, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 2, 0, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 6, 2, 6, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 5, 2, 6, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );

         for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 6; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Crossing2 extends StructureNetherBridgePieces.Piece {
      public Crossing2() {
      }

      public Crossing2(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.H((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 1, 0, true);
         this.S((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 1, true);
         this.m((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 1, true);
      }

      public static StructureNetherBridgePieces.Crossing2 func_175878_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, 0, 0, 5, 7, 5, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Crossing2(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 4, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 0, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 4, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Crossing3 extends StructureNetherBridgePieces.Piece {
      public Crossing3() {
      }

      public Crossing3(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      public Crossing3(Random random2, int i, int j) {
         super(0);
         this.coordBaseMode = EnumFacing.Plane.HORIZONTAL.random(random2);
         switch (this.coordBaseMode) {
            case NORTH:
            case SOUTH:
               this.boundingBox = new StructureBoundingBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
         }
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.H((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 8, 3, false);
         this.S((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 3, 8, false);
         this.m((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 3, 8, false);
      }

      public static StructureNetherBridgePieces.Crossing3 func_175885_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -8, -3, 0, 19, 10, 19, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Crossing3(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 3, 0, 11, 4, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 7, 18, 4, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 5, 0, 10, 7, 18, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 8, 18, 7, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 5, 0, 7, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 5, 11, 7, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 0, 11, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 11, 11, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 7, 7, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 7, 18, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 11, 7, 5, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 11, 18, 5, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 2, 0, 11, 2, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 2, 13, 11, 2, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 0, 0, 11, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 0, 15, 11, 1, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 7; i <= 11; i++) {
            for (int j = 0; j <= 2; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, 18 - j, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 7, 5, 2, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 13, 2, 7, 18, 2, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 7, 3, 1, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 15, 0, 7, 18, 1, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int k = 0; k <= 2; k++) {
            for (int l = 7; l <= 11; l++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), k, -1, l, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), 18 - k, -1, l, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class End extends StructureNetherBridgePieces.Piece {
      private int fillSeed;

      public End() {
      }

      public End(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
         this.fillSeed = random2.nextInt();
      }

      public static StructureNetherBridgePieces.End func_175884_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -3, 0, 5, 10, 8, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.End(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.fillSeed = tagCompound.getInteger("Seed");
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Seed", this.fillSeed);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         Random random = new Random(this.fillSeed);

         for (int i = 0; i <= 4; i++) {
            for (int j = 3; j <= 4; j++) {
               int k = random.nextInt(8);
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  i,
                  j,
                  0,
                  i,
                  j,
                  k,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            }
         }

         int l = random.nextInt(8);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 0, 5, l, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         l = random.nextInt(8);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 5, 0, 4, 5, l, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int m = 0; m <= 4; m++) {
            int n = random.nextInt(5);
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               m,
               2,
               0,
               m,
               2,
               n,
               Blocks.nether_brick.getDefaultState(),
               Blocks.nether_brick.getDefaultState(),
               false
            );
         }

         for (int i2 = 0; i2 <= 4; i2++) {
            for (int i3 = 0; i3 <= 1; i3++) {
               int i4 = random.nextInt(3);
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  i2,
                  i3,
                  0,
                  i2,
                  i3,
                  i4,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            }
         }

         return true;
      }
   }

   public static class Entrance extends StructureNetherBridgePieces.Piece {
      public Entrance() {
      }

      public Entrance(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.H((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 5, 3, true);
      }

      public static StructureNetherBridgePieces.Entrance func_175881_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -5, -3, 0, 13, 14, 13, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Entrance(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 0, 12, 4, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 12, 13, 12, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 1, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 0, 12, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 11, 4, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 11, 10, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 11, 7, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 0, 10, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 0, 7, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 11, 2, 10, 12, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 8, 0, 7, 8, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );

         for (int i = 1; i <= 11; i += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i,
               10,
               0,
               i,
               11,
               0,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i,
               10,
               12,
               i,
               11,
               12,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               10,
               i,
               0,
               11,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               12,
               10,
               i,
               12,
               11,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), i, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), i, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 0, 13, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 12, 13, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), i + 1, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), i + 1, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, i + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, i + 1, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, 0, structureBoundingBoxIn);

         for (int j = 3; j <= 9; j += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               1,
               7,
               j,
               1,
               8,
               j,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               11,
               7,
               j,
               11,
               8,
               j,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 8, 2, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 12, 2, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 0, 8, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 9, 8, 1, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 4, 3, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 0, 4, 12, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int k = 4; k <= 8; k++) {
            for (int l = 0; l <= 2; l++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), k, -1, l, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), k, -1, 12 - l, structureBoundingBoxIn);
            }
         }

         for (int m = 0; m <= 2; m++) {
            for (int n = 4; n <= 8; n++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), m, -1, n, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), 12 - m, -1, n, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 5, 5, 7, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 6, 6, 4, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 6, 0, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.flowing_lava.getDefaultState(), 6, 5, 6, structureBoundingBoxIn);
         BlockPos blockPos = new BlockPos(this.getXWithOffset(6, 6), this.getYWithOffset(5), this.getZWithOffset(6, 6));
         if (structureBoundingBoxIn.isVecInside(blockPos)) {
            worldIn.forceBlockUpdateTick(Blocks.flowing_lava, blockPos, randomIn);
         }

         return true;
      }
   }

   public static class NetherStalkRoom extends StructureNetherBridgePieces.Piece {
      public NetherStalkRoom() {
      }

      public NetherStalkRoom(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.H((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 5, 3, true);
         this.H((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 5, 11, true);
      }

      public static StructureNetherBridgePieces.NetherStalkRoom func_175875_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -5, -3, 0, 13, 14, 13, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.NetherStalkRoom(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 0, 12, 4, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 12, 13, 12, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 1, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 0, 12, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 11, 4, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 11, 10, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 11, 7, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 0, 10, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 0, 7, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 11, 2, 10, 12, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 1; i <= 11; i += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i,
               10,
               0,
               i,
               11,
               0,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i,
               10,
               12,
               i,
               11,
               12,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               10,
               i,
               0,
               11,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               12,
               10,
               i,
               12,
               11,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), i, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), i, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 0, 13, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 12, 13, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), i + 1, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), i + 1, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, i + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, i + 1, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, 0, structureBoundingBoxIn);

         for (int j = 3; j <= 9; j += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               1,
               7,
               j,
               1,
               8,
               j,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               11,
               7,
               j,
               11,
               8,
               j,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
         }

         int k = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 3);

         for (int l = 0; l <= 6; l++) {
            int m = l + 4;

            for (int n = 5; n <= 7; n++) {
               this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(k), n, 5 + l, m, structureBoundingBoxIn);
            }

            if (m >= 5 && m <= 8) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  5,
                  5,
                  m,
                  7,
                  l + 4,
                  m,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            } else if (m >= 9 && m <= 10) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  5,
                  8,
                  m,
                  7,
                  l + 4,
                  m,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            }

            if (l >= 1) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 5, 6 + l, m, 7, 9 + l, m, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false
               );
            }
         }

         for (int i2 = 5; i2 <= 7; i2++) {
            this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(k), i2, 12, 11, structureBoundingBoxIn);
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 6, 7, 5, 7, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 6, 7, 7, 7, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 13, 12, 7, 13, 12, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 2, 3, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 9, 3, 5, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 4, 2, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 5, 2, 10, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 5, 9, 10, 5, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 10, 5, 4, 10, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         int i3 = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 0);
         int i4 = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 1);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i4), 4, 5, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i4), 4, 5, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i4), 4, 5, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i4), 4, 5, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i3), 8, 5, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i3), 8, 5, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i3), 8, 5, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(i3), 8, 5, 10, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 4, 4, 4, 4, 8, Blocks.soul_sand.getDefaultState(), Blocks.soul_sand.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 4, 4, 9, 4, 8, Blocks.soul_sand.getDefaultState(), Blocks.soul_sand.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 5, 4, 4, 5, 8, Blocks.nether_wart.getDefaultState(), Blocks.nether_wart.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 4, 9, 5, 8, Blocks.nether_wart.getDefaultState(), Blocks.nether_wart.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 8, 2, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 12, 2, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 0, 8, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 9, 8, 1, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 4, 3, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 0, 4, 12, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i5 = 4; i5 <= 8; i5++) {
            for (int i6 = 0; i6 <= 2; i6++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i5, -1, i6, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i5, -1, 12 - i6, structureBoundingBoxIn);
            }
         }

         for (int i7 = 0; i7 <= 2; i7++) {
            for (int i8 = 4; i8 <= 8; i8++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i7, -1, i8, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), 12 - i7, -1, i8, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public abstract static class Piece extends StructureComponent {
      public static final List<WeightedRandomChestContent> field_111019_a = Lists.newArrayList(
         new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 5),
         new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 5),
         new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 15),
         new WeightedRandomChestContent(Items.golden_sword, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.golden_chestplate, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.flint_and_steel, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.nether_wart, 0, 3, 7, 5),
         new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10),
         new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 8),
         new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 3),
         new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.obsidian), 0, 2, 4, 2)
      );

      public Piece() {
      }

      public Piece(int i) {
         super(i);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
      }

      private int getTotalWeight(List<StructureNetherBridgePieces.PieceWeight> list) {
         boolean bl = false;
         int i = 0;

         for (StructureNetherBridgePieces.PieceWeight pieceWeight : list) {
            if (pieceWeight.field_78824_d > 0 && pieceWeight.field_78827_c < pieceWeight.field_78824_d) {
               bl = true;
            }

            i += pieceWeight.field_78826_b;
         }

         return bl ? i : -1;
      }

      private StructureNetherBridgePieces.Piece func_175871_a(
         StructureNetherBridgePieces.Start start2,
         List<StructureNetherBridgePieces.PieceWeight> list,
         List<StructureComponent> list2,
         Random random2,
         int i,
         int j,
         int k,
         EnumFacing enumFacing,
         int l
      ) {
         int m = this.getTotalWeight(list);
         boolean bl = m > 0 && l <= 30;
         int n = 0;

         while (n < 5 && bl) {
            n++;
            int i2 = random2.nextInt(m);

            for (StructureNetherBridgePieces.PieceWeight pieceWeight : list) {
               i2 -= pieceWeight.field_78826_b;
               if (i2 < 0) {
                  if (!pieceWeight.func_78822_a(l) || pieceWeight == start2.theNetherBridgePieceWeight && !pieceWeight.field_78825_e) {
                     break;
                  }

                  StructureNetherBridgePieces.Piece piece = StructureNetherBridgePieces.func_175887_b(pieceWeight, list2, random2, i, j, k, enumFacing, l);
                  if (piece != null) {
                     pieceWeight.field_78827_c++;
                     start2.theNetherBridgePieceWeight = pieceWeight;
                     if (!pieceWeight.func_78823_a()) {
                        list.remove(pieceWeight);
                     }

                     return piece;
                  }
               }
            }
         }

         return StructureNetherBridgePieces.End.func_175884_a(list2, random2, i, j, k, enumFacing, l);
      }

      private StructureComponent func_175870_a(
         StructureNetherBridgePieces.Start start2, List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l, boolean bl
      ) {
         if (Math.abs(i - start2.getBoundingBox().minX) <= 112 && Math.abs(k - start2.getBoundingBox().minZ) <= 112) {
            List list2 = start2.primaryWeights;
            if (bl) {
               list2 = start2.secondaryWeights;
            }

            StructureNetherBridgePieces.Piece piece = this.func_175871_a(start2, list2, list, random2, i, j, k, enumFacing, l + 1);
            if (piece != null) {
               list.add(piece);
               start2.field_74967_d.add(piece);
            }

            return piece;
         } else {
            return StructureNetherBridgePieces.End.func_175884_a(list, random2, i, j, k, enumFacing, l);
         }
      }

      public StructureComponent H(StructureNetherBridgePieces.Start start2, List<StructureComponent> list, Random random2, int i, int j, boolean bl) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + j,
                     this.boundingBox.minZ - 1,
                     this.coordBaseMode,
                     this.getComponentType(),
                     bl
                  );
               case SOUTH:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + j,
                     this.boundingBox.maxZ + 1,
                     this.coordBaseMode,
                     this.getComponentType(),
                     bl
                  );
               case WEST:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + j,
                     this.boundingBox.minZ + i,
                     this.coordBaseMode,
                     this.getComponentType(),
                     bl
                  );
               case EAST:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + j,
                     this.boundingBox.minZ + i,
                     this.coordBaseMode,
                     this.getComponentType(),
                     bl
                  );
            }
         }

         return null;
      }

      public StructureComponent S(StructureNetherBridgePieces.Start start2, List<StructureComponent> list, Random random2, int i, int j, boolean bl) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + j,
                     EnumFacing.WEST,
                     this.getComponentType(),
                     bl
                  );
               case SOUTH:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + j,
                     EnumFacing.WEST,
                     this.getComponentType(),
                     bl
                  );
               case WEST:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.minX + j,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType(),
                     bl
                  );
               case EAST:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.minX + j,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType(),
                     bl
                  );
            }
         }

         return null;
      }

      public StructureComponent m(StructureNetherBridgePieces.Start start2, List<StructureComponent> list, Random random2, int i, int j, boolean bl) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + j,
                     EnumFacing.EAST,
                     this.getComponentType(),
                     bl
                  );
               case SOUTH:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + j,
                     EnumFacing.EAST,
                     this.getComponentType(),
                     bl
                  );
               case WEST:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.minX + j,
                     this.boundingBox.minY + i,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType(),
                     bl
                  );
               case EAST:
                  return this.func_175870_a(
                     start2,
                     list,
                     random2,
                     this.boundingBox.minX + j,
                     this.boundingBox.minY + i,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType(),
                     bl
                  );
            }
         }

         return null;
      }

      public static boolean isAboveGround(StructureBoundingBox structureBoundingBox) {
         return structureBoundingBox != null && structureBoundingBox.minY > 10;
      }
   }

   public static class PieceWeight {
      public Class<? extends StructureNetherBridgePieces.Piece> weightClass;
      public final int field_78826_b;
      public int field_78827_c;
      public int field_78824_d;
      public boolean field_78825_e;

      public PieceWeight(Class<? extends StructureNetherBridgePieces.Piece> class2, int i, int j, boolean bl) {
         this.weightClass = class2;
         this.field_78826_b = i;
         this.field_78824_d = j;
         this.field_78825_e = bl;
      }

      public PieceWeight(Class<? extends StructureNetherBridgePieces.Piece> class2, int i, int j) {
         this(class2, i, j, false);
      }

      public boolean func_78822_a(int i) {
         return this.field_78824_d == 0 || this.field_78827_c < this.field_78824_d;
      }

      public boolean func_78823_a() {
         return this.field_78824_d == 0 || this.field_78827_c < this.field_78824_d;
      }
   }

   public static class Stairs extends StructureNetherBridgePieces.Piece {
      public Stairs() {
      }

      public Stairs(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.m((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 6, 2, false);
      }

      public static StructureNetherBridgePieces.Stairs func_175872_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, int l, EnumFacing enumFacing
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -2, 0, 0, 7, 11, 7, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Stairs(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 6, 1, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 6, 10, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 1, 8, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 2, 0, 6, 8, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 1, 0, 8, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 2, 1, 6, 8, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 6, 5, 8, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 2, 0, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 3, 2, 6, 5, 2, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 3, 4, 6, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 5, 4, 3, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 2, 5, 3, 4, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 2, 5, 2, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 5, 1, 6, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 7, 1, 5, 7, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 8, 2, 6, 8, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 6, 0, 4, 8, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 5, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );

         for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 6; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Start extends StructureNetherBridgePieces.Crossing3 {
      public StructureNetherBridgePieces.PieceWeight theNetherBridgePieceWeight;
      public List<StructureNetherBridgePieces.PieceWeight> primaryWeights;
      public List<StructureNetherBridgePieces.PieceWeight> secondaryWeights;
      public List<StructureComponent> field_74967_d = Lists.newArrayList();

      public Start() {
      }

      public Start(Random random2, int i, int j) {
         super(random2, i, j);
         this.primaryWeights = Lists.newArrayList();

         for (StructureNetherBridgePieces.PieceWeight pieceWeight : StructureNetherBridgePieces.primaryComponents) {
            pieceWeight.field_78827_c = 0;
            this.primaryWeights.add(pieceWeight);
         }

         this.secondaryWeights = Lists.newArrayList();

         for (StructureNetherBridgePieces.PieceWeight pieceWeight2 : StructureNetherBridgePieces.secondaryComponents) {
            pieceWeight2.field_78827_c = 0;
            this.secondaryWeights.add(pieceWeight2);
         }
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
      }
   }

   public static class Straight extends StructureNetherBridgePieces.Piece {
      public Straight() {
      }

      public Straight(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.H((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 1, 3, false);
      }

      public static StructureNetherBridgePieces.Straight func_175882_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -3, 0, 5, 10, 19, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Straight(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 0, 4, 4, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 0, 3, 7, 18, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 0, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 5, 0, 4, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 2, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 13, 4, 2, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 15, 4, 1, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 2; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, 18 - j, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 4, 0, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            0,
            3,
            14,
            0,
            4,
            14,
            Blocks.nether_brick_fence.getDefaultState(),
            Blocks.nether_brick_fence.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            0,
            1,
            17,
            0,
            4,
            17,
            Blocks.nether_brick_fence.getDefaultState(),
            Blocks.nether_brick_fence.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 4, 4, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            4,
            3,
            14,
            4,
            4,
            14,
            Blocks.nether_brick_fence.getDefaultState(),
            Blocks.nether_brick_fence.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            4,
            1,
            17,
            4,
            4,
            17,
            Blocks.nether_brick_fence.getDefaultState(),
            Blocks.nether_brick_fence.getDefaultState(),
            false
         );
         return true;
      }
   }

   public static class Throne extends StructureNetherBridgePieces.Piece {
      private boolean hasSpawner;

      public Throne() {
      }

      public Throne(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasSpawner = tagCompound.getBoolean("Mob");
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Mob", this.hasSpawner);
      }

      public static StructureNetherBridgePieces.Throne func_175874_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, int l, EnumFacing enumFacing
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -2, 0, 0, 7, 8, 9, enumFacing);
         return isAboveGround(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureNetherBridgePieces.Throne(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 6, 7, 7, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 0, 5, 1, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 1, 5, 2, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 2, 5, 3, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 4, 3, 5, 4, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 0, 1, 4, 2, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 2, 0, 5, 4, 2, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 5, 2, 1, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 5, 2, 5, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 3, 0, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 5, 3, 6, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 5, 8, 5, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 1, 6, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 5, 6, 3, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 3, 0, 6, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 6, 3, 6, 6, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 6, 8, 5, 7, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 8, 8, 4, 8, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         if (!this.hasSpawner) {
            BlockPos blockPos = new BlockPos(this.getXWithOffset(3, 5), this.getYWithOffset(5), this.getZWithOffset(3, 5));
            if (structureBoundingBoxIn.isVecInside(blockPos)) {
               this.hasSpawner = true;
               worldIn.setBlockState(blockPos, Blocks.mob_spawner.getDefaultState(), 2);
               TileEntity tileEntity = worldIn.getTileEntity(blockPos);
               if (tileEntity instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic().setEntityName("Blaze");
               }
            }
         }

         for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 6; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }
}
