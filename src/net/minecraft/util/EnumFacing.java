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
   private static final EnumFacing[] HORIZONTALS = new EnumFacing[4];
   private static final Map<String, EnumFacing> NAME_LOOKUP = Maps.newHashMap();
   private static final EnumFacing[] ENUM_VALUES = new EnumFacing[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};

   EnumFacing(String string2, int j, int k, int l, int m, String string3, EnumFacing.AxisDirection axisDirection, EnumFacing.Axis axis, Vec3i vec3i) {
      this.index = k;
      this.horizontalIndex = m;
      this.opposite = l;
      this.name = string3;
      this.axis = axis;
      this.axisDirection = axisDirection;
      this.directionVec = vec3i;
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
   public EnumFacing rotateAround(EnumFacing.Axis axis) {
      switch (axis) {
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
            throw new IllegalStateException("Unable to get CW facing for axis " + axis);
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

   public String getName2() {
      return this.name;
   }

   public EnumFacing.Axis getAxis() {
      return this.axis;
   }

   public static EnumFacing byName(String name) {
      return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
   }

   public static EnumFacing getFront(int index) {
      return VALUES[MathHelper.abs_int(index % VALUES.length)];
   }

   public static EnumFacing getHorizontal(int i) {
      return HORIZONTALS[MathHelper.abs_int(i % HORIZONTALS.length)];
   }

   public static EnumFacing fromAngle(double angle) {
      return getHorizontal(MathHelper.floor_double(angle / 90.0 + 0.5) & 3);
   }

   public static EnumFacing random(Random rand) {
      return values()[rand.nextInt(values().length)];
   }

   public static EnumFacing getFacingFromVector(float f, float g, float h) {
      EnumFacing enumFacing = NORTH;
      float f2 = Float.MIN_VALUE;

      for (EnumFacing enumFacing2 : values()) {
         float f3 = f * enumFacing2.directionVec.getX() + g * enumFacing2.directionVec.getY() + h * enumFacing2.directionVec.getZ();
         if (f3 > f2) {
            f2 = f3;
            enumFacing = enumFacing2;
         }
      }

      return enumFacing;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public static EnumFacing getFacingFromAxis(EnumFacing.AxisDirection axisDirection, EnumFacing.Axis axis) {
      for (EnumFacing enumFacing : values()) {
         if (enumFacing.getAxisDirection() == axisDirection && enumFacing.getAxis() == axis) {
            return enumFacing;
         }
      }

      throw new IllegalArgumentException("No such direction: " + axisDirection + " " + axis);
   }

   public Vec3i getDirectionVec() {
      return this.directionVec;
   }

   static {
      for (EnumFacing enumFacing : values()) {
         VALUES[enumFacing.index] = enumFacing;
         if (enumFacing.getAxis().isHorizontal()) {
            HORIZONTALS[enumFacing.horizontalIndex] = enumFacing;
         }

         NAME_LOOKUP.put(enumFacing.getName2().toLowerCase(), enumFacing);
      }
   }

   public enum Axis implements Predicate<EnumFacing>, IStringSerializable {
      X("X", 0, "x", EnumFacing.Plane.HORIZONTAL),
      Y("Y", 1, "y", EnumFacing.Plane.VERTICAL),
      Z("Z", 2, "z", EnumFacing.Plane.HORIZONTAL);

      private static final Map<String, EnumFacing.Axis> NAME_LOOKUP = Maps.newHashMap();
      private final String name;
      private final EnumFacing.Plane plane;
      private static final EnumFacing.Axis[] AXIS_VALUES = new EnumFacing.Axis[]{X, Y, Z};

      Axis(String string2, int j, String string3, EnumFacing.Plane plane) {
         this.name = string3;
         this.plane = plane;
      }

      public static EnumFacing.Axis byName(String nameIn) {
         return nameIn == null ? null : NAME_LOOKUP.get(nameIn.toLowerCase());
      }

      public String getName2() {
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

      public boolean apply(EnumFacing enumFacing) {
         return enumFacing != null && enumFacing.getAxis() == this;
      }

      public EnumFacing.Plane getPlane() {
         return this.plane;
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (EnumFacing.Axis axis : values()) {
            NAME_LOOKUP.put(axis.getName2().toLowerCase(), axis);
         }
      }
   }

   public enum AxisDirection {
      POSITIVE("POSITIVE", 0, 1, "Towards positive"),
      NEGATIVE("NEGATIVE", 1, -1, "Towards negative");

      private final int offset;
      private final String description;
      private static final EnumFacing.AxisDirection[] AXIS_DIRECTION_VALUES = new EnumFacing.AxisDirection[]{POSITIVE, NEGATIVE};

      AxisDirection(String string2, int j, int k, String string3) {
         this.offset = k;
         this.description = string3;
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

      private static final EnumFacing.Plane[] PLANE_VALUES = new EnumFacing.Plane[]{HORIZONTAL, VERTICAL};

      Plane(String string2, int j) {
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

      public EnumFacing random(Random rand) {
         EnumFacing[] enumFacings = this.facings();
         return enumFacings[rand.nextInt(enumFacings.length)];
      }

      public boolean apply(EnumFacing enumFacing) {
         return enumFacing != null && enumFacing.getAxis().getPlane() == this;
      }

      @Override
      public Iterator<EnumFacing> iterator() {
         return Iterators.forArray(this.facings());
      }
   }
}
