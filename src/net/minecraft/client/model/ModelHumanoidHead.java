package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelHumanoidHead extends ModelSkeletonHead {
   private final ModelRenderer head = new ModelRenderer(this, 32, 0);

   public ModelHumanoidHead() {
      super(0, 0, 64, 64);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F);
      this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   @Override
   public void render(Entity entityIn, float f, float g, float h, float f2, float f3, float scale) {
      super.render(entityIn, f, g, h, f2, f3, scale);
      this.head.render(scale);
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      this.head.rotateAngleY = this.skeletonHead.rotateAngleY;
      this.head.rotateAngleX = this.skeletonHead.rotateAngleX;
   }
}
