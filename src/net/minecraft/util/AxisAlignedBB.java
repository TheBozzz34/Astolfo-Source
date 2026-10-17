package net.minecraft.util;

public class AxisAlignedBB {
   public double minX;
   public double minY;
   public double minZ;
   public double maxX;
   public double maxY;
   public double maxZ;

   public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
      this.minX = Math.min(x1, x2);
      this.minY = Math.min(y1, y2);
      this.minZ = Math.min(z1, z2);
      this.maxX = Math.max(x1, x2);
      this.maxY = Math.max(y1, y2);
      this.maxZ = Math.max(z1, z2);
   }

   public AxisAlignedBB(BlockPos pos1, BlockPos pos2) {
      this.minX = pos1.getX();
      this.minY = pos1.getY();
      this.minZ = pos1.getZ();
      this.maxX = pos2.getX();
      this.maxY = pos2.getY();
      this.maxZ = pos2.getZ();
   }

   public AxisAlignedBB(Vec3 vec3, Vec3 vec32) {
      this.minX = vec3.xCoord;
      this.minY = vec3.yCoord;
      this.minZ = vec3.zCoord;
      this.maxX = vec32.xCoord;
      this.maxY = vec32.yCoord;
      this.maxZ = vec32.zCoord;
   }

   public AxisAlignedBB addCoord(double x, double y, double z) {
      double d = this.minX;
      double e = this.minY;
      double d2 = this.minZ;
      double d3 = this.maxX;
      double d4 = this.maxY;
      double d5 = this.maxZ;
      if (x < 0.0) {
         d += x;
      } else if (x > 0.0) {
         d3 += x;
      }

      if (y < 0.0) {
         e += y;
      } else if (y > 0.0) {
         d4 += y;
      }

      if (z < 0.0) {
         d2 += z;
      } else if (z > 0.0) {
         d5 += z;
      }

      return new AxisAlignedBB(d, e, d2, d3, d4, d5);
   }

   public AxisAlignedBB expand(double x, double y, double z) {
      double d = this.minX - x;
      double e = this.minY - y;
      double d2 = this.minZ - z;
      double d3 = this.maxX + x;
      double d4 = this.maxY + y;
      double d5 = this.maxZ + z;
      return new AxisAlignedBB(d, e, d2, d3, d4, d5);
   }

   public AxisAlignedBB union(AxisAlignedBB other) {
      double d = Math.min(this.minX, other.minX);
      double e = Math.min(this.minY, other.minY);
      double d2 = Math.min(this.minZ, other.minZ);
      double d3 = Math.max(this.maxX, other.maxX);
      double d4 = Math.max(this.maxY, other.maxY);
      double d5 = Math.max(this.maxZ, other.maxZ);
      return new AxisAlignedBB(d, e, d2, d3, d4, d5);
   }

   public AxisAlignedBB offsetInPlace(double d, double e, double d2) {
      this.minX += d;
      this.minY += e;
      this.minZ += d2;
      this.maxX += d;
      this.maxY += e;
      this.maxZ += d2;
      return this;
   }

   public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
      double d = Math.min(x1, x2);
      double e = Math.min(y1, y2);
      double d2 = Math.min(z1, z2);
      double d3 = Math.max(x1, x2);
      double d4 = Math.max(y1, y2);
      double d5 = Math.max(z1, z2);
      return new AxisAlignedBB(d, e, d2, d3, d4, d5);
   }

   public AxisAlignedBB offset(double x, double y, double z) {
      return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
   }

   public AxisAlignedBB offsetByBlockPos(BlockPos blockPos) {
      return this.offset(blockPos.getX(), blockPos.getY(), blockPos.getZ());
   }

   public double calculateXOffset(AxisAlignedBB other, double offsetX) {
      if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
         if (offsetX > 0.0 && other.maxX <= this.minX) {
            double d = this.minX - other.maxX;
            if (d < offsetX) {
               offsetX = d;
            }
         } else if (offsetX < 0.0 && other.minX >= this.maxX) {
            double e = this.maxX - other.minX;
            if (e > offsetX) {
               offsetX = e;
            }
         }

         return offsetX;
      } else {
         return offsetX;
      }
   }

   public double calculateYOffset(AxisAlignedBB other, double offsetY) {
      if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
         if (offsetY > 0.0 && other.maxY <= this.minY) {
            double d = this.minY - other.maxY;
            if (d < offsetY) {
               offsetY = d;
            }
         } else if (offsetY < 0.0 && other.minY >= this.maxY) {
            double e = this.maxY - other.minY;
            if (e > offsetY) {
               offsetY = e;
            }
         }

         return offsetY;
      } else {
         return offsetY;
      }
   }

   public double calculateZOffset(AxisAlignedBB other, double offsetZ) {
      if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY) {
         if (offsetZ > 0.0 && other.maxZ <= this.minZ) {
            double d = this.minZ - other.maxZ;
            if (d < offsetZ) {
               offsetZ = d;
            }
         } else if (offsetZ < 0.0 && other.minZ >= this.maxZ) {
            double e = this.maxZ - other.minZ;
            if (e > offsetZ) {
               offsetZ = e;
            }
         }

         return offsetZ;
      } else {
         return offsetZ;
      }
   }

   public boolean intersectsWith(AxisAlignedBB other) {
      return !(other.maxX > this.minX) || !(other.minX < this.maxX)
         ? false
         : (other.maxY > this.minY && other.minY < this.maxY ? other.maxZ > this.minZ && other.minZ < this.maxZ : false);
   }

   public boolean isVecInside(Vec3 vec) {
      return !(vec.xCoord > this.minX) || !(vec.xCoord < this.maxX)
         ? false
         : (vec.yCoord > this.minY && vec.yCoord < this.maxY ? vec.zCoord > this.minZ && vec.zCoord < this.maxZ : false);
   }

   public double getAverageEdgeLength() {
      double d = this.maxX - this.minX;
      double e = this.maxY - this.minY;
      double d2 = this.maxZ - this.minZ;
      return (d + e + d2) / 3.0;
   }

   public AxisAlignedBB contract(double x, double y, double z) {
      double d = this.minX + x;
      double e = this.minY + y;
      double d2 = this.minZ + z;
      double d3 = this.maxX - x;
      double d4 = this.maxY - y;
      double d5 = this.maxZ - z;
      return new AxisAlignedBB(d, e, d2, d3, d4, d5);
   }

   public MovingObjectPosition calculateIntercept(Vec3 vecA, Vec3 vecB) {
      Vec3 vec3 = vecA.getIntermediateWithXValue(vecB, this.minX);
      Vec3 vec32 = vecA.getIntermediateWithXValue(vecB, this.maxX);
      Vec3 vec33 = vecA.getIntermediateWithYValue(vecB, this.minY);
      Vec3 vec34 = vecA.getIntermediateWithYValue(vecB, this.maxY);
      Vec3 vec35 = vecA.getIntermediateWithZValue(vecB, this.minZ);
      Vec3 vec36 = vecA.getIntermediateWithZValue(vecB, this.maxZ);
      if (!this.isVecInYZ(vec3)) {
         vec3 = null;
      }

      if (!this.isVecInYZ(vec32)) {
         vec32 = null;
      }

      if (!this.isVecInXZ(vec33)) {
         vec33 = null;
      }

      if (!this.isVecInXZ(vec34)) {
         vec34 = null;
      }

      if (!this.isVecInXY(vec35)) {
         vec35 = null;
      }

      if (!this.isVecInXY(vec36)) {
         vec36 = null;
      }

      Vec3 vec37 = null;
      if (vec3 != null) {
         vec37 = vec3;
      }

      if (vec32 != null && (vec37 == null || vecA.squareDistanceTo(vec32) < vecA.squareDistanceTo(vec37))) {
         vec37 = vec32;
      }

      if (vec33 != null && (vec37 == null || vecA.squareDistanceTo(vec33) < vecA.squareDistanceTo(vec37))) {
         vec37 = vec33;
      }

      if (vec34 != null && (vec37 == null || vecA.squareDistanceTo(vec34) < vecA.squareDistanceTo(vec37))) {
         vec37 = vec34;
      }

      if (vec35 != null && (vec37 == null || vecA.squareDistanceTo(vec35) < vecA.squareDistanceTo(vec37))) {
         vec37 = vec35;
      }

      if (vec36 != null && (vec37 == null || vecA.squareDistanceTo(vec36) < vecA.squareDistanceTo(vec37))) {
         vec37 = vec36;
      }

      if (vec37 == null) {
         return null;
      }

      EnumFacing enumFacing = null;
      if (vec37 == vec3) {
         enumFacing = EnumFacing.WEST;
      } else if (vec37 == vec32) {
         enumFacing = EnumFacing.EAST;
      } else if (vec37 == vec33) {
         enumFacing = EnumFacing.DOWN;
      } else if (vec37 == vec34) {
         enumFacing = EnumFacing.UP;
      } else if (vec37 == vec35) {
         enumFacing = EnumFacing.NORTH;
      } else {
         enumFacing = EnumFacing.SOUTH;
      }

      return new MovingObjectPosition(vec37, enumFacing);
   }

   private boolean isVecInYZ(Vec3 vec) {
      return vec == null ? false : vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
   }

   private boolean isVecInXZ(Vec3 vec) {
      return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
   }

   private boolean isVecInXY(Vec3 vec) {
      return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
   }

   @Override
   public String toString() {
      return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   public boolean hasNaN() {
      return Double.isNaN(this.minX)
         || Double.isNaN(this.minY)
         || Double.isNaN(this.minZ)
         || Double.isNaN(this.maxX)
         || Double.isNaN(this.maxY)
         || Double.isNaN(this.maxZ);
   }
}
