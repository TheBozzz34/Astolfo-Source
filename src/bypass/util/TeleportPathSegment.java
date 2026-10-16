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
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private static final Vector3[] neighborOffsets = new Vector3[]{
      new Vector3(1.0, 0.0, 0.0), new Vector3(-1.0, 0.0, 0.0), new Vector3(0.0, 0.0, 1.0), new Vector3(0.0, 0.0, -1.0)
   };
   private final Vector3 start;
   private final Vector3 goal;
   private final ArrayList<PathNode> closedNodes = new ArrayList<>();
   private final ArrayList<PathNode> openNodes = new ArrayList<>();
   private ArrayList<Vector3> path = new ArrayList<>();

   public TeleportPathSegment(Vector3 vector3, Vector3 vector32) {
      this.start = vector3.add(0.0, 0.0, 0.0).floor();
      this.goal = vector32.add(0.0, 0.0, 0.0).floor();
   }

   public static boolean isWalkable(Vector3 vector3) {
      BlockPos blockPos = new BlockPos(vector3);
      return !isSolidBlock(blockPos) && !isSolidBlock(blockPos.add(0, 1, 0)) ? canStandOn(blockPos.add(0, -1, 0)) : false;
   }

   private static boolean isSolidBlock(BlockPos blockPos) {
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

   private static boolean canStandOn(BlockPos blockPos) {
      Block block = mc.theWorld.getBlockState(blockPos).getBlock();
      return !(block instanceof BlockFence) && !(block instanceof BlockWall);
   }

   public ArrayList<Vector3> getPath() {
      return this.path;
   }

   public void findPathDefault() {
      this.findPathWithLimits(1000, 4);
   }

   public void findPathWithLimits(int i, int j) {
      this.path.clear();
      this.openNodes.clear();
      ArrayList arrayList = new ArrayList();
      Vector3 vector3 = this.start;
      arrayList.add(vector3);
      Vector3[] vector3s = neighborOffsets;
      this.openNodes.add(new PathNode(vector3, null, arrayList, vector3.distanceSq(this.goal), 0.0, 0.0));

      label51:
      for (int k = 0; k < i; k++) {
         ArrayList arrayList2 = this.openNodes;
         int l = arrayList2.size();
         arrayList2.sort(new PathNodeComparator());
         int m = 0;
         if (l == 0) {
            break;
         }

         for (int n = 0; n < l; n++) {
            PathNode pathNode = (PathNode)arrayList2.get(n);
            if (++m <= j) {
               arrayList2.remove(pathNode);
               this.closedNodes.add(pathNode);
               Vector3 vector32 = pathNode.getPosition();
               int i2 = vector3s.length;

               for (int i3 = 0; i3 < i2; i3++) {
                  Vector3 vector33 = vector32.add(vector3s[i3]).floor();
                  if (isWalkable(vector33) && this.tryAddNode(pathNode, vector33, 0.0)) {
                     break label51;
                  }
               }

               Vector3 vector34 = vector32.add(0.0, 1.0, 0.0).floor();
               Vector3 vector35;
               if (isWalkable(vector34) && this.tryAddNode(pathNode, vector34, 0.0)
                  || isWalkable(vector35 = vector32.add(0.0, -1.0, 0.0).floor()) && this.tryAddNode(pathNode, vector35, 0.0)) {
                  break label51;
               }
               continue;
            }
         }
      }

      this.closedNodes.sort(new PathNodeComparator());
      this.path = this.closedNodes.get(0).getPath();
   }

   public PathNode findNode(Vector3 vector3) {
      ArrayList arrayList = this.closedNodes;
      int i = arrayList.size();

      for (int j = 0; j < i; j++) {
         PathNode pathNode = (PathNode)arrayList.get(j);
         Vector3 vector32 = pathNode.getPosition();
         if (vector32.getX() == vector3.getX() && vector32.getY() == vector3.getY() && vector32.getZ() == vector3.getZ()) {
            return pathNode;
         }
      }

      ArrayList arrayList2 = this.openNodes;
      int k = arrayList2.size();

      for (int l = 0; l < k; l++) {
         PathNode pathNode2 = (PathNode)arrayList2.get(l);
         Vector3 vector33 = pathNode2.getPosition();
         if (vector33.getX() == vector3.getX() && vector33.getY() == vector3.getY() && vector33.getZ() == vector3.getZ()) {
            return pathNode2;
         }
      }

      return null;
   }

   public boolean tryAddNode(PathNode pathNode, Vector3 vector3, double d) {
      PathNode pathNode2 = this.findNode(vector3);
      double e = d;
      if (pathNode != null) {
         e += pathNode.getTotalCost();
      }

      Vector3 vector32 = this.goal;
      ArrayList arrayList = pathNode.getPath();
      if (pathNode2 == null) {
         if (vector3.getX() == vector32.getX() && vector3.getY() == vector32.getY() && vector3.getZ() == vector32.getZ()) {
            this.path.clear();
            this.path = arrayList;
            this.path.add(vector3);
            return true;
         }

         arrayList.add(vector3);
         this.openNodes.add(new PathNode(vector3, pathNode, arrayList, vector3.distanceSq(vector32), d, e));
      } else if (pathNode2.getStepCost() > d) {
         arrayList.add(vector3);
         pathNode2.setPosition(vector3);
         pathNode2.setParent(pathNode);
         pathNode2.setPath(arrayList);
         pathNode2.setDistanceToGoalSq(vector3.distanceSq(vector32));
         pathNode2.setStepCost(d);
         pathNode2.setTotalCost(e);
      }

      return false;
   }
}
