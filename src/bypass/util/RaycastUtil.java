package bypass.util;

import bypass.ClientAccess;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.src.Reflector;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public final class RaycastUtil implements ClientAccess {
   private static final long b = 73866837190362L;

   public static Entity r(double var0, EntityFilter var2) {
      return L(var0, RotationUtil.A.r(), RotationUtil.A.z(), var2);
   }

   private static Entity L(double var0, float var2, float var3, EntityFilter var4) {
      Entity var8 = null;
      double var9 = mc.playerController.getBlockReachDistance();
      double var11 = var9;
      MovingObjectPosition var13 = H(var9, var2, var3);
      Vec3 var14 = mc.thePlayer.getPositionEyes(1.0F);
      if (mc.playerController.L()) {
         var9 = 6.0;
         var11 = 6.0;
      }

      if (var13 != null) {
         var11 = var13.hitVec.distanceTo(var14);
      }

      Vec3 var15 = mc.thePlayer.getVectorForRotation(var3, var2);
      Vec3 var16 = var14.addVector(var15.xCoord * var9, var15.yCoord * var9, var15.zCoord * var9);
      List var17 = mc.theWorld
         .getEntitiesInAABBexcluding(
            mc.thePlayer,
            mc.thePlayer.getEntityBoundingBox().addCoord(var15.xCoord * var9, var15.yCoord * var9, var15.zCoord * var9).expand(1.0, 1.0, 1.0),
            Predicates.and(EntitySelectors.NOT_SPECTATING)
         );
      double var18 = var11;

      for (Object var21$raw : var17) {
         Entity var21 = (Entity)var21$raw;
         if (var4.j(var21)) {
            float var22 = var21.getCollisionBorderSize();
            AxisAlignedBB var23 = var21.getEntityBoundingBox().expand(var22, var22, var22);
            MovingObjectPosition var24 = var23.calculateIntercept(var14, var16);
            if (var23.isVecInside(var14)) {
               if (var18 >= 0.0) {
                  var8 = var21;
                  var18 = 0.0;
               }
            } else if (var24 != null) {
               double var25 = var14.distanceTo(var24.hitVec);
               if (var25 < var18 || var18 == 0.0) {
                  boolean var27 = false;
                  if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                     var27 = Reflector.callBoolean(var21, Reflector.ForgeEntity_canRiderInteract);
                  }

                  if (var21 != var21.ridingEntity || var27) {
                     var8 = var21;
                     var18 = var25;
                  } else if (var18 == 0.0) {
                     var8 = var21;
                  }
               }
            }
         }
      }

      return var8;
   }

   private static MovingObjectPosition H(double var0, float var2, float var3) {
      Vec3 var4 = mc.thePlayer.getPositionEyes(1.0F);
      Vec3 var5 = mc.thePlayer.getVectorForRotation(var3, var2);
      Vec3 var6 = var4.addVector(var5.xCoord * var0, var5.yCoord * var0, var5.zCoord * var0);
      return mc.theWorld.rayTraceBlocks(var4, var6, false, false, true);
   }
}
