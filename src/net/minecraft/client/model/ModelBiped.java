package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBiped extends ModelBase {
   public ModelRenderer bipedHead;
   public ModelRenderer bipedHeadwear;
   public ModelRenderer bipedBody;
   public ModelRenderer bipedRightArm;
   public ModelRenderer bipedLeftArm;
   public ModelRenderer bipedRightLeg;
   public ModelRenderer bipedLeftLeg;
   public int heldItemLeft;
   public int heldItemRight;
   public boolean isSneak;
   public boolean aimedBow;
   private static final long d = 96022216633921L;

   public ModelBiped() {
      this(0.0F);
   }

   public ModelBiped(float var1) {
      this(var1, 0.0F, 64, 32);
   }

   public ModelBiped(float var1, float var2, int var3, int var4) {
      this.textureWidth = var3;
      this.textureHeight = var4;
      this.bipedHead = new ModelRenderer(this, 0, 0);
      this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1);
      this.bipedHead.setRotationPoint(0.0F, 0.0F + var2, 0.0F);
      this.bipedHeadwear = new ModelRenderer(this, 32, 0);
      this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1 + 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + var2, 0.0F);
      this.bipedBody = new ModelRenderer(this, 16, 16);
      this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, var1);
      this.bipedBody.setRotationPoint(0.0F, 0.0F + var2, 0.0F);
      this.bipedRightArm = new ModelRenderer(this, 40, 16);
      this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + var2, 0.0F);
      this.bipedLeftArm = new ModelRenderer(this, 40, 16);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + var2, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 0, 16);
      this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + var2, 0.0F);
      this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + var2, 0.0F);
   }

   @Override
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         float var11 = 2.0F;
         GlStateManager.scale(1.5F / var11, 1.5F / var11, 1.5F / var11);
         GlStateManager.translate(0.0F, 16.0F * var7, 0.0F);
         this.bipedHead.render(var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(1.0F / var11, 1.0F / var11, 1.0F / var11);
         GlStateManager.translate(0.0F, 24.0F * var7, 0.0F);
         this.bipedBody.render(var7);
         this.bipedRightArm.render(var7);
         this.bipedLeftArm.render(var7);
         this.bipedRightLeg.render(var7);
         this.bipedLeftLeg.render(var7);
         this.bipedHeadwear.render(var7);
      } else {
         if (var1.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         this.bipedHead.render(var7);
         this.bipedBody.render(var7);
         this.bipedRightArm.render(var7);
         this.bipedLeftArm.render(var7);
         this.bipedRightLeg.render(var7);
         this.bipedLeftLeg.render(var7);
         this.bipedHeadwear.render(var7);
      }

      GlStateManager.popMatrix();
   }

   @Override
   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      this.bipedHead.rotateAngleY = var4 / (180.0F / (float)Math.PI);
      this.bipedHead.rotateAngleX = var5 / (180.0F / (float)Math.PI);
      this.bipedRightArm.rotateAngleX = MathHelper.cos(var1 * 0.6662F + (float) Math.PI) * 2.0F * var2 * 0.5F;
      this.bipedLeftArm.rotateAngleX = MathHelper.cos(var1 * 0.6662F) * 2.0F * var2 * 0.5F;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightLeg.rotateAngleX = MathHelper.cos(var1 * 0.6662F) * 1.4F * var2;
      this.bipedLeftLeg.rotateAngleX = MathHelper.cos(var1 * 0.6662F + (float) Math.PI) * 1.4F * var2;
      this.bipedRightLeg.rotateAngleY = 0.0F;
      this.bipedLeftLeg.rotateAngleY = 0.0F;
      if (this.isRiding) {
         this.bipedRightArm.rotateAngleX += (float) (-Math.PI / 5);
         this.bipedLeftArm.rotateAngleX += (float) (-Math.PI / 5);
         this.bipedRightLeg.rotateAngleX = (float) (-Math.PI * 2.0 / 5.0);
         this.bipedLeftLeg.rotateAngleX = (float) (-Math.PI * 2.0 / 5.0);
         this.bipedRightLeg.rotateAngleY = (float) (Math.PI / 10);
         this.bipedLeftLeg.rotateAngleY = (float) (-Math.PI / 10);
      }

      if (this.heldItemLeft != 0) {
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (float) (Math.PI / 10) * this.heldItemLeft;
      }

      this.bipedRightArm.rotateAngleY = 0.0F;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      switch (this.heldItemRight) {
         case 0:
         case 2:
         default:
            break;
         case 1:
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float) (Math.PI / 10) * this.heldItemRight;
            break;
         case 3:
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float) (Math.PI / 10) * this.heldItemRight;
            this.bipedRightArm.rotateAngleY = (float) (-Math.PI / 6);
      }

      this.bipedLeftArm.rotateAngleY = 0.0F;
      if (this.swingProgress > -9990.0F) {
         float var11 = this.swingProgress;
         this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(var11) * (float) Math.PI * 2.0F) * 0.2F;
         this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotateAngleY = this.bipedRightArm.rotateAngleY + this.bipedBody.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = this.bipedLeftArm.rotateAngleY + this.bipedBody.rotateAngleY;
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX + this.bipedBody.rotateAngleY;
         var11 = 1.0F - this.swingProgress;
         var11 *= var11;
         var11 *= var11;
         var11 = 1.0F - var11;
         float var12 = MathHelper.sin(var11 * (float) Math.PI);
         float var13 = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
         this.bipedRightArm.rotateAngleX = (float)(this.bipedRightArm.rotateAngleX - (var12 * 1.2 + var13));
         this.bipedRightArm.rotateAngleY = this.bipedRightArm.rotateAngleY + this.bipedBody.rotateAngleY * 2.0F;
         this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.4F;
      }

      if (this.isSneak) {
         this.bipedBody.rotateAngleX = 0.5F;
         this.bipedRightArm.rotateAngleX += 0.4F;
         this.bipedLeftArm.rotateAngleX += 0.4F;
         this.bipedRightLeg.rotationPointZ = 4.0F;
         this.bipedLeftLeg.rotationPointZ = 4.0F;
         this.bipedRightLeg.rotationPointY = 9.0F;
         this.bipedLeftLeg.rotationPointY = 9.0F;
         this.bipedHead.rotationPointY = 1.0F;
      } else {
         this.bipedBody.rotateAngleX = 0.0F;
         this.bipedRightLeg.rotationPointZ = 0.1F;
         this.bipedLeftLeg.rotationPointZ = 0.1F;
         this.bipedRightLeg.rotationPointY = 12.0F;
         this.bipedLeftLeg.rotationPointY = 12.0F;
         this.bipedHead.rotationPointY = 0.0F;
      }

      this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + (MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F);
      this.bipedLeftArm.rotateAngleZ = this.bipedLeftArm.rotateAngleZ - (MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F);
      this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX + MathHelper.sin(var3 * 0.067F) * 0.05F;
      this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX - MathHelper.sin(var3 * 0.067F) * 0.05F;
      if (this.aimedBow) {
         float var18 = 0.0F;
         float var19 = 0.0F;
         this.bipedRightArm.rotateAngleZ = 0.0F;
         this.bipedLeftArm.rotateAngleZ = 0.0F;
         this.bipedRightArm.rotateAngleY = -(0.1F - var18 * 0.6F) + this.bipedHead.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.1F - var18 * 0.6F + this.bipedHead.rotateAngleY + 0.4F;
         this.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2) + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = (float) (-Math.PI / 2) + this.bipedHead.rotateAngleX;
         this.bipedRightArm.rotateAngleX -= var18 * 1.2F - var19 * 0.4F;
         this.bipedLeftArm.rotateAngleX -= var18 * 1.2F - var19 * 0.4F;
         this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + (MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F);
         this.bipedLeftArm.rotateAngleZ = this.bipedLeftArm.rotateAngleZ - (MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F);
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX + MathHelper.sin(var3 * 0.067F) * 0.05F;
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX - MathHelper.sin(var3 * 0.067F) * 0.05F;
      }

      copyModelAngles(this.bipedHead, this.bipedHeadwear);
   }

   @Override
   public void setModelAttributes(ModelBase var1) {
      super.setModelAttributes(var1);
      if (var1 instanceof ModelBiped) {
         ModelBiped var5 = (ModelBiped)var1;
         this.heldItemLeft = var5.heldItemLeft;
         this.heldItemRight = var5.heldItemRight;
         this.isSneak = var5.isSneak;
         this.aimedBow = var5.aimedBow;
      }
   }

   public void setInvisible(boolean var1) {
      this.bipedHead.showModel = var1;
      this.bipedHeadwear.showModel = var1;
      this.bipedBody.showModel = var1;
      this.bipedRightArm.showModel = var1;
      this.bipedLeftArm.showModel = var1;
      this.bipedRightLeg.showModel = var1;
      this.bipedLeftLeg.showModel = var1;
   }

   public void postRenderArm(float var1) {
      this.bipedRightArm.postRender(var1);
   }
}
