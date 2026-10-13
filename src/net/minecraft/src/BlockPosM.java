package net.minecraft.src;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class BlockPosM extends BlockPos {
   private int mx;
   private int my;
   private int mz;
   private int level;
   private BlockPosM[] facings;
   private boolean needsUpdate;
   private static final long a = 138849673806756L;
   private static final long q = 840837197920731142L;

   public BlockPosM(int var1, int var2, int var3) {
      this(var1, var2, var3, 0);
   }

   public BlockPosM(double var1, double var3, double var5) {
      this(MathHelper.floor_double(var1), MathHelper.floor_double(var3), MathHelper.floor_double(var5));
   }

   public BlockPosM(int var1, int var2, int var3, int var4) {
      super(0, 0, 0);
      this.mx = var1;
      this.my = var2;
      this.mz = var3;
      this.level = var4;
   }

   @Override
   public int getX() {
      return this.mx;
   }

   @Override
   public int getY() {
      return this.my;
   }

   @Override
   public int getZ() {
      return this.mz;
   }

   public void setXyz(int var1, int var2, int var3) {
      this.mx = var1;
      this.my = var2;
      this.mz = var3;
      this.needsUpdate = true;
   }

   public void setXyz(double var1, double var3, double var5) {
      this.setXyz(MathHelper.floor_double(var1), MathHelper.floor_double(var3), MathHelper.floor_double(var5));
   }

   @Override
   public BlockPos offset(EnumFacing var1) {
      if (this.level <= 0) {
         return super.offset(var1, 1);
      }

      if (this.facings == null) {
         this.facings = new BlockPosM[EnumFacing.VALUES.length];
      }

      if (this.needsUpdate) {
         this.update();
      }

      int var5 = var1.getIndex();
      BlockPosM var6 = this.facings[var5];
      if (var6 == null) {
         int var7 = this.mx + var1.getFrontOffsetX();
         int var8 = this.my + var1.getFrontOffsetY();
         int var9 = this.mz + var1.getFrontOffsetZ();
         var6 = new BlockPosM(var7, var8, var9, this.level - 1);
         this.facings[var5] = var6;
      }

      return var6;
   }

   @Override
   public BlockPos offset(EnumFacing var1, int var2) {
      return var2 == 1 ? this.offset(var1) : super.offset(var1, var2);
   }

   private void update() {
      for (int var4 = 0; var4 < (int)q; var4++) {
         BlockPosM var5 = this.facings[var4];
         if (var5 != null) {
            EnumFacing var6 = EnumFacing.VALUES[var4];
            int var7 = this.mx + var6.getFrontOffsetX();
            int var8 = this.my + var6.getFrontOffsetY();
            int var9 = this.mz + var6.getFrontOffsetZ();
            var5.setXyz(var7, var8, var9);
         }
      }

      this.needsUpdate = false;
   }

   public static Iterable m(BlockPos var0, BlockPos var1) {
      BlockPos var2 = new BlockPos(Math.min(var0.getX(), var1.getX()), Math.min(var0.getY(), var1.getY()), Math.min(var0.getZ(), var1.getZ()));
      BlockPos var3 = new BlockPos(Math.max(var0.getX(), var1.getX()), Math.max(var0.getY(), var1.getY()), Math.max(var0.getZ(), var1.getZ()));
      return new BlockPosM.AllInBoxIterable(var2, var3);
   }

   public BlockPos getImmutable() {
      return new BlockPos(this.getX(), this.getY(), this.getZ());
   }

   public static final class AllInBoxIterable implements Iterable {
      public final BlockPos G;
      public final BlockPos z;

      public AllInBoxIterable(BlockPos var1, BlockPos var2) {
         this.G = var1;
         this.z = var2;
      }

      @Override
      public Iterator iterator() {
         return new BlockPosM.AllInBoxIterable.r4d();
      }

      public class r4d extends AbstractIterator {
         private BlockPosM M = null;
         private static final long a = 1294726928773L;

         public BlockPosM Y() {
            if (this.M == null) {
               this.M = new BlockPosM(AllInBoxIterable.this.G.getX(), AllInBoxIterable.this.G.getY(), AllInBoxIterable.this.G.getZ(), 3);
               return this.M;
            }

            if (this.M.equals(AllInBoxIterable.this.z)) {
               return (BlockPosM)this.endOfData();
            }

            int var4 = this.M.getX();
            int var5 = this.M.getY();
            int var6 = this.M.getZ();
            if (var4 < AllInBoxIterable.this.z.getX()) {
               var4++;
            } else if (var5 < AllInBoxIterable.this.z.getY()) {
               var4 = AllInBoxIterable.this.G.getX();
               var5++;
            } else if (var6 < AllInBoxIterable.this.z.getZ()) {
               var4 = AllInBoxIterable.this.G.getX();
               var5 = AllInBoxIterable.this.G.getY();
               var6++;
            }

            this.M.setXyz(var4, var5, var6);
            return this.M;
         }

         @Override
         public Object computeNext() {
            return this.Y();
         }
      }
   }
}
