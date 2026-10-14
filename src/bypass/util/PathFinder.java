package bypass.util;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class PathFinder {
   public static ArrayList<Vector3> i(Vector3 vector3, Vector3 vector32, double d) {
      if (!k(new BlockPos(vector3.toVec3()))) {
         vector3 = vector3.add(0.0, 1.0, 0.0);
      }

      PathSegment pathSegment = new PathSegment(vector3, vector32);
      pathSegment.Q();
      int j = 0;
      Vector3 vector33 = null;
      Vector3 vector34 = null;
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = pathSegment.W();

      for (Object object : arrayList2) {
         Vector3 vector35 = (Vector3)object;
         if (j != 0 && j != arrayList2.size() - 1) {
            boolean bl = true;
            if (vector35.distanceSq(vector34) > d * d) {
               bl = false;
            } else {
               double e = Math.min(vector34.getX(), vector35.getX());
               double d2 = Math.min(vector34.getY(), vector35.getY());
               double d3 = Math.min(vector34.getZ(), vector35.getZ());
               double d4 = Math.max(vector34.getX(), vector35.getX());
               double d5 = Math.max(vector34.getY(), vector35.getY());
               double d6 = Math.max(vector34.getZ(), vector35.getZ());

               for (int l = (int)e; l <= d4; l++) {
                  for (int m = (int)d2; m <= d5; m++) {
                     for (int n = (int)d3; n <= d6; n++) {
                        if (!PathSegment.H(l, m, n)) {
                           bl = false;
                           break;
                        }
                     }
                  }
               }
            }

            if (!bl) {
               arrayList.add(vector33.add(0.5, 0.0, 0.5));
               vector34 = vector33;
            }
         } else {
            if (vector33 != null) {
               arrayList.add(vector33.add(0.5, 0.0, 0.5));
            }

            arrayList.add(vector35.add(0.5, 0.0, 0.5));
            vector34 = vector35;
         }

         vector33 = vector35;
         j++;
      }

      return arrayList;
   }

   private static boolean k(BlockPos blockPos) {
      Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ())).getBlock();
      return block.getMaterial() == Material.air
         || block.getMaterial() == Material.plants
         || block.getMaterial() == Material.vine
         || block == Blocks.ladder
         || block == Blocks.water
         || block == Blocks.flowing_water
         || block == Blocks.wall_sign
         || block == Blocks.standing_sign;
   }
}
