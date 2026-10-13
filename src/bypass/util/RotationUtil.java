package bypass.util;

import bypass.ClientAccess;
import com.sun.istack.internal.NotNull;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public final class RotationUtil implements ClientAccess {
   public static Random j = new Random();
   public static int P;
   public static AimRotation y;
   public static AimRotation A = new AimRotation(0.0F, 0.0F);
   public static boolean X = false;
   public static double G = j.nextDouble();
   public static double z = j.nextDouble();
   public static double q = j.nextDouble();
   private static final long b = 54267540324266L;

   public static AimRotation E(Vec3 var0, boolean var1) {
      Vec3 var6 = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      if (var1) {
         var6.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
      }

      double var7 = var0.xCoord - var6.xCoord;
      double var9 = var0.yCoord - var6.yCoord;
      double var11 = var0.zCoord - var6.zCoord;
      return new AimRotation(
         MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(var11, var7)) - 90.0F),
         MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(Math.atan2(var9, Math.sqrt(var7 * var7 + var11 * var11)))))
      );
   }

   public static Vec3 l(AxisAlignedBB var0) {
      return new Vec3(var0.minX + (var0.maxX - var0.minX) * 0.5, var0.minY + (var0.maxY - var0.minY) * 0.5, var0.minZ + (var0.maxZ - var0.minZ) * 0.5);
   }

   public static AimPoint Z(AxisAlignedBB var0, boolean var1, boolean var2, boolean var3, boolean var4) {
      if (var1) {
         Vec3 var20 = new Vec3(
            var0.minX + (var0.maxX - var0.minX) * (G * 0.3 + 1.0),
            var0.minY + (var0.maxY - var0.minY) * (z * 0.3 + 1.0),
            var0.minZ + (var0.maxZ - var0.minZ) * (q * 0.3 + 1.0)
         );
         return new AimPoint(var20, E(var20, var3));
      }

      Vec3 var8 = new Vec3(
         var0.minX + (var0.maxX - var0.minX) * G * 0.8, var0.minY + (var0.maxY - var0.minY) * z * 0.8, var0.minZ + (var0.maxZ - var0.minZ) * q * 0.8
      );
      AimRotation var9 = E(var8, var3);
      AimPoint var10 = null;

      for (double var11 = 0.15; var11 < 0.85; var11 += 0.1) {
         for (double var13 = 0.15; var13 < 1.0; var13 += 0.1) {
            for (double var15 = 0.15; var15 < 0.85; var15 += 0.1) {
               Vec3 var17 = new Vec3(
                  var0.minX + (var0.maxX - var0.minX) * var11, var0.minY + (var0.maxY - var0.minY) * var13, var0.minZ + (var0.maxZ - var0.minZ) * var15
               );
               AimRotation var18 = E(var17, var3);
               if (var4 || n(var17)) {
                  AimPoint var19 = new AimPoint(var17, var18);
                  if (var10 == null || (var2 ? D(var19.r(), var9) < D(var10.r(), var9) : G(var19.r()) < G(var10.r()))) {
                     var10 = var19;
                  }
               }
            }
         }
      }

      return var10;
   }

   public static double h(Entity var0) {
      AimRotation var1 = E(l(var0.getEntityBoundingBox()), true);
      return D(var1, new AimRotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
   }

   public static double G(AimRotation var0) {
      return A == null ? 0.0 : D(var0, A);
   }

   public static double D(AimRotation var0, AimRotation var1) {
      return Math.hypot(h(var0.r(), var1.r()), var0.z() - var1.z());
   }

   @NotNull
   public static AimRotation U(AimRotation var0, AimRotation var1, float var2) {
      float var5 = h(var1.r(), var0.r());
      float var6 = h(var1.z(), var0.z());
      return new AimRotation(var0.r() + (var5 > var2 ? var2 : Math.max(var5, -var2)), var0.z() + (var6 > var2 ? var2 : Math.max(var6, -var2)));
   }

   private static float h(float var0, float var1) {
      return ((var0 - var1) % 360.0F + 540.0F) % 360.0F - 180.0F;
   }

   public static Vec3 m(AimRotation var0) {
      float var1 = MathHelper.cos(-var0.r() * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var2 = MathHelper.sin(-var0.r() * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var3 = -MathHelper.cos(-var0.z() * (float) (Math.PI / 180.0));
      float var4 = MathHelper.sin(-var0.z() * (float) (Math.PI / 180.0));
      return new Vec3(var2 * var3, var4, var1 * var3);
   }

   public static boolean isFaced(Entity var0, double var1) {
      return RaycastUtil.r(var1, var1x -> var1x == var0) != null;
   }

   public static boolean n(Vec3 var0) {
      Vec3 var3 = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      return mc.theWorld.rayTraceBlocks(var3, var0) == null;
   }

   public static void O(AimRotation var0, boolean var1) {
      K(var0, 0, var1);
   }

   public static void K(AimRotation var0, int var1, boolean var2) {
      if (!Double.isNaN(var0.r()) && !Double.isNaN(var0.z()) && !(var0.z() > 90.0F) && !(var0.z() < -90.0F)) {
         if (var2) {
            var0.n(mc.gameSettings.Zm);
         }

         y = var0;
         P = var1;
      }
   }

   public static void H() {
      P = 0;
      y = null;
   }
}
