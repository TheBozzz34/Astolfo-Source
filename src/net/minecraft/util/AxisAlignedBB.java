package net.minecraft.util;

public class AxisAlignedBB {
   public double minX;
   public double minY;
   public double minZ;
   public double maxX;
   public double maxY;
   public double maxZ;
   private static final long a = 127124594499623L;

   public AxisAlignedBB(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.minX = Math.min(var1, var7);
      this.minY = Math.min(var3, var9);
      this.minZ = Math.min(var5, var11);
      this.maxX = Math.max(var1, var7);
      this.maxY = Math.max(var3, var9);
      this.maxZ = Math.max(var5, var11);
   }

   public AxisAlignedBB(BlockPos var1, BlockPos var2) {
      this.minX = var1.getX();
      this.minY = var1.getY();
      this.minZ = var1.getZ();
      this.maxX = var2.getX();
      this.maxY = var2.getY();
      this.maxZ = var2.getZ();
   }

   public AxisAlignedBB(Vec3 var1, Vec3 var2) {
      this.minX = var1.xCoord;
      this.minY = var1.yCoord;
      this.minZ = var1.zCoord;
      this.maxX = var2.xCoord;
      this.maxY = var2.yCoord;
      this.maxZ = var2.zCoord;
   }

   public AxisAlignedBB addCoord(double var1, double var3, double var5) {
      double var10 = this.minX;
      double var12 = this.minY;
      double var14 = this.minZ;
      double var16 = this.maxX;
      double var18 = this.maxY;
      double var20 = this.maxZ;
      if (var1 < 0.0) {
         var10 += var1;
      } else if (var1 > 0.0) {
         var16 += var1;
      }

      if (var3 < 0.0) {
         var12 += var3;
      } else if (var3 > 0.0) {
         var18 += var3;
      }

      if (var5 < 0.0) {
         var14 += var5;
      } else if (var5 > 0.0) {
         var20 += var5;
      }

      return new AxisAlignedBB(var10, var12, var14, var16, var18, var20);
   }

   public AxisAlignedBB expand(double var1, double var3, double var5) {
      double var7 = this.minX - var1;
      double var9 = this.minY - var3;
      double var11 = this.minZ - var5;
      double var13 = this.maxX + var1;
      double var15 = this.maxY + var3;
      double var17 = this.maxZ + var5;
      return new AxisAlignedBB(var7, var9, var11, var13, var15, var17);
   }

   public AxisAlignedBB union(AxisAlignedBB var1) {
      double var2 = Math.min(this.minX, var1.minX);
      double var4 = Math.min(this.minY, var1.minY);
      double var6 = Math.min(this.minZ, var1.minZ);
      double var8 = Math.max(this.maxX, var1.maxX);
      double var10 = Math.max(this.maxY, var1.maxY);
      double var12 = Math.max(this.maxZ, var1.maxZ);
      return new AxisAlignedBB(var2, var4, var6, var8, var10, var12);
   }

   public AxisAlignedBB X(double var1, double var3, double var5) {
      this.minX += var1;
      this.minY += var3;
      this.minZ += var5;
      this.maxX += var1;
      this.maxY += var3;
      this.maxZ += var5;
      return this;
   }

   public static AxisAlignedBB fromBounds(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = Math.min(var0, var6);
      double var14 = Math.min(var2, var8);
      double var16 = Math.min(var4, var10);
      double var18 = Math.max(var0, var6);
      double var20 = Math.max(var2, var8);
      double var22 = Math.max(var4, var10);
      return new AxisAlignedBB(var12, var14, var16, var18, var20, var22);
   }

   public AxisAlignedBB offset(double var1, double var3, double var5) {
      return new AxisAlignedBB(this.minX + var1, this.minY + var3, this.minZ + var5, this.maxX + var1, this.maxY + var3, this.maxZ + var5);
   }

   public AxisAlignedBB K(BlockPos var1) {
      return this.offset(var1.getX(), var1.getY(), var1.getZ());
   }

   public double A(AxisAlignedBB var1, double var2) {
      if (var1.maxY > this.minY && var1.minY < this.maxY && var1.maxZ > this.minZ && var1.minZ < this.maxZ) {
         if (var2 > 0.0 && var1.maxX <= this.minX) {
            double var9 = this.minX - var1.maxX;
            if (var9 < var2) {
               var2 = var9;
            }
         } else if (var2 < 0.0 && var1.minX >= this.maxX) {
            double var7 = this.maxX - var1.minX;
            if (var7 > var2) {
               var2 = var7;
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public double calculateYOffset(AxisAlignedBB var1, double var2) {
      if (var1.maxX > this.minX && var1.minX < this.maxX && var1.maxZ > this.minZ && var1.minZ < this.maxZ) {
         if (var2 > 0.0 && var1.maxY <= this.minY) {
            double var9 = this.minY - var1.maxY;
            if (var9 < var2) {
               var2 = var9;
            }
         } else if (var2 < 0.0 && var1.minY >= this.maxY) {
            double var7 = this.maxY - var1.minY;
            if (var7 > var2) {
               var2 = var7;
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public double v(AxisAlignedBB var1, double var2) {
      if (var1.maxX > this.minX && var1.minX < this.maxX && var1.maxY > this.minY && var1.minY < this.maxY) {
         if (var2 > 0.0 && var1.maxZ <= this.minZ) {
            double var9 = this.minZ - var1.maxZ;
            if (var9 < var2) {
               var2 = var9;
            }
         } else if (var2 < 0.0 && var1.minZ >= this.maxZ) {
            double var7 = this.maxZ - var1.minZ;
            if (var7 > var2) {
               var2 = var7;
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public boolean intersectsWith(AxisAlignedBB var1) {
      return !(var1.maxX > this.minX) || !(var1.minX < this.maxX)
         ? false
         : (var1.maxY > this.minY && var1.minY < this.maxY ? var1.maxZ > this.minZ && var1.minZ < this.maxZ : false);
   }

   public boolean isVecInside(Vec3 var1) {
      return !(var1.xCoord > this.minX) || !(var1.xCoord < this.maxX)
         ? false
         : (var1.yCoord > this.minY && var1.yCoord < this.maxY ? var1.zCoord > this.minZ && var1.zCoord < this.maxZ : false);
   }

   public double getAverageEdgeLength() {
      double var1 = this.maxX - this.minX;
      double var3 = this.maxY - this.minY;
      double var5 = this.maxZ - this.minZ;
      return (var1 + var3 + var5) / 3.0;
   }

   public AxisAlignedBB contract(double var1, double var3, double var5) {
      double var7 = this.minX + var1;
      double var9 = this.minY + var3;
      double var11 = this.minZ + var5;
      double var13 = this.maxX - var1;
      double var15 = this.maxY - var3;
      double var17 = this.maxZ - var5;
      return new AxisAlignedBB(var7, var9, var11, var13, var15, var17);
   }

   public MovingObjectPosition calculateIntercept(Vec3 var1, Vec3 var2) {
      Vec3 var6 = var1.getIntermediateWithXValue(var2, this.minX);
      Vec3 var7 = var1.getIntermediateWithXValue(var2, this.maxX);
      Vec3 var8 = var1.getIntermediateWithYValue(var2, this.minY);
      Vec3 var9 = var1.getIntermediateWithYValue(var2, this.maxY);
      Vec3 var10 = var1.getIntermediateWithZValue(var2, this.minZ);
      Vec3 var11 = var1.getIntermediateWithZValue(var2, this.maxZ);
      if (!this.isVecInYZ(var6)) {
         var6 = null;
      }

      if (!this.isVecInYZ(var7)) {
         var7 = null;
      }

      if (!this.isVecInXZ(var8)) {
         var8 = null;
      }

      if (!this.isVecInXZ(var9)) {
         var9 = null;
      }

      if (!this.isVecInXY(var10)) {
         var10 = null;
      }

      if (!this.isVecInXY(var11)) {
         var11 = null;
      }

      Vec3 var12 = null;
      if (var6 != null) {
         var12 = var6;
      }

      if (var7 != null && (var12 == null || var1.squareDistanceTo(var7) < var1.squareDistanceTo(var12))) {
         var12 = var7;
      }

      if (var8 != null && (var12 == null || var1.squareDistanceTo(var8) < var1.squareDistanceTo(var12))) {
         var12 = var8;
      }

      if (var9 != null && (var12 == null || var1.squareDistanceTo(var9) < var1.squareDistanceTo(var12))) {
         var12 = var9;
      }

      if (var10 != null && (var12 == null || var1.squareDistanceTo(var10) < var1.squareDistanceTo(var12))) {
         var12 = var10;
      }

      if (var11 != null && (var12 == null || var1.squareDistanceTo(var11) < var1.squareDistanceTo(var12))) {
         var12 = var11;
      }

      if (var12 == null) {
         return null;
      }

      EnumFacing var13 = null;
      if (var12 == var6) {
         var13 = EnumFacing.WEST;
      } else if (var12 == var7) {
         var13 = EnumFacing.EAST;
      } else if (var12 == var8) {
         var13 = EnumFacing.DOWN;
      } else if (var12 == var9) {
         var13 = EnumFacing.UP;
      } else if (var12 == var10) {
         var13 = EnumFacing.NORTH;
      } else {
         var13 = EnumFacing.SOUTH;
      }

      return new MovingObjectPosition(var12, var13);
   }

   private boolean isVecInYZ(Vec3 var1) {
      return var1 == null ? false : var1.yCoord >= this.minY && var1.yCoord <= this.maxY && var1.zCoord >= this.minZ && var1.zCoord <= this.maxZ;
   }

   private boolean isVecInXZ(Vec3 var1) {
      return var1 == null ? false : var1.xCoord >= this.minX && var1.xCoord <= this.maxX && var1.zCoord >= this.minZ && var1.zCoord <= this.maxZ;
   }

   private boolean isVecInXY(Vec3 var1) {
      return var1 == null ? false : var1.xCoord >= this.minX && var1.xCoord <= this.maxX && var1.yCoord >= this.minY && var1.yCoord <= this.maxY;
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
