package bypass.util;

import bypass.ClientAccess;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class TeleportPath implements ClientAccess {
   private static final long b = 66698012576278L;

   public static ArrayList<Vector3> P(Vector3 var0, Vector3 var1, int var2) {
      if (R(new BlockPos(var0.toVec3()))) {
         var0 = var0.add(0.0, 1.0, 0.0);
      }

      TeleportPathSegment var6 = new TeleportPathSegment(var0, var1);
      var6.H();
      int var7 = 0;
      Vector3 var8 = null;
      Vector3 var9 = null;
      ArrayList var10 = new ArrayList();
      ArrayList var11 = var6.c();

      for (Object var13$raw : var11) {
         Vector3 var13 = (Vector3)var13$raw;
         if (var7 != 0 && var7 != var11.size() - 1) {
            boolean var14 = true;
            if (var13.distanceSq(var9) > var2) {
               var14 = false;
            } else {
               double var15 = Math.min(var9.getX(), var13.getX());
               double var17 = Math.min(var9.getY(), var13.getY());
               double var19 = Math.min(var9.getZ(), var13.getZ());
               double var21 = Math.max(var9.getX(), var13.getX());
               double var23 = Math.max(var9.getY(), var13.getY());
               double var25 = Math.max(var9.getZ(), var13.getZ());

               label54:
               for (int var27 = (int)var15; var27 <= var21; var27++) {
                  for (int var28 = (int)var17; var28 <= var23; var28++) {
                     for (int var29 = (int)var19; var29 <= var25; var29++) {
                        if (!TeleportPathSegment.D(new Vector3(var27, var28, var29))) {
                           var14 = false;
                           break label54;
                        }
                     }
                  }
               }
            }

            if (!var14) {
               var10.add(var8.add(0.5, 0.0, 0.5));
               var9 = var8;
            }
         } else {
            if (var8 != null) {
               var10.add(var8.add(0.5, 0.0, 0.5));
            }

            var10.add(var13.add(0.5, 0.0, 0.5));
            var9 = var13;
         }

         var8 = var13;
         var7++;
      }

      return var10;
   }

   public static ArrayList<Vector3> U(Vector3 var0, Vector3 var1, int var2) {
      if (R(new BlockPos(var0.toVec3()))) {
         var0 = var0.add(0.0, 1.0, 0.0);
      }

      TeleportPathSegment var6 = new TeleportPathSegment(var0, var1);
      var6.H();
      int var7 = 0;
      Vector3 var8 = null;
      Vector3 var9 = null;
      ArrayList var10 = new ArrayList();
      ArrayList var11 = var6.c();

      for (Object var13$raw : var11) {
         Vector3 var13 = (Vector3)var13$raw;
         if (var7 != 0 && var7 != var11.size() - 1) {
            boolean var14 = true;
            if (var13.distanceSq(var9) > var2) {
               var14 = false;
            } else {
               double var15 = Math.min(var9.getX(), var13.getX());
               double var17 = Math.min(var9.getY(), var13.getY());
               double var19 = Math.min(var9.getZ(), var13.getZ());
               double var21 = Math.max(var9.getX(), var13.getX());
               double var23 = Math.max(var9.getY(), var13.getY());
               double var25 = Math.max(var9.getZ(), var13.getZ());

               label54:
               for (int var27 = (int)var15; var27 <= var21; var27++) {
                  for (int var28 = (int)var17; var28 <= var23; var28++) {
                     for (int var29 = (int)var19; var29 <= var25; var29++) {
                        if (!TeleportPathSegment.D(new Vector3(var27, var28, var29))) {
                           var14 = false;
                           break label54;
                        }
                     }
                  }
               }
            }

            if (!var14) {
               var10.add(var8.add(0.5, 0.0, 0.5));
               var9 = var8;
            }
         } else {
            if (var8 != null) {
               var10.add(var8.add(0.5, 0.0, 0.5));
            }

            var10.add(var13.add(0.5, 0.0, 0.5));
            var9 = var13;
         }

         var8 = var13;
         var7++;
      }

      return var10;
   }

   private static boolean R(BlockPos var0) {
      Block var4 = mc.theWorld.getBlockState(var0).getBlock();
      return var4.getMaterial() != Material.air
         && var4.getMaterial() != Material.plants
         && var4.getMaterial() != Material.vine
         && var4 != Blocks.ladder
         && var4 != Blocks.water
         && var4 != Blocks.flowing_water
         && var4 != Blocks.wall_sign
         && var4 != Blocks.standing_sign;
   }
}
