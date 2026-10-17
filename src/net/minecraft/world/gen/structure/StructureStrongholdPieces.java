package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureStrongholdPieces {
   private static final StructureStrongholdPieces.PieceWeight[] pieceWeightArray = new StructureStrongholdPieces.PieceWeight[]{
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Straight.class, 40, 0),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Prison.class, 5, 5),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.LeftTurn.class, 20, 0),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.RightTurn.class, 20, 0),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.RoomCrossing.class, 10, 6),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.StairsStraight.class, 5, 5),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Stairs.class, 5, 5),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Crossing.class, 5, 4),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.ChestCorridor.class, 5, 4),
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Library.class, 10, 2) {
         @Override
         public boolean canSpawnMoreStructuresOfType(int i) {
            return super.canSpawnMoreStructuresOfType(i) && i > 4;
         }
      },
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.PortalRoom.class, 20, 1) {
         @Override
         public boolean canSpawnMoreStructuresOfType(int i) {
            return super.canSpawnMoreStructuresOfType(i) && i > 5;
         }
      }
   };
   private static List<StructureStrongholdPieces.PieceWeight> structurePieceList;
   private static Class<? extends StructureStrongholdPieces.Stronghold> strongComponentType;
   public static int totalWeight;
   private static final StructureStrongholdPieces.Stones strongholdStones = new StructureStrongholdPieces.Stones();

   public static void registerStrongholdPieces() {
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.ChestCorridor.class, "SHCC");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Corridor.class, "SHFC");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Crossing.class, "SH5C");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.LeftTurn.class, "SHLT");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Library.class, "SHLi");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.PortalRoom.class, "SHPR");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Prison.class, "SHPH");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.RightTurn.class, "SHRT");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.RoomCrossing.class, "SHRC");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Stairs.class, "SHSD");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Stairs2.class, "SHStart");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Straight.class, "SHS");
      MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.StairsStraight.class, "SHSSD");
   }

   public static void prepareStructurePieces() {
      structurePieceList = Lists.newArrayList();

      for (StructureStrongholdPieces.PieceWeight pieceWeight : pieceWeightArray) {
         pieceWeight.instancesSpawned = 0;
         structurePieceList.add(pieceWeight);
      }

      strongComponentType = null;
   }

   private static boolean canAddStructurePieces() {
      boolean bl = false;
      totalWeight = 0;

      for (StructureStrongholdPieces.PieceWeight pieceWeight2 : structurePieceList) {
         if (pieceWeight2.instancesLimit > 0 && pieceWeight2.instancesSpawned < pieceWeight2.instancesLimit) {
            bl = true;
         }

         totalWeight = totalWeight + pieceWeight2.pieceWeight;
      }

      return bl;
   }

   private static StructureStrongholdPieces.Stronghold func_175954_a(
      Class<? extends StructureStrongholdPieces.Stronghold> class2,
      List<StructureComponent> list,
      Random random2,
      int i,
      int j,
      int k,
      EnumFacing enumFacing,
      int l
   ) {
      StructureStrongholdPieces.Stronghold stronghold = null;
      if (class2 == StructureStrongholdPieces.Straight.class) {
         stronghold = StructureStrongholdPieces.Straight.func_175862_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureStrongholdPieces.Prison.class) {
         stronghold = StructureStrongholdPieces.Prison.func_175860_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureStrongholdPieces.LeftTurn.class) {
         stronghold = StructureStrongholdPieces.LeftTurn.func_175867_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureStrongholdPieces.RightTurn.class) {
         stronghold = StructureStrongholdPieces.RightTurn.func_175867_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureStrongholdPieces.RoomCrossing.class) {
         stronghold = StructureStrongholdPieces.RoomCrossing.func_175859_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureStrongholdPieces.StairsStraight.class) {
         stronghold = StructureStrongholdPieces.StairsStraight.func_175861_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureStrongholdPieces.Stairs.class) {
         stronghold = StructureStrongholdPieces.Stairs.func_175863_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureStrongholdPieces.Crossing.class) {
         stronghold = StructureStrongholdPieces.Crossing.func_175866_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureStrongholdPieces.ChestCorridor.class) {
         stronghold = StructureStrongholdPieces.ChestCorridor.func_175868_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureStrongholdPieces.Library.class) {
         stronghold = StructureStrongholdPieces.Library.func_175864_a(list, random2, i, j, k, enumFacing, l);
      } else if (class2 == StructureStrongholdPieces.PortalRoom.class) {
         stronghold = StructureStrongholdPieces.PortalRoom.func_175865_a(list, random2, i, j, k, enumFacing, l);
      }

      return stronghold;
   }

   private static StructureStrongholdPieces.Stronghold func_175955_b(
      StructureStrongholdPieces.Stairs2 stairs2, List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
   ) {
      if (!canAddStructurePieces()) {
         return null;
      }

      if (strongComponentType != null) {
         StructureStrongholdPieces.Stronghold stronghold = func_175954_a(strongComponentType, list, random2, i, j, k, enumFacing, l);
         strongComponentType = null;
         if (stronghold != null) {
            return stronghold;
         }
      }

      int m = 0;

      while (m < 5) {
         m++;
         int n = random2.nextInt(totalWeight);

         for (StructureStrongholdPieces.PieceWeight pieceWeight2 : structurePieceList) {
            n -= pieceWeight2.pieceWeight;
            if (n < 0) {
               if (!pieceWeight2.canSpawnMoreStructuresOfType(l) || pieceWeight2 == stairs2.strongholdPieceWeight) {
                  break;
               }

               StructureStrongholdPieces.Stronghold stronghold2 = func_175954_a(pieceWeight2.pieceClass, list, random2, i, j, k, enumFacing, l);
               if (stronghold2 != null) {
                  pieceWeight2.instancesSpawned++;
                  stairs2.strongholdPieceWeight = pieceWeight2;
                  if (!pieceWeight2.canSpawnMoreStructures()) {
                     structurePieceList.remove(pieceWeight2);
                  }

                  return stronghold2;
               }
            }
         }
      }

      StructureBoundingBox structureBoundingBox = StructureStrongholdPieces.Corridor.func_175869_a(list, random2, i, j, k, enumFacing);
      return structureBoundingBox != null && structureBoundingBox.minY > 1 ? new StructureStrongholdPieces.Corridor(l, random2, structureBoundingBox, enumFacing) : null;
   }

   private static StructureComponent func_175953_c(
      StructureStrongholdPieces.Stairs2 stairs2, List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
   ) {
      if (l > 50) {
         return null;
      }

      if (Math.abs(i - stairs2.getBoundingBox().minX) <= 112 && Math.abs(k - stairs2.getBoundingBox().minZ) <= 112) {
         StructureStrongholdPieces.Stronghold stronghold = func_175955_b(stairs2, list, random2, i, j, k, enumFacing, l + 1);
         if (stronghold != null) {
            list.add(stronghold);
            stairs2.field_75026_c.add(stronghold);
         }

         return stronghold;
      } else {
         return null;
      }
   }

   public static class ChestCorridor extends StructureStrongholdPieces.Stronghold {
      private static final List<WeightedRandomChestContent> strongholdChestContents = Lists.newArrayList(
         new WeightedRandomChestContent(Items.ender_pearl, 0, 1, 1, 10),
         new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
         new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
         new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
         new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
         new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
         new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15),
         new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.iron_sword, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.iron_chestplate, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.iron_helmet, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.iron_leggings, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.iron_boots, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
      );
      private boolean hasMadeChest;

      public ChestCorridor() {
      }

      public ChestCorridor(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.field_143013_d = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Chest", this.hasMadeChest);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasMadeChest = tagCompound.getBoolean("Chest");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StructureStrongholdPieces.ChestCorridor func_175868_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -1, 0, 5, 5, 7, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.ChestCorridor(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 6, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.field_143013_d, 1, 1, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 1, 4, Blocks.stonebrick.getDefaultState(), Blocks.stonebrick.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 2, 4, structureBoundingBoxIn);

         for (int i = 2; i <= 4; i++) {
            this.setBlockState(
               worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 2, 1, i, structureBoundingBoxIn
            );
         }

         if (!this.hasMadeChest
            && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3)))) {
            this.hasMadeChest = true;
            this.generateChestContents(
               worldIn,
               structureBoundingBoxIn,
               randomIn,
               3,
               2,
               3,
               WeightedRandomChestContent.func_177629_a(strongholdChestContents, Items.enchanted_book.getRandom(randomIn)),
               2 + randomIn.nextInt(2)
            );
         }

         return true;
      }
   }

   public static class Corridor extends StructureStrongholdPieces.Stronghold {
      private int field_74993_a;

      public Corridor() {
      }

      public Corridor(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
         this.field_74993_a = enumFacing != EnumFacing.NORTH && enumFacing != EnumFacing.SOUTH ? structureBoundingBox.G() : structureBoundingBox.S();
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Steps", this.field_74993_a);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.field_74993_a = tagCompound.getInteger("Steps");
      }

      public static StructureBoundingBox func_175869_a(List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -1, 0, 5, 5, 4, enumFacing);
         StructureComponent structureComponent = StructureComponent.findIntersecting(list, structureBoundingBox);
         if (structureComponent == null) {
            return null;
         }

         if (structureComponent.getBoundingBox().minY == structureBoundingBox.minY) {
            for (int l = 3; l >= 1; l--) {
               structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -1, 0, 5, 5, l - 1, enumFacing);
               if (!structureComponent.getBoundingBox().intersectsWith(structureBoundingBox)) {
                  return StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -1, 0, 5, 5, l, enumFacing);
               }
            }
         }

         return null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         for (int i = 0; i < this.field_74993_a; i++) {
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 0, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 4, 0, i, structureBoundingBoxIn);

            for (int j = 1; j <= 3; j++) {
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 0, j, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, j, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, j, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), 3, j, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 4, j, i, structureBoundingBoxIn);
            }

            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 0, 4, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 4, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 4, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 4, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 4, 4, i, structureBoundingBoxIn);
         }

         return true;
      }
   }

   public static class Crossing extends StructureStrongholdPieces.Stronghold {
      private boolean field_74996_b;
      private boolean field_74997_c;
      private boolean field_74995_d;
      private boolean field_74999_h;

      public Crossing() {
      }

      public Crossing(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.field_143013_d = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox;
         this.field_74996_b = random2.nextBoolean();
         this.field_74997_c = random2.nextBoolean();
         this.field_74995_d = random2.nextBoolean();
         this.field_74999_h = random2.nextInt(3) > 0;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("leftLow", this.field_74996_b);
         tagCompound.setBoolean("leftHigh", this.field_74997_c);
         tagCompound.setBoolean("rightLow", this.field_74995_d);
         tagCompound.setBoolean("rightHigh", this.field_74999_h);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.field_74996_b = tagCompound.getBoolean("leftLow");
         this.field_74997_c = tagCompound.getBoolean("leftHigh");
         this.field_74995_d = tagCompound.getBoolean("rightLow");
         this.field_74999_h = tagCompound.getBoolean("rightHigh");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int i = 3;
         int j = 5;
         if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.NORTH) {
            i = 8 - i;
            j = 8 - j;
         }

         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 5, 1);
         if (this.field_74996_b) {
            this.a((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, i, 1);
         }

         if (this.field_74997_c) {
            this.a((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, j, 7);
         }

         if (this.field_74995_d) {
            this.u((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, i, 1);
         }

         if (this.field_74999_h) {
            this.u((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, j, 7);
         }
      }

      public static StructureStrongholdPieces.Crossing func_175866_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -4, -3, 0, 10, 9, 11, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.Crossing(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 9, 8, 10, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.field_143013_d, 4, 3, 0);
         if (this.field_74996_b) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 5, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.field_74995_d) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 3, 1, 9, 5, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.field_74997_c) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 7, 0, 7, 9, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.field_74999_h) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 5, 7, 9, 7, 9, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 10, 7, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 2, 1, 8, 2, 6, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 5, 4, 4, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, 1, 5, 8, 4, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 4, 7, 3, 4, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 5, 3, 3, 6, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 4, 3, 3, 4, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 6, 3, 4, 6, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 1, 7, 7, 1, 8, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 9, 7, 1, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 7, 7, 2, 7, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 7, 4, 5, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 5, 7, 8, 5, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 5, 7, 7, 5, 9, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 6, 5, 6, structureBoundingBoxIn);
         return true;
      }
   }

   public static class LeftTurn extends StructureStrongholdPieces.Stronghold {
      public LeftTurn() {
      }

      public LeftTurn(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.field_143013_d = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.u((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         } else {
            this.a((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         }
      }

      public static StructureStrongholdPieces.LeftTurn func_175867_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -1, 0, 5, 5, 5, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.LeftTurn(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 4, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.field_143013_d, 1, 1, 0);
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         return true;
      }
   }

   public static class Library extends StructureStrongholdPieces.Stronghold {
      private static final List<WeightedRandomChestContent> strongholdLibraryChestContents = Lists.newArrayList(
         new WeightedRandomChestContent(Items.book, 0, 1, 3, 20),
         new WeightedRandomChestContent(Items.paper, 0, 2, 7, 20),
         new WeightedRandomChestContent(Items.map, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.compass, 0, 1, 1, 1)
      );
      private boolean isLargeRoom;

      public Library() {
      }

      public Library(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.field_143013_d = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox;
         this.isLargeRoom = structureBoundingBox.getYSize() > 6;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Tall", this.isLargeRoom);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.isLargeRoom = tagCompound.getBoolean("Tall");
      }

      public static StructureStrongholdPieces.Library func_175864_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -4, -1, 0, 14, 11, 15, enumFacing);
         if (!canStrongholdGoDeeper(structureBoundingBox) || StructureComponent.findIntersecting(list, structureBoundingBox) != null) {
            structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -4, -1, 0, 14, 6, 15, enumFacing);
            if (!canStrongholdGoDeeper(structureBoundingBox) || StructureComponent.findIntersecting(list, structureBoundingBox) != null) {
               return null;
            }
         }

         return new StructureStrongholdPieces.Library(l, random2, structureBoundingBox, enumFacing);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         byte b = 11;
         if (!this.isLargeRoom) {
            b = 6;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 13, b - 1, 14, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.field_143013_d, 4, 1, 0);
         this.func_175805_a(
            worldIn, structureBoundingBoxIn, randomIn, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.web.getDefaultState(), Blocks.web.getDefaultState(), false
         );

         for (int i = 1; i <= 13; i++) {
            if ((i - 1) % 4 == 0) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 1, 1, i, 1, 4, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 12, 1, i, 12, 4, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
               );
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 2, 3, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 11, 3, i, structureBoundingBoxIn);
               if (this.isLargeRoom) {
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 1, 6, i, 1, 9, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
                  );
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 12, 6, i, 12, 9, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
                  );
               }
            } else {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 1, 1, i, 1, 4, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 12, 1, i, 12, 4, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
               );
               if (this.isLargeRoom) {
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 1, 6, i, 1, 9, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
                  );
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 12, 6, i, 12, 9, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
                  );
               }
            }
         }

         for (int j = 3; j < 12; j += 2) {
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 3, 1, j, 4, 3, j, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 6, 1, j, 7, 3, j, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 9, 1, j, 10, 3, j, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
            );
         }

         if (this.isLargeRoom) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 3, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 5, 1, 12, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 1, 9, 5, 2, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 12, 9, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 9, 5, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 5, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 9, 5, 10, structureBoundingBoxIn);
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 3, 6, 2, 3, 6, 12, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 10, 6, 2, 10, 6, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 4, 6, 2, 9, 6, 2, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 4, 6, 12, 8, 6, 12, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 9, 6, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 8, 6, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 9, 6, 10, structureBoundingBoxIn);
            int k = this.getMetadataWithOffset(Blocks.ladder, 3);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(k), 10, 1, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(k), 10, 2, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(k), 10, 3, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(k), 10, 4, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(k), 10, 5, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(k), 10, 6, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(k), 10, 7, 13, structureBoundingBoxIn);
            byte b2 = 7;
            byte b3 = 7;
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 1, 9, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2, 9, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 1, 8, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2, 8, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 1, 7, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2, 7, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 2, 7, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 + 1, 7, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 1, 7, b3 - 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 1, 7, b3 + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2, 7, b3 - 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2, 7, b3 + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2 - 2, 8, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2 + 1, 8, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2 - 1, 8, b3 - 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2 - 1, 8, b3 + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2, 8, b3 - 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2, 8, b3 + 1, structureBoundingBoxIn);
         }

         this.generateChestContents(
            worldIn,
            structureBoundingBoxIn,
            randomIn,
            3,
            3,
            5,
            WeightedRandomChestContent.func_177629_a(strongholdLibraryChestContents, Items.enchanted_book.getRandom(randomIn, 1, 5, 2)),
            1 + randomIn.nextInt(4)
         );
         if (this.isLargeRoom) {
            this.setBlockState(worldIn, Blocks.air.getDefaultState(), 12, 9, 1, structureBoundingBoxIn);
            this.generateChestContents(
               worldIn,
               structureBoundingBoxIn,
               randomIn,
               12,
               8,
               1,
               WeightedRandomChestContent.func_177629_a(strongholdLibraryChestContents, Items.enchanted_book.getRandom(randomIn, 1, 5, 2)),
               1 + randomIn.nextInt(4)
            );
         }

         return true;
      }
   }

   public static class PieceWeight {
      public Class<? extends StructureStrongholdPieces.Stronghold> pieceClass;
      public final int pieceWeight;
      public int instancesSpawned;
      public int instancesLimit;

      public PieceWeight(Class<? extends StructureStrongholdPieces.Stronghold> class2, int i, int j) {
         this.pieceClass = class2;
         this.pieceWeight = i;
         this.instancesLimit = j;
      }

      public boolean canSpawnMoreStructuresOfType(int i) {
         return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
      }

      public boolean canSpawnMoreStructures() {
         return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
      }
   }

   public static class PortalRoom extends StructureStrongholdPieces.Stronghold {
      private boolean hasSpawner;

      public PortalRoom() {
      }

      public PortalRoom(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Mob", this.hasSpawner);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasSpawner = tagCompound.getBoolean("Mob");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (componentIn != null) {
            ((StructureStrongholdPieces.Stairs2)componentIn).strongholdPortalRoom = this;
         }
      }

      public static StructureStrongholdPieces.PortalRoom func_175865_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -4, -1, 0, 11, 8, 16, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.PortalRoom(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 10, 7, 15, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
         int i = 6;
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, i, 1, 1, i, 14, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, i, 1, 9, i, 14, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, i, 1, 8, i, 2, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, i, 14, 8, i, 14, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 2, 1, 4, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, 1, 1, 9, 1, 4, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 1, 1, 1, 1, 3, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 1, 1, 9, 1, 3, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false
         );
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 3, 1, 8, 7, 1, 12, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 1, 9, 6, 1, 11, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false
         );

         for (int j = 3; j < 14; j += 2) {
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 0, 3, j, 0, 4, j, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 10, 3, j, 10, 4, j, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false
            );
         }

         for (int k = 2; k < 9; k += 2) {
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, k, 3, 15, k, 4, 15, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false
            );
         }

         int l = this.getMetadataWithOffset(Blocks.stone_brick_stairs, 3);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 5, 6, 1, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 2, 6, 6, 2, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 3, 7, 6, 3, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);

         for (int m = 4; m <= 6; m++) {
            this.setBlockState(worldIn, Blocks.stone_brick_stairs.getStateFromMeta(l), m, 1, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_brick_stairs.getStateFromMeta(l), m, 2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_brick_stairs.getStateFromMeta(l), m, 3, 6, structureBoundingBoxIn);
         }

         int n = EnumFacing.NORTH.getHorizontalIndex();
         int i2 = EnumFacing.SOUTH.getHorizontalIndex();
         int i3 = EnumFacing.EAST.getHorizontalIndex();
         int i4 = EnumFacing.WEST.getHorizontalIndex();
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  n = EnumFacing.SOUTH.getHorizontalIndex();
                  i2 = EnumFacing.NORTH.getHorizontalIndex();
                  break;
               case WEST:
                  n = EnumFacing.WEST.getHorizontalIndex();
                  i2 = EnumFacing.EAST.getHorizontalIndex();
                  i3 = EnumFacing.SOUTH.getHorizontalIndex();
                  i4 = EnumFacing.NORTH.getHorizontalIndex();
                  break;
               case EAST:
                  n = EnumFacing.EAST.getHorizontalIndex();
                  i2 = EnumFacing.WEST.getHorizontalIndex();
                  i3 = EnumFacing.SOUTH.getHorizontalIndex();
                  i4 = EnumFacing.NORTH.getHorizontalIndex();
            }
         }

         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(n).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            4,
            3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(n).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            5,
            3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(n).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            6,
            3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(i2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            4,
            3,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(i2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            5,
            3,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(i2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            6,
            3,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(i3).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            3,
            3,
            9,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(i3).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            3,
            3,
            10,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(i3).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            3,
            3,
            11,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(i4).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            7,
            3,
            9,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(i4).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            7,
            3,
            10,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(i4).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            7,
            3,
            11,
            structureBoundingBoxIn
         );
         if (!this.hasSpawner) {
            i = this.getYWithOffset(3);
            BlockPos blockPos = new BlockPos(this.getXWithOffset(5, 6), i, this.getZWithOffset(5, 6));
            if (structureBoundingBoxIn.isVecInside(blockPos)) {
               this.hasSpawner = true;
               worldIn.setBlockState(blockPos, Blocks.mob_spawner.getDefaultState(), 2);
               TileEntity tileEntity = worldIn.getTileEntity(blockPos);
               if (tileEntity instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic().setEntityName("Silverfish");
               }
            }
         }

         return true;
      }
   }

   public static class Prison extends StructureStrongholdPieces.Stronghold {
      public Prison() {
      }

      public Prison(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.field_143013_d = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StructureStrongholdPieces.Prison func_175860_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -1, 0, 9, 5, 11, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.Prison(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 4, 10, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.field_143013_d, 1, 1, 0);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 10, 3, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 1, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 3, 4, 3, 3, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 7, 4, 3, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 9, 4, 3, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 4, 4, 3, 6, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 5, 7, 3, 5, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), 4, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), 4, 3, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3)), 4, 1, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3) + 8), 4, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3)), 4, 1, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3) + 8), 4, 2, 8, structureBoundingBoxIn);
         return true;
      }
   }

   public static class RightTurn extends StructureStrongholdPieces.LeftTurn {
      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.a((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         } else {
            this.u((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 4, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.field_143013_d, 1, 1, 0);
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         return true;
      }
   }

   public static class RoomCrossing extends StructureStrongholdPieces.Stronghold {
      private static final List<WeightedRandomChestContent> strongholdRoomCrossingChestContents = Lists.newArrayList(
         new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
         new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
         new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
         new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10),
         new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
         new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15),
         new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1)
      );
      public int roomType;

      public RoomCrossing() {
      }

      public RoomCrossing(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.field_143013_d = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox;
         this.roomType = random2.nextInt(5);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Type", this.roomType);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.roomType = tagCompound.getInteger("Type");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 4, 1);
         this.a((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 4);
         this.u((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 4);
      }

      public static StructureStrongholdPieces.RoomCrossing func_175859_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -4, -1, 0, 11, 7, 11, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.RoomCrossing(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 10, 6, 10, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.field_143013_d, 4, 1, 0);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 10, 6, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 4, 0, 3, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 4, 10, 3, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         switch (this.roomType) {
            case 0:
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 4, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 6, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 5, 3, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 4, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 4, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 4, 1, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 6, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 6, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 6, 1, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 5, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 5, 1, 6, structureBoundingBoxIn);
               break;
            case 1:
               for (int i = 0; i < 5; i++) {
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 1, 3 + i, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 7, 1, 3 + i, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3 + i, 1, 3, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3 + i, 1, 7, structureBoundingBoxIn);
               }

               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.flowing_water.getDefaultState(), 5, 4, 5, structureBoundingBoxIn);
               break;
            case 2:
               for (int j = 1; j <= 9; j++) {
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 1, 3, j, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 9, 3, j, structureBoundingBoxIn);
               }

               for (int k = 1; k <= 9; k++) {
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), k, 3, 1, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), k, 3, 9, structureBoundingBoxIn);
               }

               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 1, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 3, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, 3, 5, structureBoundingBoxIn);

               for (int l = 1; l <= 3; l++) {
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, l, 4, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, l, 4, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, l, 6, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, l, 6, structureBoundingBoxIn);
               }

               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);

               for (int m = 2; m <= 8; m++) {
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 2, 3, m, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 3, m, structureBoundingBoxIn);
                  if (m <= 3 || m >= 7) {
                     this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 4, 3, m, structureBoundingBoxIn);
                     this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 5, 3, m, structureBoundingBoxIn);
                     this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 6, 3, m, structureBoundingBoxIn);
                  }

                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 7, 3, m, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 3, m, structureBoundingBoxIn);
               }

               this.setBlockState(
                  worldIn,
                  Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())),
                  9,
                  1,
                  3,
                  structureBoundingBoxIn
               );
               this.setBlockState(
                  worldIn,
                  Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())),
                  9,
                  2,
                  3,
                  structureBoundingBoxIn
               );
               this.setBlockState(
                  worldIn,
                  Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())),
                  9,
                  3,
                  3,
                  structureBoundingBoxIn
               );
               this.generateChestContents(
                  worldIn,
                  structureBoundingBoxIn,
                  randomIn,
                  3,
                  4,
                  8,
                  WeightedRandomChestContent.func_177629_a(strongholdRoomCrossingChestContents, Items.enchanted_book.getRandom(randomIn)),
                  1 + randomIn.nextInt(4)
               );
         }

         return true;
      }
   }

   public static class Stairs extends StructureStrongholdPieces.Stronghold {
      private boolean field_75024_a;

      public Stairs() {
      }

      public Stairs(int i, Random random2, int j, int k) {
         super(i);
         this.field_75024_a = true;
         this.coordBaseMode = EnumFacing.Plane.HORIZONTAL.random(random2);
         this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.OPENING;
         switch (this.coordBaseMode) {
            case SOUTH:
            case NORTH:
               this.boundingBox = new StructureBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
         }
      }

      public Stairs(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.field_75024_a = false;
         this.coordBaseMode = enumFacing;
         this.field_143013_d = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Source", this.field_75024_a);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.field_75024_a = tagCompound.getBoolean("Source");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (this.field_75024_a) {
            StructureStrongholdPieces.strongComponentType = StructureStrongholdPieces.Crossing.class;
         }

         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StructureStrongholdPieces.Stairs func_175863_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -7, 0, 5, 11, 5, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.Stairs(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 10, 4, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.field_143013_d, 1, 7, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 6, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 5, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 6, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 5, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 5, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 3, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 3, 3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 1, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 1, 3, structureBoundingBoxIn);
         return true;
      }
   }

   public static class Stairs2 extends StructureStrongholdPieces.Stairs {
      public StructureStrongholdPieces.PieceWeight strongholdPieceWeight;
      public StructureStrongholdPieces.PortalRoom strongholdPortalRoom;
      public List<StructureComponent> field_75026_c = Lists.newArrayList();

      public Stairs2() {
      }

      public Stairs2(int i, Random random2, int j, int k) {
         super(0, random2, j, k);
      }

      @Override
      public BlockPos getBoundingBoxCenter() {
         return this.strongholdPortalRoom != null ? this.strongholdPortalRoom.getBoundingBoxCenter() : super.getBoundingBoxCenter();
      }
   }

   public static class StairsStraight extends StructureStrongholdPieces.Stronghold {
      public StairsStraight() {
      }

      public StairsStraight(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.field_143013_d = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StructureStrongholdPieces.StairsStraight func_175861_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -7, 0, 5, 11, 8, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.StairsStraight(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 10, 7, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.field_143013_d, 1, 7, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);
         int i = this.getMetadataWithOffset(Blocks.stone_stairs, 2);

         for (int j = 0; j < 6; j++) {
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(i), 1, 6 - j, 1 + j, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(i), 2, 6 - j, 1 + j, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(i), 3, 6 - j, 1 + j, structureBoundingBoxIn);
            if (j < 5) {
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 5 - j, 1 + j, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 5 - j, 1 + j, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 5 - j, 1 + j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Stones extends StructureComponent.BlockSelector {
      public Stones() {
      }

      @Override
      public void selectBlocks(Random rand, int x, int y, int z, boolean bl) {
         if (bl) {
            float f = rand.nextFloat();
            if (f < 0.2F) {
               this.blockstate = Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.CRACKED_META);
            } else if (f < 0.5F) {
               this.blockstate = Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.MOSSY_META);
            } else if (f < 0.55F) {
               this.blockstate = Blocks.monster_egg.getStateFromMeta(BlockSilverfish.EnumType.STONEBRICK.getMetadata());
            } else {
               this.blockstate = Blocks.stonebrick.getDefaultState();
            }
         } else {
            this.blockstate = Blocks.air.getDefaultState();
         }
      }
   }

   public static class Straight extends StructureStrongholdPieces.Stronghold {
      private boolean expandsX;
      private boolean expandsZ;

      public Straight() {
      }

      public Straight(int i, Random random2, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.field_143013_d = this.getRandomDoor(random2);
         this.boundingBox = structureBoundingBox;
         this.expandsX = random2.nextInt(2) == 0;
         this.expandsZ = random2.nextInt(2) == 0;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Left", this.expandsX);
         tagCompound.setBoolean("Right", this.expandsZ);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.expandsX = tagCompound.getBoolean("Left");
         this.expandsZ = tagCompound.getBoolean("Right");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         if (this.expandsX) {
            this.a((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 2);
         }

         if (this.expandsZ) {
            this.u((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 2);
         }
      }

      public static StructureStrongholdPieces.Straight func_175862_a(
         List<StructureComponent> list, Random random2, int i, int j, int k, EnumFacing enumFacing, int l
      ) {
         StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(i, j, k, -1, -1, 0, 5, 5, 7, enumFacing);
         return canStrongholdGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null
            ? new StructureStrongholdPieces.Straight(l, random2, structureBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 6, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.field_143013_d, 1, 1, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 1, 2, 1, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 3, 2, 1, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 1, 2, 5, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 3, 2, 5, Blocks.torch.getDefaultState());
         if (this.expandsX) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 2, 0, 3, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.expandsZ) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 2, 4, 3, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         return true;
      }
   }

   public abstract static class Stronghold extends StructureComponent {
      public StructureStrongholdPieces.Stronghold.Door field_143013_d = StructureStrongholdPieces.Stronghold.Door.OPENING;

      public Stronghold() {
      }

      public Stronghold(int i) {
         super(i);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setString("EntryDoor", this.field_143013_d.name());
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.valueOf(tagCompound.getString("EntryDoor"));
      }

      public void placeDoor(
         World worldIn, Random random2, StructureBoundingBox structureBoundingBox, StructureStrongholdPieces.Stronghold.Door door, int i, int j, int k
      ) {
         switch (door) {
            case OPENING:
            default:
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, i, j, k, i + 3 - 1, j + 3 - 1, k, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false
               );
               break;
            case WOOD_DOOR:
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, j, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, j + 1, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, j + 2, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 1, j + 2, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, j + 2, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, j + 1, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, j, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.oak_door.getDefaultState(), i + 1, j, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.oak_door.getStateFromMeta(8), i + 1, j + 1, k, structureBoundingBox);
               break;
            case GRATES:
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), i + 1, j, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), i + 1, j + 1, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i, j, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i, j + 1, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i, j + 2, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i + 1, j + 2, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i + 2, j + 2, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i + 2, j + 1, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), i + 2, j, k, structureBoundingBox);
               break;
            case IRON_DOOR:
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, j, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, j + 1, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i, j + 2, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 1, j + 2, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, j + 2, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, j + 1, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), i + 2, j, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_door.getDefaultState(), i + 1, j, k, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(8), i + 1, j + 1, k, structureBoundingBox);
               this.setBlockState(
                  worldIn, Blocks.stone_button.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_button, 4)), i + 2, j + 1, k + 1, structureBoundingBox
               );
               this.setBlockState(
                  worldIn, Blocks.stone_button.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_button, 3)), i + 2, j + 1, k - 1, structureBoundingBox
               );
         }
      }

      public StructureStrongholdPieces.Stronghold.Door getRandomDoor(Random random2) {
         int i = random2.nextInt(5);
         switch (i) {
            case 0:
            case 1:
            default:
               return StructureStrongholdPieces.Stronghold.Door.OPENING;
            case 2:
               return StructureStrongholdPieces.Stronghold.Door.WOOD_DOOR;
            case 3:
               return StructureStrongholdPieces.Stronghold.Door.GRATES;
            case 4:
               return StructureStrongholdPieces.Stronghold.Door.IRON_DOOR;
         }
      }

      public StructureComponent getNextComponentNormal(StructureStrongholdPieces.Stairs2 stairs2, List<StructureComponent> list, Random random2, int i, int j) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + j,
                     this.boundingBox.maxZ + 1,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + j,
                     this.boundingBox.minZ + i,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + j,
                     this.boundingBox.minZ + i,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + i,
                     this.boundingBox.minY + j,
                     this.boundingBox.minZ - 1,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public StructureComponent a(StructureStrongholdPieces.Stairs2 stairs2, List<StructureComponent> list, Random random2, int i, int j) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + j,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + j,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + j,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + j,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public StructureComponent u(StructureStrongholdPieces.Stairs2 stairs2, List<StructureComponent> list, Random random2, int i, int j) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + j,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + j,
                     this.boundingBox.minY + i,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.minX + j,
                     this.boundingBox.minY + i,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.func_175953_c(
                     stairs2,
                     list,
                     random2,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + i,
                     this.boundingBox.minZ + j,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public static boolean canStrongholdGoDeeper(StructureBoundingBox structureBoundingBox) {
         return structureBoundingBox != null && structureBoundingBox.minY > 10;
      }

      public enum Door {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;
      }
   }
}
