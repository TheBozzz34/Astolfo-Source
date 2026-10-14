package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelWitch extends ModelVillager {
   public boolean field_82900_g;
   private ModelRenderer field_82901_h = new ModelRenderer(this).setTextureSize(64, 128);
   private ModelRenderer witchHat;
   private static final long b = 39182929443546L;

   public ModelWitch(float f) {
      super(f, 0.0F, 64, 128);
      this.field_82901_h.setRotationPoint(0.0F, -2.0F, 0.0F);
      this.field_82901_h.setTextureOffset(0, 0).addBox(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
      this.villagerNose.addChild(this.field_82901_h);
      this.witchHat = new ModelRenderer(this).setTextureSize(64, 128);
      this.witchHat.setRotationPoint(-5.0F, -10.03125F, -5.0F);
      this.witchHat.setTextureOffset(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
      this.villagerHead.addChild(this.witchHat);
      ModelRenderer modelRenderer = new ModelRenderer(this).setTextureSize(64, 128);
      modelRenderer.setRotationPoint(1.75F, -4.0F, 2.0F);
      modelRenderer.setTextureOffset(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
      modelRenderer.rotateAngleX = -0.05235988F;
      modelRenderer.rotateAngleZ = 0.02617994F;
      this.witchHat.addChild(modelRenderer);
      ModelRenderer modelRenderer2 = new ModelRenderer(this).setTextureSize(64, 128);
      modelRenderer2.setRotationPoint(1.75F, -4.0F, 2.0F);
      modelRenderer2.setTextureOffset(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
      modelRenderer2.rotateAngleX = -0.10471976F;
      modelRenderer2.rotateAngleZ = 0.05235988F;
      modelRenderer.addChild(modelRenderer2);
      ModelRenderer modelRenderer3 = new ModelRenderer(this).setTextureSize(64, 128);
      modelRenderer3.setRotationPoint(1.75F, -2.0F, 2.0F);
      modelRenderer3.setTextureOffset(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
      modelRenderer3.rotateAngleX = (float) (-Math.PI / 15);
      modelRenderer3.rotateAngleZ = 0.10471976F;
      modelRenderer2.addChild(modelRenderer3);
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      this.villagerNose.offsetX = this.villagerNose.offsetY = this.villagerNose.offsetZ = 0.0F;
      float f = 0.01F * (entityIn.getEntityId() % 10);
      this.villagerNose.rotateAngleX = MathHelper.sin(entityIn.ticksExisted * f) * 4.5F * (float) Math.PI / 180.0F;
      this.villagerNose.rotateAngleY = 0.0F;
      this.villagerNose.rotateAngleZ = MathHelper.cos(entityIn.ticksExisted * f) * 2.5F * (float) Math.PI / 180.0F;
      if (this.field_82900_g) {
         this.villagerNose.rotateAngleX = -0.9F;
         this.villagerNose.offsetZ = -0.09375F;
         this.villagerNose.offsetY = 0.1875F;
      }
   }
}
