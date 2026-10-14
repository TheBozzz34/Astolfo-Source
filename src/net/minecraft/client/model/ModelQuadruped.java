package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelQuadruped extends ModelBase {
   public ModelRenderer head = new ModelRenderer(this, 0, 0);
   public ModelRenderer body;
   public ModelRenderer leg1;
   public ModelRenderer leg2;
   public ModelRenderer leg3;
   public ModelRenderer leg4;
   public float childYOffset = 8.0F;
   public float childZOffset = 4.0F;
   private static final long e = 35630541843045L;

   public ModelQuadruped(int i, float f) {
      this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, f);
      this.head.setRotationPoint(0.0F, 18 - i, -6.0F);
      this.body = new ModelRenderer(this, 28, 8);
      this.body.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, f);
      this.body.setRotationPoint(0.0F, 17 - i, 2.0F);
      this.leg1 = new ModelRenderer(this, 0, 16);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
      this.leg1.setRotationPoint(-3.0F, 24 - i, 7.0F);
      this.leg2 = new ModelRenderer(this, 0, 16);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
      this.leg2.setRotationPoint(3.0F, 24 - i, 7.0F);
      this.leg3 = new ModelRenderer(this, 0, 16);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
      this.leg3.setRotationPoint(-3.0F, 24 - i, -5.0F);
      this.leg4 = new ModelRenderer(this, 0, 16);
      this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
      this.leg4.setRotationPoint(3.0F, 24 - i, -5.0F);
   }

   @Override
   public void render(Entity entityIn, float f, float g, float h, float f2, float f3, float scale) {
      this.setRotationAngles(f, g, h, f2, f3, scale, entityIn);
      if (this.isChild) {
         float f4 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, this.childYOffset * scale, this.childZOffset * scale);
         this.head.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(1.0F / f4, 1.0F / f4, 1.0F / f4);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.body.render(scale);
         this.leg1.render(scale);
         this.leg2.render(scale);
         this.leg3.render(scale);
         this.leg4.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.head.render(scale);
         this.body.render(scale);
         this.leg1.render(scale);
         this.leg2.render(scale);
         this.leg3.render(scale);
         this.leg4.render(scale);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      this.head.rotateAngleX = headPitch / (180.0F / (float)Math.PI);
      this.head.rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.body.rotateAngleX = (float) (Math.PI / 2);
      this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
      this.leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
      this.leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
   }
}
