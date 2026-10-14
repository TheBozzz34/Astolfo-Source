package net.minecraft.world.gen.structure;

import com.google.common.base.Objects;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

public class StructureBoundingBox {
   public int minX;
   public int minY;
   public int minZ;
   public int maxX;
   public int maxY;
   public int maxZ;
   private static final long a = 57668130757804L;

   public StructureBoundingBox() {
   }

   public StructureBoundingBox(int[] coords) {
      if (coords.length == 6) {
         this.minX = coords[0];
         this.minY = coords[1];
         this.minZ = coords[2];
         this.maxX = coords[3];
         this.maxY = coords[4];
         this.maxZ = coords[5];
      }
   }

   public static StructureBoundingBox getNewBoundingBox() {
      return new StructureBoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
   }

   public static StructureBoundingBox getComponentToAddBoundingBox(int i, int j, int k, int l, int m, int n, int i2, int i3, int i4, EnumFacing enumFacing) {
      switch (enumFacing) {
         case NORTH:
            return new StructureBoundingBox(i + l, j + m, k - i4 + 1 + n, i + i2 - 1 + l, j + i3 - 1 + m, k + n);
         case SOUTH:
            return new StructureBoundingBox(i + l, j + m, k + n, i + i2 - 1 + l, j + i3 - 1 + m, k + i4 - 1 + n);
         case WEST:
            return new StructureBoundingBox(i - i4 + 1 + n, j + m, k + l, i + n, j + i3 - 1 + m, k + i2 - 1 + l);
         case EAST:
            return new StructureBoundingBox(i + n, j + m, k + l, i + i4 - 1 + n, j + i3 - 1 + m, k + i2 - 1 + l);
         default:
            return new StructureBoundingBox(i + l, j + m, k + n, i + i2 - 1 + l, j + i3 - 1 + m, k + i4 - 1 + n);
      }
   }

   public static StructureBoundingBox func_175899_a(int i, int j, int k, int l, int m, int n) {
      return new StructureBoundingBox(Math.min(i, l), Math.min(j, m), Math.min(k, n), Math.max(i, l), Math.max(j, m), Math.max(k, n));
   }

   public StructureBoundingBox(StructureBoundingBox structurebb) {
      this.minX = structurebb.minX;
      this.minY = structurebb.minY;
      this.minZ = structurebb.minZ;
      this.maxX = structurebb.maxX;
      this.maxY = structurebb.maxY;
      this.maxZ = structurebb.maxZ;
   }

   public StructureBoundingBox(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
      this.minX = xMin;
      this.minY = yMin;
      this.minZ = zMin;
      this.maxX = xMax;
      this.maxY = yMax;
      this.maxZ = zMax;
   }

   public StructureBoundingBox(Vec3i vec1, Vec3i vec2) {
      this.minX = Math.min(vec1.getX(), vec2.getX());
      this.minY = Math.min(vec1.getY(), vec2.getY());
      this.minZ = Math.min(vec1.getZ(), vec2.getZ());
      this.maxX = Math.max(vec1.getX(), vec2.getX());
      this.maxY = Math.max(vec1.getY(), vec2.getY());
      this.maxZ = Math.max(vec1.getZ(), vec2.getZ());
   }

   public StructureBoundingBox(int xMin, int zMin, int xMax, int zMax) {
      this.minX = xMin;
      this.minZ = zMin;
      this.maxX = xMax;
      this.maxZ = zMax;
      this.minY = 1;
      this.maxY = 512;
   }

   public boolean intersectsWith(StructureBoundingBox structurebb) {
      return this.maxX >= structurebb.minX
         && this.minX <= structurebb.maxX
         && this.maxZ >= structurebb.minZ
         && this.minZ <= structurebb.maxZ
         && this.maxY >= structurebb.minY
         && this.minY <= structurebb.maxY;
   }

   public boolean intersectsWith(int minXIn, int minZIn, int maxXIn, int maxZIn) {
      return this.maxX >= minXIn && this.minX <= maxXIn && this.maxZ >= minZIn && this.minZ <= maxZIn;
   }

   public void expandTo(StructureBoundingBox sbb) {
      this.minX = Math.min(this.minX, sbb.minX);
      this.minY = Math.min(this.minY, sbb.minY);
      this.minZ = Math.min(this.minZ, sbb.minZ);
      this.maxX = Math.max(this.maxX, sbb.maxX);
      this.maxY = Math.max(this.maxY, sbb.maxY);
      this.maxZ = Math.max(this.maxZ, sbb.maxZ);
   }

   public void offset(int x, int y, int z) {
      this.minX += x;
      this.minY += y;
      this.minZ += z;
      this.maxX += x;
      this.maxY += y;
      this.maxZ += z;
   }

   public boolean isVecInside(Vec3i vec) {
      return vec.getX() >= this.minX
         && vec.getX() <= this.maxX
         && vec.getZ() >= this.minZ
         && vec.getZ() <= this.maxZ
         && vec.getY() >= this.minY
         && vec.getY() <= this.maxY;
   }

   public Vec3i func_175896_b() {
      return new Vec3i(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
   }

   public int G() {
      return this.maxX - this.minX + 1;
   }

   public int getYSize() {
      return this.maxY - this.minY + 1;
   }

   public int S() {
      return this.maxZ - this.minZ + 1;
   }

   public Vec3i getCenter() {
      return new BlockPos(this.minX + (this.maxX - this.minX + 1) / 2, this.minY + (this.maxY - this.minY + 1) / 2, this.minZ + (this.maxZ - this.minZ + 1) / 2);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
         .add("x0", this.minX)
         .add("y0", this.minY)
         .add("z0", this.minZ)
         .add("x1", this.maxX)
         .add("y1", this.maxY)
         .add("z1", this.maxZ)
         .toString();
   }

   public NBTTagIntArray toNBTTagIntArray() {
      return new NBTTagIntArray(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
   }
}
