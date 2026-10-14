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

   public static AimRotation E(Vec3 vec3, boolean bl) {
      Vec3 vec32 = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      if (bl) {
         vec32.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
      }

      double d = vec3.xCoord - vec32.xCoord;
      double e = vec3.yCoord - vec32.yCoord;
      double d2 = vec3.zCoord - vec32.zCoord;
      return new AimRotation(
         MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(d2, d)) - 90.0F),
         MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(Math.atan2(e, Math.sqrt(d * d + d2 * d2)))))
      );
   }

   public static Vec3 l(AxisAlignedBB axisAlignedBB) {
      return new Vec3(
         axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * 0.5,
         axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * 0.5,
         axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * 0.5
      );
   }

   public static AimPoint Z(AxisAlignedBB axisAlignedBB, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
      if (bl) {
         Vec3 vec3 = new Vec3(
            axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * (G * 0.3 + 1.0),
            axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * (z * 0.3 + 1.0),
            axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * (q * 0.3 + 1.0)
         );
         return new AimPoint(vec3, E(vec3, bl3));
      }

      Vec3 vec32 = new Vec3(
         axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * G * 0.8,
         axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * z * 0.8,
         axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * q * 0.8
      );
      AimRotation aimRotation = E(vec32, bl3);
      AimPoint aimPoint = null;

      for (double d = 0.15; d < 0.85; d += 0.1) {
         for (double e = 0.15; e < 1.0; e += 0.1) {
            for (double d2 = 0.15; d2 < 0.85; d2 += 0.1) {
               Vec3 vec33 = new Vec3(
                  axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * d,
                  axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * e,
                  axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * d2
               );
               AimRotation aimRotation2 = E(vec33, bl3);
               if (bl4 || n(vec33)) {
                  AimPoint aimPoint2 = new AimPoint(vec33, aimRotation2);
                  if (aimPoint == null || (bl2 ? D(aimPoint2.r(), aimRotation) < D(aimPoint.r(), aimRotation) : G(aimPoint2.r()) < G(aimPoint.r()))) {
                     aimPoint = aimPoint2;
                  }
               }
            }
         }
      }

      return aimPoint;
   }

   public static double h(Entity entity) {
      AimRotation aimRotation = E(l(entity.getEntityBoundingBox()), true);
      return D(aimRotation, new AimRotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
   }

   public static double G(AimRotation aimRotation) {
      return A == null ? 0.0 : D(aimRotation, A);
   }

   public static double D(AimRotation aimRotation, AimRotation aimRotation2) {
      return Math.hypot(h(aimRotation.r(), aimRotation2.r()), aimRotation.z() - aimRotation2.z());
   }

   @NotNull
   public static AimRotation U(AimRotation aimRotation, AimRotation aimRotation2, float f) {
      float g = h(aimRotation2.r(), aimRotation.r());
      float f2 = h(aimRotation2.z(), aimRotation.z());
      return new AimRotation(aimRotation.r() + (g > f ? f : Math.max(g, -f)), aimRotation.z() + (f2 > f ? f : Math.max(f2, -f)));
   }

   private static float h(float f, float g) {
      return ((f - g) % 360.0F + 540.0F) % 360.0F - 180.0F;
   }

   public static Vec3 m(AimRotation aimRotation) {
      float f = MathHelper.cos(-aimRotation.r() * (float) (Math.PI / 180.0) - (float) Math.PI);
      float g = MathHelper.sin(-aimRotation.r() * (float) (Math.PI / 180.0) - (float) Math.PI);
      float h = -MathHelper.cos(-aimRotation.z() * (float) (Math.PI / 180.0));
      float f2 = MathHelper.sin(-aimRotation.z() * (float) (Math.PI / 180.0));
      return new Vec3(g * h, f2, f * h);
   }

   public static boolean isFaced(Entity entity, double d) {
      return RaycastUtil.r(d, entity2 -> entity2 == entity) != null;
   }

   public static boolean n(Vec3 vec3) {
      Vec3 vec32 = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      return mc.theWorld.rayTraceBlocks(vec32, vec3) == null;
   }

   public static void O(AimRotation aimRotation, boolean bl) {
      K(aimRotation, 0, bl);
   }

   public static void K(AimRotation aimRotation, int i, boolean bl) {
      if (!Double.isNaN(aimRotation.r()) && !Double.isNaN(aimRotation.z()) && !(aimRotation.z() > 90.0F) && !(aimRotation.z() < -90.0F)) {
         if (bl) {
            aimRotation.n(mc.gameSettings.Zm);
         }

         y = aimRotation;
         P = i;
      }
   }

   public static void H() {
      P = 0;
      y = null;
   }
}
