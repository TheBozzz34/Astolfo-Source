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
   public static Entity raycastEntity(double d, EntityFilter entityFilter) {
      return raycastEntityAt(d, RotationUtil.serverRotation.getYaw(), RotationUtil.serverRotation.getPitch(), entityFilter);
   }

   private static Entity raycastEntityAt(double d, float f, float g, EntityFilter entityFilter) {
      Entity entity = null;
      double e = mc.playerController.getBlockReachDistance();
      double d2 = e;
      MovingObjectPosition movingObjectPosition = rayTraceBlocks(e, f, g);
      Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0F);
      if (mc.playerController.L()) {
         e = 6.0;
         d2 = 6.0;
      }

      if (movingObjectPosition != null) {
         d2 = movingObjectPosition.hitVec.distanceTo(vec3);
      }

      Vec3 vec32 = mc.thePlayer.getVectorForRotation(g, f);
      Vec3 vec33 = vec3.addVector(vec32.xCoord * e, vec32.yCoord * e, vec32.zCoord * e);
      List list = mc.theWorld
         .getEntitiesInAABBexcluding(
            mc.thePlayer,
            mc.thePlayer.getEntityBoundingBox().addCoord(vec32.xCoord * e, vec32.yCoord * e, vec32.zCoord * e).expand(1.0, 1.0, 1.0),
            Predicates.and(EntitySelectors.NOT_SPECTATING)
         );
      double d3 = d2;

      for (Object object : list) {
         Entity entity2 = (Entity)object;
         if (entityFilter.matches(entity2)) {
            float h = entity2.getCollisionBorderSize();
            AxisAlignedBB axisAlignedBB = entity2.getEntityBoundingBox().expand(h, h, h);
            MovingObjectPosition movingObjectPosition2 = axisAlignedBB.calculateIntercept(vec3, vec33);
            if (axisAlignedBB.isVecInside(vec3)) {
               if (d3 >= 0.0) {
                  entity = entity2;
                  d3 = 0.0;
               }
            } else if (movingObjectPosition2 != null) {
               double d4 = vec3.distanceTo(movingObjectPosition2.hitVec);
               if (d4 < d3 || d3 == 0.0) {
                  boolean bl = false;
                  if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                     bl = Reflector.callBoolean(entity2, Reflector.ForgeEntity_canRiderInteract);
                  }

                  if (entity2 != entity2.ridingEntity || bl) {
                     entity = entity2;
                     d3 = d4;
                  } else if (d3 == 0.0) {
                     entity = entity2;
                  }
               }
            }
         }
      }

      return entity;
   }

   private static MovingObjectPosition rayTraceBlocks(double d, float f, float g) {
      Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0F);
      Vec3 vec32 = mc.thePlayer.getVectorForRotation(g, f);
      Vec3 vec33 = vec3.addVector(vec32.xCoord * d, vec32.yCoord * d, vec32.zCoord * d);
      return mc.theWorld.rayTraceBlocks(vec3, vec33, false, false, true);
   }
}
