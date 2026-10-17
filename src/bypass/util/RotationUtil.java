package bypass.util;

import bypass.ClientAccess;
import com.sun.istack.internal.NotNull;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public final class RotationUtil implements ClientAccess {
   public static Random random = new Random();
   public static int keepRotationTicks;
   public static AimRotation targetRotation;
   public static AimRotation serverRotation = new AimRotation(0.0F, 0.0F);
   public static boolean rotationFlag = false;
   public static double randomOffsetX = random.nextDouble();
   public static double randomOffsetY = random.nextDouble();
   public static double randomOffsetZ = random.nextDouble();

   public static AimRotation getRotationTo(Vec3 vec3, boolean bl) {
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

   public static Vec3 getBoxCenter(AxisAlignedBB axisAlignedBB) {
      return new Vec3(
         axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * 0.5,
         axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * 0.5,
         axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * 0.5
      );
   }

   public static AimPoint findBestAimPoint(AxisAlignedBB axisAlignedBB, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
      if (bl) {
         Vec3 vec3 = new Vec3(
            axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * (randomOffsetX * 0.3 + 1.0),
            axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * (randomOffsetY * 0.3 + 1.0),
            axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * (randomOffsetZ * 0.3 + 1.0)
         );
         return new AimPoint(vec3, getRotationTo(vec3, bl3));
      }

      Vec3 vec32 = new Vec3(
         axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * randomOffsetX * 0.8,
         axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * randomOffsetY * 0.8,
         axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * randomOffsetZ * 0.8
      );
      AimRotation aimRotation = getRotationTo(vec32, bl3);
      AimPoint aimPoint = null;

      for (double d = 0.15; d < 0.85; d += 0.1) {
         for (double e = 0.15; e < 1.0; e += 0.1) {
            for (double d2 = 0.15; d2 < 0.85; d2 += 0.1) {
               Vec3 vec33 = new Vec3(
                  axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * d,
                  axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * e,
                  axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * d2
               );
               AimRotation aimRotation2 = getRotationTo(vec33, bl3);
               if (bl4 || canSeePoint(vec33)) {
                  AimPoint aimPoint2 = new AimPoint(vec33, aimRotation2);
                  if (aimPoint == null
                     || (
                        bl2
                           ? getRotationDifference(aimPoint2.getRotation(), aimRotation) < getRotationDifference(aimPoint.getRotation(), aimRotation)
                           : getDifferenceToServerRotation(aimPoint2.getRotation()) < getDifferenceToServerRotation(aimPoint.getRotation())
                     )) {
                     aimPoint = aimPoint2;
                  }
               }
            }
         }
      }

      return aimPoint;
   }

   public static double getRotationDifferenceToEntity(Entity entity) {
      AimRotation aimRotation = getRotationTo(getBoxCenter(entity.getEntityBoundingBox()), true);
      return getRotationDifference(aimRotation, new AimRotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
   }

   public static double getDifferenceToServerRotation(AimRotation aimRotation) {
      return serverRotation == null ? 0.0 : getRotationDifference(aimRotation, serverRotation);
   }

   public static double getRotationDifference(AimRotation aimRotation, AimRotation aimRotation2) {
      return Math.hypot(getAngleDifference(aimRotation.getYaw(), aimRotation2.getYaw()), aimRotation.getPitch() - aimRotation2.getPitch());
   }

   @NotNull
   public static AimRotation limitRotationStep(AimRotation aimRotation, AimRotation aimRotation2, float f) {
      float g = getAngleDifference(aimRotation2.getYaw(), aimRotation.getYaw());
      float h = getAngleDifference(aimRotation2.getPitch(), aimRotation.getPitch());
      return new AimRotation(aimRotation.getYaw() + (g > f ? f : Math.max(g, -f)), aimRotation.getPitch() + (h > f ? f : Math.max(h, -f)));
   }

   private static float getAngleDifference(float f, float g) {
      return ((f - g) % 360.0F + 540.0F) % 360.0F - 180.0F;
   }

   public static Vec3 getLookVector(AimRotation aimRotation) {
      float f = MathHelper.cos(-aimRotation.getYaw() * (float) (Math.PI / 180.0) - (float) Math.PI);
      float g = MathHelper.sin(-aimRotation.getYaw() * (float) (Math.PI / 180.0) - (float) Math.PI);
      float h = -MathHelper.cos(-aimRotation.getPitch() * (float) (Math.PI / 180.0));
      float f2 = MathHelper.sin(-aimRotation.getPitch() * (float) (Math.PI / 180.0));
      return new Vec3(g * h, f2, f * h);
   }

   public static boolean isFaced(Entity entity, double d) {
      return RaycastUtil.raycastEntity(d, entity2 -> entity2 == entity) != null;
   }

   public static boolean canSeePoint(Vec3 vec3) {
      Vec3 vec32 = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      return mc.theWorld.rayTraceBlocks(vec32, vec3) == null;
   }

   public static void setTargetRotation(AimRotation aimRotation, boolean bl) {
      setTargetRotationFor(aimRotation, 0, bl);
   }

   public static void setTargetRotationFor(AimRotation aimRotation, int i, boolean bl) {
      if (!Double.isNaN(aimRotation.getYaw())
         && !Double.isNaN(aimRotation.getPitch())
         && !(aimRotation.getPitch() > 90.0F)
         && !(aimRotation.getPitch() < -90.0F)) {
         if (bl) {
            aimRotation.applyGcdFix(mc.gameSettings.mouseSensitivity);
         }

         targetRotation = aimRotation;
         keepRotationTicks = i;
      }
   }

   public static void resetTargetRotation() {
      keepRotationTicks = 0;
      targetRotation = null;
   }
}
