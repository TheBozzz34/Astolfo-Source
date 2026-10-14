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

   public Iterator3d(BlockPos posStart, BlockPos posEnd, int width, int height) {
      boolean bl = posStart.getX() > posEnd.getX();
      boolean bl2 = posStart.getY() > posEnd.getY();
      boolean bl3 = posStart.getZ() > posEnd.getZ();
      posStart = this.reverseCoord(posStart, bl, bl2, bl3);
      posEnd = this.reverseCoord(posEnd, bl, bl2, bl3);
      this.kX = bl ? -1 : 1;
      this.kY = bl2 ? -1 : 1;
      this.kZ = bl3 ? -1 : 1;
      Vec3 vec3 = new Vec3(posEnd.getX() - posStart.getX(), posEnd.getY() - posStart.getY(), posEnd.getZ() - posStart.getZ());
      Vec3 vec32 = vec3.normalize();
      Vec3 vec33 = new Vec3(1.0, 0.0, 0.0);
      double d = vec32.dotProduct(vec33);
      double e = Math.abs(d);
      Vec3 vec34 = new Vec3(0.0, 1.0, 0.0);
      double d2 = vec32.dotProduct(vec34);
      double d3 = Math.abs(d2);
      Vec3 vec35 = new Vec3(0.0, 0.0, 1.0);
      double d4 = vec32.dotProduct(vec35);
      double d5 = Math.abs(d4);
      if (d5 >= d3 && d5 >= e) {
         this.axis = 2;
         BlockPos blockPos = new BlockPos(posStart.getZ(), posStart.getY() - width, posStart.getX() - height);
         BlockPos blockPos2 = new BlockPos(posEnd.getZ(), posStart.getY() + width + 1, posStart.getX() + height + 1);
         int i = posEnd.getZ() - posStart.getZ();
         double d6 = (posEnd.getY() - posStart.getY()) / (1.0 * i);
         double d7 = (posEnd.getX() - posStart.getX()) / (1.0 * i);
         this.iteratorAxis = new IteratorAxis(blockPos, blockPos2, d6, d7);
      } else if (d3 >= e && d3 >= d5) {
         this.axis = 1;
         BlockPos blockPos3 = new BlockPos(posStart.getY(), posStart.getX() - width, posStart.getZ() - height);
         BlockPos blockPos4 = new BlockPos(posEnd.getY(), posStart.getX() + width + 1, posStart.getZ() + height + 1);
         int j = posEnd.getY() - posStart.getY();
         double d8 = (posEnd.getX() - posStart.getX()) / (1.0 * j);
         double d9 = (posEnd.getZ() - posStart.getZ()) / (1.0 * j);
         this.iteratorAxis = new IteratorAxis(blockPos3, blockPos4, d8, d9);
      } else {
         this.axis = 0;
         BlockPos blockPos5 = new BlockPos(posStart.getX(), posStart.getY() - width, posStart.getZ() - height);
         BlockPos blockPos6 = new BlockPos(posEnd.getX(), posStart.getY() + width + 1, posStart.getZ() + height + 1);
         int k = posEnd.getX() - posStart.getX();
         double d10 = (posEnd.getY() - posStart.getY()) / (1.0 * k);
         double d11 = (posEnd.getZ() - posStart.getZ()) / (1.0 * k);
         this.iteratorAxis = new IteratorAxis(blockPos5, blockPos6, d10, d11);
      }
   }

   private BlockPos reverseCoord(BlockPos pos, boolean revX, boolean revY, boolean revZ) {
      if (revX) {
         pos = new BlockPos(-pos.getX(), pos.getY(), pos.getZ());
      }

      if (revY) {
         pos = new BlockPos(pos.getX(), -pos.getY(), pos.getZ());
      }

      if (revZ) {
         pos = new BlockPos(pos.getX(), pos.getY(), -pos.getZ());
      }

      return pos;
   }

   @Override
   public boolean hasNext() {
      return this.iteratorAxis.hasNext();
   }

   public BlockPos next() {
      BlockPos blockPos2 = this.iteratorAxis.next();
      switch (this.axis) {
         case 0:
            this.blockPos.setXyz(blockPos2.getX() * this.kX, blockPos2.getY() * this.kY, blockPos2.getZ() * this.kZ);
            return this.blockPos;
         case 1:
            this.blockPos.setXyz(blockPos2.getY() * this.kX, blockPos2.getX() * this.kY, blockPos2.getZ() * this.kZ);
            return this.blockPos;
         case 2:
            this.blockPos.setXyz(blockPos2.getZ() * this.kX, blockPos2.getY() * this.kY, blockPos2.getX() * this.kZ);
            return this.blockPos;
         default:
            this.blockPos.setXyz(blockPos2.getX() * this.kX, blockPos2.getY() * this.kY, blockPos2.getZ() * this.kZ);
            return this.blockPos;
      }
   }

   @Override
   public void remove() {
      throw new RuntimeException("Not supported");
   }

   public static void main(String[] args) {
      BlockPos blockPos = new BlockPos(10, 20, 30);
      BlockPos blockPos2 = new BlockPos(30, 40, 20);
      Iterator3d iterator3d = new Iterator3d(blockPos, blockPos2, 1, 1);

      while (iterator3d.hasNext()) {
         BlockPos blockPos3 = iterator3d.next();
         System.out.println("" + blockPos3);
      }
   }
}
