package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class StructureOceanMonumentPieces {
   private static final long a = 112669843685151L;

   public static void registerOceanMonumentPieces() {
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.MonumentBuilding.class, "OMB");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.MonumentCoreRoom.class, "OMCR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleXRoom.class, "OMDXR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleXYRoom.class, "OMDXYR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleYRoom.class, "OMDYR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleYZRoom.class, "OMDYZR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleZRoom.class, "OMDZR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.EntryRoom.class, "OMEntry");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.Penthouse.class, "OMPenthouse");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.SimpleRoom.class, "OMSimple");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.SimpleTopRoom.class, "OMSimpleT");
   }

   public static class DoubleXRoom extends StructureOceanMonumentPieces.Piece {
      private static final long b = 32845360933793L;

      public DoubleXRoom() {
      }

      public DoubleXRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         super(1, enumFacing, roomDefinition, 2, 1, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition = this.field_175830_k.field_175965_b[EnumFacing.EAST.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = this.field_175830_k;
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(worldIn, structureBoundingBoxIn, 8, 0, roomDefinition.field_175966_c[EnumFacing.DOWN.getIndex()]);
            this.func_175821_a(worldIn, structureBoundingBoxIn, 0, 0, roomDefinition2.field_175966_c[EnumFacing.DOWN.getIndex()]);
         }

         if (roomDefinition2.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 1, 4, 1, 7, 4, 6, field_175828_a);
         }

         if (roomDefinition.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 8, 4, 1, 14, 4, 6, field_175828_a);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 3, 0, 15, 3, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 15, 3, 0, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 14, 3, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 7, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 2, 0, 15, 2, 7, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 15, 2, 0, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 14, 2, 7, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 1, 0, 15, 1, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 15, 1, 0, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 14, 1, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 10, 1, 4, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 2, 0, 9, 2, 3, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 0, 10, 3, 4, field_175826_b, field_175826_b, false);
         this.setBlockState(worldIn, field_175825_e, 6, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175825_e, 9, 2, 3, structureBoundingBoxIn);
         if (roomDefinition2.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
         }

         if (roomDefinition2.field_175966_c[EnumFacing.NORTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, false);
         }

         if (roomDefinition2.field_175966_c[EnumFacing.WEST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 11, 1, 0, 12, 2, 0, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.NORTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 11, 1, 7, 12, 2, 7, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.EAST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 15, 1, 3, 15, 2, 4, false);
         }

         return true;
      }
   }

   public static class DoubleXYRoom extends StructureOceanMonumentPieces.Piece {
      private static final long b = 51787979560191L;

      public DoubleXYRoom() {
      }

      public DoubleXYRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         super(1, enumFacing, roomDefinition, 2, 2, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition = this.field_175830_k.field_175965_b[EnumFacing.EAST.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = this.field_175830_k;
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3 = roomDefinition2.field_175965_b[EnumFacing.UP.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition roomDefinition4 = roomDefinition.field_175965_b[EnumFacing.UP.getIndex()];
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(worldIn, structureBoundingBoxIn, 8, 0, roomDefinition.field_175966_c[EnumFacing.DOWN.getIndex()]);
            this.func_175821_a(worldIn, structureBoundingBoxIn, 0, 0, roomDefinition2.field_175966_c[EnumFacing.DOWN.getIndex()]);
         }

         if (roomDefinition3.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 1, 8, 1, 7, 8, 6, field_175828_a);
         }

         if (roomDefinition4.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 8, 8, 1, 14, 8, 6, field_175828_a);
         }

         for (int i = 1; i <= 7; i++) {
            IBlockState iBlockState = field_175826_b;
            if (i == 2 || i == 6) {
               iBlockState = field_175828_a;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, 0, 0, i, 7, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, i, 0, 15, i, 7, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 0, 15, i, 0, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 7, 14, i, 7, iBlockState, iBlockState, false);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 3, 2, 7, 4, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 4, 7, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 5, 4, 7, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 3, 13, 7, 4, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 11, 1, 2, 12, 7, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 11, 1, 5, 12, 7, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 3, 5, 3, 4, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 3, 10, 3, 4, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 7, 2, 10, 7, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 2, 5, 7, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 5, 2, 10, 7, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 5, 5, 7, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 5, 5, 10, 7, 5, field_175826_b, field_175826_b, false);
         this.setBlockState(worldIn, field_175826_b, 6, 6, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175826_b, 9, 6, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175826_b, 6, 6, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175826_b, 9, 6, 5, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 3, 6, 4, 4, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 4, 3, 10, 4, 4, field_175826_b, field_175826_b, false);
         this.setBlockState(worldIn, field_175825_e, 5, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175825_e, 5, 4, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175825_e, 10, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175825_e, 10, 4, 5, structureBoundingBoxIn);
         if (roomDefinition2.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
         }

         if (roomDefinition2.field_175966_c[EnumFacing.NORTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, false);
         }

         if (roomDefinition2.field_175966_c[EnumFacing.WEST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 11, 1, 0, 12, 2, 0, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.NORTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 11, 1, 7, 12, 2, 7, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.EAST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 15, 1, 3, 15, 2, 4, false);
         }

         if (roomDefinition3.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 5, 0, 4, 6, 0, false);
         }

         if (roomDefinition3.field_175966_c[EnumFacing.NORTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 5, 7, 4, 6, 7, false);
         }

         if (roomDefinition3.field_175966_c[EnumFacing.WEST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 5, 3, 0, 6, 4, false);
         }

         if (roomDefinition4.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 11, 5, 0, 12, 6, 0, false);
         }

         if (roomDefinition4.field_175966_c[EnumFacing.NORTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 11, 5, 7, 12, 6, 7, false);
         }

         if (roomDefinition4.field_175966_c[EnumFacing.EAST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 15, 5, 3, 15, 6, 4, false);
         }

         return true;
      }
   }

   public static class DoubleYRoom extends StructureOceanMonumentPieces.Piece {
      private static final long b = 96519389516918L;

      public DoubleYRoom() {
      }

      public DoubleYRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         super(1, enumFacing, roomDefinition, 1, 2, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(worldIn, structureBoundingBoxIn, 0, 0, this.field_175830_k.field_175966_c[EnumFacing.DOWN.getIndex()]);
         }

         StructureOceanMonumentPieces.RoomDefinition roomDefinition = this.field_175830_k.field_175965_b[EnumFacing.UP.getIndex()];
         if (roomDefinition.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 1, 8, 1, 6, 8, 6, field_175828_a);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 0, 4, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 0, 7, 4, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 0, 6, 4, 0, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 7, 6, 4, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 4, 1, 2, 4, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 2, 1, 4, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 1, 5, 4, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 2, 6, 4, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 4, 5, 2, 4, 6, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 5, 1, 4, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 5, 5, 4, 6, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 5, 6, 4, 5, field_175826_b, field_175826_b, false);
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = this.field_175830_k;

         for (int i = 1; i <= 5; i += 4) {
            byte b = 0;
            if (roomDefinition2.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, i, b, 2, i + 2, b, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, i, b, 5, i + 2, b, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, i + 2, b, 4, i + 2, b, field_175826_b, field_175826_b, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, b, 7, i + 2, b, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i + 1, b, 7, i + 1, b, field_175828_a, field_175828_a, false);
            }

            b = 7;
            if (roomDefinition2.field_175966_c[EnumFacing.NORTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, i, b, 2, i + 2, b, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, i, b, 5, i + 2, b, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, i + 2, b, 4, i + 2, b, field_175826_b, field_175826_b, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, b, 7, i + 2, b, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i + 1, b, 7, i + 1, b, field_175828_a, field_175828_a, false);
            }

            byte b2 = 0;
            if (roomDefinition2.field_175966_c[EnumFacing.WEST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, i, 2, b2, i + 2, 2, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, i, 5, b2, i + 2, 5, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, i + 2, 3, b2, i + 2, 4, field_175826_b, field_175826_b, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, i, 0, b2, i + 2, 7, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, i + 1, 0, b2, i + 1, 7, field_175828_a, field_175828_a, false);
            }

            b2 = 7;
            if (roomDefinition2.field_175966_c[EnumFacing.EAST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, i, 2, b2, i + 2, 2, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, i, 5, b2, i + 2, 5, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, i + 2, 3, b2, i + 2, 4, field_175826_b, field_175826_b, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, i, 0, b2, i + 2, 7, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, i + 1, 0, b2, i + 1, 7, field_175828_a, field_175828_a, false);
            }

            roomDefinition2 = roomDefinition;
         }

         return true;
      }
   }

   public static class DoubleYZRoom extends StructureOceanMonumentPieces.Piece {
      private static final long b = 135100606117628L;

      public DoubleYZRoom() {
      }

      public DoubleYZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         super(1, enumFacing, roomDefinition, 1, 2, 2);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition = this.field_175830_k.field_175965_b[EnumFacing.NORTH.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = this.field_175830_k;
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3 = roomDefinition.field_175965_b[EnumFacing.UP.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition roomDefinition4 = roomDefinition2.field_175965_b[EnumFacing.UP.getIndex()];
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(worldIn, structureBoundingBoxIn, 0, 8, roomDefinition.field_175966_c[EnumFacing.DOWN.getIndex()]);
            this.func_175821_a(worldIn, structureBoundingBoxIn, 0, 0, roomDefinition2.field_175966_c[EnumFacing.DOWN.getIndex()]);
         }

         if (roomDefinition4.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 1, 8, 1, 6, 8, 7, field_175828_a);
         }

         if (roomDefinition3.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 1, 8, 8, 6, 8, 14, field_175828_a);
         }

         for (int i = 1; i <= 7; i++) {
            IBlockState iBlockState = field_175826_b;
            if (i == 2 || i == 6) {
               iBlockState = field_175828_a;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, 0, 0, i, 15, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, i, 0, 7, i, 15, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 0, 6, i, 0, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 15, 6, i, 15, iBlockState, iBlockState, false);
         }

         for (int j = 1; j <= 7; j++) {
            IBlockState iBlockState2 = field_175827_c;
            if (j == 2 || j == 6) {
               iBlockState2 = field_175825_e;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, j, 7, 4, j, 8, iBlockState2, iBlockState2, false);
         }

         if (roomDefinition2.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
         }

         if (roomDefinition2.field_175966_c[EnumFacing.EAST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, false);
         }

         if (roomDefinition2.field_175966_c[EnumFacing.WEST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.NORTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 15, 4, 2, 15, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.WEST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 1, 11, 0, 2, 12, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.EAST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 7, 1, 11, 7, 2, 12, false);
         }

         if (roomDefinition4.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 5, 0, 4, 6, 0, false);
         }

         if (roomDefinition4.field_175966_c[EnumFacing.EAST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 7, 5, 3, 7, 6, 4, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 2, 6, 4, 5, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 2, 6, 3, 2, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 5, 6, 3, 5, field_175826_b, field_175826_b, false);
         }

         if (roomDefinition4.field_175966_c[EnumFacing.WEST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 5, 3, 0, 6, 4, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 2, 2, 4, 5, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 2, 1, 3, 2, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 5, 1, 3, 5, field_175826_b, field_175826_b, false);
         }

         if (roomDefinition3.field_175966_c[EnumFacing.NORTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 5, 15, 4, 6, 15, false);
         }

         if (roomDefinition3.field_175966_c[EnumFacing.WEST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 5, 11, 0, 6, 12, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 10, 2, 4, 13, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 10, 1, 3, 10, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 13, 1, 3, 13, field_175826_b, field_175826_b, false);
         }

         if (roomDefinition3.field_175966_c[EnumFacing.EAST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 7, 5, 11, 7, 6, 12, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 10, 6, 4, 13, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 10, 6, 3, 10, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 13, 6, 3, 13, field_175826_b, field_175826_b, false);
         }

         return true;
      }
   }

   public static class DoubleZRoom extends StructureOceanMonumentPieces.Piece {
      private static final long b = 11766339272571L;

      public DoubleZRoom() {
      }

      public DoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         super(1, enumFacing, roomDefinition, 1, 1, 2);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition = this.field_175830_k.field_175965_b[EnumFacing.NORTH.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = this.field_175830_k;
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(worldIn, structureBoundingBoxIn, 0, 8, roomDefinition.field_175966_c[EnumFacing.DOWN.getIndex()]);
            this.func_175821_a(worldIn, structureBoundingBoxIn, 0, 0, roomDefinition2.field_175966_c[EnumFacing.DOWN.getIndex()]);
         }

         if (roomDefinition2.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 1, 4, 1, 6, 4, 7, field_175828_a);
         }

         if (roomDefinition.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 1, 4, 8, 6, 4, 14, field_175828_a);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 15, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 3, 15, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 7, 3, 0, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 15, 6, 3, 15, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 15, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 15, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 2, 0, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 15, 6, 2, 15, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 15, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 15, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 7, 1, 0, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 15, 6, 1, 15, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 1, 1, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 1, 6, 1, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, 1, 3, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 1, 6, 3, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 13, 1, 1, 14, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 13, 6, 1, 14, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 13, 1, 3, 14, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 13, 6, 3, 14, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 2, 3, 6, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 6, 5, 3, 6, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 9, 2, 3, 9, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 9, 5, 3, 9, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 6, 4, 2, 6, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 9, 4, 2, 9, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 7, 2, 2, 8, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 7, 5, 2, 8, field_175826_b, field_175826_b, false);
         this.setBlockState(worldIn, field_175825_e, 2, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175825_e, 5, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175825_e, 2, 2, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175825_e, 5, 2, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175826_b, 2, 3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175826_b, 5, 3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175826_b, 2, 3, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175826_b, 5, 3, 10, structureBoundingBoxIn);
         if (roomDefinition2.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
         }

         if (roomDefinition2.field_175966_c[EnumFacing.EAST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, false);
         }

         if (roomDefinition2.field_175966_c[EnumFacing.WEST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.NORTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 15, 4, 2, 15, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.WEST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 1, 11, 0, 2, 12, false);
         }

         if (roomDefinition.field_175966_c[EnumFacing.EAST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 7, 1, 11, 7, 2, 12, false);
         }

         return true;
      }
   }

   public static class EntryRoom extends StructureOceanMonumentPieces.Piece {
      private static final long b = 91606798059742L;

      public EntryRoom() {
      }

      public EntryRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         super(1, enumFacing, roomDefinition, 1, 1, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 2, 3, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 0, 7, 3, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 1, 2, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 2, 0, 7, 2, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 7, 7, 3, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 6, 3, 0, field_175826_b, field_175826_b, false);
         if (this.field_175830_k.field_175966_c[EnumFacing.NORTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, false);
         }

         if (this.field_175830_k.field_175966_c[EnumFacing.WEST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 1, 3, 1, 2, 4, false);
         }

         if (this.field_175830_k.field_175966_c[EnumFacing.EAST.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 6, 1, 3, 7, 2, 4, false);
         }

         return true;
      }
   }

   public static class FitSimpleRoomHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      public FitSimpleRoomHelper() {
      }

      @Override
      public boolean func_175969_a(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         return true;
      }

      @Override
      public StructureOceanMonumentPieces.Piece func_175968_a(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         roomDefinition.field_175963_d = true;
         return new StructureOceanMonumentPieces.SimpleRoom(enumFacing, roomDefinition, random2);
      }
   }

   public static class FitSimpleRoomTopHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      private static final long a = 46671882833911L;

      public FitSimpleRoomTopHelper() {
      }

      @Override
      public boolean func_175969_a(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         return !roomDefinition.field_175966_c[EnumFacing.WEST.getIndex()]
            && !roomDefinition.field_175966_c[EnumFacing.EAST.getIndex()]
            && !roomDefinition.field_175966_c[EnumFacing.NORTH.getIndex()]
            && !roomDefinition.field_175966_c[EnumFacing.SOUTH.getIndex()]
            && !roomDefinition.field_175966_c[EnumFacing.UP.getIndex()];
      }

      @Override
      public StructureOceanMonumentPieces.Piece func_175968_a(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         roomDefinition.field_175963_d = true;
         return new StructureOceanMonumentPieces.SimpleTopRoom(enumFacing, roomDefinition, random2);
      }
   }

   public static class MonumentBuilding extends StructureOceanMonumentPieces.Piece {
      private StructureOceanMonumentPieces.RoomDefinition field_175845_o;
      private StructureOceanMonumentPieces.RoomDefinition field_175844_p;
      private List<StructureOceanMonumentPieces.Piece> field_175843_q = Lists.newArrayList();
      private static final long b = 92213242309833L;

      public MonumentBuilding() {
      }

      public MonumentBuilding(Random random2, int i, int j, EnumFacing enumFacing) {
         super(0);
         this.coordBaseMode = enumFacing;
         switch (this.coordBaseMode) {
            case NORTH:
            case SOUTH:
               this.boundingBox = new StructureBoundingBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
         }

         List list = this.func_175836_a(random2);
         this.field_175845_o.field_175963_d = true;
         this.field_175843_q.add(new StructureOceanMonumentPieces.EntryRoom(this.coordBaseMode, this.field_175845_o));
         this.field_175843_q.add(new StructureOceanMonumentPieces.MonumentCoreRoom(this.coordBaseMode, this.field_175844_p, random2));
         ArrayList arrayList = Lists.newArrayList();
         arrayList.add(new StructureOceanMonumentPieces.XYDoubleRoomFitHelper());
         arrayList.add(new StructureOceanMonumentPieces.YZDoubleRoomFitHelper());
         arrayList.add(new StructureOceanMonumentPieces.ZDoubleRoomFitHelper());
         arrayList.add(new StructureOceanMonumentPieces.XDoubleRoomFitHelper());
         arrayList.add(new StructureOceanMonumentPieces.YDoubleRoomFitHelper());
         arrayList.add(new StructureOceanMonumentPieces.FitSimpleRoomTopHelper());
         arrayList.add(new StructureOceanMonumentPieces.FitSimpleRoomHelper());

         for (Object object : list) {
            StructureOceanMonumentPieces.RoomDefinition roomDefinition = (StructureOceanMonumentPieces.RoomDefinition)object;
            if (!roomDefinition.field_175963_d && !roomDefinition.func_175961_b()) {
               for (Object object2 : arrayList) {
                  StructureOceanMonumentPieces.MonumentRoomFitHelper monumentRoomFitHelper = (StructureOceanMonumentPieces.MonumentRoomFitHelper)object2;
                  if (monumentRoomFitHelper.func_175969_a(roomDefinition)) {
                     this.field_175843_q.add(monumentRoomFitHelper.func_175968_a(this.coordBaseMode, roomDefinition, random2));
                     break;
                  }
               }
            }
         }

         int k = this.boundingBox.minY;
         int l = this.getXWithOffset(9, 22);
         int m = this.getZWithOffset(9, 22);

         for (StructureOceanMonumentPieces.Piece piece : this.field_175843_q) {
            piece.getBoundingBox().offset(l, k, m);
         }

         StructureBoundingBox structureBoundingBox = StructureBoundingBox.func_175899_a(
            this.getXWithOffset(1, 1),
            this.getYWithOffset(1),
            this.getZWithOffset(1, 1),
            this.getXWithOffset(23, 21),
            this.getYWithOffset(8),
            this.getZWithOffset(23, 21)
         );
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.func_175899_a(
            this.getXWithOffset(34, 1),
            this.getYWithOffset(1),
            this.getZWithOffset(34, 1),
            this.getXWithOffset(56, 21),
            this.getYWithOffset(8),
            this.getZWithOffset(56, 21)
         );
         StructureBoundingBox structureBoundingBox3 = StructureBoundingBox.func_175899_a(
            this.getXWithOffset(22, 22),
            this.getYWithOffset(13),
            this.getZWithOffset(22, 22),
            this.getXWithOffset(35, 35),
            this.getYWithOffset(17),
            this.getZWithOffset(35, 35)
         );
         int n = random2.nextInt();
         this.field_175843_q.add(new StructureOceanMonumentPieces.WingRoom(this.coordBaseMode, structureBoundingBox, n++));
         this.field_175843_q.add(new StructureOceanMonumentPieces.WingRoom(this.coordBaseMode, structureBoundingBox2, n++));
         this.field_175843_q.add(new StructureOceanMonumentPieces.Penthouse(this.coordBaseMode, structureBoundingBox3));
      }

      private List<StructureOceanMonumentPieces.RoomDefinition> func_175836_a(Random random2) {
         StructureOceanMonumentPieces.RoomDefinition[] roomDefinitions = new StructureOceanMonumentPieces.RoomDefinition[75];

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
               byte b = 0;
               int k = func_175820_a(i, b, j);
               roomDefinitions[k] = new StructureOceanMonumentPieces.RoomDefinition(k);
            }
         }

         for (int l = 0; l < 5; l++) {
            for (int m = 0; m < 4; m++) {
               byte b2 = 1;
               int n = func_175820_a(l, b2, m);
               roomDefinitions[n] = new StructureOceanMonumentPieces.RoomDefinition(n);
            }
         }

         for (int i2 = 1; i2 < 4; i2++) {
            for (int i3 = 0; i3 < 2; i3++) {
               byte b3 = 2;
               int i4 = func_175820_a(i2, b3, i3);
               roomDefinitions[i4] = new StructureOceanMonumentPieces.RoomDefinition(i4);
            }
         }

         this.field_175845_o = roomDefinitions[field_175823_g];

         for (int i5 = 0; i5 < 5; i5++) {
            for (int i6 = 0; i6 < 5; i6++) {
               for (int i7 = 0; i7 < 3; i7++) {
                  int i8 = func_175820_a(i5, i7, i6);
                  if (roomDefinitions[i8] != null) {
                     for (EnumFacing enumFacing : EnumFacing.values()) {
                        int i9 = i5 + enumFacing.getFrontOffsetX();
                        int i10 = i7 + enumFacing.getFrontOffsetY();
                        int i11 = i6 + enumFacing.getFrontOffsetZ();
                        if (i9 >= 0 && i9 < 5 && i11 >= 0 && i11 < 5 && i10 >= 0 && i10 < 3) {
                           int i12 = func_175820_a(i9, i10, i11);
                           if (roomDefinitions[i12] != null) {
                              if (i11 != i6) {
                                 roomDefinitions[i8].func_175957_a(enumFacing.getOpposite(), roomDefinitions[i12]);
                              } else {
                                 roomDefinitions[i8].func_175957_a(enumFacing, roomDefinitions[i12]);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         StructureOceanMonumentPieces.RoomDefinition roomDefinition;
         roomDefinitions[field_175831_h].func_175957_a(EnumFacing.UP, roomDefinition = new StructureOceanMonumentPieces.RoomDefinition(1003));
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2;
         roomDefinitions[field_175832_i].func_175957_a(EnumFacing.SOUTH, roomDefinition2 = new StructureOceanMonumentPieces.RoomDefinition(1001));
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3;
         roomDefinitions[field_175829_j].func_175957_a(EnumFacing.SOUTH, roomDefinition3 = new StructureOceanMonumentPieces.RoomDefinition(1002));
         roomDefinition.field_175963_d = true;
         roomDefinition2.field_175963_d = true;
         roomDefinition3.field_175963_d = true;
         this.field_175845_o.field_175964_e = true;
         this.field_175844_p = roomDefinitions[func_175820_a(random2.nextInt(4), 0, 2)];
         this.field_175844_p.field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.EAST.getIndex()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.NORTH.getIndex()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.EAST.getIndex()].field_175965_b[EnumFacing.NORTH.getIndex()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.UP.getIndex()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.EAST.getIndex()].field_175965_b[EnumFacing.UP.getIndex()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.NORTH.getIndex()].field_175965_b[EnumFacing.UP.getIndex()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.EAST.getIndex()].field_175965_b[EnumFacing.NORTH.getIndex()].field_175965_b[EnumFacing.UP.getIndex()].field_175963_d = true;
         ArrayList arrayList = Lists.newArrayList();

         for (StructureOceanMonumentPieces.RoomDefinition roomDefinition4 : roomDefinitions) {
            if (roomDefinition4 != null) {
               roomDefinition4.func_175958_a();
               arrayList.add(roomDefinition4);
            }
         }

         roomDefinition.func_175958_a();
         Collections.shuffle(arrayList, random2);
         int i13 = 1;

         for (Object object : arrayList) {
            StructureOceanMonumentPieces.RoomDefinition roomDefinition5 = (StructureOceanMonumentPieces.RoomDefinition)object;
            int i14 = 0;
            int i15 = 0;

            while (i14 < 2 && i15 < 5) {
               i15++;
               int i16 = random2.nextInt(6);
               if (roomDefinition5.field_175966_c[i16]) {
                  int i17 = EnumFacing.getFront(i16).getOpposite().getIndex();
                  roomDefinition5.field_175966_c[i16] = false;
                  roomDefinition5.field_175965_b[i16].field_175966_c[i17] = false;
                  if (roomDefinition5.func_175959_a(i13++) && roomDefinition5.field_175965_b[i16].func_175959_a(i13++)) {
                     i14++;
                  } else {
                     roomDefinition5.field_175966_c[i16] = true;
                     roomDefinition5.field_175965_b[i16].field_175966_c[i17] = true;
                  }
               }
            }
         }

         arrayList.add(roomDefinition);
         arrayList.add(roomDefinition2);
         arrayList.add(roomDefinition3);
         return arrayList;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         int i = Math.max(worldIn.getSeaLevel(), 64) - this.boundingBox.minY;
         this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 0, 0, 58, i, 58, false);
         this.func_175840_a(false, 0, worldIn, randomIn, structureBoundingBoxIn);
         this.func_175840_a(true, 33, worldIn, randomIn, structureBoundingBoxIn);
         this.func_175839_b(worldIn, randomIn, structureBoundingBoxIn);
         this.func_175837_c(worldIn, randomIn, structureBoundingBoxIn);
         this.func_175841_d(worldIn, randomIn, structureBoundingBoxIn);
         this.func_175835_e(worldIn, randomIn, structureBoundingBoxIn);
         this.func_175842_f(worldIn, randomIn, structureBoundingBoxIn);
         this.func_175838_g(worldIn, randomIn, structureBoundingBoxIn);

         for (int j = 0; j < 7; j++) {
            int k = 0;

            while (k < 7) {
               if (k == 0 && j == 3) {
                  k = 6;
               }

               int l = j * 9;
               int m = k * 9;

               for (int n = 0; n < 4; n++) {
                  for (int i2 = 0; i2 < 4; i2++) {
                     this.setBlockState(worldIn, field_175826_b, l + n, 0, m + i2, structureBoundingBoxIn);
                     this.replaceAirAndLiquidDownwards(worldIn, field_175826_b, l + n, -1, m + i2, structureBoundingBoxIn);
                  }
               }

               if (j != 0 && j != 6) {
                  k += 6;
               } else {
                  k++;
               }
            }
         }

         for (int i3 = 0; i3 < 5; i3++) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, -1 - i3, 0 + i3 * 2, -1 - i3, -1 - i3, 23, 58 + i3, false);
            this.func_181655_a(worldIn, structureBoundingBoxIn, 58 + i3, 0 + i3 * 2, -1 - i3, 58 + i3, 23, 58 + i3, false);
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0 - i3, 0 + i3 * 2, -1 - i3, 57 + i3, 23, -1 - i3, false);
            this.func_181655_a(worldIn, structureBoundingBoxIn, 0 - i3, 0 + i3 * 2, 58 + i3, 57 + i3, 23, 58 + i3, false);
         }

         for (StructureOceanMonumentPieces.Piece piece : this.field_175843_q) {
            if (piece.getBoundingBox().intersectsWith(structureBoundingBoxIn)) {
               piece.addComponentParts(worldIn, randomIn, structureBoundingBoxIn);
            }
         }

         return true;
      }

      private void func_175840_a(boolean bl, int i, World worldIn, Random random2, StructureBoundingBox structureBoundingBox) {
         if (this.func_175818_a(structureBoundingBox, i, 0, i + 23, 20)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 0, 0, 0, i + 24, 0, 20, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, i + 0, 1, 0, i + 24, 10, 20, false);

            for (int j = 0; j < 4; j++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, i + j, j + 1, j, i + j, j + 1, 20, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, i + j + 7, j + 5, j + 7, i + j + 7, j + 5, 20, field_175826_b, field_175826_b, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, i + 17 - j, j + 5, j + 7, i + 17 - j, j + 5, 20, field_175826_b, field_175826_b, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, i + 24 - j, j + 1, j, i + 24 - j, j + 1, 20, field_175826_b, field_175826_b, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, i + j + 1, j + 1, j, i + 23 - j, j + 1, j, field_175826_b, field_175826_b, false
               );
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBox,
                  i + j + 8,
                  j + 5,
                  j + 7,
                  i + 16 - j,
                  j + 5,
                  j + 7,
                  field_175826_b,
                  field_175826_b,
                  false
               );
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, i + 4, 4, 4, i + 6, 4, 20, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 7, 4, 4, i + 17, 4, 6, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 18, 4, 4, i + 20, 4, 20, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 11, 8, 11, i + 13, 8, 20, field_175828_a, field_175828_a, false);
            this.setBlockState(worldIn, field_175824_d, i + 12, 9, 12, structureBoundingBox);
            this.setBlockState(worldIn, field_175824_d, i + 12, 9, 15, structureBoundingBox);
            this.setBlockState(worldIn, field_175824_d, i + 12, 9, 18, structureBoundingBox);
            int k = bl ? i + 19 : i + 5;
            int l = bl ? i + 5 : i + 19;

            for (int m = 20; m >= 5; m -= 3) {
               this.setBlockState(worldIn, field_175824_d, k, 5, m, structureBoundingBox);
            }

            for (int n = 19; n >= 7; n -= 3) {
               this.setBlockState(worldIn, field_175824_d, l, 5, n, structureBoundingBox);
            }

            for (int i2 = 0; i2 < 4; i2++) {
               int i3 = bl ? i + (24 - (17 - i2 * 3)) : i + 17 - i2 * 3;
               this.setBlockState(worldIn, field_175824_d, i3, 5, 5, structureBoundingBox);
            }

            this.setBlockState(worldIn, field_175824_d, l, 5, 5, structureBoundingBox);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 11, 1, 12, i + 13, 7, 12, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 12, 1, 11, i + 12, 7, 13, field_175828_a, field_175828_a, false);
         }
      }

      private void func_175839_b(World worldIn, Random random2, StructureBoundingBox structureBoundingBox) {
         if (this.func_175818_a(structureBoundingBox, 22, 5, 35, 17)) {
            this.func_181655_a(worldIn, structureBoundingBox, 25, 0, 0, 32, 8, 20, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBox, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, field_175826_b, field_175826_b, false);
               this.setBlockState(worldIn, field_175826_b, 25, 5, 5 + i * 4, structureBoundingBox);
               this.setBlockState(worldIn, field_175826_b, 26, 6, 5 + i * 4, structureBoundingBox);
               this.setBlockState(worldIn, field_175825_e, 26, 5, 5 + i * 4, structureBoundingBox);
               this.fillWithBlocks(worldIn, structureBoundingBox, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBox, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, field_175826_b, field_175826_b, false);
               this.setBlockState(worldIn, field_175826_b, 32, 5, 5 + i * 4, structureBoundingBox);
               this.setBlockState(worldIn, field_175826_b, 31, 6, 5 + i * 4, structureBoundingBox);
               this.setBlockState(worldIn, field_175825_e, 31, 5, 5 + i * 4, structureBoundingBox);
               this.fillWithBlocks(worldIn, structureBoundingBox, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, field_175828_a, field_175828_a, false);
            }
         }
      }

      private void func_175837_c(World worldIn, Random random2, StructureBoundingBox structureBoundingBox) {
         if (this.func_175818_a(structureBoundingBox, 15, 20, 42, 21)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 15, 0, 21, 42, 0, 21, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 26, 1, 21, 31, 3, 21, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 12, 21, 36, 12, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 17, 11, 21, 40, 11, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 16, 10, 21, 41, 10, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 15, 7, 21, 42, 9, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 16, 6, 21, 41, 6, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 17, 5, 21, 40, 5, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 4, 21, 36, 4, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 22, 3, 21, 26, 3, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 31, 3, 21, 35, 3, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 23, 2, 21, 25, 2, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 32, 2, 21, 34, 2, 21, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 28, 4, 20, 29, 4, 21, field_175826_b, field_175826_b, false);
            this.setBlockState(worldIn, field_175826_b, 27, 3, 21, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 30, 3, 21, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 26, 2, 21, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 31, 2, 21, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 25, 1, 21, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 32, 1, 21, structureBoundingBox);

            for (int i = 0; i < 7; i++) {
               this.setBlockState(worldIn, field_175827_c, 28 - i, 6 + i, 21, structureBoundingBox);
               this.setBlockState(worldIn, field_175827_c, 29 + i, 6 + i, 21, structureBoundingBox);
            }

            for (int j = 0; j < 4; j++) {
               this.setBlockState(worldIn, field_175827_c, 28 - j, 9 + j, 21, structureBoundingBox);
               this.setBlockState(worldIn, field_175827_c, 29 + j, 9 + j, 21, structureBoundingBox);
            }

            this.setBlockState(worldIn, field_175827_c, 28, 12, 21, structureBoundingBox);
            this.setBlockState(worldIn, field_175827_c, 29, 12, 21, structureBoundingBox);

            for (int k = 0; k < 3; k++) {
               this.setBlockState(worldIn, field_175827_c, 22 - k * 2, 8, 21, structureBoundingBox);
               this.setBlockState(worldIn, field_175827_c, 22 - k * 2, 9, 21, structureBoundingBox);
               this.setBlockState(worldIn, field_175827_c, 35 + k * 2, 8, 21, structureBoundingBox);
               this.setBlockState(worldIn, field_175827_c, 35 + k * 2, 9, 21, structureBoundingBox);
            }

            this.func_181655_a(worldIn, structureBoundingBox, 15, 13, 21, 42, 15, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 15, 1, 21, 15, 6, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 16, 1, 21, 16, 5, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 17, 1, 21, 20, 4, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 21, 1, 21, 21, 3, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 22, 1, 21, 22, 2, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 23, 1, 21, 24, 1, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 42, 1, 21, 42, 6, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 41, 1, 21, 41, 5, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 37, 1, 21, 40, 4, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 36, 1, 21, 36, 3, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 33, 1, 21, 34, 1, 21, false);
            this.func_181655_a(worldIn, structureBoundingBox, 35, 1, 21, 35, 2, 21, false);
         }
      }

      private void func_175841_d(World worldIn, Random random2, StructureBoundingBox structureBoundingBox) {
         if (this.func_175818_a(structureBoundingBox, 21, 21, 36, 36)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 0, 22, 36, 0, 36, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 21, 1, 22, 36, 23, 36, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, field_175826_b, field_175826_b, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, field_175826_b, field_175826_b, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, field_175826_b, field_175826_b, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, field_175826_b, field_175826_b, false
               );
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, 25, 16, 25, 32, 16, 32, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 25, 17, 25, 25, 19, 25, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 32, 17, 25, 32, 19, 25, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 25, 17, 32, 25, 19, 32, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 32, 17, 32, 32, 19, 32, field_175826_b, field_175826_b, false);
            this.setBlockState(worldIn, field_175826_b, 26, 20, 26, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 27, 21, 27, structureBoundingBox);
            this.setBlockState(worldIn, field_175825_e, 27, 20, 27, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 26, 20, 31, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 27, 21, 30, structureBoundingBox);
            this.setBlockState(worldIn, field_175825_e, 27, 20, 30, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 31, 20, 31, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 30, 21, 30, structureBoundingBox);
            this.setBlockState(worldIn, field_175825_e, 30, 20, 30, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 31, 20, 26, structureBoundingBox);
            this.setBlockState(worldIn, field_175826_b, 30, 21, 27, structureBoundingBox);
            this.setBlockState(worldIn, field_175825_e, 30, 20, 27, structureBoundingBox);
            this.fillWithBlocks(worldIn, structureBoundingBox, 28, 21, 27, 29, 21, 27, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 27, 21, 28, 27, 21, 29, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 28, 21, 30, 29, 21, 30, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 30, 21, 28, 30, 21, 29, field_175828_a, field_175828_a, false);
         }
      }

      private void func_175835_e(World worldIn, Random random2, StructureBoundingBox structureBoundingBox) {
         if (this.func_175818_a(structureBoundingBox, 0, 21, 6, 58)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 0, 0, 21, 6, 0, 57, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 0, 1, 21, 6, 7, 57, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 4, 4, 21, 6, 4, 53, field_175828_a, field_175828_a, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, i, i + 1, 21, i, i + 1, 57 - i, field_175826_b, field_175826_b, false);
            }

            for (int j = 23; j < 53; j += 3) {
               this.setBlockState(worldIn, field_175824_d, 5, 5, j, structureBoundingBox);
            }

            this.setBlockState(worldIn, field_175824_d, 5, 5, 52, structureBoundingBox);

            for (int k = 0; k < 4; k++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, k, k + 1, 21, k, k + 1, 57 - k, field_175826_b, field_175826_b, false);
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, 4, 1, 52, 6, 3, 52, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 5, 1, 51, 5, 3, 53, field_175828_a, field_175828_a, false);
         }

         if (this.func_175818_a(structureBoundingBox, 51, 21, 58, 58)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 51, 0, 21, 57, 0, 57, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 51, 1, 21, 57, 7, 57, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 51, 4, 21, 53, 4, 53, field_175828_a, field_175828_a, false);

            for (int l = 0; l < 4; l++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 57 - l, l + 1, 21, 57 - l, l + 1, 57 - l, field_175826_b, field_175826_b, false
               );
            }

            for (int m = 23; m < 53; m += 3) {
               this.setBlockState(worldIn, field_175824_d, 52, 5, m, structureBoundingBox);
            }

            this.setBlockState(worldIn, field_175824_d, 52, 5, 52, structureBoundingBox);
            this.fillWithBlocks(worldIn, structureBoundingBox, 51, 1, 52, 53, 3, 52, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 52, 1, 51, 52, 3, 53, field_175828_a, field_175828_a, false);
         }

         if (this.func_175818_a(structureBoundingBox, 0, 51, 57, 57)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 7, 0, 51, 50, 0, 57, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 7, 1, 51, 50, 10, 57, false);

            for (int n = 0; n < 4; n++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, n + 1, n + 1, 57 - n, 56 - n, n + 1, 57 - n, field_175826_b, field_175826_b, false
               );
            }
         }
      }

      private void func_175842_f(World worldIn, Random random2, StructureBoundingBox structureBoundingBox) {
         if (this.func_175818_a(structureBoundingBox, 7, 21, 13, 50)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 7, 0, 21, 13, 0, 50, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 7, 1, 21, 13, 10, 50, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 11, 8, 21, 13, 8, 53, field_175828_a, field_175828_a, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, i + 7, i + 5, 21, i + 7, i + 5, 54, field_175826_b, field_175826_b, false);
            }

            for (int j = 21; j <= 45; j += 3) {
               this.setBlockState(worldIn, field_175824_d, 12, 9, j, structureBoundingBox);
            }
         }

         if (this.func_175818_a(structureBoundingBox, 44, 21, 50, 54)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 44, 0, 21, 50, 0, 50, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 44, 1, 21, 50, 10, 50, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 44, 8, 21, 46, 8, 53, field_175828_a, field_175828_a, false);

            for (int k = 0; k < 4; k++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 50 - k, k + 5, 21, 50 - k, k + 5, 54, field_175826_b, field_175826_b, false);
            }

            for (int l = 21; l <= 45; l += 3) {
               this.setBlockState(worldIn, field_175824_d, 45, 9, l, structureBoundingBox);
            }
         }

         if (this.func_175818_a(structureBoundingBox, 8, 44, 49, 54)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 14, 0, 44, 43, 0, 50, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 14, 1, 44, 43, 10, 50, false);

            for (int m = 12; m <= 45; m += 3) {
               this.setBlockState(worldIn, field_175824_d, m, 9, 45, structureBoundingBox);
               this.setBlockState(worldIn, field_175824_d, m, 9, 52, structureBoundingBox);
               if (m == 12 || m == 18 || m == 24 || m == 33 || m == 39 || m == 45) {
                  this.setBlockState(worldIn, field_175824_d, m, 9, 47, structureBoundingBox);
                  this.setBlockState(worldIn, field_175824_d, m, 9, 50, structureBoundingBox);
                  this.setBlockState(worldIn, field_175824_d, m, 10, 45, structureBoundingBox);
                  this.setBlockState(worldIn, field_175824_d, m, 10, 46, structureBoundingBox);
                  this.setBlockState(worldIn, field_175824_d, m, 10, 51, structureBoundingBox);
                  this.setBlockState(worldIn, field_175824_d, m, 10, 52, structureBoundingBox);
                  this.setBlockState(worldIn, field_175824_d, m, 11, 47, structureBoundingBox);
                  this.setBlockState(worldIn, field_175824_d, m, 11, 50, structureBoundingBox);
                  this.setBlockState(worldIn, field_175824_d, m, 12, 48, structureBoundingBox);
                  this.setBlockState(worldIn, field_175824_d, m, 12, 49, structureBoundingBox);
               }
            }

            for (int n = 0; n < 3; n++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 8 + n, 5 + n, 54, 49 - n, 5 + n, 54, field_175828_a, field_175828_a, false);
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, 11, 8, 54, 46, 8, 54, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 14, 8, 44, 43, 8, 53, field_175828_a, field_175828_a, false);
         }
      }

      private void func_175838_g(World worldIn, Random random2, StructureBoundingBox structureBoundingBox) {
         if (this.func_175818_a(structureBoundingBox, 14, 21, 20, 43)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 14, 0, 21, 20, 0, 43, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 14, 1, 22, 20, 14, 43, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 18, 12, 22, 20, 12, 39, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 18, 12, 21, 20, 12, 21, field_175826_b, field_175826_b, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, field_175826_b, field_175826_b, false
               );
            }

            for (int j = 23; j <= 39; j += 3) {
               this.setBlockState(worldIn, field_175824_d, 19, 13, j, structureBoundingBox);
            }
         }

         if (this.func_175818_a(structureBoundingBox, 37, 21, 43, 43)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 37, 0, 21, 43, 0, 43, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 37, 1, 22, 43, 14, 43, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 37, 12, 22, 39, 12, 39, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 37, 12, 21, 39, 12, 21, field_175826_b, field_175826_b, false);

            for (int k = 0; k < 4; k++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 43 - k, k + 9, 21, 43 - k, k + 9, 43 - k, field_175826_b, field_175826_b, false
               );
            }

            for (int l = 23; l <= 39; l += 3) {
               this.setBlockState(worldIn, field_175824_d, 38, 13, l, structureBoundingBox);
            }
         }

         if (this.func_175818_a(structureBoundingBox, 15, 37, 42, 43)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 0, 37, 36, 0, 43, field_175828_a, field_175828_a, false);
            this.func_181655_a(worldIn, structureBoundingBox, 21, 1, 37, 36, 14, 43, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 12, 37, 36, 12, 39, field_175828_a, field_175828_a, false);

            for (int m = 0; m < 4; m++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, 15 + m, m + 9, 43 - m, 42 - m, m + 9, 43 - m, field_175826_b, field_175826_b, false
               );
            }

            for (int n = 21; n <= 36; n += 3) {
               this.setBlockState(worldIn, field_175824_d, n, 13, 38, structureBoundingBox);
            }
         }
      }
   }

   public static class MonumentCoreRoom extends StructureOceanMonumentPieces.Piece {
      private static final long b = 128672371331595L;

      public MonumentCoreRoom() {
      }

      public MonumentCoreRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         super(1, enumFacing, roomDefinition, 2, 2, 2);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.func_175819_a(worldIn, structureBoundingBoxIn, 1, 8, 0, 14, 8, 14, field_175828_a);
         byte b = 7;
         IBlockState iBlockState = field_175826_b;
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, b, 0, 0, b, 15, iBlockState, iBlockState, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, b, 0, 15, b, 15, iBlockState, iBlockState, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, b, 0, 15, b, 0, iBlockState, iBlockState, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, b, 15, 14, b, 15, iBlockState, iBlockState, false);

         for (int i = 1; i <= 6; i++) {
            iBlockState = field_175826_b;
            if (i == 2 || i == 6) {
               iBlockState = field_175828_a;
            }

            for (int j = 0; j <= 15; j += 15) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, j, i, 0, j, i, 1, iBlockState, iBlockState, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, j, i, 6, j, i, 9, iBlockState, iBlockState, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, j, i, 14, j, i, 15, iBlockState, iBlockState, false);
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 0, 1, i, 0, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, i, 0, 9, i, 0, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 14, i, 0, 14, i, 0, iBlockState, iBlockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 15, 14, i, 15, iBlockState, iBlockState, false);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 6, 9, 6, 9, field_175827_c, field_175827_c, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 7, 8, 5, 8, Blocks.gold_block.getDefaultState(), Blocks.gold_block.getDefaultState(), false);

         for (int k = 3; k <= 6; k += 3) {
            for (int l = 6; l <= 9; l += 3) {
               this.setBlockState(worldIn, field_175825_e, l, k, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, field_175825_e, l, k, 9, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 6, 5, 2, 6, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 9, 5, 2, 9, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 6, 10, 2, 6, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 9, 10, 2, 9, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 5, 6, 2, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 5, 9, 2, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 10, 6, 2, 10, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 10, 9, 2, 10, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 5, 5, 6, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 10, 5, 6, 10, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 2, 5, 10, 6, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 2, 10, 10, 6, 10, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 7, 1, 5, 7, 6, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 7, 1, 10, 7, 6, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 7, 9, 5, 7, 14, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 7, 9, 10, 7, 14, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 7, 5, 6, 7, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 7, 10, 6, 7, 10, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 7, 5, 14, 7, 5, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 7, 10, 14, 7, 10, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 2, 1, 3, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 1, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 2, 13, 1, 3, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 2, 12, 1, 2, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 12, 2, 1, 13, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 13, 3, 1, 13, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 12, 13, 1, 13, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 13, 12, 1, 13, field_175826_b, field_175826_b, false);
         return true;
      }
   }

   public interface MonumentRoomFitHelper {
      boolean func_175969_a(StructureOceanMonumentPieces.RoomDefinition roomDefinition);

      StructureOceanMonumentPieces.Piece func_175968_a(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2);
   }

   public static class Penthouse extends StructureOceanMonumentPieces.Piece {
      private static final long b = 32554719035458L;

      public Penthouse() {
      }

      public Penthouse(EnumFacing enumFacing, StructureBoundingBox structureBoundingBox) {
         super(enumFacing, structureBoundingBox);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, -1, 2, 11, -1, 11, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, -1, 0, 1, -1, 11, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, -1, 0, 13, -1, 11, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, -1, 0, 11, -1, 1, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, -1, 12, 11, -1, 13, field_175828_a, field_175828_a, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 0, 13, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 0, 0, 13, 0, 13, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 12, 0, 0, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 13, 12, 0, 13, field_175826_b, field_175826_b, false);

         for (int i = 2; i <= 11; i += 3) {
            this.setBlockState(worldIn, field_175825_e, 0, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 13, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, i, 0, 0, structureBoundingBoxIn);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 3, 4, 0, 9, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 3, 11, 0, 9, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 9, 9, 0, 11, field_175826_b, field_175826_b, false);
         this.setBlockState(worldIn, field_175826_b, 5, 0, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175826_b, 8, 0, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175826_b, 10, 0, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, field_175826_b, 3, 0, 10, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 3, 3, 0, 7, field_175827_c, field_175827_c, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 0, 3, 10, 0, 7, field_175827_c, field_175827_c, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 10, 7, 0, 10, field_175827_c, field_175827_c, false);
         byte b = 3;

         for (int j = 0; j < 2; j++) {
            for (int k = 2; k <= 8; k += 3) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b, 0, k, b, 2, k, field_175826_b, field_175826_b, false);
            }

            b = 10;
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 10, 5, 2, 10, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 10, 8, 2, 10, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, -1, 7, 7, -1, 8, field_175827_c, field_175827_c, false);
         this.func_181655_a(worldIn, structureBoundingBoxIn, 6, -1, 3, 7, -1, 4, false);
         this.func_175817_a(worldIn, structureBoundingBoxIn, 6, 1, 6);
         return true;
      }
   }

   public abstract static class Piece extends StructureComponent {
      public static final IBlockState field_175828_a = Blocks.prismarine.getStateFromMeta(BlockPrismarine.ROUGH_META);
      public static final IBlockState field_175826_b = Blocks.prismarine.getStateFromMeta(BlockPrismarine.BRICKS_META);
      public static final IBlockState field_175827_c = Blocks.prismarine.getStateFromMeta(BlockPrismarine.DARK_META);
      public static final IBlockState field_175824_d = field_175826_b;
      public static final IBlockState field_175825_e = Blocks.sea_lantern.getDefaultState();
      public static final IBlockState field_175822_f = Blocks.water.getDefaultState();
      public static final int field_175823_g = func_175820_a(2, 0, 0);
      public static final int field_175831_h = func_175820_a(2, 2, 0);
      public static final int field_175832_i = func_175820_a(0, 1, 0);
      public static final int field_175829_j = func_175820_a(4, 1, 0);
      public StructureOceanMonumentPieces.RoomDefinition field_175830_k;
      private static final long g = 19626435600854L;

      public static final int func_175820_a(int i, int j, int k) {
         return j * 25 + k * 5 + i;
      }

      public Piece() {
         super(0);
      }

      public Piece(int i) {
         super(i);
      }

      public Piece(EnumFacing enumFacing, StructureBoundingBox structureBoundingBox) {
         super(1);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      public Piece(int i, EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, int j, int k, int l) {
         super(i);
         this.coordBaseMode = enumFacing;
         this.field_175830_k = roomDefinition;
         int m = roomDefinition.field_175967_a;
         int n = m % 5;
         int i2 = m / 5 % 5;
         int i3 = m / 25;
         if (enumFacing != EnumFacing.NORTH && enumFacing != EnumFacing.SOUTH) {
            this.boundingBox = new StructureBoundingBox(0, 0, 0, l * 8 - 1, k * 4 - 1, j * 8 - 1);
         } else {
            this.boundingBox = new StructureBoundingBox(0, 0, 0, j * 8 - 1, k * 4 - 1, l * 8 - 1);
         }

         switch (enumFacing) {
            case NORTH:
               this.boundingBox.offset(n * 8, i3 * 4, -(i2 + l) * 8 + 1);
               break;
            case SOUTH:
               this.boundingBox.offset(n * 8, i3 * 4, i2 * 8);
               break;
            case WEST:
               this.boundingBox.offset(-(i2 + l) * 8 + 1, i3 * 4, n * 8);
               break;
            default:
               this.boundingBox.offset(i2 * 8, i3 * 4, n * 8);
         }
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
      }

      public void func_181655_a(World world, StructureBoundingBox structureBoundingBox, int i, int j, int k, int l, int m, int n, boolean bl) {
         for (int i2 = j; i2 <= m; i2++) {
            for (int i3 = i; i3 <= l; i3++) {
               for (int i4 = k; i4 <= n; i4++) {
                  if (!bl || this.getBlockStateFromPos(world, i3, i2, i4, structureBoundingBox).getBlock().getMaterial() != Material.air) {
                     if (this.getYWithOffset(i2) >= world.getSeaLevel()) {
                        this.setBlockState(world, Blocks.air.getDefaultState(), i3, i2, i4, structureBoundingBox);
                     } else {
                        this.setBlockState(world, field_175822_f, i3, i2, i4, structureBoundingBox);
                     }
                  }
               }
            }
         }
      }

      public void func_175821_a(World worldIn, StructureBoundingBox structureBoundingBox, int i, int j, boolean bl) {
         if (bl) {
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 0, 0, j + 0, i + 2, 0, j + 8 - 1, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 5, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 3, 0, j + 0, i + 4, 0, j + 2, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 3, 0, j + 5, i + 4, 0, j + 8 - 1, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 3, 0, j + 2, i + 4, 0, j + 2, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 3, 0, j + 5, i + 4, 0, j + 5, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 2, 0, j + 3, i + 2, 0, j + 4, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 5, 0, j + 3, i + 5, 0, j + 4, field_175826_b, field_175826_b, false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBox, i + 0, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, field_175828_a, field_175828_a, false);
         }
      }

      public void func_175819_a(World worldIn, StructureBoundingBox structureBoundingBox, int i, int j, int k, int l, int m, int n, IBlockState iBlockState) {
         for (int i2 = j; i2 <= m; i2++) {
            for (int i3 = i; i3 <= l; i3++) {
               for (int i4 = k; i4 <= n; i4++) {
                  if (this.getBlockStateFromPos(worldIn, i3, i2, i4, structureBoundingBox) == field_175822_f) {
                     this.setBlockState(worldIn, iBlockState, i3, i2, i4, structureBoundingBox);
                  }
               }
            }
         }
      }

      public boolean func_175818_a(StructureBoundingBox structureBoundingBox, int i, int j, int k, int l) {
         int m = this.getXWithOffset(i, j);
         int n = this.getZWithOffset(i, j);
         int i2 = this.getXWithOffset(k, l);
         int i3 = this.getZWithOffset(k, l);
         return structureBoundingBox.intersectsWith(Math.min(m, i2), Math.min(n, i3), Math.max(m, i2), Math.max(n, i3));
      }

      public boolean func_175817_a(World worldIn, StructureBoundingBox structureBoundingBox, int i, int j, int k) {
         int l = this.getXWithOffset(i, k);
         int m = this.getYWithOffset(j);
         int n = this.getZWithOffset(i, k);
         if (structureBoundingBox.isVecInside(new BlockPos(l, m, n))) {
            EntityGuardian entityGuardian = new EntityGuardian(worldIn);
            entityGuardian.setElder(true);
            entityGuardian.heal(entityGuardian.getMaxHealth());
            entityGuardian.setLocationAndAngles(l + 0.5, m, n + 0.5, 0.0F, 0.0F);
            entityGuardian.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityGuardian)), (IEntityLivingData)null);
            worldIn.spawnEntityInWorld(entityGuardian);
            return true;
         } else {
            return false;
         }
      }
   }

   public static class RoomDefinition {
      public int field_175967_a;
      public StructureOceanMonumentPieces.RoomDefinition[] field_175965_b = new StructureOceanMonumentPieces.RoomDefinition[6];
      public boolean[] field_175966_c = new boolean[6];
      public boolean field_175963_d;
      public boolean field_175964_e;
      public int field_175962_f;
      private static final long a = 132977840891621L;

      public RoomDefinition(int i) {
         this.field_175967_a = i;
      }

      public void func_175957_a(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         this.field_175965_b[enumFacing.getIndex()] = roomDefinition;
         roomDefinition.field_175965_b[enumFacing.getOpposite().getIndex()] = this;
      }

      public void func_175958_a() {
         for (int i = 0; i < 6; i++) {
            this.field_175966_c[i] = this.field_175965_b[i] != null;
         }
      }

      public boolean func_175959_a(int i) {
         if (this.field_175964_e) {
            return true;
         }

         this.field_175962_f = i;

         for (int j = 0; j < 6; j++) {
            if (this.field_175965_b[j] != null
               && this.field_175966_c[j]
               && this.field_175965_b[j].field_175962_f != i
               && this.field_175965_b[j].func_175959_a(i)) {
               return true;
            }
         }

         return false;
      }

      public boolean func_175961_b() {
         return this.field_175967_a >= 75;
      }

      public int func_175960_c() {
         int i = 0;

         for (int j = 0; j < 6; j++) {
            if (this.field_175966_c[j]) {
               i++;
            }
         }

         return i;
      }
   }

   public static class SimpleRoom extends StructureOceanMonumentPieces.Piece {
      private int field_175833_o;
      private static final long b = 34223389130760L;

      public SimpleRoom() {
      }

      public SimpleRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         super(1, enumFacing, roomDefinition, 1, 1, 1);
         this.field_175833_o = random2.nextInt(3);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(worldIn, structureBoundingBoxIn, 0, 0, this.field_175830_k.field_175966_c[EnumFacing.DOWN.getIndex()]);
         }

         if (this.field_175830_k.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 1, 4, 1, 6, 4, 6, field_175828_a);
         }

         boolean bl = this.field_175833_o != 0
            && randomIn.nextBoolean()
            && !this.field_175830_k.field_175966_c[EnumFacing.DOWN.getIndex()]
            && !this.field_175830_k.field_175966_c[EnumFacing.UP.getIndex()]
            && this.field_175830_k.func_175960_c() > 1;
         if (this.field_175833_o == 0) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 2, 1, 2, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 2, 3, 2, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 2, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 2, 2, 0, field_175828_a, field_175828_a, false);
            this.setBlockState(worldIn, field_175825_e, 1, 2, 1, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 7, 1, 2, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 0, 7, 3, 2, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 2, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 0, 6, 2, 0, field_175828_a, field_175828_a, false);
            this.setBlockState(worldIn, field_175825_e, 6, 2, 1, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 5, 2, 1, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 5, 2, 3, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 5, 0, 2, 7, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 2, 2, 7, field_175828_a, field_175828_a, false);
            this.setBlockState(worldIn, field_175825_e, 1, 2, 6, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 5, 7, 1, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 5, 7, 3, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 5, 7, 2, 7, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 7, 6, 2, 7, field_175828_a, field_175828_a, false);
            this.setBlockState(worldIn, field_175825_e, 6, 2, 6, structureBoundingBoxIn);
            if (this.field_175830_k.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 0, 4, 3, 0, field_175826_b, field_175826_b, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 0, 4, 3, 1, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 0, 4, 2, 0, field_175828_a, field_175828_a, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 1, 1, field_175826_b, field_175826_b, false);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.NORTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 7, 4, 3, 7, field_175826_b, field_175826_b, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 6, 4, 3, 7, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 7, 4, 2, 7, field_175828_a, field_175828_a, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 6, 4, 1, 7, field_175826_b, field_175826_b, false);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.WEST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 3, 0, 3, 4, field_175826_b, field_175826_b, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 3, 1, 3, 4, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 3, 0, 2, 4, field_175828_a, field_175828_a, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 3, 1, 1, 4, field_175826_b, field_175826_b, false);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.EAST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 3, 7, 3, 4, field_175826_b, field_175826_b, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 3, 7, 3, 4, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 3, 7, 2, 4, field_175828_a, field_175828_a, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 3, 7, 1, 4, field_175826_b, field_175826_b, false);
            }
         } else if (this.field_175833_o == 1) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 2, 3, 2, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 5, 2, 3, 5, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 5, 5, 3, 5, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 2, 5, 3, 2, field_175826_b, field_175826_b, false);
            this.setBlockState(worldIn, field_175825_e, 2, 2, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 2, 2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 5, 2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 5, 2, 2, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 1, 3, 0, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 1, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 7, 1, 3, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 6, 0, 3, 6, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 7, 7, 3, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 6, 7, 3, 6, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 0, 7, 3, 0, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 1, 7, 3, 1, field_175826_b, field_175826_b, false);
            this.setBlockState(worldIn, field_175828_a, 1, 2, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175828_a, 0, 2, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175828_a, 1, 2, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175828_a, 0, 2, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175828_a, 6, 2, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175828_a, 7, 2, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175828_a, 6, 2, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175828_a, 7, 2, 1, structureBoundingBoxIn);
            if (!this.field_175830_k.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 6, 3, 0, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 6, 2, 0, field_175828_a, field_175828_a, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 6, 1, 0, field_175826_b, field_175826_b, false);
            }

            if (!this.field_175830_k.field_175966_c[EnumFacing.NORTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 6, 3, 7, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 6, 2, 7, field_175828_a, field_175828_a, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 6, 1, 7, field_175826_b, field_175826_b, false);
            }

            if (!this.field_175830_k.field_175966_c[EnumFacing.WEST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 3, 6, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 1, 0, 2, 6, field_175828_a, field_175828_a, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 1, 6, field_175826_b, field_175826_b, false);
            }

            if (!this.field_175830_k.field_175966_c[EnumFacing.EAST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 1, 7, 3, 6, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 1, 7, 2, 6, field_175828_a, field_175828_a, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 1, 7, 1, 6, field_175826_b, field_175826_b, false);
            }
         } else if (this.field_175833_o == 2) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 6, 1, 0, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 6, 1, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 7, field_175827_c, field_175827_c, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 7, field_175827_c, field_175827_c, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 6, 2, 0, field_175827_c, field_175827_c, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 6, 2, 7, field_175827_c, field_175827_c, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 3, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 6, 3, 0, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 6, 3, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, field_175827_c, field_175827_c, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, field_175827_c, field_175827_c, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, field_175827_c, field_175827_c, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, field_175827_c, field_175827_c, false);
            if (this.field_175830_k.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
               this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.NORTH.getIndex()]) {
               this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, false);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.WEST.getIndex()]) {
               this.func_181655_a(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, false);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.EAST.getIndex()]) {
               this.func_181655_a(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, false);
            }
         }

         if (bl) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 3, 4, 1, 4, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 3, 4, 2, 4, field_175828_a, field_175828_a, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 3, 4, 3, 4, field_175826_b, field_175826_b, false);
         }

         return true;
      }
   }

   public static class SimpleTopRoom extends StructureOceanMonumentPieces.Piece {
      private static final long b = 4173082555260L;

      public SimpleTopRoom() {
      }

      public SimpleTopRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         super(1, enumFacing, roomDefinition, 1, 1, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(worldIn, structureBoundingBoxIn, 0, 0, this.field_175830_k.field_175966_c[EnumFacing.DOWN.getIndex()]);
         }

         if (this.field_175830_k.field_175965_b[EnumFacing.UP.getIndex()] == null) {
            this.func_175819_a(worldIn, structureBoundingBoxIn, 1, 4, 1, 6, 4, 6, field_175828_a);
         }

         for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 6; j++) {
               if (randomIn.nextInt(3) != 0) {
                  int k = 2 + (randomIn.nextInt(4) == 0 ? 0 : 1);
                  this.fillWithBlocks(
                     worldIn,
                     structureBoundingBoxIn,
                     i,
                     k,
                     j,
                     i,
                     3,
                     j,
                     Blocks.sponge.getStateFromMeta(1),
                     Blocks.sponge.getStateFromMeta(1),
                     false
                  );
               }
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 6, 1, 0, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 6, 1, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 7, field_175827_c, field_175827_c, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 7, field_175827_c, field_175827_c, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 6, 2, 0, field_175827_c, field_175827_c, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 6, 2, 7, field_175827_c, field_175827_c, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 3, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 6, 3, 0, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 6, 3, 7, field_175826_b, field_175826_b, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, field_175827_c, field_175827_c, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, field_175827_c, field_175827_c, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, field_175827_c, field_175827_c, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, field_175827_c, field_175827_c, false);
         if (this.field_175830_k.field_175966_c[EnumFacing.SOUTH.getIndex()]) {
            this.func_181655_a(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
         }

         return true;
      }
   }

   public static class WingRoom extends StructureOceanMonumentPieces.Piece {
      private int field_175834_o;
      private static final long b = 79906429099015L;

      public WingRoom() {
      }

      public WingRoom(EnumFacing enumFacing, StructureBoundingBox structureBoundingBox, int i) {
         super(enumFacing, structureBoundingBox);
         this.field_175834_o = i & 1;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         long l = b ^ 65776948531496L;
         if (this.field_175834_o == 0) {
            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, field_175826_b, field_175826_b, false
               );
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 0, 6, 15, 0, 16, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 6, 6, 3, 20, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 16, 0, 6, 16, 3, 20, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 7, 7, 1, 20, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 1, 7, 15, 1, 20, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 6, 9, 3, 6, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 6, 15, 3, 6, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 7, 9, 1, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 7, 14, 1, 7, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 5, 13, 0, 5, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 0, 7, 12, 0, 7, field_175827_c, field_175827_c, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 10, 8, 0, 12, field_175827_c, field_175827_c, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 14, 0, 10, 14, 0, 12, field_175827_c, field_175827_c, false);

            for (int j = 18; j >= 7; j -= 3) {
               this.setBlockState(worldIn, field_175825_e, 6, 3, j, structureBoundingBoxIn);
               this.setBlockState(worldIn, field_175825_e, 16, 3, j, structureBoundingBoxIn);
            }

            this.setBlockState(worldIn, field_175825_e, 10, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 12, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 10, 0, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 12, 0, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 8, 3, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 14, 3, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175826_b, 4, 2, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 4, 1, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175826_b, 4, 0, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175826_b, 18, 2, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 18, 1, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175826_b, 18, 0, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175826_b, 4, 2, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 4, 1, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175826_b, 4, 0, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175826_b, 18, 2, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175825_e, 18, 1, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175826_b, 18, 0, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175826_b, 9, 7, 20, structureBoundingBoxIn);
            this.setBlockState(worldIn, field_175826_b, 13, 7, 20, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 21, 7, 4, 21, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 0, 21, 16, 4, 21, field_175826_b, field_175826_b, false);
            this.func_175817_a(worldIn, structureBoundingBoxIn, 11, 2, 16);
         } else if (this.field_175834_o == 1) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 3, 18, 13, 3, 20, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 18, 9, 2, 18, field_175826_b, field_175826_b, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 0, 18, 13, 2, 18, field_175826_b, field_175826_b, false);
            byte b2 = 9;
            byte b3 = 20;
            byte b4 = 5;

            for (int k = 0; k < 2; k++) {
               this.setBlockState(worldIn, field_175826_b, b2, b4 + 1, b3, structureBoundingBoxIn);
               this.setBlockState(worldIn, field_175825_e, b2, b4, b3, structureBoundingBoxIn);
               long m = 1143257020355153389L ^ l;
               this.setBlockState(worldIn, field_175826_b, b2, b4 - 1, b3, structureBoundingBoxIn);
               b2 = 13;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 7, 15, 3, 14, field_175826_b, field_175826_b, false);
            b2 = 10;

            for (int n = 0; n < 2; n++) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, 0, 10, b2, 6, 10, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, 0, 12, b2, 6, 12, field_175826_b, field_175826_b, false);
               this.setBlockState(worldIn, field_175825_e, b2, 0, 10, structureBoundingBoxIn);
               this.setBlockState(worldIn, field_175825_e, b2, 0, 12, structureBoundingBoxIn);
               this.setBlockState(worldIn, field_175825_e, b2, 4, 10, structureBoundingBoxIn);
               this.setBlockState(worldIn, field_175825_e, b2, 4, 12, structureBoundingBoxIn);
               b2 = 12;
            }

            b2 = 8;

            for (int i2 = 0; i2 < 2; i2++) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, 0, 7, b2, 2, 7, field_175826_b, field_175826_b, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, b2, 0, 14, b2, 2, 14, field_175826_b, field_175826_b, false);
               b2 = 14;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 3, 8, 8, 3, 13, field_175827_c, field_175827_c, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 14, 3, 8, 14, 3, 13, field_175827_c, field_175827_c, false);
            this.func_175817_a(worldIn, structureBoundingBoxIn, 11, 5, 13);
         }

         return true;
      }
   }

   public static class XDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      private static final long a = 73319407361597L;

      public XDoubleRoomFitHelper() {
      }

      @Override
      public boolean func_175969_a(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         return roomDefinition.field_175966_c[EnumFacing.EAST.getIndex()] && !roomDefinition.field_175965_b[EnumFacing.EAST.getIndex()].field_175963_d;
      }

      @Override
      public StructureOceanMonumentPieces.Piece func_175968_a(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         roomDefinition.field_175963_d = true;
         roomDefinition.field_175965_b[EnumFacing.EAST.getIndex()].field_175963_d = true;
         return new StructureOceanMonumentPieces.DoubleXRoom(enumFacing, roomDefinition, random2);
      }
   }

   public static class XYDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      private static final long a = 22242257750520L;

      public XYDoubleRoomFitHelper() {
      }

      @Override
      public boolean func_175969_a(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         if (roomDefinition.field_175966_c[EnumFacing.EAST.getIndex()]
            && !roomDefinition.field_175965_b[EnumFacing.EAST.getIndex()].field_175963_d
            && roomDefinition.field_175966_c[EnumFacing.UP.getIndex()]
            && !roomDefinition.field_175965_b[EnumFacing.UP.getIndex()].field_175963_d) {
            StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = roomDefinition.field_175965_b[EnumFacing.EAST.getIndex()];
            return roomDefinition2.field_175966_c[EnumFacing.UP.getIndex()] && !roomDefinition2.field_175965_b[EnumFacing.UP.getIndex()].field_175963_d;
         } else {
            return false;
         }
      }

      @Override
      public StructureOceanMonumentPieces.Piece func_175968_a(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         roomDefinition.field_175963_d = true;
         roomDefinition.field_175965_b[EnumFacing.EAST.getIndex()].field_175963_d = true;
         roomDefinition.field_175965_b[EnumFacing.UP.getIndex()].field_175963_d = true;
         roomDefinition.field_175965_b[EnumFacing.EAST.getIndex()].field_175965_b[EnumFacing.UP.getIndex()].field_175963_d = true;
         return new StructureOceanMonumentPieces.DoubleXYRoom(enumFacing, roomDefinition, random2);
      }
   }

   public static class YDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      private static final long a = 56213066498337L;

      public YDoubleRoomFitHelper() {
      }

      @Override
      public boolean func_175969_a(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         return roomDefinition.field_175966_c[EnumFacing.UP.getIndex()] && !roomDefinition.field_175965_b[EnumFacing.UP.getIndex()].field_175963_d;
      }

      @Override
      public StructureOceanMonumentPieces.Piece func_175968_a(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         roomDefinition.field_175963_d = true;
         roomDefinition.field_175965_b[EnumFacing.UP.getIndex()].field_175963_d = true;
         return new StructureOceanMonumentPieces.DoubleYRoom(enumFacing, roomDefinition, random2);
      }
   }

   public static class YZDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      private static final long a = 87906146264163L;

      public YZDoubleRoomFitHelper() {
      }

      @Override
      public boolean func_175969_a(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         if (roomDefinition.field_175966_c[EnumFacing.NORTH.getIndex()]
            && !roomDefinition.field_175965_b[EnumFacing.NORTH.getIndex()].field_175963_d
            && roomDefinition.field_175966_c[EnumFacing.UP.getIndex()]
            && !roomDefinition.field_175965_b[EnumFacing.UP.getIndex()].field_175963_d) {
            StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = roomDefinition.field_175965_b[EnumFacing.NORTH.getIndex()];
            return roomDefinition2.field_175966_c[EnumFacing.UP.getIndex()] && !roomDefinition2.field_175965_b[EnumFacing.UP.getIndex()].field_175963_d;
         } else {
            return false;
         }
      }

      @Override
      public StructureOceanMonumentPieces.Piece func_175968_a(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         roomDefinition.field_175963_d = true;
         roomDefinition.field_175965_b[EnumFacing.NORTH.getIndex()].field_175963_d = true;
         roomDefinition.field_175965_b[EnumFacing.UP.getIndex()].field_175963_d = true;
         roomDefinition.field_175965_b[EnumFacing.NORTH.getIndex()].field_175965_b[EnumFacing.UP.getIndex()].field_175963_d = true;
         return new StructureOceanMonumentPieces.DoubleYZRoom(enumFacing, roomDefinition, random2);
      }
   }

   public static class ZDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      private static final long a = 89915992218853L;

      public ZDoubleRoomFitHelper() {
      }

      @Override
      public boolean func_175969_a(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         return roomDefinition.field_175966_c[EnumFacing.NORTH.getIndex()] && !roomDefinition.field_175965_b[EnumFacing.NORTH.getIndex()].field_175963_d;
      }

      @Override
      public StructureOceanMonumentPieces.Piece func_175968_a(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random2) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = roomDefinition;
         if (!roomDefinition.field_175966_c[EnumFacing.NORTH.getIndex()] || roomDefinition.field_175965_b[EnumFacing.NORTH.getIndex()].field_175963_d) {
            roomDefinition2 = roomDefinition.field_175965_b[EnumFacing.SOUTH.getIndex()];
         }

         roomDefinition2.field_175963_d = true;
         roomDefinition2.field_175965_b[EnumFacing.NORTH.getIndex()].field_175963_d = true;
         return new StructureOceanMonumentPieces.DoubleZRoom(enumFacing, roomDefinition2, random2);
      }
   }
}
