package bypass.util;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class PathFinder {
   private static final long a = 16919220023187L;

   public static ArrayList<Vector3> i(Vector3 var0, Vector3 var1, double var2) {
      if (!k(new BlockPos(var0.toVec3()))) {
         var0 = var0.add(0.0, 1.0, 0.0);
      }

      PathSegment var7 = new PathSegment(var0, var1);
      var7.Q();
      int var8 = 0;
      Vector3 var9 = null;
      Vector3 var10 = null;
      ArrayList var11 = new ArrayList();
      ArrayList var12 = var7.W();

      for (Object var14$raw : var12) {
         Vector3 var14 = (Vector3)var14$raw;
         if (var8 != 0 && var8 != var12.size() - 1) {
            boolean var15 = true;
            if (var14.distanceSq(var10) > var2 * var2) {
               var15 = false;
            } else {
               double var16 = Math.min(var10.getX(), var14.getX());
               double var18 = Math.min(var10.getY(), var14.getY());
               double var20 = Math.min(var10.getZ(), var14.getZ());
               double var22 = Math.max(var10.getX(), var14.getX());
               double var24 = Math.max(var10.getY(), var14.getY());
               double var26 = Math.max(var10.getZ(), var14.getZ());

               for (int var28 = (int)var16; var28 <= var22; var28++) {
                  for (int var29 = (int)var18; var29 <= var24; var29++) {
                     for (int var30 = (int)var20; var30 <= var26; var30++) {
                        if (!PathSegment.H(var28, var29, var30)) {
                           var15 = false;
                           break;
                        }
                     }
                  }
               }
            }

            if (!var15) {
               var11.add(var9.add(0.5, 0.0, 0.5));
               var10 = var9;
            }
         } else {
            if (var9 != null) {
               var11.add(var9.add(0.5, 0.0, 0.5));
            }

            var11.add(var14.add(0.5, 0.0, 0.5));
            var10 = var14;
         }

         var9 = var14;
         var8++;
      }

      return var11;
   }

   private static boolean k(BlockPos var0) {
      Block var4 = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(var0.getX(), var0.getY(), var0.getZ())).getBlock();
      return var4.getMaterial() == Material.air
         || var4.getMaterial() == Material.plants
         || var4.getMaterial() == Material.vine
         || var4 == Blocks.ladder
         || var4 == Blocks.water
         || var4 == Blocks.flowing_water
         || var4 == Blocks.wall_sign
         || var4 == Blocks.standing_sign;
   }
}
