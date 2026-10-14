package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;

public class ModelIronGolem extends ModelBase {
   public ModelRenderer ironGolemHead;
   public ModelRenderer ironGolemBody;
   public ModelRenderer ironGolemRightArm;
   public ModelRenderer ironGolemLeftArm;
   public ModelRenderer ironGolemLeftLeg;
   public ModelRenderer ironGolemRightLeg;

   public ModelIronGolem() {
      this(0.0F);
   }

   public ModelIronGolem(float f) {
      this(f, -7.0F);
   }

   public ModelIronGolem(float f, float g) {
      short s = 128;
      short s2 = 128;
      this.ironGolemHead = new ModelRenderer(this).setTextureSize(s, s2);
      this.ironGolemHead.setRotationPoint(0.0F, 0.0F + g, -2.0F);
      this.ironGolemHead.setTextureOffset(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8, 10, 8, f);
      this.ironGolemHead.setTextureOffset(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2, 4, 2, f);
      this.ironGolemBody = new ModelRenderer(this).setTextureSize(s, s2);
      this.ironGolemBody.setRotationPoint(0.0F, 0.0F + g, 0.0F);
      this.ironGolemBody.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18, 12, 11, f);
      this.ironGolemBody.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6, f + 0.5F);
      this.ironGolemRightArm = new ModelRenderer(this).setTextureSize(s, s2);
      this.ironGolemRightArm.setRotationPoint(0.0F, -7.0F, 0.0F);
      this.ironGolemRightArm.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4, 30, 6, f);
      this.ironGolemLeftArm = new ModelRenderer(this).setTextureSize(s, s2);
      this.ironGolemLeftArm.setRotationPoint(0.0F, -7.0F, 0.0F);
      this.ironGolemLeftArm.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4, 30, 6, f);
      this.ironGolemLeftLeg = new ModelRenderer(this, 0, 22).setTextureSize(s, s2);
      this.ironGolemLeftLeg.setRotationPoint(-4.0F, 18.0F + g, 0.0F);
      this.ironGolemLeftLeg.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
      this.ironGolemRightLeg = new ModelRenderer(this, 0, 22).setTextureSize(s, s2);
      this.ironGolemRightLeg.mirror = true;
      this.ironGolemRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0F, 18.0F + g, 0.0F);
      this.ironGolemRightLeg.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
   }

   @Override
   public void render(Entity entityIn, float f, float g, float h, float f2, float f3, float scale) {
      this.setRotationAngles(f, g, h, f2, f3, scale, entityIn);
      this.ironGolemHead.render(scale);
      this.ironGolemBody.render(scale);
      this.ironGolemLeftLeg.render(scale);
      this.ironGolemRightLeg.render(scale);
      this.ironGolemRightArm.render(scale);
      this.ironGolemLeftArm.render(scale);
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      this.ironGolemHead.rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.ironGolemHead.rotateAngleX = headPitch / (180.0F / (float)Math.PI);
      this.ironGolemLeftLeg.rotateAngleX = -1.5F * this.func_78172_a(limbSwing, 13.0F) * limbSwingAmount;
      this.ironGolemRightLeg.rotateAngleX = 1.5F * this.func_78172_a(limbSwing, 13.0F) * limbSwingAmount;
      this.ironGolemLeftLeg.rotateAngleY = 0.0F;
      this.ironGolemRightLeg.rotateAngleY = 0.0F;
   }

   @Override
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float f, float g, float partialTickTime) {
      EntityIronGolem entityIronGolem = (EntityIronGolem)entitylivingbaseIn;
      int i = entityIronGolem.getAttackTimer();
      if (i > 0) {
         this.ironGolemRightArm.rotateAngleX = -2.0F + 1.5F * this.func_78172_a(i - partialTickTime, 10.0F);
         this.ironGolemLeftArm.rotateAngleX = -2.0F + 1.5F * this.func_78172_a(i - partialTickTime, 10.0F);
      } else {
         int j = entityIronGolem.getHoldRoseTick();
         if (j > 0) {
            this.ironGolemRightArm.rotateAngleX = -0.8F + 0.025F * this.func_78172_a(j, 70.0F);
            this.ironGolemLeftArm.rotateAngleX = 0.0F;
         } else {
            this.ironGolemRightArm.rotateAngleX = (-0.2F + 1.5F * this.func_78172_a(f, 13.0F)) * g;
            this.ironGolemLeftArm.rotateAngleX = (-0.2F - 1.5F * this.func_78172_a(f, 13.0F)) * g;
         }
      }
   }

   private float func_78172_a(float f, float g) {
      return (Math.abs(f % g - g * 0.5F) - g * 0.25F) / (g * 0.25F);
   }
}
