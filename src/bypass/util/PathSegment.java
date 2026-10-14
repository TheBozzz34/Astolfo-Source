package bypass.util;

import java.util.ArrayList;
import java.util.Comparator;
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

public class PathSegment {
   private static final Vector3[] a = new Vector3[]{
      new Vector3(1.0, 0.0, 0.0), new Vector3(-1.0, 0.0, 0.0), new Vector3(0.0, 0.0, 1.0), new Vector3(0.0, 0.0, -1.0)
   };
   private Vector3 E;
   private Vector3 v;
   private ArrayList<Vector3> c = new ArrayList<>();
   private ArrayList<PathSegmentNode> Y = new ArrayList<>();
   private ArrayList<PathSegmentNode> A = new ArrayList<>();
   private double x = 9.0;
   private boolean g = true;
   private static final long b = 110660964146179L;
   private static final long d = -8986403517014998040L;

   public PathSegment(Vector3 vector3, Vector3 vector32) {
      this.E = vector3.add(0.0, 0.0, 0.0).floor();
      this.v = vector32.add(0.0, 0.0, 0.0).floor();
   }

   private static boolean V(Vector3 vector3) {
      return H((int)vector3.getX(), (int)vector3.getY(), (int)vector3.getZ());
   }

   public static boolean H(int i, int j, int k) {
      BlockPos blockPos = new BlockPos(i, j, k);
      BlockPos blockPos2 = new BlockPos(i, j + 1, k);
      BlockPos blockPos3 = new BlockPos(i, j - 1, k);
      return !I(blockPos) && !I(blockPos2) && R(blockPos3);
   }

   private static boolean I(BlockPos blockPos) {
      Minecraft minecraft = Minecraft.getMinecraft();
      return minecraft.theWorld.getBlockState(blockPos).getBlock().isFullBlock()
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockSlab
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockStairs
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockCactus
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockChest
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockEnderChest
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockSkull
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockPane
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockFence
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockWall
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockGlass
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockPistonBase
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockPistonExtension
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockPistonMoving
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockStainedGlass
         || minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockTrapDoor;
   }

   private static boolean R(BlockPos blockPos) {
      Minecraft minecraft = Minecraft.getMinecraft();
      return !(minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockFence)
         && !(minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockWall);
   }

   public ArrayList<Vector3> W() {
      return this.c;
   }

   public void Q() {
      this.J((int)d, 4);
   }

   public void J(int i, int j) {
      this.c.clear();
      this.A.clear();
      ArrayList arrayList = new ArrayList();
      arrayList.add(this.E);
      this.A.add(new PathSegmentNode(this, this.E, null, arrayList, this.E.distanceSq(this.v), 0.0, 0.0));

      label55:
      for (int k = 0; k < i; k++) {
         this.A.sort(new PathSegment.f0());
         int l = 0;
         if (this.A.size() == 0) {
            break;
         }

         for (PathSegmentNode pathSegmentNode : new ArrayList<>(this.A)) {
            if (++l <= j) {
               this.A.remove(pathSegmentNode);
               this.Y.add(pathSegmentNode);

               for (Vector3 vector3 : a) {
                  Vector3 vector32 = PathSegmentNode.i(pathSegmentNode).add(vector3).floor();
                  if (V(vector32) && this.p(pathSegmentNode, vector32)) {
                     break label55;
                  }
               }

               Vector3 vector33 = PathSegmentNode.i(pathSegmentNode).add(0.0, 1.0, 0.0).floor();
               if (V(vector33) && this.p(pathSegmentNode, vector33)) {
                  break label55;
               }

               Vector3 vector34 = PathSegmentNode.i(pathSegmentNode).add(0.0, -1.0, 0.0).floor();
               if (V(vector34) && this.p(pathSegmentNode, vector34)) {
                  break label55;
               }
               continue;
            }
         }
      }

      if (this.g) {
         this.Y.sort(new PathSegment.f0());
         this.c = this.Y.get(0).K();
      }
   }

   private PathSegmentNode b(Vector3 vector3) {
      for (PathSegmentNode pathSegmentNode : this.Y) {
         if (PathSegmentNode.i(pathSegmentNode).getX() == vector3.getX()
            && PathSegmentNode.i(pathSegmentNode).getY() == vector3.getY()
            && PathSegmentNode.i(pathSegmentNode).getZ() == vector3.getZ()) {
            return pathSegmentNode;
         }
      }

      for (PathSegmentNode pathSegmentNode2 : this.A) {
         if (PathSegmentNode.i(pathSegmentNode2).getX() == vector3.getX()
            && PathSegmentNode.i(pathSegmentNode2).getY() == vector3.getY()
            && PathSegmentNode.i(pathSegmentNode2).getZ() == vector3.getZ()) {
            return pathSegmentNode2;
         }
      }

      return null;
   }

   private boolean p(PathSegmentNode pathSegmentNode, Vector3 vector3) {
      PathSegmentNode pathSegmentNode2 = this.b(vector3);
      double d = 0.0;
      if (pathSegmentNode != null) {
         d += PathSegmentNode.X(pathSegmentNode);
      }

      if (pathSegmentNode2 == null) {
         if (vector3.getX() == this.v.getX() && vector3.getY() == this.v.getY() && vector3.getZ() == this.v.getZ()
            || this.x != 0.0 && vector3.distanceSq(this.v) <= this.x) {
            this.c.clear();
            this.c = pathSegmentNode.K();
            this.c.add(vector3);
            return true;
         }

         ArrayList arrayList = new ArrayList<>(pathSegmentNode.K());
         arrayList.add(vector3);
         this.A.add(new PathSegmentNode(this, vector3, pathSegmentNode, arrayList, vector3.distanceSq(this.v), 0.0, d));
      } else if (PathSegmentNode.T(pathSegmentNode2) > 0.0) {
         ArrayList arrayList2 = new ArrayList<>(pathSegmentNode.K());
         arrayList2.add(vector3);
         PathSegmentNode.R(pathSegmentNode2, vector3);
         pathSegmentNode2.l(pathSegmentNode);
         pathSegmentNode2.P(arrayList2);
         PathSegmentNode.z(pathSegmentNode2, vector3.distanceSq(this.v));
         PathSegmentNode.s(pathSegmentNode2, 0.0);
         PathSegmentNode.K(pathSegmentNode2, d);
      }

      return false;
   }

   public class f0 implements Comparator<PathSegmentNode> {
      public int compare(PathSegmentNode pathSegmentNode, PathSegmentNode pathSegmentNode2) {
         return (int)(
            PathSegmentNode.z(pathSegmentNode)
               + PathSegmentNode.X(pathSegmentNode)
               - (PathSegmentNode.z(pathSegmentNode2) + PathSegmentNode.X(pathSegmentNode2))
         );
      }
   }
}
