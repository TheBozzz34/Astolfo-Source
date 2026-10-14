package bypass.util;

import bypass.ClientAccess;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class TeleportPath implements ClientAccess {
   public static ArrayList<Vector3> P(Vector3 vector3, Vector3 vector32, int i) {
      if (R(new BlockPos(vector3.toVec3()))) {
         vector3 = vector3.add(0.0, 1.0, 0.0);
      }

      TeleportPathSegment teleportPathSegment = new TeleportPathSegment(vector3, vector32);
      teleportPathSegment.H();
      int j = 0;
      Vector3 vector33 = null;
      Vector3 vector34 = null;
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = teleportPathSegment.c();

      for (Object object : arrayList2) {
         Vector3 vector35 = (Vector3)object;
         if (j != 0 && j != arrayList2.size() - 1) {
            boolean bl = true;
            if (vector35.distanceSq(vector34) > i) {
               bl = false;
            } else {
               double d = Math.min(vector34.getX(), vector35.getX());
               double e = Math.min(vector34.getY(), vector35.getY());
               double d2 = Math.min(vector34.getZ(), vector35.getZ());
               double d3 = Math.max(vector34.getX(), vector35.getX());
               double d4 = Math.max(vector34.getY(), vector35.getY());
               double d5 = Math.max(vector34.getZ(), vector35.getZ());

               label54:
               for (int k = (int)d; k <= d3; k++) {
                  for (int l = (int)e; l <= d4; l++) {
                     for (int m = (int)d2; m <= d5; m++) {
                        if (!TeleportPathSegment.D(new Vector3(k, l, m))) {
                           bl = false;
                           break label54;
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

   public static ArrayList<Vector3> U(Vector3 vector3, Vector3 vector32, int i) {
      if (R(new BlockPos(vector3.toVec3()))) {
         vector3 = vector3.add(0.0, 1.0, 0.0);
      }

      TeleportPathSegment teleportPathSegment = new TeleportPathSegment(vector3, vector32);
      teleportPathSegment.H();
      int j = 0;
      Vector3 vector33 = null;
      Vector3 vector34 = null;
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = teleportPathSegment.c();

      for (Object object : arrayList2) {
         Vector3 vector35 = (Vector3)object;
         if (j != 0 && j != arrayList2.size() - 1) {
            boolean bl = true;
            if (vector35.distanceSq(vector34) > i) {
               bl = false;
            } else {
               double d = Math.min(vector34.getX(), vector35.getX());
               double e = Math.min(vector34.getY(), vector35.getY());
               double d2 = Math.min(vector34.getZ(), vector35.getZ());
               double d3 = Math.max(vector34.getX(), vector35.getX());
               double d4 = Math.max(vector34.getY(), vector35.getY());
               double d5 = Math.max(vector34.getZ(), vector35.getZ());

               label54:
               for (int k = (int)d; k <= d3; k++) {
                  for (int l = (int)e; l <= d4; l++) {
                     for (int m = (int)d2; m <= d5; m++) {
                        if (!TeleportPathSegment.D(new Vector3(k, l, m))) {
                           bl = false;
                           break label54;
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

   private static boolean R(BlockPos blockPos) {
      Block block = mc.theWorld.getBlockState(blockPos).getBlock();
      return block.getMaterial() != Material.air
         && block.getMaterial() != Material.plants
         && block.getMaterial() != Material.vine
         && block != Blocks.ladder
         && block != Blocks.water
         && block != Blocks.flowing_water
         && block != Blocks.wall_sign
         && block != Blocks.standing_sign;
   }
}
