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
   private static final Vector3[] neighborOffsets = new Vector3[]{
      new Vector3(1.0, 0.0, 0.0), new Vector3(-1.0, 0.0, 0.0), new Vector3(0.0, 0.0, 1.0), new Vector3(0.0, 0.0, -1.0)
   };
   private Vector3 start;
   private Vector3 goal;
   private ArrayList<Vector3> path = new ArrayList<>();
   private ArrayList<PathSegmentNode> closedNodes = new ArrayList<>();
   private ArrayList<PathSegmentNode> openNodes = new ArrayList<>();
   private double goalThresholdSq = 9.0;
   private boolean useBestOnFailure = true;

   public PathSegment(Vector3 vector3, Vector3 vector32) {
      this.start = vector3.add(0.0, 0.0, 0.0).floor();
      this.goal = vector32.add(0.0, 0.0, 0.0).floor();
   }

   private static boolean isWalkable(Vector3 vector3) {
      return isWalkableAt((int)vector3.getX(), (int)vector3.getY(), (int)vector3.getZ());
   }

   public static boolean isWalkableAt(int i, int j, int k) {
      BlockPos blockPos = new BlockPos(i, j, k);
      BlockPos blockPos2 = new BlockPos(i, j + 1, k);
      BlockPos blockPos3 = new BlockPos(i, j - 1, k);
      return !isSolidBlock(blockPos) && !isSolidBlock(blockPos2) && canStandOn(blockPos3);
   }

   private static boolean isSolidBlock(BlockPos blockPos) {
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

   private static boolean canStandOn(BlockPos blockPos) {
      Minecraft minecraft = Minecraft.getMinecraft();
      return !(minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockFence)
         && !(minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockWall);
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
      arrayList.add(this.start);
      this.openNodes.add(new PathSegmentNode(this, this.start, null, arrayList, this.start.distanceSq(this.goal), 0.0, 0.0));

      label55:
      for (int k = 0; k < i; k++) {
         this.openNodes.sort(new PathSegment.f0());
         int l = 0;
         if (this.openNodes.size() == 0) {
            break;
         }

         for (PathSegmentNode pathSegmentNode : new ArrayList<>(this.openNodes)) {
            if (++l <= j) {
               this.openNodes.remove(pathSegmentNode);
               this.closedNodes.add(pathSegmentNode);

               for (Vector3 vector3 : neighborOffsets) {
                  Vector3 vector32 = PathSegmentNode.getPositionOf(pathSegmentNode).add(vector3).floor();
                  if (isWalkable(vector32) && this.tryAddNode(pathSegmentNode, vector32)) {
                     break label55;
                  }
               }

               Vector3 vector33 = PathSegmentNode.getPositionOf(pathSegmentNode).add(0.0, 1.0, 0.0).floor();
               if (isWalkable(vector33) && this.tryAddNode(pathSegmentNode, vector33)) {
                  break label55;
               }

               Vector3 vector34 = PathSegmentNode.getPositionOf(pathSegmentNode).add(0.0, -1.0, 0.0).floor();
               if (isWalkable(vector34) && this.tryAddNode(pathSegmentNode, vector34)) {
                  break label55;
               }
               continue;
            }
         }
      }

      if (this.useBestOnFailure) {
         this.closedNodes.sort(new PathSegment.f0());
         this.path = this.closedNodes.get(0).getPath();
      }
   }

   private PathSegmentNode findNode(Vector3 vector3) {
      for (PathSegmentNode pathSegmentNode : this.closedNodes) {
         if (PathSegmentNode.getPositionOf(pathSegmentNode).getX() == vector3.getX()
            && PathSegmentNode.getPositionOf(pathSegmentNode).getY() == vector3.getY()
            && PathSegmentNode.getPositionOf(pathSegmentNode).getZ() == vector3.getZ()) {
            return pathSegmentNode;
         }
      }

      for (PathSegmentNode pathSegmentNode2 : this.openNodes) {
         if (PathSegmentNode.getPositionOf(pathSegmentNode2).getX() == vector3.getX()
            && PathSegmentNode.getPositionOf(pathSegmentNode2).getY() == vector3.getY()
            && PathSegmentNode.getPositionOf(pathSegmentNode2).getZ() == vector3.getZ()) {
            return pathSegmentNode2;
         }
      }

      return null;
   }

   private boolean tryAddNode(PathSegmentNode pathSegmentNode, Vector3 vector3) {
      PathSegmentNode pathSegmentNode2 = this.findNode(vector3);
      double d = 0.0;
      if (pathSegmentNode != null) {
         d += PathSegmentNode.getTotalCostOf(pathSegmentNode);
      }

      if (pathSegmentNode2 == null) {
         if (vector3.getX() == this.goal.getX() && vector3.getY() == this.goal.getY() && vector3.getZ() == this.goal.getZ()
            || this.goalThresholdSq != 0.0 && vector3.distanceSq(this.goal) <= this.goalThresholdSq) {
            this.path.clear();
            this.path = pathSegmentNode.getPath();
            this.path.add(vector3);
            return true;
         }

         ArrayList arrayList = new ArrayList<>(pathSegmentNode.getPath());
         arrayList.add(vector3);
         this.openNodes.add(new PathSegmentNode(this, vector3, pathSegmentNode, arrayList, vector3.distanceSq(this.goal), 0.0, d));
      } else if (PathSegmentNode.getStepCostOf(pathSegmentNode2) > 0.0) {
         ArrayList arrayList2 = new ArrayList<>(pathSegmentNode.getPath());
         arrayList2.add(vector3);
         PathSegmentNode.setPositionOf(pathSegmentNode2, vector3);
         pathSegmentNode2.setParent(pathSegmentNode);
         pathSegmentNode2.setPath(arrayList2);
         PathSegmentNode.setHeuristicOf(pathSegmentNode2, vector3.distanceSq(this.goal));
         PathSegmentNode.setStepCostOf(pathSegmentNode2, 0.0);
         PathSegmentNode.setTotalCostOf(pathSegmentNode2, d);
      }

      return false;
   }

   public class f0 implements Comparator<PathSegmentNode> {
      public int compare(PathSegmentNode pathSegmentNode, PathSegmentNode pathSegmentNode2) {
         return (int)(
            PathSegmentNode.getHeuristicOf(pathSegmentNode)
               + PathSegmentNode.getTotalCostOf(pathSegmentNode)
               - (PathSegmentNode.getHeuristicOf(pathSegmentNode2) + PathSegmentNode.getTotalCostOf(pathSegmentNode2))
         );
      }
   }
}
