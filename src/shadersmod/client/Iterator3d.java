package shadersmod.client;

import java.util.Iterator;
import net.minecraft.src.BlockPosM;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class Iterator3d implements Iterator<BlockPos> {
   private IteratorAxis iteratorAxis;
   private BlockPosM blockPos = new BlockPosM(0, 0, 0);
   private int axis = 0;
   private int kX;
   private int kY;
   private int kZ;
   private static final int Q = 2;
   private static final long a = 11131627207805L;

   public Iterator3d(BlockPos var1, BlockPos var2, int var3, int var4) {
      boolean var8 = var1.getX() > var2.getX();
      boolean var9 = var1.getY() > var2.getY();
      boolean var10 = var1.getZ() > var2.getZ();
      var1 = this.reverseCoord(var1, var8, var9, var10);
      var2 = this.reverseCoord(var2, var8, var9, var10);
      this.kX = var8 ? -1 : 1;
      this.kY = var9 ? -1 : 1;
      this.kZ = var10 ? -1 : 1;
      Vec3 var11 = new Vec3(var2.getX() - var1.getX(), var2.getY() - var1.getY(), var2.getZ() - var1.getZ());
      Vec3 var12 = var11.normalize();
      Vec3 var13 = new Vec3(1.0, 0.0, 0.0);
      double var14 = var12.dotProduct(var13);
      double var16 = Math.abs(var14);
      Vec3 var18 = new Vec3(0.0, 1.0, 0.0);
      double var19 = var12.dotProduct(var18);
      double var21 = Math.abs(var19);
      Vec3 var23 = new Vec3(0.0, 0.0, 1.0);
      double var24 = var12.dotProduct(var23);
      double var26 = Math.abs(var24);
      if (var26 >= var21 && var26 >= var16) {
         this.axis = 2;
         BlockPos var38 = new BlockPos(var1.getZ(), var1.getY() - var3, var1.getX() - var4);
         BlockPos var40 = new BlockPos(var2.getZ(), var1.getY() + var3 + 1, var1.getX() + var4 + 1);
         int var42 = var2.getZ() - var1.getZ();
         double var44 = (var2.getY() - var1.getY()) / (1.0 * var42);
         double var46 = (var2.getX() - var1.getX()) / (1.0 * var42);
         this.iteratorAxis = new IteratorAxis(var38, var40, var44, var46);
      } else if (var21 >= var16 && var21 >= var26) {
         this.axis = 1;
         BlockPos var37 = new BlockPos(var1.getY(), var1.getX() - var3, var1.getZ() - var4);
         BlockPos var39 = new BlockPos(var2.getY(), var1.getX() + var3 + 1, var1.getZ() + var4 + 1);
         int var41 = var2.getY() - var1.getY();
         double var43 = (var2.getX() - var1.getX()) / (1.0 * var41);
         double var45 = (var2.getZ() - var1.getZ()) / (1.0 * var41);
         this.iteratorAxis = new IteratorAxis(var37, var39, var43, var45);
      } else {
         this.axis = 0;
         BlockPos var28 = new BlockPos(var1.getX(), var1.getY() - var3, var1.getZ() - var4);
         BlockPos var29 = new BlockPos(var2.getX(), var1.getY() + var3 + 1, var1.getZ() + var4 + 1);
         int var30 = var2.getX() - var1.getX();
         double var31 = (var2.getY() - var1.getY()) / (1.0 * var30);
         double var33 = (var2.getZ() - var1.getZ()) / (1.0 * var30);
         this.iteratorAxis = new IteratorAxis(var28, var29, var31, var33);
      }
   }

   private BlockPos reverseCoord(BlockPos var1, boolean var2, boolean var3, boolean var4) {
      if (var2) {
         var1 = new BlockPos(-var1.getX(), var1.getY(), var1.getZ());
      }

      if (var3) {
         var1 = new BlockPos(var1.getX(), -var1.getY(), var1.getZ());
      }

      if (var4) {
         var1 = new BlockPos(var1.getX(), var1.getY(), -var1.getZ());
      }

      return var1;
   }

   @Override
   public boolean hasNext() {
      return this.iteratorAxis.hasNext();
   }

   public BlockPos next() {
      BlockPos var4 = this.iteratorAxis.next();
      switch (this.axis) {
         case 0:
            this.blockPos.setXyz(var4.getX() * this.kX, var4.getY() * this.kY, var4.getZ() * this.kZ);
            return this.blockPos;
         case 1:
            this.blockPos.setXyz(var4.getY() * this.kX, var4.getX() * this.kY, var4.getZ() * this.kZ);
            return this.blockPos;
         case 2:
            this.blockPos.setXyz(var4.getZ() * this.kX, var4.getY() * this.kY, var4.getX() * this.kZ);
            return this.blockPos;
         default:
            this.blockPos.setXyz(var4.getX() * this.kX, var4.getY() * this.kY, var4.getZ() * this.kZ);
            return this.blockPos;
      }
   }

   @Override
   public void remove() {
      throw new RuntimeException("Not supported");
   }

   public static void main(String[] var0) {
      BlockPos var4 = new BlockPos(10, 20, 30);
      BlockPos var5 = new BlockPos(30, 40, 20);
      Iterator3d var6 = new Iterator3d(var4, var5, 1, 1);

      while (var6.hasNext()) {
         BlockPos var7 = var6.next();
         System.out.println("" + var7);
      }
   }

   static {
      long var11 = a ^ 48647763933626L;
      long var10000 = var11 ^ 5905291233816531510L;
   }
}
