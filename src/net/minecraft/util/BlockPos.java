package net.minecraft.util;

import bypass.util.Vector3;
import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.entity.Entity;

public class BlockPos extends Vec3i {
   public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
   private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
   private static final int NUM_Z_BITS = NUM_X_BITS;
   private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
   private static final int Y_SHIFT = 0 + NUM_Z_BITS;
   private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
   private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
   private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
   private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;
   private static final long b = 108386554913412L;

   public BlockPos(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public BlockPos(double var1, double var3, double var5) {
      super(var1, var3, var5);
   }

   public BlockPos(Entity var1) {
      this(var1.posX, var1.posY, var1.posZ);
   }

   public BlockPos(Vec3 var1) {
      this(var1.xCoord, var1.yCoord, var1.zCoord);
   }

   public BlockPos(Vector3 var1) {
      this(var1.getX(), var1.getY(), var1.getZ());
   }

   public BlockPos(Vec3i var1) {
      this(var1.getX(), var1.getY(), var1.getZ());
   }

   public BlockPos add(double var1, double var3, double var5) {
      return var1 == 0.0 && var3 == 0.0 && var5 == 0.0 ? this : new BlockPos(this.getX() + var1, this.getY() + var3, this.getZ() + var5);
   }

   public BlockPos add(int var1, int var2, int var3) {
      return var1 == 0 && var2 == 0 && var3 == 0 ? this : new BlockPos(this.getX() + var1, this.getY() + var2, this.getZ() + var3);
   }

   public BlockPos add(Vec3i var1) {
      return var1.getX() == 0 && var1.getY() == 0 && var1.getZ() == 0
         ? this
         : new BlockPos(this.getX() + var1.getX(), this.getY() + var1.getY(), this.getZ() + var1.getZ());
   }

   public BlockPos subtract(Vec3i var1) {
      return var1.getX() == 0 && var1.getY() == 0 && var1.getZ() == 0
         ? this
         : new BlockPos(this.getX() - var1.getX(), this.getY() - var1.getY(), this.getZ() - var1.getZ());
   }

   public BlockPos up() {
      return this.up(1);
   }

   public BlockPos up(int var1) {
      return this.offset(EnumFacing.UP, var1);
   }

   public BlockPos down() {
      return this.down(1);
   }

   public BlockPos down(int var1) {
      return this.offset(EnumFacing.DOWN, var1);
   }

   public BlockPos north() {
      return this.north(1);
   }

   public BlockPos north(int var1) {
      return this.offset(EnumFacing.NORTH, var1);
   }

   public BlockPos south() {
      return this.south(1);
   }

   public BlockPos south(int var1) {
      return this.offset(EnumFacing.SOUTH, var1);
   }

   public BlockPos west() {
      return this.west(1);
   }

   public BlockPos west(int var1) {
      return this.offset(EnumFacing.WEST, var1);
   }

   public BlockPos east() {
      return this.east(1);
   }

   public BlockPos east(int var1) {
      return this.offset(EnumFacing.EAST, var1);
   }

   public BlockPos offset(EnumFacing var1) {
      return this.offset(var1, 1);
   }

   public BlockPos offset(EnumFacing var1, int var2) {
      return var2 == 0
         ? this
         : new BlockPos(this.getX() + var1.getFrontOffsetX() * var2, this.getY() + var1.getFrontOffsetY() * var2, this.getZ() + var1.getFrontOffsetZ() * var2);
   }

   public BlockPos crossProduct(Vec3i var1) {
      return new BlockPos(
         this.getY() * var1.getZ() - this.getZ() * var1.getY(),
         this.getZ() * var1.getX() - this.getX() * var1.getZ(),
         this.getX() * var1.getY() - this.getY() * var1.getX()
      );
   }

   public long toLong() {
      return (this.getX() & X_MASK) << X_SHIFT | (this.getY() & Y_MASK) << Y_SHIFT | (this.getZ() & Z_MASK) << 0;
   }

   public static BlockPos fromLong(long var0) {
      int var4 = (int)(var0 << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
      int var5 = (int)(var0 << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
      int var6 = (int)(var0 << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
      return new BlockPos(var4, var5, var6);
   }

   public static Iterable<BlockPos> getAllInBox(BlockPos var0, BlockPos var1) {
      final BlockPos var2 = new BlockPos(Math.min(var0.getX(), var1.getX()), Math.min(var0.getY(), var1.getY()), Math.min(var0.getZ(), var1.getZ()));
      final BlockPos var3 = new BlockPos(Math.max(var0.getX(), var1.getX()), Math.max(var0.getY(), var1.getY()), Math.max(var0.getZ(), var1.getZ()));
      return new Iterable<BlockPos>() {
         @Override
         public Iterator<BlockPos> iterator() {
            return new AbstractIterator<BlockPos>() {
               private BlockPos lastReturned = null;
               private static final long a = 45254728709738L;

               public BlockPos computeNext() {
                  if (this.lastReturned == null) {
                     this.lastReturned = var2;
                     return this.lastReturned;
                  }

                  if (this.lastReturned.equals(var3)) {
                     return this.endOfData();
                  }

                  int var4 = this.lastReturned.getX();
                  int var5 = this.lastReturned.getY();
                  int var6 = this.lastReturned.getZ();
                  if (var4 < var3.getX()) {
                     var4++;
                  } else if (var5 < var3.getY()) {
                     var4 = var2.getX();
                     var5++;
                  } else if (var6 < var3.getZ()) {
                     var4 = var2.getX();
                     var5 = var2.getY();
                     var6++;
                  }

                  this.lastReturned = new BlockPos(var4, var5, var6);
                  return this.lastReturned;
               }
            };
         }
      };
   }

   public static Iterable<BlockPos.MutableBlockPos> getAllInBoxMutable(BlockPos var0, BlockPos var1) {
      final BlockPos var2 = new BlockPos(Math.min(var0.getX(), var1.getX()), Math.min(var0.getY(), var1.getY()), Math.min(var0.getZ(), var1.getZ()));
      final BlockPos var3 = new BlockPos(Math.max(var0.getX(), var1.getX()), Math.max(var0.getY(), var1.getY()), Math.max(var0.getZ(), var1.getZ()));
      return new Iterable<BlockPos.MutableBlockPos>() {
         @Override
         public Iterator<BlockPos.MutableBlockPos> iterator() {
            return new AbstractIterator<BlockPos.MutableBlockPos>() {
               private BlockPos.MutableBlockPos theBlockPos = null;
               private static final long a = 26671989468164L;

               public BlockPos.MutableBlockPos computeNext() {
                  if (this.theBlockPos == null) {
                     this.theBlockPos = new BlockPos.MutableBlockPos(var2.getX(), var2.getY(), var2.getZ());
                     return this.theBlockPos;
                  }

                  if (this.theBlockPos.equals(var3)) {
                     return this.endOfData();
                  }

                  int var4 = this.theBlockPos.getX();
                  int var5 = this.theBlockPos.getY();
                  int var6 = this.theBlockPos.getZ();
                  if (var4 < var3.getX()) {
                     var4++;
                  } else if (var5 < var3.getY()) {
                     var4 = var2.getX();
                     var5++;
                  } else if (var6 < var3.getZ()) {
                     var4 = var2.getX();
                     var5 = var2.getY();
                     var6++;
                  }

                  this.theBlockPos.x = var4;
                  this.theBlockPos.y = var5;
                  this.theBlockPos.z = var6;
                  return this.theBlockPos;
               }
            };
         }
      };
   }

   public static final class MutableBlockPos extends BlockPos {
      private int x;
      private int y;
      private int z;

      public MutableBlockPos() {
         this(0, 0, 0);
      }

      public MutableBlockPos(int var1, int var2, int var3) {
         super(0, 0, 0);
         this.x = var1;
         this.y = var2;
         this.z = var3;
      }

      @Override
      public int getX() {
         return this.x;
      }

      @Override
      public int getY() {
         return this.y;
      }

      @Override
      public int getZ() {
         return this.z;
      }

      public BlockPos.MutableBlockPos set(int var1, int var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
         return this;
      }
   }
}
