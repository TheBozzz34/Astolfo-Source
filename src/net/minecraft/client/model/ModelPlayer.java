package net.minecraft.client.model;

import bypass.Astolfo;
import bypass.module.visuals.Skeleton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class ModelPlayer extends ModelBiped {
   public ModelRenderer bipedLeftArmwear;
   public ModelRenderer bipedRightArmwear;
   public ModelRenderer bipedLeftLegwear;
   public ModelRenderer bipedRightLegwear;
   public ModelRenderer bipedBodyWear;
   private ModelRenderer bipedCape;
   private ModelRenderer bipedDeadmau5Head;
   private boolean smallArms;
   private static final long a = 28816121258200L;

   public ModelPlayer(float f, boolean bl) {
      super(f, 0.0F, 64, 64);
      this.smallArms = bl;
      this.bipedDeadmau5Head = new ModelRenderer(this, 24, 0);
      this.bipedDeadmau5Head.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, f);
      this.bipedCape = new ModelRenderer(this, 0, 0);
      this.bipedCape.setTextureSize(64, 32);
      this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, f);
      if (bl) {
         this.bipedLeftArm = new ModelRenderer(this, 32, 48);
         this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f);
         this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
         this.bipedRightArm = new ModelRenderer(this, 40, 16);
         this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f);
         this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
         this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
         this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
         this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
         this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
         this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
         this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
      } else {
         this.bipedLeftArm = new ModelRenderer(this, 32, 48);
         this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
         this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
         this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
         this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
         this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
         this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
         this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
         this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
      }

      this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
      this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
      this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
      this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
      this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
      this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
      this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
      this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
      this.bipedBodyWear = new ModelRenderer(this, 16, 32);
      this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.25F);
      this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   @Override
   public void render(Entity entityIn, float f, float g, float h, float f2, float f3, float scale) {
      long l = a ^ 102529563793852L;
      long m = l ^ 123156094780631L;
      super.render(entityIn, f, g, h, f2, f3, scale);
      if (entityIn != Minecraft.getMinecraft().thePlayer && Astolfo.INSTANCE.getModuleManager().get(Skeleton.class).isEnabled()) {
         Skeleton.saveRotations((EntityPlayer)entityIn, this);
      }

      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f4 = 2.0F;
         GlStateManager.scale(1.0F / f4, 1.0F / f4, 1.0F / f4);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.bipedLeftLegwear.render(scale);
         this.bipedRightLegwear.render(scale);
         this.bipedLeftArmwear.render(scale);
         this.bipedRightArmwear.render(scale);
         this.bipedBodyWear.render(scale);
      } else {
         if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         this.bipedLeftLegwear.render(scale);
         this.bipedRightLegwear.render(scale);
         this.bipedLeftArmwear.render(scale);
         this.bipedRightArmwear.render(scale);
         this.bipedBodyWear.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void renderDeadmau5Head(float f) {
      copyModelAngles(this.bipedHead, this.bipedDeadmau5Head);
      this.bipedDeadmau5Head.rotationPointX = 0.0F;
      this.bipedDeadmau5Head.rotationPointY = 0.0F;
      this.bipedDeadmau5Head.render(f);
   }

   public void X(float f) {
      this.bipedCape.render(f);
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
      copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
      copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
      copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
      copyModelAngles(this.bipedBody, this.bipedBodyWear);
   }

   public void renderRightArm() {
      this.bipedRightArm.render(0.0625F);
      this.bipedRightArmwear.render(0.0625F);
   }

   public void renderLeftArm() {
      this.bipedLeftArm.render(0.0625F);
      this.bipedLeftArmwear.render(0.0625F);
   }

   @Override
   public void setInvisible(boolean invisible) {
      super.setInvisible(invisible);
      this.bipedLeftArmwear.showModel = invisible;
      this.bipedRightArmwear.showModel = invisible;
      this.bipedLeftLegwear.showModel = invisible;
      this.bipedRightLegwear.showModel = invisible;
      this.bipedBodyWear.showModel = invisible;
      this.bipedCape.showModel = invisible;
      this.bipedDeadmau5Head.showModel = invisible;
   }

   @Override
   public void postRenderArm(float scale) {
      if (this.smallArms) {
         this.bipedRightArm.rotationPointX++;
         this.bipedRightArm.postRender(scale);
         this.bipedRightArm.rotationPointX--;
      } else {
         this.bipedRightArm.postRender(scale);
      }
   }
}
