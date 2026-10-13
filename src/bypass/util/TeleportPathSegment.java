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

   public TeleportPathSegment(Vector3 var1, Vector3 var2) {
      this.T = var1.add(0.0, 0.0, 0.0).floor();
      this.Q = var2.add(0.0, 0.0, 0.0).floor();
   }

   public static boolean D(Vector3 var0) {
      BlockPos var4 = new BlockPos(var0);
      return !E(var4) && !E(var4.add(0, 1, 0)) ? Z(var4.add(0, -1, 0)) : false;
   }

   private static boolean E(BlockPos var0) {
      Block var4 = mc.theWorld.getBlockState(var0).getBlock();
      return var4.isFullBlock()
         || var4 instanceof BlockSlab
         || var4 instanceof BlockStairs
         || var4 instanceof BlockCactus
         || var4 instanceof BlockChest
         || var4 instanceof BlockEnderChest
         || var4 instanceof BlockSkull
         || var4 instanceof BlockPane
         || var4 instanceof BlockFence
         || var4 instanceof BlockWall
         || var4 instanceof BlockGlass
         || var4 instanceof BlockPistonBase
         || var4 instanceof BlockPistonExtension
         || var4 instanceof BlockPistonMoving
         || var4 instanceof BlockStainedGlass
         || var4 instanceof BlockTrapDoor;
   }

   private static boolean Z(BlockPos var0) {
      Block var4 = mc.theWorld.getBlockState(var0).getBlock();
      return !(var4 instanceof BlockFence) && !(var4 instanceof BlockWall);
   }

   public ArrayList<Vector3> c() {
      return this.p;
   }

   public void H() {
      this.i((int)c, 4);
   }

   public void i(int var1, int var2) {
      this.p.clear();
      this.j.clear();
      ArrayList var7 = new ArrayList();
      Vector3 var8 = this.T;
      var7.add(var8);
      Vector3[] var9 = w;
      this.j.add(new PathNode(var8, null, var7, var8.distanceSq(this.Q), 0.0, 0.0));

      label51:
      for (int var10 = 0; var10 < var1; var10++) {
         ArrayList var11 = this.j;
         int var12 = var11.size();
         var11.sort(new PathNodeComparator());
         int var13 = 0;
         if (var12 == 0) {
            break;
         }

         for (int var14 = 0; var14 < var12; var14++) {
            PathNode var16 = (PathNode)var11.get(var14);
            if (++var13 <= var2) {
               var11.remove(var16);
               this.W.add(var16);
               Vector3 var17 = var16.K();
               int var18 = var9.length;

               for (int var19 = 0; var19 < var18; var19++) {
                  Vector3 var20 = var17.add(var9[var19]).floor();
                  if (D(var20) && this.w(var16, var20, 0.0)) {
                     break label51;
                  }
               }

               Vector3 var21 = var17.add(0.0, 1.0, 0.0).floor();
               Vector3 var15;
               if (D(var21) && this.w(var16, var21, 0.0) || D(var15 = var17.add(0.0, -1.0, 0.0).floor()) && this.w(var16, var15, 0.0)) {
                  break label51;
               }
               continue;
            }
         }
      }

      this.W.sort(new PathNodeComparator());
      this.p = this.W.get(0).l();
   }

   public PathNode f(Vector3 var1) {
      ArrayList var5 = this.W;
      int var6 = var5.size();

      for (int var7 = 0; var7 < var6; var7++) {
         PathNode var8 = (PathNode)var5.get(var7);
         Vector3 var9 = var8.K();
         if (var9.getX() == var1.getX() && var9.getY() == var1.getY() && var9.getZ() == var1.getZ()) {
            return var8;
         }
      }

      ArrayList var12 = this.j;
      int var13 = var12.size();

      for (int var14 = 0; var14 < var13; var14++) {
         PathNode var10 = (PathNode)var12.get(var14);
         Vector3 var11 = var10.K();
         if (var11.getX() == var1.getX() && var11.getY() == var1.getY() && var11.getZ() == var1.getZ()) {
            return var10;
         }
      }

      return null;
   }

   public boolean w(PathNode var1, Vector3 var2, double var3) {
      PathNode var8 = this.f(var2);
      double var9 = var3;
      if (var1 != null) {
         var9 += var1.d();
      }

      Vector3 var11 = this.Q;
      ArrayList var12 = var1.l();
      if (var8 == null) {
         if (var2.getX() == var11.getX() && var2.getY() == var11.getY() && var2.getZ() == var11.getZ()) {
            this.p.clear();
            this.p = var12;
            this.p.add(var2);
            return true;
         }

         var12.add(var2);
         this.j.add(new PathNode(var2, var1, var12, var2.distanceSq(var11), var3, var9));
      } else if (var8.u() > var3) {
         var12.add(var2);
         var8.p(var2);
         var8.u(var1);
         var8.t(var12);
         var8.y(var2.distanceSq(var11));
         var8.b(var3);
         var8.S(var9);
      }

      return false;
   }
}
