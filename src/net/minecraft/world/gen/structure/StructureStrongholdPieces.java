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
         private static final long a = 78045452043236L;

         @Override
         public boolean canSpawnMoreStructuresOfType(int var1) {
            return super.canSpawnMoreStructuresOfType(var1) && var1 > 4;
         }
      },
      new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.PortalRoom.class, 20, 1) {
         private static final long a = 121246647958689L;

         @Override
         public boolean canSpawnMoreStructuresOfType(int var1) {
            return super.canSpawnMoreStructuresOfType(var1) && var1 > 5;
         }
      }
   };
   private static List<StructureStrongholdPieces.PieceWeight> structurePieceList;
   private static Class<? extends StructureStrongholdPieces.Stronghold> strongComponentType;
   public static int totalWeight;
   private static final StructureStrongholdPieces.Stones strongholdStones = new StructureStrongholdPieces.Stones();
   private static final long a = 52752509603700L;

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

      for (StructureStrongholdPieces.PieceWeight var6 : pieceWeightArray) {
         var6.instancesSpawned = 0;
         structurePieceList.add(var6);
      }

      strongComponentType = null;
   }

   private static boolean canAddStructurePieces() {
      boolean var3 = false;
      totalWeight = 0;

      for (StructureStrongholdPieces.PieceWeight var5 : structurePieceList) {
         if (var5.instancesLimit > 0 && var5.instancesSpawned < var5.instancesLimit) {
            var3 = true;
         }

         totalWeight = totalWeight + var5.pieceWeight;
      }

      return var3;
   }

   private static StructureStrongholdPieces.Stronghold func_175954_a(
      Class<? extends StructureStrongholdPieces.Stronghold> var0,
      List<StructureComponent> var1,
      Random var2,
      int var3,
      int var4,
      int var5,
      EnumFacing var6,
      int var7
   ) {
      StructureStrongholdPieces.Stronghold var11 = null;
      if (var0 == StructureStrongholdPieces.Straight.class) {
         var11 = StructureStrongholdPieces.Straight.func_175862_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.Prison.class) {
         var11 = StructureStrongholdPieces.Prison.func_175860_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.LeftTurn.class) {
         var11 = StructureStrongholdPieces.LeftTurn.func_175867_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.RightTurn.class) {
         var11 = StructureStrongholdPieces.RightTurn.func_175867_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.RoomCrossing.class) {
         var11 = StructureStrongholdPieces.RoomCrossing.func_175859_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.StairsStraight.class) {
         var11 = StructureStrongholdPieces.StairsStraight.func_175861_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.Stairs.class) {
         var11 = StructureStrongholdPieces.Stairs.func_175863_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.Crossing.class) {
         var11 = StructureStrongholdPieces.Crossing.func_175866_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.ChestCorridor.class) {
         var11 = StructureStrongholdPieces.ChestCorridor.func_175868_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.Library.class) {
         var11 = StructureStrongholdPieces.Library.func_175864_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.PortalRoom.class) {
         var11 = StructureStrongholdPieces.PortalRoom.func_175865_a(var1, var2, var3, var4, var5, var6, var7);
      }

      return var11;
   }

   private static StructureStrongholdPieces.Stronghold func_175955_b(
      StructureStrongholdPieces.Stairs2 var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7
   ) {
      if (!canAddStructurePieces()) {
         return null;
      }

      if (strongComponentType != null) {
         StructureStrongholdPieces.Stronghold var11 = func_175954_a(strongComponentType, var1, var2, var3, var4, var5, var6, var7);
         strongComponentType = null;
         if (var11 != null) {
            return var11;
         }
      }

      int var16 = 0;

      while (var16 < 5) {
         var16++;
         int var12 = var2.nextInt(totalWeight);

         for (StructureStrongholdPieces.PieceWeight var14 : structurePieceList) {
            var12 -= var14.pieceWeight;
            if (var12 < 0) {
               if (!var14.canSpawnMoreStructuresOfType(var7) || var14 == var0.strongholdPieceWeight) {
                  break;
               }

               StructureStrongholdPieces.Stronghold var15 = func_175954_a(var14.pieceClass, var1, var2, var3, var4, var5, var6, var7);
               if (var15 != null) {
                  var14.instancesSpawned++;
                  var0.strongholdPieceWeight = var14;
                  if (!var14.canSpawnMoreStructures()) {
                     structurePieceList.remove(var14);
                  }

                  return var15;
               }
            }
         }
      }

      StructureBoundingBox var17 = StructureStrongholdPieces.Corridor.func_175869_a(var1, var2, var3, var4, var5, var6);
      return var17 != null && var17.minY > 1 ? new StructureStrongholdPieces.Corridor(var7, var2, var17, var6) : null;
   }

   private static StructureComponent func_175953_c(
      StructureStrongholdPieces.Stairs2 var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7
   ) {
      if (var7 > 50) {
         return null;
      }

      if (Math.abs(var3 - var0.getBoundingBox().minX) <= 112 && Math.abs(var5 - var0.getBoundingBox().minZ) <= 112) {
         StructureStrongholdPieces.Stronghold var11 = func_175955_b(var0, var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var11 != null) {
            var1.add(var11);
            var0.field_75026_c.add(var11);
         }

         return var11;
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
      private static final long b = 88448600953720L;

      public ChestCorridor() {
      }

      public ChestCorridor(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.field_143013_d = this.getRandomDoor(var2);
         this.boundingBox = var3;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setBoolean("Chest", this.hasMadeChest);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.hasMadeChest = var1.getBoolean("Chest");
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.W((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StructureStrongholdPieces.ChestCorridor func_175868_a(
         List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6
      ) {
         StructureBoundingBox var9 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
         return canStrongholdGoDeeper(var9) && StructureComponent.findIntersecting(var0, var9) == null
            ? new StructureStrongholdPieces.ChestCorridor(var6, var1, var9, var5)
            : null;
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 4, 4, 6, true, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         this.placeDoor(var1, var2, var3, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.fillWithBlocks(var1, var3, 3, 1, 2, 3, 1, 4, Blocks.stonebrick.getDefaultState(), Blocks.stonebrick.getDefaultState(), false);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 1, 1, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 1, 5, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 2, 2, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 2, 4, var3);

         for (int var7 = 2; var7 <= 4; var7++) {
            this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 2, 1, var7, var3);
         }

         if (!this.hasMadeChest && var3.isVecInside(new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3)))) {
            this.hasMadeChest = true;
            this.generateChestContents(
               var1,
               var3,
               var2,
               3,
               2,
               3,
               WeightedRandomChestContent.func_177629_a(strongholdChestContents, Items.enchanted_book.getRandom(var2)),
               2 + var2.nextInt(2)
            );
         }

         return true;
      }
   }

   public static class Corridor extends StructureStrongholdPieces.Stronghold {
      private int field_74993_a;
      private static final long b = 94141847458886L;

      public Corridor() {
      }

      public Corridor(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.boundingBox = var3;
         this.field_74993_a = var4 != EnumFacing.NORTH && var4 != EnumFacing.SOUTH ? var3.G() : var3.S();
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setInteger("Steps", this.field_74993_a);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.field_74993_a = var1.getInteger("Steps");
      }

      public static StructureBoundingBox func_175869_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         StructureBoundingBox var10 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -1, 0, 5, 5, 4, var5);
         StructureComponent var11 = StructureComponent.findIntersecting(var0, var10);
         if (var11 == null) {
            return null;
         }

         if (var11.getBoundingBox().minY == var10.minY) {
            for (int var12 = 3; var12 >= 1; var12--) {
               var10 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -1, 0, 5, 5, var12 - 1, var5);
               if (!var11.getBoundingBox().intersectsWith(var10)) {
                  return StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -1, 0, 5, 5, var12, var5);
               }
            }
         }

         return null;
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         for (int var7 = 0; var7 < this.field_74993_a; var7++) {
            this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 0, 0, var7, var3);
            this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 1, 0, var7, var3);
            this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 2, 0, var7, var3);
            this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 3, 0, var7, var3);
            this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 4, 0, var7, var3);

            for (int var8 = 1; var8 <= 3; var8++) {
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 0, var8, var7, var3);
               this.setBlockState(var1, Blocks.air.getDefaultState(), 1, var8, var7, var3);
               this.setBlockState(var1, Blocks.air.getDefaultState(), 2, var8, var7, var3);
               this.setBlockState(var1, Blocks.air.getDefaultState(), 3, var8, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 4, var8, var7, var3);
            }

            this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 0, 4, var7, var3);
            this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 1, 4, var7, var3);
            this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 2, 4, var7, var3);
            this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 3, 4, var7, var3);
            this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 4, 4, var7, var3);
         }

         return true;
      }
   }

   public static class Crossing extends StructureStrongholdPieces.Stronghold {
      private boolean field_74996_b;
      private boolean field_74997_c;
      private boolean field_74995_d;
      private boolean field_74999_h;
      private static final long b = 53678967703418L;

      public Crossing() {
      }

      public Crossing(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.field_143013_d = this.getRandomDoor(var2);
         this.boundingBox = var3;
         this.field_74996_b = var2.nextBoolean();
         this.field_74997_c = var2.nextBoolean();
         this.field_74995_d = var2.nextBoolean();
         this.field_74999_h = var2.nextInt(3) > 0;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setBoolean("leftLow", this.field_74996_b);
         var1.setBoolean("leftHigh", this.field_74997_c);
         var1.setBoolean("rightLow", this.field_74995_d);
         var1.setBoolean("rightHigh", this.field_74999_h);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.field_74996_b = var1.getBoolean("leftLow");
         this.field_74997_c = var1.getBoolean("leftHigh");
         this.field_74995_d = var1.getBoolean("rightLow");
         this.field_74999_h = var1.getBoolean("rightHigh");
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         int var7 = 3;
         int var8 = 5;
         if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.NORTH) {
            var7 = 8 - var7;
            var8 = 8 - var8;
         }

         this.W((StructureStrongholdPieces.Stairs2)var1, var2, var3, 5, 1);
         if (this.field_74996_b) {
            this.a((StructureStrongholdPieces.Stairs2)var1, var2, var3, var7, 1);
         }

         if (this.field_74997_c) {
            this.a((StructureStrongholdPieces.Stairs2)var1, var2, var3, var8, 7);
         }

         if (this.field_74995_d) {
            this.u((StructureStrongholdPieces.Stairs2)var1, var2, var3, var7, 1);
         }

         if (this.field_74999_h) {
            this.u((StructureStrongholdPieces.Stairs2)var1, var2, var3, var8, 7);
         }
      }

      public static StructureStrongholdPieces.Crossing func_175866_a(
         List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6
      ) {
         StructureBoundingBox var9 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -4, -3, 0, 10, 9, 11, var5);
         return canStrongholdGoDeeper(var9) && StructureComponent.findIntersecting(var0, var9) == null
            ? new StructureStrongholdPieces.Crossing(var6, var1, var9, var5)
            : null;
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 9, 8, 10, true, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, this.field_143013_d, 4, 3, 0);
         if (this.field_74996_b) {
            this.fillWithBlocks(var1, var3, 0, 3, 1, 0, 5, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.field_74995_d) {
            this.fillWithBlocks(var1, var3, 9, 3, 1, 9, 5, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.field_74997_c) {
            this.fillWithBlocks(var1, var3, 0, 5, 7, 0, 7, 9, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.field_74999_h) {
            this.fillWithBlocks(var1, var3, 9, 5, 7, 9, 7, 9, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         this.fillWithBlocks(var1, var3, 5, 1, 10, 7, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithRandomizedBlocks(var1, var3, 1, 2, 1, 8, 2, 6, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 4, 1, 5, 4, 4, 9, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 8, 1, 5, 8, 4, 9, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 1, 4, 7, 3, 4, 9, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 1, 3, 5, 3, 3, 6, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(var1, var3, 1, 3, 4, 3, 3, 4, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 1, 4, 6, 3, 4, 6, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithRandomizedBlocks(var1, var3, 5, 1, 7, 7, 1, 8, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(var1, var3, 5, 1, 9, 7, 1, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 5, 2, 7, 7, 2, 7, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 4, 5, 7, 4, 5, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 8, 5, 7, 8, 5, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 5, 5, 7, 7, 5, 9, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false);
         this.setBlockState(var1, Blocks.torch.getDefaultState(), 6, 5, 6, var3);
         return true;
      }
   }

   public static class LeftTurn extends StructureStrongholdPieces.Stronghold {
      private static final long b = 98785213541309L;

      public LeftTurn() {
      }

      public LeftTurn(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.field_143013_d = this.getRandomDoor(var2);
         this.boundingBox = var3;
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.u((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         } else {
            this.a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         }
      }

      public static StructureStrongholdPieces.LeftTurn func_175867_a(
         List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6
      ) {
         StructureBoundingBox var9 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -1, 0, 5, 5, 5, var5);
         return canStrongholdGoDeeper(var9) && StructureComponent.findIntersecting(var0, var9) == null
            ? new StructureStrongholdPieces.LeftTurn(var6, var1, var9, var5)
            : null;
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 4, 4, 4, true, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.fillWithBlocks(var1, var3, 4, 1, 1, 4, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         } else {
            this.fillWithBlocks(var1, var3, 0, 1, 1, 0, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
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
      private static final long b = 40740148265433L;

      public Library() {
      }

      public Library(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.field_143013_d = this.getRandomDoor(var2);
         this.boundingBox = var3;
         this.isLargeRoom = var3.getYSize() > 6;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setBoolean("Tall", this.isLargeRoom);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.isLargeRoom = var1.getBoolean("Tall");
      }

      public static StructureStrongholdPieces.Library func_175864_a(
         List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6
      ) {
         StructureBoundingBox var10 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -4, -1, 0, 14, 11, 15, var5);
         if (!canStrongholdGoDeeper(var10) || StructureComponent.findIntersecting(var0, var10) != null) {
            var10 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -4, -1, 0, 14, 6, 15, var5);
            if (!canStrongholdGoDeeper(var10) || StructureComponent.findIntersecting(var0, var10) != null) {
               return null;
            }
         }

         return new StructureStrongholdPieces.Library(var6, var1, var10, var5);
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         byte var7 = 11;
         if (!this.isLargeRoom) {
            var7 = 6;
         }

         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 13, var7 - 1, 14, true, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, this.field_143013_d, 4, 1, 0);
         this.func_175805_a(var1, var3, var2, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.web.getDefaultState(), Blocks.web.getDefaultState(), false);

         for (int var10 = 1; var10 <= 13; var10++) {
            if ((var10 - 1) % 4 == 0) {
               this.fillWithBlocks(var1, var3, 1, 1, var10, 1, 4, var10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
               this.fillWithBlocks(var1, var3, 12, 1, var10, 12, 4, var10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
               this.setBlockState(var1, Blocks.torch.getDefaultState(), 2, 3, var10, var3);
               this.setBlockState(var1, Blocks.torch.getDefaultState(), 11, 3, var10, var3);
               if (this.isLargeRoom) {
                  this.fillWithBlocks(var1, var3, 1, 6, var10, 1, 9, var10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
                  this.fillWithBlocks(var1, var3, 12, 6, var10, 12, 9, var10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
               }
            } else {
               this.fillWithBlocks(var1, var3, 1, 1, var10, 1, 4, var10, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false);
               this.fillWithBlocks(var1, var3, 12, 1, var10, 12, 4, var10, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false);
               if (this.isLargeRoom) {
                  this.fillWithBlocks(var1, var3, 1, 6, var10, 1, 9, var10, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false);
                  this.fillWithBlocks(var1, var3, 12, 6, var10, 12, 9, var10, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false);
               }
            }
         }

         for (int var13 = 3; var13 < 12; var13 += 2) {
            this.fillWithBlocks(var1, var3, 3, 1, var13, 4, 3, var13, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false);
            this.fillWithBlocks(var1, var3, 6, 1, var13, 7, 3, var13, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false);
            this.fillWithBlocks(var1, var3, 9, 1, var13, 10, 3, var13, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false);
         }

         if (this.isLargeRoom) {
            this.fillWithBlocks(var1, var3, 1, 5, 1, 3, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(var1, var3, 10, 5, 1, 12, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(var1, var3, 4, 5, 1, 9, 5, 2, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(var1, var3, 4, 5, 12, 9, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.setBlockState(var1, Blocks.planks.getDefaultState(), 9, 5, 11, var3);
            this.setBlockState(var1, Blocks.planks.getDefaultState(), 8, 5, 11, var3);
            this.setBlockState(var1, Blocks.planks.getDefaultState(), 9, 5, 10, var3);
            this.fillWithBlocks(var1, var3, 3, 6, 2, 3, 6, 12, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
            this.fillWithBlocks(var1, var3, 10, 6, 2, 10, 6, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
            this.fillWithBlocks(var1, var3, 4, 6, 2, 9, 6, 2, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
            this.fillWithBlocks(var1, var3, 4, 6, 12, 8, 6, 12, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), 9, 6, 11, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), 8, 6, 11, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), 9, 6, 10, var3);
            int var14 = this.getMetadataWithOffset(Blocks.ladder, 3);
            this.setBlockState(var1, Blocks.ladder.getStateFromMeta(var14), 10, 1, 13, var3);
            this.setBlockState(var1, Blocks.ladder.getStateFromMeta(var14), 10, 2, 13, var3);
            this.setBlockState(var1, Blocks.ladder.getStateFromMeta(var14), 10, 3, 13, var3);
            this.setBlockState(var1, Blocks.ladder.getStateFromMeta(var14), 10, 4, 13, var3);
            this.setBlockState(var1, Blocks.ladder.getStateFromMeta(var14), 10, 5, 13, var3);
            this.setBlockState(var1, Blocks.ladder.getStateFromMeta(var14), 10, 6, 13, var3);
            this.setBlockState(var1, Blocks.ladder.getStateFromMeta(var14), 10, 7, 13, var3);
            byte var11 = 7;
            byte var12 = 7;
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11 - 1, 9, var12, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11, 9, var12, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11 - 1, 8, var12, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11, 8, var12, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11 - 1, 7, var12, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11, 7, var12, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11 - 2, 7, var12, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11 + 1, 7, var12, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11 - 1, 7, var12 - 1, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11 - 1, 7, var12 + 1, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11, 7, var12 - 1, var3);
            this.setBlockState(var1, Blocks.oak_fence.getDefaultState(), var11, 7, var12 + 1, var3);
            this.setBlockState(var1, Blocks.torch.getDefaultState(), var11 - 2, 8, var12, var3);
            this.setBlockState(var1, Blocks.torch.getDefaultState(), var11 + 1, 8, var12, var3);
            this.setBlockState(var1, Blocks.torch.getDefaultState(), var11 - 1, 8, var12 - 1, var3);
            this.setBlockState(var1, Blocks.torch.getDefaultState(), var11 - 1, 8, var12 + 1, var3);
            this.setBlockState(var1, Blocks.torch.getDefaultState(), var11, 8, var12 - 1, var3);
            this.setBlockState(var1, Blocks.torch.getDefaultState(), var11, 8, var12 + 1, var3);
         }

         this.generateChestContents(
            var1,
            var3,
            var2,
            3,
            3,
            5,
            WeightedRandomChestContent.func_177629_a(strongholdLibraryChestContents, Items.enchanted_book.getRandom(var2, 1, 5, 2)),
            1 + var2.nextInt(4)
         );
         if (this.isLargeRoom) {
            this.setBlockState(var1, Blocks.air.getDefaultState(), 12, 9, 1, var3);
            this.generateChestContents(
               var1,
               var3,
               var2,
               12,
               8,
               1,
               WeightedRandomChestContent.func_177629_a(strongholdLibraryChestContents, Items.enchanted_book.getRandom(var2, 1, 5, 2)),
               1 + var2.nextInt(4)
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
      private static final long b = 120027453006196L;

      public PieceWeight(Class<? extends StructureStrongholdPieces.Stronghold> var1, int var2, int var3) {
         this.pieceClass = var1;
         this.pieceWeight = var2;
         this.instancesLimit = var3;
      }

      public boolean canSpawnMoreStructuresOfType(int var1) {
         return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
      }

      public boolean canSpawnMoreStructures() {
         return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
      }
   }

   public static class PortalRoom extends StructureStrongholdPieces.Stronghold {
      private boolean hasSpawner;
      private static final long b = 75669339547891L;

      public PortalRoom() {
      }

      public PortalRoom(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.boundingBox = var3;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setBoolean("Mob", this.hasSpawner);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.hasSpawner = var1.getBoolean("Mob");
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         if (var1 != null) {
            ((StructureStrongholdPieces.Stairs2)var1).strongholdPortalRoom = this;
         }
      }

      public static StructureStrongholdPieces.PortalRoom func_175865_a(
         List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6
      ) {
         StructureBoundingBox var9 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -4, -1, 0, 11, 8, 16, var5);
         return canStrongholdGoDeeper(var9) && StructureComponent.findIntersecting(var0, var9) == null
            ? new StructureStrongholdPieces.PortalRoom(var6, var1, var9, var5)
            : null;
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 10, 7, 15, false, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, StructureStrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
         int var7 = 6;
         this.fillWithRandomizedBlocks(var1, var3, 1, var7, 1, 1, var7, 14, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 9, var7, 1, 9, var7, 14, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 2, var7, 1, 8, var7, 2, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 2, var7, 14, 8, var7, 14, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 1, 1, 1, 2, 1, 4, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 8, 1, 1, 9, 1, 4, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(var1, var3, 1, 1, 1, 1, 1, 3, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 9, 1, 1, 9, 1, 3, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false);
         this.fillWithRandomizedBlocks(var1, var3, 3, 1, 8, 7, 1, 12, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(var1, var3, 4, 1, 9, 6, 1, 11, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false);

         for (int var8 = 3; var8 < 14; var8 += 2) {
            this.fillWithBlocks(var1, var3, 0, 3, var8, 0, 4, var8, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
            this.fillWithBlocks(var1, var3, 10, 3, var8, 10, 4, var8, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
         }

         for (int var16 = 2; var16 < 9; var16 += 2) {
            this.fillWithBlocks(var1, var3, var16, 3, 15, var16, 4, 15, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
         }

         int var17 = this.getMetadataWithOffset(Blocks.stone_brick_stairs, 3);
         this.fillWithRandomizedBlocks(var1, var3, 4, 1, 5, 6, 1, 7, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 4, 2, 6, 6, 2, 7, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 4, 3, 7, 6, 3, 7, false, var2, StructureStrongholdPieces.strongholdStones);

         for (int var9 = 4; var9 <= 6; var9++) {
            this.setBlockState(var1, Blocks.stone_brick_stairs.getStateFromMeta(var17), var9, 1, 4, var3);
            this.setBlockState(var1, Blocks.stone_brick_stairs.getStateFromMeta(var17), var9, 2, 5, var3);
            this.setBlockState(var1, Blocks.stone_brick_stairs.getStateFromMeta(var17), var9, 3, 6, var3);
         }

         int var18 = EnumFacing.NORTH.getHorizontalIndex();
         int var10 = EnumFacing.SOUTH.getHorizontalIndex();
         int var11 = EnumFacing.EAST.getHorizontalIndex();
         int var12 = EnumFacing.WEST.getHorizontalIndex();
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  var18 = EnumFacing.SOUTH.getHorizontalIndex();
                  var10 = EnumFacing.NORTH.getHorizontalIndex();
                  break;
               case WEST:
                  var18 = EnumFacing.WEST.getHorizontalIndex();
                  var10 = EnumFacing.EAST.getHorizontalIndex();
                  var11 = EnumFacing.SOUTH.getHorizontalIndex();
                  var12 = EnumFacing.NORTH.getHorizontalIndex();
                  break;
               case EAST:
                  var18 = EnumFacing.EAST.getHorizontalIndex();
                  var10 = EnumFacing.WEST.getHorizontalIndex();
                  var11 = EnumFacing.SOUTH.getHorizontalIndex();
                  var12 = EnumFacing.NORTH.getHorizontalIndex();
            }
         }

         this.setBlockState(var1, Blocks.end_portal_frame.getStateFromMeta(var18).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 4, 3, 8, var3);
         this.setBlockState(var1, Blocks.end_portal_frame.getStateFromMeta(var18).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 5, 3, 8, var3);
         this.setBlockState(var1, Blocks.end_portal_frame.getStateFromMeta(var18).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 6, 3, 8, var3);
         this.setBlockState(
            var1, Blocks.end_portal_frame.getStateFromMeta(var10).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 4, 3, 12, var3
         );
         this.setBlockState(
            var1, Blocks.end_portal_frame.getStateFromMeta(var10).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 5, 3, 12, var3
         );
         this.setBlockState(
            var1, Blocks.end_portal_frame.getStateFromMeta(var10).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 6, 3, 12, var3
         );
         this.setBlockState(var1, Blocks.end_portal_frame.getStateFromMeta(var11).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 3, 3, 9, var3);
         this.setBlockState(
            var1, Blocks.end_portal_frame.getStateFromMeta(var11).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 3, 3, 10, var3
         );
         this.setBlockState(
            var1, Blocks.end_portal_frame.getStateFromMeta(var11).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 3, 3, 11, var3
         );
         this.setBlockState(var1, Blocks.end_portal_frame.getStateFromMeta(var12).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 7, 3, 9, var3);
         this.setBlockState(
            var1, Blocks.end_portal_frame.getStateFromMeta(var12).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 7, 3, 10, var3
         );
         this.setBlockState(
            var1, Blocks.end_portal_frame.getStateFromMeta(var12).withProperty(BlockEndPortalFrame.EYE, var2.nextFloat() > 0.9F), 7, 3, 11, var3
         );
         if (!this.hasSpawner) {
            var7 = this.getYWithOffset(3);
            BlockPos var13 = new BlockPos(this.getXWithOffset(5, 6), var7, this.getZWithOffset(5, 6));
            if (var3.isVecInside(var13)) {
               this.hasSpawner = true;
               var1.setBlockState(var13, Blocks.mob_spawner.getDefaultState(), 2);
               TileEntity var14 = var1.getTileEntity(var13);
               if (var14 instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)var14).getSpawnerBaseLogic().setEntityName("Silverfish");
               }
            }
         }

         return true;
      }
   }

   public static class Prison extends StructureStrongholdPieces.Stronghold {
      private static final long b = 97633624903990L;

      public Prison() {
      }

      public Prison(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.field_143013_d = this.getRandomDoor(var2);
         this.boundingBox = var3;
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.W((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StructureStrongholdPieces.Prison func_175860_a(
         List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6
      ) {
         StructureBoundingBox var9 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -1, 0, 9, 5, 11, var5);
         return canStrongholdGoDeeper(var9) && StructureComponent.findIntersecting(var0, var9) == null
            ? new StructureStrongholdPieces.Prison(var6, var1, var9, var5)
            : null;
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 8, 4, 10, true, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         this.fillWithBlocks(var1, var3, 1, 1, 10, 3, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithRandomizedBlocks(var1, var3, 4, 1, 1, 4, 3, 1, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 4, 1, 3, 4, 3, 3, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 4, 1, 7, 4, 3, 7, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(var1, var3, 4, 1, 9, 4, 3, 9, false, var2, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(var1, var3, 4, 1, 4, 4, 3, 6, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 5, 1, 5, 7, 3, 5, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
         this.setBlockState(var1, Blocks.iron_bars.getDefaultState(), 4, 3, 2, var3);
         this.setBlockState(var1, Blocks.iron_bars.getDefaultState(), 4, 3, 8, var3);
         this.setBlockState(var1, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3)), 4, 1, 2, var3);
         this.setBlockState(var1, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3) + 8), 4, 2, 2, var3);
         this.setBlockState(var1, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3)), 4, 1, 8, var3);
         this.setBlockState(var1, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3) + 8), 4, 2, 8, var3);
         return true;
      }
   }

   public static class RightTurn extends StructureStrongholdPieces.LeftTurn {
      private static final long c = 85362289470711L;

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         } else {
            this.u((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         }
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 4, 4, 4, true, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.fillWithBlocks(var1, var3, 0, 1, 1, 0, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         } else {
            this.fillWithBlocks(var1, var3, 4, 1, 1, 4, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
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
      private static final long b = 116045137394020L;

      public RoomCrossing() {
      }

      public RoomCrossing(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.field_143013_d = this.getRandomDoor(var2);
         this.boundingBox = var3;
         this.roomType = var2.nextInt(5);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setInteger("Type", this.roomType);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.roomType = var1.getInteger("Type");
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.W((StructureStrongholdPieces.Stairs2)var1, var2, var3, 4, 1);
         this.a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 4);
         this.u((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 4);
      }

      public static StructureStrongholdPieces.RoomCrossing func_175859_a(
         List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6
      ) {
         StructureBoundingBox var9 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -4, -1, 0, 11, 7, 11, var5);
         return canStrongholdGoDeeper(var9) && StructureComponent.findIntersecting(var0, var9) == null
            ? new StructureStrongholdPieces.RoomCrossing(var6, var1, var9, var5)
            : null;
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 10, 6, 10, true, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, this.field_143013_d, 4, 1, 0);
         this.fillWithBlocks(var1, var3, 4, 1, 10, 6, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 0, 1, 4, 0, 3, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(var1, var3, 10, 1, 4, 10, 3, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         switch (this.roomType) {
            case 0:
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 5, 1, 5, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 5, 2, 5, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 5, 3, 5, var3);
               this.setBlockState(var1, Blocks.torch.getDefaultState(), 4, 3, 5, var3);
               this.setBlockState(var1, Blocks.torch.getDefaultState(), 6, 3, 5, var3);
               this.setBlockState(var1, Blocks.torch.getDefaultState(), 5, 3, 4, var3);
               this.setBlockState(var1, Blocks.torch.getDefaultState(), 5, 3, 6, var3);
               this.setBlockState(var1, Blocks.stone_slab.getDefaultState(), 4, 1, 4, var3);
               this.setBlockState(var1, Blocks.stone_slab.getDefaultState(), 4, 1, 5, var3);
               this.setBlockState(var1, Blocks.stone_slab.getDefaultState(), 4, 1, 6, var3);
               this.setBlockState(var1, Blocks.stone_slab.getDefaultState(), 6, 1, 4, var3);
               this.setBlockState(var1, Blocks.stone_slab.getDefaultState(), 6, 1, 5, var3);
               this.setBlockState(var1, Blocks.stone_slab.getDefaultState(), 6, 1, 6, var3);
               this.setBlockState(var1, Blocks.stone_slab.getDefaultState(), 5, 1, 4, var3);
               this.setBlockState(var1, Blocks.stone_slab.getDefaultState(), 5, 1, 6, var3);
               break;
            case 1:
               for (int var11 = 0; var11 < 5; var11++) {
                  this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 3, 1, 3 + var11, var3);
                  this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 7, 1, 3 + var11, var3);
                  this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 3 + var11, 1, 3, var3);
                  this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 3 + var11, 1, 7, var3);
               }

               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 5, 1, 5, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 5, 2, 5, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 5, 3, 5, var3);
               this.setBlockState(var1, Blocks.flowing_water.getDefaultState(), 5, 4, 5, var3);
               break;
            case 2:
               for (int var7 = 1; var7 <= 9; var7++) {
                  this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 1, 3, var7, var3);
                  this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 9, 3, var7, var3);
               }

               for (int var8 = 1; var8 <= 9; var8++) {
                  this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), var8, 3, 1, var3);
                  this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), var8, 3, 9, var3);
               }

               this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 5, 1, 4, var3);
               this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 5, 1, 6, var3);
               this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 5, 3, 4, var3);
               this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 5, 3, 6, var3);
               this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 4, 1, 5, var3);
               this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 6, 1, 5, var3);
               this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 4, 3, 5, var3);
               this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 6, 3, 5, var3);

               for (int var9 = 1; var9 <= 3; var9++) {
                  this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 4, var9, 4, var3);
                  this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 6, var9, 4, var3);
                  this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 4, var9, 6, var3);
                  this.setBlockState(var1, Blocks.cobblestone.getDefaultState(), 6, var9, 6, var3);
               }

               this.setBlockState(var1, Blocks.torch.getDefaultState(), 5, 3, 5, var3);

               for (int var10 = 2; var10 <= 8; var10++) {
                  this.setBlockState(var1, Blocks.planks.getDefaultState(), 2, 3, var10, var3);
                  this.setBlockState(var1, Blocks.planks.getDefaultState(), 3, 3, var10, var3);
                  if (var10 <= 3 || var10 >= 7) {
                     this.setBlockState(var1, Blocks.planks.getDefaultState(), 4, 3, var10, var3);
                     this.setBlockState(var1, Blocks.planks.getDefaultState(), 5, 3, var10, var3);
                     this.setBlockState(var1, Blocks.planks.getDefaultState(), 6, 3, var10, var3);
                  }

                  this.setBlockState(var1, Blocks.planks.getDefaultState(), 7, 3, var10, var3);
                  this.setBlockState(var1, Blocks.planks.getDefaultState(), 8, 3, var10, var3);
               }

               this.setBlockState(var1, Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())), 9, 1, 3, var3);
               this.setBlockState(var1, Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())), 9, 2, 3, var3);
               this.setBlockState(var1, Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())), 9, 3, 3, var3);
               this.generateChestContents(
                  var1,
                  var3,
                  var2,
                  3,
                  4,
                  8,
                  WeightedRandomChestContent.func_177629_a(strongholdRoomCrossingChestContents, Items.enchanted_book.getRandom(var2)),
                  1 + var2.nextInt(4)
               );
         }

         return true;
      }
   }

   public static class Stairs extends StructureStrongholdPieces.Stronghold {
      private boolean field_75024_a;
      private static final long b = 22256484920355L;

      public Stairs() {
      }

      public Stairs(int var1, Random var2, int var3, int var4) {
         super(var1);
         this.field_75024_a = true;
         this.coordBaseMode = EnumFacing.Plane.HORIZONTAL.random(var2);
         this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.OPENING;
         switch (this.coordBaseMode) {
            case SOUTH:
            case NORTH:
               this.boundingBox = new StructureBoundingBox(var3, 64, var4, var3 + 5 - 1, 74, var4 + 5 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(var3, 64, var4, var3 + 5 - 1, 74, var4 + 5 - 1);
         }
      }

      public Stairs(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_75024_a = false;
         this.coordBaseMode = var4;
         this.field_143013_d = this.getRandomDoor(var2);
         this.boundingBox = var3;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setBoolean("Source", this.field_75024_a);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.field_75024_a = var1.getBoolean("Source");
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         if (this.field_75024_a) {
            StructureStrongholdPieces.strongComponentType = StructureStrongholdPieces.Crossing.class;
         }

         this.W((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StructureStrongholdPieces.Stairs func_175863_a(
         List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6
      ) {
         StructureBoundingBox var9 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -7, 0, 5, 11, 5, var5);
         return canStrongholdGoDeeper(var9) && StructureComponent.findIntersecting(var0, var9) == null
            ? new StructureStrongholdPieces.Stairs(var6, var1, var9, var5)
            : null;
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 4, 10, 4, true, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, this.field_143013_d, 1, 7, 0);
         this.placeDoor(var1, var2, var3, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 2, 6, 1, var3);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 1, 5, 1, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 6, 1, var3);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 1, 5, 2, var3);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 1, 4, 3, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 5, 3, var3);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 2, 4, 3, var3);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 3, 3, 3, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 3, 4, 3, var3);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 3, 3, 2, var3);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 3, 2, 1, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 3, 3, 1, var3);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 2, 2, 1, var3);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 1, 1, 1, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 2, 1, var3);
         this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 1, 1, 2, var3);
         this.setBlockState(var1, Blocks.stone_slab.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 1, 3, var3);
         return true;
      }
   }

   public static class Stairs2 extends StructureStrongholdPieces.Stairs {
      public StructureStrongholdPieces.PieceWeight strongholdPieceWeight;
      public StructureStrongholdPieces.PortalRoom strongholdPortalRoom;
      public List<StructureComponent> field_75026_c = Lists.newArrayList();
      private static final long c = 66282815303087L;

      public Stairs2() {
      }

      public Stairs2(int var1, Random var2, int var3, int var4) {
         super(0, var2, var3, var4);
      }

      @Override
      public BlockPos getBoundingBoxCenter() {
         return this.strongholdPortalRoom != null ? this.strongholdPortalRoom.getBoundingBoxCenter() : super.getBoundingBoxCenter();
      }
   }

   public static class StairsStraight extends StructureStrongholdPieces.Stronghold {
      private static final long b = 104552364647408L;

      public StairsStraight() {
      }

      public StairsStraight(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.field_143013_d = this.getRandomDoor(var2);
         this.boundingBox = var3;
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.W((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StructureStrongholdPieces.StairsStraight func_175861_a(
         List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6
      ) {
         StructureBoundingBox var9 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -7, 0, 5, 11, 8, var5);
         return canStrongholdGoDeeper(var9) && StructureComponent.findIntersecting(var0, var9) == null
            ? new StructureStrongholdPieces.StairsStraight(var6, var1, var9, var5)
            : null;
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 4, 10, 7, true, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, this.field_143013_d, 1, 7, 0);
         this.placeDoor(var1, var2, var3, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);
         int var7 = this.getMetadataWithOffset(Blocks.stone_stairs, 2);

         for (int var8 = 0; var8 < 6; var8++) {
            this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 1, 6 - var8, 1 + var8, var3);
            this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 2, 6 - var8, 1 + var8, var3);
            this.setBlockState(var1, Blocks.stone_stairs.getStateFromMeta(var7), 3, 6 - var8, 1 + var8, var3);
            if (var8 < 5) {
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 1, 5 - var8, 1 + var8, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 2, 5 - var8, 1 + var8, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), 3, 5 - var8, 1 + var8, var3);
            }
         }

         return true;
      }
   }

   public static class Stones extends StructureComponent.BlockSelector {
      private static final long a = 86426317688353L;

      public Stones() {
      }

      @Override
      public void selectBlocks(Random var1, int var2, int var3, int var4, boolean var5) {
         if (var5) {
            float var9 = var1.nextFloat();
            if (var9 < 0.2F) {
               this.blockstate = Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.CRACKED_META);
            } else if (var9 < 0.5F) {
               this.blockstate = Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.MOSSY_META);
            } else if (var9 < 0.55F) {
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
      private static final long b = 31820415018191L;

      public Straight() {
      }

      public Straight(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.coordBaseMode = var4;
         this.field_143013_d = this.getRandomDoor(var2);
         this.boundingBox = var3;
         this.expandsX = var2.nextInt(2) == 0;
         this.expandsZ = var2.nextInt(2) == 0;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         super.writeStructureToNBT(var1);
         var1.setBoolean("Left", this.expandsX);
         var1.setBoolean("Right", this.expandsZ);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         super.readStructureFromNBT(var1);
         this.expandsX = var1.getBoolean("Left");
         this.expandsZ = var1.getBoolean("Right");
      }

      @Override
      public void buildComponent(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.W((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         if (this.expandsX) {
            this.a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 2);
         }

         if (this.expandsZ) {
            this.u((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 2);
         }
      }

      public static StructureStrongholdPieces.Straight func_175862_a(
         List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6
      ) {
         StructureBoundingBox var9 = StructureBoundingBox.getComponentToAddBoundingBox(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
         return canStrongholdGoDeeper(var9) && StructureComponent.findIntersecting(var0, var9) == null
            ? new StructureStrongholdPieces.Straight(var6, var1, var9, var5)
            : null;
      }

      @Override
      public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
         if (this.isLiquidInStructureBoundingBox(var1, var3)) {
            return false;
         }

         this.fillWithRandomizedBlocks(var1, var3, 0, 0, 0, 4, 4, 6, true, var2, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         this.placeDoor(var1, var2, var3, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.randomlyPlaceBlock(var1, var3, var2, 0.1F, 1, 2, 1, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(var1, var3, var2, 0.1F, 3, 2, 1, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(var1, var3, var2, 0.1F, 1, 2, 5, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(var1, var3, var2, 0.1F, 3, 2, 5, Blocks.torch.getDefaultState());
         if (this.expandsX) {
            this.fillWithBlocks(var1, var3, 0, 1, 2, 0, 3, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.expandsZ) {
            this.fillWithBlocks(var1, var3, 4, 1, 2, 4, 3, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         return true;
      }
   }

   public abstract static class Stronghold extends StructureComponent {
      public StructureStrongholdPieces.Stronghold.Door field_143013_d = StructureStrongholdPieces.Stronghold.Door.OPENING;
      private static final long d = 139091803513956L;

      public Stronghold() {
      }

      public Stronghold(int var1) {
         super(var1);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound var1) {
         var1.setString("EntryDoor", this.field_143013_d.name());
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound var1) {
         this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.valueOf(var1.getString("EntryDoor"));
      }

      public void placeDoor(World var1, Random var2, StructureBoundingBox var3, StructureStrongholdPieces.Stronghold.Door var4, int var5, int var6, int var7) {
         switch (var4) {
            case OPENING:
            default:
               this.fillWithBlocks(
                  var1, var3, var5, var6, var7, var5 + 3 - 1, var6 + 3 - 1, var7, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false
               );
               break;
            case WOOD_DOOR:
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5, var6, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5, var6 + 1, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5, var6 + 2, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5 + 1, var6 + 2, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5 + 2, var6 + 2, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5 + 2, var6 + 1, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5 + 2, var6, var7, var3);
               this.setBlockState(var1, Blocks.oak_door.getDefaultState(), var5 + 1, var6, var7, var3);
               this.setBlockState(var1, Blocks.oak_door.getStateFromMeta(8), var5 + 1, var6 + 1, var7, var3);
               break;
            case GRATES:
               this.setBlockState(var1, Blocks.air.getDefaultState(), var5 + 1, var6, var7, var3);
               this.setBlockState(var1, Blocks.air.getDefaultState(), var5 + 1, var6 + 1, var7, var3);
               this.setBlockState(var1, Blocks.iron_bars.getDefaultState(), var5, var6, var7, var3);
               this.setBlockState(var1, Blocks.iron_bars.getDefaultState(), var5, var6 + 1, var7, var3);
               this.setBlockState(var1, Blocks.iron_bars.getDefaultState(), var5, var6 + 2, var7, var3);
               this.setBlockState(var1, Blocks.iron_bars.getDefaultState(), var5 + 1, var6 + 2, var7, var3);
               this.setBlockState(var1, Blocks.iron_bars.getDefaultState(), var5 + 2, var6 + 2, var7, var3);
               this.setBlockState(var1, Blocks.iron_bars.getDefaultState(), var5 + 2, var6 + 1, var7, var3);
               this.setBlockState(var1, Blocks.iron_bars.getDefaultState(), var5 + 2, var6, var7, var3);
               break;
            case IRON_DOOR:
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5, var6, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5, var6 + 1, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5, var6 + 2, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5 + 1, var6 + 2, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5 + 2, var6 + 2, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5 + 2, var6 + 1, var7, var3);
               this.setBlockState(var1, Blocks.stonebrick.getDefaultState(), var5 + 2, var6, var7, var3);
               this.setBlockState(var1, Blocks.iron_door.getDefaultState(), var5 + 1, var6, var7, var3);
               this.setBlockState(var1, Blocks.iron_door.getStateFromMeta(8), var5 + 1, var6 + 1, var7, var3);
               this.setBlockState(
                  var1, Blocks.stone_button.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_button, 4)), var5 + 2, var6 + 1, var7 + 1, var3
               );
               this.setBlockState(
                  var1, Blocks.stone_button.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_button, 3)), var5 + 2, var6 + 1, var7 - 1, var3
               );
         }
      }

      public StructureStrongholdPieces.Stronghold.Door getRandomDoor(Random var1) {
         int var4 = var1.nextInt(5);
         switch (var4) {
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

      public StructureComponent W(StructureStrongholdPieces.Stairs2 var1, List<StructureComponent> var2, Random var3, int var4, int var5) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.minX + var4,
                     this.boundingBox.minY + var5,
                     this.boundingBox.maxZ + 1,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + var5,
                     this.boundingBox.minZ + var4,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + var5,
                     this.boundingBox.minZ + var4,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.minX + var4,
                     this.boundingBox.minY + var5,
                     this.boundingBox.minZ - 1,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public StructureComponent a(StructureStrongholdPieces.Stairs2 var1, List<StructureComponent> var2, Random var3, int var4, int var5) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + var4,
                     this.boundingBox.minZ + var5,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.minX + var5,
                     this.boundingBox.minY + var4,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.minX + var5,
                     this.boundingBox.minY + var4,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + var4,
                     this.boundingBox.minZ + var5,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public StructureComponent u(StructureStrongholdPieces.Stairs2 var1, List<StructureComponent> var2, Random var3, int var4, int var5) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + var4,
                     this.boundingBox.minZ + var5,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.minX + var5,
                     this.boundingBox.minY + var4,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.minX + var5,
                     this.boundingBox.minY + var4,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.func_175953_c(
                     var1,
                     var2,
                     var3,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + var4,
                     this.boundingBox.minZ + var5,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public static boolean canStrongholdGoDeeper(StructureBoundingBox var0) {
         return var0 != null && var0.minY > 10;
      }

      public enum Door {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;
      }
   }
}
