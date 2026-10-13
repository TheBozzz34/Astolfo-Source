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

   public PathSegment(Vector3 var1, Vector3 var2) {
      this.E = var1.add(0.0, 0.0, 0.0).floor();
      this.v = var2.add(0.0, 0.0, 0.0).floor();
   }

   private static boolean V(Vector3 var0) {
      return H((int)var0.getX(), (int)var0.getY(), (int)var0.getZ());
   }

   public static boolean H(int var0, int var1, int var2) {
      BlockPos var6 = new BlockPos(var0, var1, var2);
      BlockPos var7 = new BlockPos(var0, var1 + 1, var2);
      BlockPos var8 = new BlockPos(var0, var1 - 1, var2);
      return !I(var6) && !I(var7) && R(var8);
   }

   private static boolean I(BlockPos var0) {
      Minecraft var4 = Minecraft.getMinecraft();
      return var4.theWorld.getBlockState(var0).getBlock().isFullBlock()
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockSlab
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockStairs
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockCactus
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockChest
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockEnderChest
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockSkull
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockPane
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockFence
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockWall
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockGlass
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockPistonBase
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockPistonExtension
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockPistonMoving
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockStainedGlass
         || var4.theWorld.getBlockState(var0).getBlock() instanceof BlockTrapDoor;
   }

   private static boolean R(BlockPos var0) {
      Minecraft var4 = Minecraft.getMinecraft();
      return !(var4.theWorld.getBlockState(var0).getBlock() instanceof BlockFence) && !(var4.theWorld.getBlockState(var0).getBlock() instanceof BlockWall);
   }

   public ArrayList<Vector3> W() {
      return this.c;
   }

   public void Q() {
      this.J((int)d, 4);
   }

   public void J(int var1, int var2) {
      this.c.clear();
      this.A.clear();
      ArrayList var7 = new ArrayList();
      var7.add(this.E);
      this.A.add(new PathSegmentNode(this, this.E, null, var7, this.E.distanceSq(this.v), 0.0, 0.0));

      label55:
      for (int var8 = 0; var8 < var1; var8++) {
         this.A.sort(new PathSegment.f0());
         int var9 = 0;
         if (this.A.size() == 0) {
            break;
         }

         for (PathSegmentNode var11 : new ArrayList<>(this.A)) {
            if (++var9 <= var2) {
               this.A.remove(var11);
               this.Y.add(var11);

               for (Vector3 var15 : a) {
                  Vector3 var16 = PathSegmentNode.i(var11).add(var15).floor();
                  if (V(var16) && this.p(var11, var16)) {
                     break label55;
                  }
               }

               Vector3 var17 = PathSegmentNode.i(var11).add(0.0, 1.0, 0.0).floor();
               if (V(var17) && this.p(var11, var17)) {
                  break label55;
               }

               Vector3 var18 = PathSegmentNode.i(var11).add(0.0, -1.0, 0.0).floor();
               if (V(var18) && this.p(var11, var18)) {
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

   private PathSegmentNode b(Vector3 var1) {
      for (PathSegmentNode var6 : this.Y) {
         if (PathSegmentNode.i(var6).getX() == var1.getX() && PathSegmentNode.i(var6).getY() == var1.getY() && PathSegmentNode.i(var6).getZ() == var1.getZ()) {
            return var6;
         }
      }

      for (PathSegmentNode var8 : this.A) {
         if (PathSegmentNode.i(var8).getX() == var1.getX() && PathSegmentNode.i(var8).getY() == var1.getY() && PathSegmentNode.i(var8).getZ() == var1.getZ()) {
            return var8;
         }
      }

      return null;
   }

   private boolean p(PathSegmentNode var1, Vector3 var2) {
      PathSegmentNode var6 = this.b(var2);
      double var7 = 0.0;
      if (var1 != null) {
         var7 += PathSegmentNode.X(var1);
      }

      if (var6 == null) {
         if (var2.getX() == this.v.getX() && var2.getY() == this.v.getY() && var2.getZ() == this.v.getZ() || this.x != 0.0 && var2.distanceSq(this.v) <= this.x
            )
          {
            this.c.clear();
            this.c = var1.K();
            this.c.add(var2);
            return true;
         }

         ArrayList var9 = new ArrayList<>(var1.K());
         var9.add(var2);
         this.A.add(new PathSegmentNode(this, var2, var1, var9, var2.distanceSq(this.v), 0.0, var7));
      } else if (PathSegmentNode.T(var6) > 0.0) {
         ArrayList var10 = new ArrayList<>(var1.K());
         var10.add(var2);
         PathSegmentNode.R(var6, var2);
         var6.l(var1);
         var6.P(var10);
         PathSegmentNode.z(var6, var2.distanceSq(this.v));
         PathSegmentNode.s(var6, 0.0);
         PathSegmentNode.K(var6, var7);
      }

      return false;
   }

   public class f0 implements Comparator<PathSegmentNode> {
      public int compare(PathSegmentNode var1, PathSegmentNode var2) {
         return (int)(PathSegmentNode.z(var1) + PathSegmentNode.X(var1) - (PathSegmentNode.z(var2) + PathSegmentNode.X(var2)));
      }
   }
}
