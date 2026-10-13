package shadersmod.client;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.src.BlockPosM;
import net.minecraft.util.BlockPos;

public class IteratorAxis implements Iterator<BlockPos> {
   private double yDelta;
   private double zDelta;
   private int xStart;
   private int xEnd;
   private double yStart;
   private double yEnd;
   private double zStart;
   private double zEnd;
   private int xNext;
   private double yNext;
   private double zNext;
   private BlockPosM pos = new BlockPosM(0, 0, 0);
   private boolean hasNext = false;
   private static final long b = 23353596639726L;

   public IteratorAxis(BlockPos var1, BlockPos var2, double var3, double var5) {
      this.yDelta = var3;
      this.zDelta = var5;
      this.xStart = var1.getX();
      this.xEnd = var2.getX();
      this.yStart = var1.getY();
      this.yEnd = var2.getY() - 0.5;
      this.zStart = var1.getZ();
      this.zEnd = var2.getZ() - 0.5;
      this.xNext = this.xStart;
      this.yNext = this.yStart;
      this.zNext = this.zStart;
      this.hasNext = this.xNext < this.xEnd && this.yNext < this.yEnd && this.zNext < this.zEnd;
   }

   @Override
   public boolean hasNext() {
      return this.hasNext;
   }

   public BlockPos next() {
      if (!this.hasNext) {
         throw new NoSuchElementException();
      }

      this.pos.setXyz(this.xNext, this.yNext, this.zNext);
      this.nextPos();
      this.hasNext = this.xNext < this.xEnd && this.yNext < this.yEnd && this.zNext < this.zEnd;
      return this.pos;
   }

   private void nextPos() {
      this.zNext++;
      if (this.zNext >= this.zEnd) {
         this.zNext = this.zStart;
         this.yNext++;
         if (this.yNext >= this.yEnd) {
            this.yNext = this.yStart;
            this.yStart = this.yStart + this.yDelta;
            this.yEnd = this.yEnd + this.yDelta;
            this.yNext = this.yStart;
            this.zStart = this.zStart + this.zDelta;
            this.zEnd = this.zEnd + this.zDelta;
            this.zNext = this.zStart;
            this.xNext++;
            if (this.xNext >= this.xEnd) {
            }
         }
      }
   }

   @Override
   public void remove() {
      throw new RuntimeException("Not implemented");
   }

   public static void main(String[] var0) throws Exception {
      BlockPos var4 = new BlockPos(-2, 10, 20);
      BlockPos var5 = new BlockPos(2, 12, 22);
      double var6 = -0.5;
      double var8 = 0.5;
      IteratorAxis var10 = new IteratorAxis(var4, var5, var6, var8);
      System.out.println("Start: " + var4 + ", end: " + var5 + ", yDelta: " + var6 + ", zDelta: " + var8);

      while (var10.hasNext()) {
         BlockPos var11 = var10.next();
         System.out.println("" + var11);
      }
   }
}
