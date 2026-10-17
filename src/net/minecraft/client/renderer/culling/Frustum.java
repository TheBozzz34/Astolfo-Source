package net.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public class Frustum implements ICamera {
   private ClippingHelper clippingHelper;
   private double xPosition;
   private double yPosition;
   private double zPosition;

   public Frustum() {
      this(ClippingHelperImpl.getInstance());
   }

   public Frustum(ClippingHelper clippingHelper) {
      this.clippingHelper = clippingHelper;
   }

   @Override
   public void setPosition(double d, double e, double d2) {
      this.xPosition = d;
      this.yPosition = e;
      this.zPosition = d2;
   }

   public boolean isBoxInFrustum(double d, double e, double d2, double d3, double d4, double d5) {
      return this.clippingHelper
         .isBoxInFrustum(d - this.xPosition, e - this.yPosition, d2 - this.zPosition, d3 - this.xPosition, d4 - this.yPosition, d5 - this.zPosition);
   }

   @Override
   public boolean isBoundingBoxInFrustum(AxisAlignedBB axisAlignedBB) {
      return this.isBoxInFrustum(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
   }
}
