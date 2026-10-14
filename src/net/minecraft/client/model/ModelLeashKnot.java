package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelLeashKnot extends ModelBase {
   public ModelRenderer field_110723_a;

   public ModelLeashKnot() {
      this(0, 0, 32, 32);
   }

   public ModelLeashKnot(int i, int j, int k, int l) {
      this.textureWidth = k;
      this.textureHeight = l;
      this.field_110723_a = new ModelRenderer(this, i, j);
      this.field_110723_a.addBox(-3.0F, -6.0F, -3.0F, 6, 8, 6, 0.0F);
      this.field_110723_a.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   @Override
   public void render(Entity entityIn, float f, float g, float h, float f2, float f3, float scale) {
      this.setRotationAngles(f, g, h, f2, f3, scale, entityIn);
      this.field_110723_a.render(scale);
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      this.field_110723_a.rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.field_110723_a.rotateAngleX = headPitch / (180.0F / (float)Math.PI);
   }
}
