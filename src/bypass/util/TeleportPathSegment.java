package bypass.util;

import bypass.ClientAccess;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class TeleportPathSegment implements ClientAccess {
   private static final Minecraft q = Minecraft.getMinecraft();
   private static final Vector3[] w = new Vector3[]{
      new Vector3(1.0, 0.0, 0.0), new Vector3(-1.0, 0.0, 0.0), new Vector3(0.0, 0.0, 1.0), new Vector3(0.0, 0.0, -1.0)
   };
   private final Vector3 T;
   private final Vector3 Q;
   private final ArrayList<PathNode> W = new ArrayList<>();
   private final ArrayList<PathNode> j = new ArrayList<>();
   private ArrayList<Vector3> p = new ArrayList<>();
   private static final long b = 99435034601936L;
   private static final long c = -3625881858811624472L;

   public TeleportPathSegment(Vector3 vector3, Vector3 vector32) {
      this.T = vector3.add(0.0, 0.0, 0.0).floor();
      this.Q = vector32.add(0.0, 0.0, 0.0).floor();
   }

   public static boolean D(Vector3 vector3) {
      BlockPos blockPos = new BlockPos(vector3);
      return !E(blockPos) && !E(blockPos.add(0, 1, 0)) ? Z(blockPos.add(0, -1, 0)) : false;
   }

   private static boolean E(BlockPos blockPos) {
      Block block = mc.theWorld.getBlockState(blockPos).getBlock();
      return block.isFullBlock()
         || block instanceof BlockSlab
         || block instanceof BlockStairs
         || block instanceof BlockCactus
         || block instanceof BlockChest
         || block instanceof BlockEnderChest
         || block instanceof BlockSkull
         || block instanceof BlockPane
         || block instanceof BlockFence
         || block instanceof BlockWall
         || block instanceof BlockGlass
         || block instanceof BlockPistonBase
         || block instanceof BlockPistonExtension
         || block instanceof BlockPistonMoving
         || block instanceof BlockStainedGlass
         || block instanceof BlockTrapDoor;
   }

   private static boolean Z(BlockPos blockPos) {
      Block block = mc.theWorld.getBlockState(blockPos).getBlock();
      return !(block instanceof BlockFence) && !(block instanceof BlockWall);
   }

   public ArrayList<Vector3> c() {
      return this.p;
   }

   public void H() {
      this.i((int)c, 4);
   }

   public void i(int i, int j) {
      this.p.clear();
      this.j.clear();
      ArrayList arrayList = new ArrayList();
      Vector3 vector3 = this.T;
      arrayList.add(vector3);
      Vector3[] vector3s = w;
      this.j.add(new PathNode(vector3, null, arrayList, vector3.distanceSq(this.Q), 0.0, 0.0));

      label51:
      for (int k = 0; k < i; k++) {
         ArrayList arrayList2 = this.j;
         int m = arrayList2.size();
         arrayList2.sort(new PathNodeComparator());
         int n = 0;
         if (m == 0) {
            break;
         }

         for (int i2 = 0; i2 < m; i2++) {
            PathNode pathNode = (PathNode)arrayList2.get(i2);
            if (++n <= j) {
               arrayList2.remove(pathNode);
               this.W.add(pathNode);
               Vector3 vector32 = pathNode.K();
               int i3 = vector3s.length;

               for (int i4 = 0; i4 < i3; i4++) {
                  Vector3 vector33 = vector32.add(vector3s[i4]).floor();
                  if (D(vector33) && this.w(pathNode, vector33, 0.0)) {
                     break label51;
                  }
               }

               Vector3 vector34 = vector32.add(0.0, 1.0, 0.0).floor();
               Vector3 vector35;
               if (D(vector34) && this.w(pathNode, vector34, 0.0) || D(vector35 = vector32.add(0.0, -1.0, 0.0).floor()) && this.w(pathNode, vector35, 0.0)) {
                  break label51;
               }
               continue;
            }
         }
      }

      this.W.sort(new PathNodeComparator());
      this.p = this.W.get(0).l();
   }

   public PathNode f(Vector3 vector3) {
      ArrayList arrayList = this.W;
      int i = arrayList.size();

      for (int k = 0; k < i; k++) {
         PathNode pathNode = (PathNode)arrayList.get(k);
         Vector3 vector32 = pathNode.K();
         if (vector32.getX() == vector3.getX() && vector32.getY() == vector3.getY() && vector32.getZ() == vector3.getZ()) {
            return pathNode;
         }
      }

      ArrayList arrayList2 = this.j;
      int l = arrayList2.size();

      for (int m = 0; m < l; m++) {
         PathNode pathNode2 = (PathNode)arrayList2.get(m);
         Vector3 vector33 = pathNode2.K();
         if (vector33.getX() == vector3.getX() && vector33.getY() == vector3.getY() && vector33.getZ() == vector3.getZ()) {
            return pathNode2;
         }
      }

      return null;
   }

   public boolean w(PathNode pathNode, Vector3 vector3, double d) {
      PathNode pathNode2 = this.f(vector3);
      double e = d;
      if (pathNode != null) {
         e += pathNode.d();
      }

      Vector3 vector32 = this.Q;
      ArrayList arrayList = pathNode.l();
      if (pathNode2 == null) {
         if (vector3.getX() == vector32.getX() && vector3.getY() == vector32.getY() && vector3.getZ() == vector32.getZ()) {
            this.p.clear();
            this.p = arrayList;
            this.p.add(vector3);
            return true;
         }

         arrayList.add(vector3);
         this.j.add(new PathNode(vector3, pathNode, arrayList, vector3.distanceSq(vector32), d, e));
      } else if (pathNode2.u() > d) {
         arrayList.add(vector3);
         pathNode2.p(vector3);
         pathNode2.u(pathNode);
         pathNode2.t(arrayList);
         pathNode2.y(vector3.distanceSq(vector32));
         pathNode2.b(d);
         pathNode2.S(e);
      }

      return false;
   }
}
