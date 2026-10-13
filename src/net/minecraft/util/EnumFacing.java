package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public enum EnumFacing implements IStringSerializable {
   DOWN("DOWN", 0, 0, 1, -1, "down", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y, new Vec3i(0, -1, 0)),
   UP("UP", 1, 1, 0, -1, "up", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH("NORTH", 2, 2, 3, 2, "north", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH("SOUTH", 3, 3, 2, 0, "south", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, 1)),
   WEST("WEST", 4, 4, 5, 1, "west", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vec3i(-1, 0, 0)),
   EAST("EAST", 5, 5, 4, 3, "east", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vec3i(1, 0, 0));

   private final int index;
   private final int opposite;
   private final int horizontalIndex;
   private final String name;
   private final EnumFacing.Axis axis;
   private final EnumFacing.AxisDirection axisDirection;
   private final Vec3i directionVec;
   public static final EnumFacing[] VALUES = new EnumFacing[6];
   private static final EnumFacing[] i = new EnumFacing[4];
   private static final Map<String, EnumFacing> NAME_LOOKUP = Maps.newHashMap();
   private static final EnumFacing[] M = new EnumFacing[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
   private static final long a = 86352628985591L;

   EnumFacing(String var3, int var4, int var5, int var6, int var7, String var8, EnumFacing.AxisDirection var9, EnumFacing.Axis var10, Vec3i var11) {
      this.index = var5;
      this.horizontalIndex = var7;
      this.opposite = var6;
      this.name = var8;
      this.axis = var10;
      this.axisDirection = var9;
      this.directionVec = var11;
   }

   public int getIndex() {
      return this.index;
   }

   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   public EnumFacing.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public EnumFacing getOpposite() {
      return VALUES[this.opposite];
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public EnumFacing rotateAround(EnumFacing.Axis var1) {
      switch (var1) {
         case X:
            if (this != WEST && this != EAST) {
               return this.rotateX();
            }

            return this;
         case Y:
            if (this != UP && this != DOWN) {
               return this.rotateY();
            }

            return this;
         case Z:
            if (this != NORTH && this != SOUTH) {
               return this.rotateZ();
            }

            return this;
         default:
            throw new IllegalStateException("Unable to get CW facing for axis " + var1);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public EnumFacing rotateY() {
      switch (this) {
         case NORTH:
            return EAST;
         case EAST:
            return SOUTH;
         case SOUTH:
            return WEST;
         case WEST:
            return NORTH;
         default:
            throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private EnumFacing rotateX() {
      switch (this) {
         case NORTH:
            return DOWN;
         case EAST:
         case WEST:
         default:
            throw new IllegalStateException("Unable to get X-rotated facing of " + this);
         case SOUTH:
            return UP;
         case UP:
            return NORTH;
         case DOWN:
            return SOUTH;
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private EnumFacing rotateZ() {
      switch (this) {
         case EAST:
            return DOWN;
         case SOUTH:
         default:
            throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
         case WEST:
            return UP;
         case UP:
            return EAST;
         case DOWN:
            return WEST;
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public EnumFacing rotateYCCW() {
      switch (this) {
         case NORTH:
            return WEST;
         case EAST:
            return NORTH;
         case SOUTH:
            return EAST;
         case WEST:
            return SOUTH;
         default:
            throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int getFrontOffsetX() {
      return this.axis == EnumFacing.Axis.X ? this.axisDirection.getOffset() : 0;
   }

   public int getFrontOffsetY() {
      return this.axis == EnumFacing.Axis.Y ? this.axisDirection.getOffset() : 0;
   }

   public int getFrontOffsetZ() {
      return this.axis == EnumFacing.Axis.Z ? this.axisDirection.getOffset() : 0;
   }

   public String j() {
      return this.name;
   }

   public EnumFacing.Axis getAxis() {
      return this.axis;
   }

   public static EnumFacing byName(String var0) {
      return var0 == null ? null : NAME_LOOKUP.get(var0.toLowerCase());
   }

   public static EnumFacing getFront(int var0) {
      return VALUES[MathHelper.abs_int(var0 % VALUES.length)];
   }

   public static EnumFacing getHorizontal(int var0) {
      return i[MathHelper.abs_int(var0 % i.length)];
   }

   public static EnumFacing fromAngle(double var0) {
      return getHorizontal(MathHelper.floor_double(var0 / 90.0 + 0.5) & 3);
   }

   public static EnumFacing random(Random var0) {
      return values()[var0.nextInt(values().length)];
   }

   public static EnumFacing getFacingFromVector(float var0, float var1, float var2) {
      EnumFacing var6 = NORTH;
      float var7 = Float.MIN_VALUE;

      for (EnumFacing var11 : values()) {
         float var12 = var0 * var11.directionVec.getX() + var1 * var11.directionVec.getY() + var2 * var11.directionVec.getZ();
         if (var12 > var7) {
            var7 = var12;
            var6 = var11;
         }
      }

      return var6;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public static EnumFacing getFacingFromAxis(EnumFacing.AxisDirection var0, EnumFacing.Axis var1) {
      for (EnumFacing var8 : values()) {
         if (var8.getAxisDirection() == var0 && var8.getAxis() == var1) {
            return var8;
         }
      }

      throw new IllegalArgumentException("No such direction: " + var0 + " " + var1);
   }

   public Vec3i getDirectionVec() {
      return this.directionVec;
   }

   static {
      for (EnumFacing var25 : values()) {
         VALUES[var25.index] = var25;
         if (var25.getAxis().isHorizontal()) {
            i[var25.horizontalIndex] = var25;
         }

         NAME_LOOKUP.put(var25.j().toLowerCase(), var25);
      }
   }

   public enum Axis implements Predicate<EnumFacing>, IStringSerializable {
      X("X", 0, "x", EnumFacing.Plane.HORIZONTAL),
      Y("Y", 1, "y", EnumFacing.Plane.VERTICAL),
      Z("Z", 2, "z", EnumFacing.Plane.HORIZONTAL);

      private static final Map<String, EnumFacing.Axis> NAME_LOOKUP = Maps.newHashMap();
      private final String name;
      private final EnumFacing.Plane plane;
      private static final EnumFacing.Axis[] S = new EnumFacing.Axis[]{X, Y, Z};
      private static final long a = 91497545210819L;

      Axis(String var3, int var4, String var5, EnumFacing.Plane var6) {
         this.name = var5;
         this.plane = var6;
      }

      public static EnumFacing.Axis byName(String var0) {
         return var0 == null ? null : NAME_LOOKUP.get(var0.toLowerCase());
      }

      public String E() {
         return this.name;
      }

      public boolean isVertical() {
         return this.plane == EnumFacing.Plane.VERTICAL;
      }

      public boolean isHorizontal() {
         return this.plane == EnumFacing.Plane.HORIZONTAL;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public boolean apply(EnumFacing var1) {
         return var1 != null && var1.getAxis() == this;
      }

      public EnumFacing.Plane getPlane() {
         return this.plane;
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (EnumFacing.Axis var3 : values()) {
            NAME_LOOKUP.put(var3.E().toLowerCase(), var3);
         }
      }
   }

   public enum AxisDirection {
      POSITIVE("POSITIVE", 0, 1, "Towards positive"),
      NEGATIVE("NEGATIVE", 1, -1, "Towards negative");

      private final int offset;
      private final String description;
      private static final EnumFacing.AxisDirection[] Q = new EnumFacing.AxisDirection[]{POSITIVE, NEGATIVE};

      AxisDirection(String var3, int var4, int var5, String var6) {
         this.offset = var5;
         this.description = var6;
      }

      public int getOffset() {
         return this.offset;
      }

      @Override
      public String toString() {
         return this.description;
      }
   }

   public enum Plane implements Predicate<EnumFacing>, Iterable {
      HORIZONTAL("HORIZONTAL", 0),
      VERTICAL("VERTICAL", 1);

      private static final EnumFacing.Plane[] c = new EnumFacing.Plane[]{HORIZONTAL, VERTICAL};
      private static final long a = 35545594493901L;

      Plane(String var3, int var4) {
      }

      public EnumFacing[] facings() {
         switch (this) {
            case HORIZONTAL:
               return new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
            case VERTICAL:
               return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
            default:
               throw new Error("Someone's been tampering with the universe!");
         }
      }

      public EnumFacing random(Random var1) {
         EnumFacing[] var2 = this.facings();
         return var2[var1.nextInt(var2.length)];
      }

      public boolean apply(EnumFacing var1) {
         return var1 != null && var1.getAxis().getPlane() == this;
      }

      @Override
      public Iterator<EnumFacing> iterator() {
         return Iterators.forArray(this.facings());
      }
   }
}
