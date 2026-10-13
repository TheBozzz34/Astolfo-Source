package net.minecraft.client.renderer;

import net.minecraft.util.EnumFacing;

public enum EnumFaceDirection {
   DOWN(
      new EnumFaceDirection.VertexInformation[]{
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         )
      }
   ),
   UP(
      new EnumFaceDirection.VertexInformation[]{
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         )
      }
   ),
   NORTH(
      new EnumFaceDirection.VertexInformation[]{
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         )
      }
   ),
   SOUTH(
      new EnumFaceDirection.VertexInformation[]{
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         )
      }
   ),
   WEST(
      new EnumFaceDirection.VertexInformation[]{
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         )
      }
   ),
   EAST(
      new EnumFaceDirection.VertexInformation[]{
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         ),
         new EnumFaceDirection.VertexInformation(
            EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
         )
      }
   );

   private static final EnumFaceDirection[] facings = new EnumFaceDirection[6];
   private final EnumFaceDirection.VertexInformation[] vertexInfos;

   public static EnumFaceDirection getFacing(EnumFacing var0) {
      return facings[var0.getIndex()];
   }

   EnumFaceDirection(EnumFaceDirection.VertexInformation[] var3) {
      this.vertexInfos = var3;
   }

   public EnumFaceDirection.VertexInformation getVertexInformation(int var1) {
      return this.vertexInfos[var1];
   }

   static {
      facings[EnumFaceDirection.Constants.DOWN_INDEX] = DOWN;
      facings[EnumFaceDirection.Constants.UP_INDEX] = UP;
      facings[EnumFaceDirection.Constants.NORTH_INDEX] = NORTH;
      facings[EnumFaceDirection.Constants.SOUTH_INDEX] = SOUTH;
      facings[EnumFaceDirection.Constants.WEST_INDEX] = WEST;
      facings[EnumFaceDirection.Constants.EAST_INDEX] = EAST;
   }

   public static final class Constants {
      public static final int SOUTH_INDEX = EnumFacing.SOUTH.getIndex();
      public static final int UP_INDEX = EnumFacing.UP.getIndex();
      public static final int EAST_INDEX = EnumFacing.EAST.getIndex();
      public static final int NORTH_INDEX = EnumFacing.NORTH.getIndex();
      public static final int DOWN_INDEX = EnumFacing.DOWN.getIndex();
      public static final int WEST_INDEX = EnumFacing.WEST.getIndex();
   }

   public static class VertexInformation {
      public final int xIndex;
      public final int yIndex;
      public final int zIndex;

      public VertexInformation(int var1, int var2, int var3) {
         this.xIndex = var1;
         this.yIndex = var2;
         this.zIndex = var3;
      }
   }
}
