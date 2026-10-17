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

   public BlockPosM(int x, int y, int z) {
      this(x, y, z, 0);
   }

   public BlockPosM(double xIn, double yIn, double zIn) {
      this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
   }

   public BlockPosM(int x, int y, int z, int level) {
      super(0, 0, 0);
      this.mx = x;
      this.my = y;
      this.mz = z;
      this.level = level;
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

   public void setXyz(int x, int y, int z) {
      this.mx = x;
      this.my = y;
      this.mz = z;
      this.needsUpdate = true;
   }

   public void setXyz(double xIn, double yIn, double zIn) {
      this.setXyz(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
   }

   @Override
   public BlockPos offset(EnumFacing facing) {
      if (this.level <= 0) {
         return super.offset(facing, 1);
      }

      if (this.facings == null) {
         this.facings = new BlockPosM[EnumFacing.VALUES.length];
      }

      if (this.needsUpdate) {
         this.update();
      }

      int i = facing.getIndex();
      BlockPosM blockPosM = this.facings[i];
      if (blockPosM == null) {
         int j = this.mx + facing.getFrontOffsetX();
         int k = this.my + facing.getFrontOffsetY();
         int l = this.mz + facing.getFrontOffsetZ();
         blockPosM = new BlockPosM(j, k, l, this.level - 1);
         this.facings[i] = blockPosM;
      }

      return blockPosM;
   }

   @Override
   public BlockPos offset(EnumFacing facing, int n) {
      return n == 1 ? this.offset(facing) : super.offset(facing, n);
   }

   private void update() {
      for (int i = 0; i < 6; i++) {
         BlockPosM blockPosM = this.facings[i];
         if (blockPosM != null) {
            EnumFacing enumFacing = EnumFacing.VALUES[i];
            int j = this.mx + enumFacing.getFrontOffsetX();
            int k = this.my + enumFacing.getFrontOffsetY();
            int l = this.mz + enumFacing.getFrontOffsetZ();
            blockPosM.setXyz(j, k, l);
         }
      }

      this.needsUpdate = false;
   }

   public static Iterable getAllInBoxPosM(BlockPos blockPos, BlockPos blockPos2) {
      BlockPos blockPos3 = new BlockPos(
         Math.min(blockPos.getX(), blockPos2.getX()), Math.min(blockPos.getY(), blockPos2.getY()), Math.min(blockPos.getZ(), blockPos2.getZ())
      );
      BlockPos blockPos4 = new BlockPos(
         Math.max(blockPos.getX(), blockPos2.getX()), Math.max(blockPos.getY(), blockPos2.getY()), Math.max(blockPos.getZ(), blockPos2.getZ())
      );
      return new BlockPosM.AllInBoxIterable(blockPos3, blockPos4);
   }

   public BlockPos getImmutable() {
      return new BlockPos(this.getX(), this.getY(), this.getZ());
   }

   public static final class AllInBoxIterable implements Iterable {
      public final BlockPos from;
      public final BlockPos z;

      public AllInBoxIterable(BlockPos blockPos, BlockPos blockPos2) {
         this.from = blockPos;
         this.z = blockPos2;
      }

      @Override
      public Iterator iterator() {
         return new BlockPosM.AllInBoxIterable.r4d();
      }

      public class r4d extends AbstractIterator {
         private BlockPosM theBlockPos = null;

         public BlockPosM computeNextPos() {
            if (this.theBlockPos == null) {
               this.theBlockPos = new BlockPosM(AllInBoxIterable.this.from.getX(), AllInBoxIterable.this.from.getY(), AllInBoxIterable.this.from.getZ(), 3);
               return this.theBlockPos;
            }

            if (this.theBlockPos.equals(AllInBoxIterable.this.z)) {
               return (BlockPosM)this.endOfData();
            }

            int i = this.theBlockPos.getX();
            int j = this.theBlockPos.getY();
            int k = this.theBlockPos.getZ();
            if (i < AllInBoxIterable.this.z.getX()) {
               i++;
            } else if (j < AllInBoxIterable.this.z.getY()) {
               i = AllInBoxIterable.this.from.getX();
               j++;
            } else if (k < AllInBoxIterable.this.z.getZ()) {
               i = AllInBoxIterable.this.from.getX();
               j = AllInBoxIterable.this.from.getY();
               k++;
            }

            this.theBlockPos.setXyz(i, j, k);
            return this.theBlockPos;
         }

         @Override
         public Object computeNext() {
            return this.computeNextPos();
         }
      }
   }
}
