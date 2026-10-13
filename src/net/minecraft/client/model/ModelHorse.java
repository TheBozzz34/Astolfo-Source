package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.MathHelper;

public class ModelHorse extends ModelBase {
   private ModelRenderer head;
   private ModelRenderer field_178711_b;
   private ModelRenderer field_178712_c;
   private ModelRenderer horseLeftEar;
   private ModelRenderer horseRightEar;
   private ModelRenderer muleLeftEar;
   private ModelRenderer muleRightEar;
   private ModelRenderer neck;
   private ModelRenderer horseFaceRopes;
   private ModelRenderer mane;
   private ModelRenderer body;
   private ModelRenderer tailBase;
   private ModelRenderer tailMiddle;
   private ModelRenderer tailTip;
   private ModelRenderer backLeftLeg;
   private ModelRenderer backLeftShin;
   private ModelRenderer backLeftHoof;
   private ModelRenderer backRightLeg;
   private ModelRenderer backRightShin;
   private ModelRenderer backRightHoof;
   private ModelRenderer frontLeftLeg;
   private ModelRenderer frontLeftShin;
   private ModelRenderer frontLeftHoof;
   private ModelRenderer frontRightLeg;
   private ModelRenderer frontRightShin;
   private ModelRenderer frontRightHoof;
   private ModelRenderer muleLeftChest;
   private ModelRenderer muleRightChest;
   private ModelRenderer horseSaddleBottom;
   private ModelRenderer horseSaddleFront;
   private ModelRenderer horseSaddleBack;
   private ModelRenderer horseLeftSaddleRope;
   private ModelRenderer horseLeftSaddleMetal;
   private ModelRenderer horseRightSaddleRope;
   private ModelRenderer horseRightSaddleMetal;
   private ModelRenderer horseLeftFaceMetal;
   private ModelRenderer horseRightFaceMetal;
   private ModelRenderer horseLeftRein;
   private ModelRenderer horseRightRein;
   private static final long q = 19478868504895L;

   public ModelHorse() {
      this.textureWidth = 128;
      this.textureHeight = 128;
      this.body = new ModelRenderer(this, 0, 34);
      this.body.addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24);
      this.body.setRotationPoint(0.0F, 11.0F, 9.0F);
      this.tailBase = new ModelRenderer(this, 44, 0);
      this.tailBase.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3);
      this.tailBase.setRotationPoint(0.0F, 3.0F, 14.0F);
      this.setBoxRotation(this.tailBase, -1.134464F, 0.0F, 0.0F);
      this.tailMiddle = new ModelRenderer(this, 38, 7);
      this.tailMiddle.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7);
      this.tailMiddle.setRotationPoint(0.0F, 3.0F, 14.0F);
      this.setBoxRotation(this.tailMiddle, -1.134464F, 0.0F, 0.0F);
      this.tailTip = new ModelRenderer(this, 24, 3);
      this.tailTip.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7);
      this.tailTip.setRotationPoint(0.0F, 3.0F, 14.0F);
      this.setBoxRotation(this.tailTip, -1.40215F, 0.0F, 0.0F);
      this.backLeftLeg = new ModelRenderer(this, 78, 29);
      this.backLeftLeg.addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5);
      this.backLeftLeg.setRotationPoint(4.0F, 9.0F, 11.0F);
      this.backLeftShin = new ModelRenderer(this, 78, 43);
      this.backLeftShin.addBox(-2.0F, 0.0F, -1.5F, 3, 5, 3);
      this.backLeftShin.setRotationPoint(4.0F, 16.0F, 11.0F);
      this.backLeftHoof = new ModelRenderer(this, 78, 51);
      this.backLeftHoof.addBox(-2.5F, 5.1F, -2.0F, 4, 3, 4);
      this.backLeftHoof.setRotationPoint(4.0F, 16.0F, 11.0F);
      this.backRightLeg = new ModelRenderer(this, 96, 29);
      this.backRightLeg.addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5);
      this.backRightLeg.setRotationPoint(-4.0F, 9.0F, 11.0F);
      this.backRightShin = new ModelRenderer(this, 96, 43);
      this.backRightShin.addBox(-1.0F, 0.0F, -1.5F, 3, 5, 3);
      this.backRightShin.setRotationPoint(-4.0F, 16.0F, 11.0F);
      this.backRightHoof = new ModelRenderer(this, 96, 51);
      this.backRightHoof.addBox(-1.5F, 5.1F, -2.0F, 4, 3, 4);
      this.backRightHoof.setRotationPoint(-4.0F, 16.0F, 11.0F);
      this.frontLeftLeg = new ModelRenderer(this, 44, 29);
      this.frontLeftLeg.addBox(-1.9F, -1.0F, -2.1F, 3, 8, 4);
      this.frontLeftLeg.setRotationPoint(4.0F, 9.0F, -8.0F);
      this.frontLeftShin = new ModelRenderer(this, 44, 41);
      this.frontLeftShin.addBox(-1.9F, 0.0F, -1.6F, 3, 5, 3);
      this.frontLeftShin.setRotationPoint(4.0F, 16.0F, -8.0F);
      this.frontLeftHoof = new ModelRenderer(this, 44, 51);
      this.frontLeftHoof.addBox(-2.4F, 5.1F, -2.1F, 4, 3, 4);
      this.frontLeftHoof.setRotationPoint(4.0F, 16.0F, -8.0F);
      this.frontRightLeg = new ModelRenderer(this, 60, 29);
      this.frontRightLeg.addBox(-1.1F, -1.0F, -2.1F, 3, 8, 4);
      this.frontRightLeg.setRotationPoint(-4.0F, 9.0F, -8.0F);
      this.frontRightShin = new ModelRenderer(this, 60, 41);
      this.frontRightShin.addBox(-1.1F, 0.0F, -1.6F, 3, 5, 3);
      this.frontRightShin.setRotationPoint(-4.0F, 16.0F, -8.0F);
      this.frontRightHoof = new ModelRenderer(this, 60, 51);
      this.frontRightHoof.addBox(-1.6F, 5.1F, -2.1F, 4, 3, 4);
      this.frontRightHoof.setRotationPoint(-4.0F, 16.0F, -8.0F);
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7);
      this.head.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.head, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.field_178711_b = new ModelRenderer(this, 24, 18);
      this.field_178711_b.addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6);
      this.field_178711_b.setRotationPoint(0.0F, 3.95F, -10.0F);
      this.setBoxRotation(this.field_178711_b, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.field_178712_c = new ModelRenderer(this, 24, 27);
      this.field_178712_c.addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5);
      this.field_178712_c.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.field_178712_c, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.head.addChild(this.field_178711_b);
      this.head.addChild(this.field_178712_c);
      this.horseLeftEar = new ModelRenderer(this, 0, 0);
      this.horseLeftEar.addBox(0.45F, -12.0F, 4.0F, 2, 3, 1);
      this.horseLeftEar.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.horseLeftEar, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.horseRightEar = new ModelRenderer(this, 0, 0);
      this.horseRightEar.addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1);
      this.horseRightEar.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.horseRightEar, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.muleLeftEar = new ModelRenderer(this, 0, 12);
      this.muleLeftEar.addBox(-2.0F, -16.0F, 4.0F, 2, 7, 1);
      this.muleLeftEar.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.muleLeftEar, (float) (Math.PI / 6), 0.0F, (float) (Math.PI / 12));
      this.muleRightEar = new ModelRenderer(this, 0, 12);
      this.muleRightEar.addBox(0.0F, -16.0F, 4.0F, 2, 7, 1);
      this.muleRightEar.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.muleRightEar, (float) (Math.PI / 6), 0.0F, (float) (-Math.PI / 12));
      this.neck = new ModelRenderer(this, 0, 12);
      this.neck.addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8);
      this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.neck, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.muleLeftChest = new ModelRenderer(this, 0, 34);
      this.muleLeftChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
      this.muleLeftChest.setRotationPoint(-7.5F, 3.0F, 10.0F);
      this.setBoxRotation(this.muleLeftChest, 0.0F, (float) (Math.PI / 2), 0.0F);
      this.muleRightChest = new ModelRenderer(this, 0, 47);
      this.muleRightChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
      this.muleRightChest.setRotationPoint(4.5F, 3.0F, 10.0F);
      this.setBoxRotation(this.muleRightChest, 0.0F, (float) (Math.PI / 2), 0.0F);
      this.horseSaddleBottom = new ModelRenderer(this, 80, 0);
      this.horseSaddleBottom.addBox(-5.0F, 0.0F, -3.0F, 10, 1, 8);
      this.horseSaddleBottom.setRotationPoint(0.0F, 2.0F, 2.0F);
      this.horseSaddleFront = new ModelRenderer(this, 106, 9);
      this.horseSaddleFront.addBox(-1.5F, -1.0F, -3.0F, 3, 1, 2);
      this.horseSaddleFront.setRotationPoint(0.0F, 2.0F, 2.0F);
      this.horseSaddleBack = new ModelRenderer(this, 80, 9);
      this.horseSaddleBack.addBox(-4.0F, -1.0F, 3.0F, 8, 1, 2);
      this.horseSaddleBack.setRotationPoint(0.0F, 2.0F, 2.0F);
      this.horseLeftSaddleMetal = new ModelRenderer(this, 74, 0);
      this.horseLeftSaddleMetal.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
      this.horseLeftSaddleMetal.setRotationPoint(5.0F, 3.0F, 2.0F);
      this.horseLeftSaddleRope = new ModelRenderer(this, 70, 0);
      this.horseLeftSaddleRope.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
      this.horseLeftSaddleRope.setRotationPoint(5.0F, 3.0F, 2.0F);
      this.horseRightSaddleMetal = new ModelRenderer(this, 74, 4);
      this.horseRightSaddleMetal.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
      this.horseRightSaddleMetal.setRotationPoint(-5.0F, 3.0F, 2.0F);
      this.horseRightSaddleRope = new ModelRenderer(this, 80, 0);
      this.horseRightSaddleRope.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
      this.horseRightSaddleRope.setRotationPoint(-5.0F, 3.0F, 2.0F);
      this.horseLeftFaceMetal = new ModelRenderer(this, 74, 13);
      this.horseLeftFaceMetal.addBox(1.5F, -8.0F, -4.0F, 1, 2, 2);
      this.horseLeftFaceMetal.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.horseLeftFaceMetal, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.horseRightFaceMetal = new ModelRenderer(this, 74, 13);
      this.horseRightFaceMetal.addBox(-2.5F, -8.0F, -4.0F, 1, 2, 2);
      this.horseRightFaceMetal.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.horseRightFaceMetal, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.horseLeftRein = new ModelRenderer(this, 44, 10);
      this.horseLeftRein.addBox(2.6F, -6.0F, -6.0F, 0, 3, 16);
      this.horseLeftRein.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.horseRightRein = new ModelRenderer(this, 44, 5);
      this.horseRightRein.addBox(-2.6F, -6.0F, -6.0F, 0, 3, 16);
      this.horseRightRein.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.mane = new ModelRenderer(this, 58, 0);
      this.mane.addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4);
      this.mane.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.mane, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.horseFaceRopes = new ModelRenderer(this, 80, 12);
      this.horseFaceRopes.addBox(-2.5F, -10.1F, -7.0F, 5, 5, 12, 0.2F);
      this.horseFaceRopes.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.horseFaceRopes, (float) (Math.PI / 6), 0.0F, 0.0F);
   }

   @Override
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      EntityHorse var11 = (EntityHorse)var1;
      int var12 = var11.getHorseType();
      float var13 = var11.getGrassEatingAmount(0.0F);
      boolean var14 = var11.isAdultHorse();
      boolean var15 = var14 && var11.isHorseSaddled();
      boolean var16 = var14 && var11.isChested();
      boolean var17 = var12 == 1 || var12 == 2;
      float var18 = var11.getHorseSize();
      boolean var19 = var11.riddenByEntity != null;
      if (var15) {
         this.horseFaceRopes.render(var7);
         this.horseSaddleBottom.render(var7);
         this.horseSaddleFront.render(var7);
         this.horseSaddleBack.render(var7);
         this.horseLeftSaddleRope.render(var7);
         this.horseLeftSaddleMetal.render(var7);
         this.horseRightSaddleRope.render(var7);
         this.horseRightSaddleMetal.render(var7);
         this.horseLeftFaceMetal.render(var7);
         this.horseRightFaceMetal.render(var7);
         if (var19) {
            this.horseLeftRein.render(var7);
            this.horseRightRein.render(var7);
         }
      }

      if (!var14) {
         GlStateManager.pushMatrix();
         GlStateManager.scale(var18, 0.5F + var18 * 0.5F, var18);
         GlStateManager.translate(0.0F, 0.95F * (1.0F - var18), 0.0F);
      }

      this.backLeftLeg.render(var7);
      this.backLeftShin.render(var7);
      this.backLeftHoof.render(var7);
      this.backRightLeg.render(var7);
      this.backRightShin.render(var7);
      this.backRightHoof.render(var7);
      this.frontLeftLeg.render(var7);
      this.frontLeftShin.render(var7);
      this.frontLeftHoof.render(var7);
      this.frontRightLeg.render(var7);
      this.frontRightShin.render(var7);
      this.frontRightHoof.render(var7);
      if (!var14) {
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(var18, var18, var18);
         GlStateManager.translate(0.0F, 1.35F * (1.0F - var18), 0.0F);
      }

      this.body.render(var7);
      this.tailBase.render(var7);
      this.tailMiddle.render(var7);
      this.tailTip.render(var7);
      this.neck.render(var7);
      this.mane.render(var7);
      if (!var14) {
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float var20 = 0.5F + var18 * var18 * 0.5F;
         GlStateManager.scale(var20, var20, var20);
         if (var13 <= 0.0F) {
            GlStateManager.translate(0.0F, 1.35F * (1.0F - var18), 0.0F);
         } else {
            GlStateManager.translate(0.0F, 0.9F * (1.0F - var18) * var13 + 1.35F * (1.0F - var18) * (1.0F - var13), 0.15F * (1.0F - var18) * var13);
         }
      }

      if (var17) {
         this.muleLeftEar.render(var7);
         this.muleRightEar.render(var7);
      } else {
         this.horseLeftEar.render(var7);
         this.horseRightEar.render(var7);
      }

      this.head.render(var7);
      if (!var14) {
         GlStateManager.popMatrix();
      }

      if (var16) {
         this.muleLeftChest.render(var7);
         this.muleRightChest.render(var7);
      }
   }

   private void setBoxRotation(ModelRenderer var1, float var2, float var3, float var4) {
      var1.rotateAngleX = var2;
      var1.rotateAngleY = var3;
      var1.rotateAngleZ = var4;
   }

   private float updateHorseRotation(float var1, float var2, float var3) {
      float var7 = var2 - var1;

      while (var7 < -180.0F) {
         var7 += 360.0F;
      }

      while (var7 >= 180.0F) {
         var7 -= 360.0F;
      }

      return var1 + var3 * var7;
   }

   @Override
   public void setLivingAnimations(EntityLivingBase var1, float var2, float var3, float var4) {
      super.setLivingAnimations(var1, var2, var3, var4);
      float var8 = this.updateHorseRotation(var1.prevRenderYawOffset, var1.renderYawOffset, var4);
      float var9 = this.updateHorseRotation(var1.prevRotationYawHead, var1.rotationYawHead, var4);
      float var10 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var4;
      float var11 = var9 - var8;
      float var12 = var10 / (180.0F / (float)Math.PI);
      if (var11 > 20.0F) {
         var11 = 20.0F;
      }

      if (var11 < -20.0F) {
         var11 = -20.0F;
      }

      if (var3 > 0.2F) {
         var12 += MathHelper.cos(var2 * 0.4F) * 0.15F * var3;
      }

      EntityHorse var13 = (EntityHorse)var1;
      float var14 = var13.getGrassEatingAmount(var4);
      float var15 = var13.getRearingAmount(var4);
      float var16 = 1.0F - var15;
      float var17 = var13.getMouthOpennessAngle(var4);
      boolean var18 = var13.field_110278_bp != 0;
      boolean var19 = var13.isHorseSaddled();
      boolean var20 = var13.riddenByEntity != null;
      float var21 = var1.ticksExisted + var4;
      float var22 = MathHelper.cos(var2 * 0.6662F + (float) Math.PI);
      float var23 = var22 * 0.8F * var3;
      this.head.rotationPointY = 4.0F;
      this.head.rotationPointZ = -10.0F;
      this.tailBase.rotationPointY = 3.0F;
      this.tailMiddle.rotationPointZ = 14.0F;
      this.muleRightChest.rotationPointY = 3.0F;
      this.muleRightChest.rotationPointZ = 10.0F;
      this.body.rotateAngleX = 0.0F;
      this.head.rotateAngleX = (float) (Math.PI / 6) + var12;
      this.head.rotateAngleY = var11 / (180.0F / (float)Math.PI);
      this.head.rotateAngleX = var15 * ((float) (Math.PI / 12) + var12) + var14 * 2.18166F + (1.0F - Math.max(var15, var14)) * this.head.rotateAngleX;
      this.head.rotateAngleY = var15 * var11 / (180.0F / (float)Math.PI) + (1.0F - Math.max(var15, var14)) * this.head.rotateAngleY;
      this.head.rotationPointY = var15 * -6.0F + var14 * 11.0F + (1.0F - Math.max(var15, var14)) * this.head.rotationPointY;
      this.head.rotationPointZ = var15 * -1.0F + var14 * -10.0F + (1.0F - Math.max(var15, var14)) * this.head.rotationPointZ;
      this.tailBase.rotationPointY = var15 * 9.0F + var16 * this.tailBase.rotationPointY;
      this.tailMiddle.rotationPointZ = var15 * 18.0F + var16 * this.tailMiddle.rotationPointZ;
      this.muleRightChest.rotationPointY = var15 * 5.5F + var16 * this.muleRightChest.rotationPointY;
      this.muleRightChest.rotationPointZ = var15 * 15.0F + var16 * this.muleRightChest.rotationPointZ;
      this.body.rotateAngleX = var15 * -45.0F / (180.0F / (float)Math.PI) + var16 * this.body.rotateAngleX;
      this.horseLeftEar.rotationPointY = this.head.rotationPointY;
      this.horseRightEar.rotationPointY = this.head.rotationPointY;
      this.muleLeftEar.rotationPointY = this.head.rotationPointY;
      this.muleRightEar.rotationPointY = this.head.rotationPointY;
      this.neck.rotationPointY = this.head.rotationPointY;
      this.field_178711_b.rotationPointY = 0.02F;
      this.field_178712_c.rotationPointY = 0.0F;
      this.mane.rotationPointY = this.head.rotationPointY;
      this.horseLeftEar.rotationPointZ = this.head.rotationPointZ;
      this.horseRightEar.rotationPointZ = this.head.rotationPointZ;
      this.muleLeftEar.rotationPointZ = this.head.rotationPointZ;
      this.muleRightEar.rotationPointZ = this.head.rotationPointZ;
      this.neck.rotationPointZ = this.head.rotationPointZ;
      this.field_178711_b.rotationPointZ = 0.02F - var17 * 1.0F;
      this.field_178712_c.rotationPointZ = 0.0F + var17 * 1.0F;
      this.mane.rotationPointZ = this.head.rotationPointZ;
      this.horseLeftEar.rotateAngleX = this.head.rotateAngleX;
      this.horseRightEar.rotateAngleX = this.head.rotateAngleX;
      this.muleLeftEar.rotateAngleX = this.head.rotateAngleX;
      this.muleRightEar.rotateAngleX = this.head.rotateAngleX;
      this.neck.rotateAngleX = this.head.rotateAngleX;
      this.field_178711_b.rotateAngleX = 0.0F - 0.09424778F * var17;
      this.field_178712_c.rotateAngleX = 0.0F + (float) (Math.PI / 20) * var17;
      this.mane.rotateAngleX = this.head.rotateAngleX;
      this.horseLeftEar.rotateAngleY = this.head.rotateAngleY;
      this.horseRightEar.rotateAngleY = this.head.rotateAngleY;
      this.muleLeftEar.rotateAngleY = this.head.rotateAngleY;
      this.muleRightEar.rotateAngleY = this.head.rotateAngleY;
      this.neck.rotateAngleY = this.head.rotateAngleY;
      this.field_178711_b.rotateAngleY = 0.0F;
      this.field_178712_c.rotateAngleY = 0.0F;
      this.mane.rotateAngleY = this.head.rotateAngleY;
      this.muleLeftChest.rotateAngleX = var23 / 5.0F;
      this.muleRightChest.rotateAngleX = -var23 / 5.0F;
      float var24 = (float) (Math.PI / 2);
      float var27 = (float) (Math.PI / 12) * var15;
      float var28 = MathHelper.cos(var21 * 0.6F + (float) Math.PI);
      this.frontLeftLeg.rotationPointY = -2.0F * var15 + 9.0F * var16;
      this.frontLeftLeg.rotationPointZ = -2.0F * var15 + -8.0F * var16;
      this.frontRightLeg.rotationPointY = this.frontLeftLeg.rotationPointY;
      this.frontRightLeg.rotationPointZ = this.frontLeftLeg.rotationPointZ;
      this.backLeftShin.rotationPointY = this.backLeftLeg.rotationPointY + MathHelper.sin((float) (Math.PI / 2) + var27 + var16 * -var22 * 0.5F * var3) * 7.0F;
      this.backLeftShin.rotationPointZ = this.backLeftLeg.rotationPointZ
         + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + var27 + var16 * -var22 * 0.5F * var3) * 7.0F;
      this.backRightShin.rotationPointY = this.backRightLeg.rotationPointY + MathHelper.sin((float) (Math.PI / 2) + var27 + var16 * var22 * 0.5F * var3) * 7.0F;
      this.backRightShin.rotationPointZ = this.backRightLeg.rotationPointZ
         + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + var27 + var16 * var22 * 0.5F * var3) * 7.0F;
      float var29 = ((float) (-Math.PI / 3) + var28) * var15 + var23 * var16;
      float var30 = ((float) (-Math.PI / 3) + -var28) * var15 + -var23 * var16;
      this.frontLeftShin.rotationPointY = this.frontLeftLeg.rotationPointY + MathHelper.sin((float) (Math.PI / 2) + var29) * 7.0F;
      this.frontLeftShin.rotationPointZ = this.frontLeftLeg.rotationPointZ + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + var29) * 7.0F;
      this.frontRightShin.rotationPointY = this.frontRightLeg.rotationPointY + MathHelper.sin((float) (Math.PI / 2) + var30) * 7.0F;
      this.frontRightShin.rotationPointZ = this.frontRightLeg.rotationPointZ + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + var30) * 7.0F;
      this.backLeftLeg.rotateAngleX = var27 + -var22 * 0.5F * var3 * var16;
      this.backLeftShin.rotateAngleX = -0.08726646F * var15 + (-var22 * 0.5F * var3 - Math.max(0.0F, var22 * 0.5F * var3)) * var16;
      this.backLeftHoof.rotateAngleX = this.backLeftShin.rotateAngleX;
      this.backRightLeg.rotateAngleX = var27 + var22 * 0.5F * var3 * var16;
      this.backRightShin.rotateAngleX = -0.08726646F * var15 + (var22 * 0.5F * var3 - Math.max(0.0F, -var22 * 0.5F * var3)) * var16;
      this.backRightHoof.rotateAngleX = this.backRightShin.rotateAngleX;
      this.frontLeftLeg.rotateAngleX = var29;
      this.frontLeftShin.rotateAngleX = (this.frontLeftLeg.rotateAngleX + (float) Math.PI * Math.max(0.0F, 0.2F + var28 * 0.2F)) * var15
         + (var23 + Math.max(0.0F, var22 * 0.5F * var3)) * var16;
      this.frontLeftHoof.rotateAngleX = this.frontLeftShin.rotateAngleX;
      this.frontRightLeg.rotateAngleX = var30;
      this.frontRightShin.rotateAngleX = (this.frontRightLeg.rotateAngleX + (float) Math.PI * Math.max(0.0F, 0.2F - var28 * 0.2F)) * var15
         + (-var23 + Math.max(0.0F, -var22 * 0.5F * var3)) * var16;
      this.frontRightHoof.rotateAngleX = this.frontRightShin.rotateAngleX;
      this.backLeftHoof.rotationPointY = this.backLeftShin.rotationPointY;
      this.backLeftHoof.rotationPointZ = this.backLeftShin.rotationPointZ;
      this.backRightHoof.rotationPointY = this.backRightShin.rotationPointY;
      this.backRightHoof.rotationPointZ = this.backRightShin.rotationPointZ;
      this.frontLeftHoof.rotationPointY = this.frontLeftShin.rotationPointY;
      this.frontLeftHoof.rotationPointZ = this.frontLeftShin.rotationPointZ;
      this.frontRightHoof.rotationPointY = this.frontRightShin.rotationPointY;
      this.frontRightHoof.rotationPointZ = this.frontRightShin.rotationPointZ;
      if (var19) {
         this.horseSaddleBottom.rotationPointY = var15 * 0.5F + var16 * 2.0F;
         this.horseSaddleBottom.rotationPointZ = var15 * 11.0F + var16 * 2.0F;
         this.horseSaddleFront.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.horseSaddleBack.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.horseLeftSaddleRope.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.horseRightSaddleRope.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.horseLeftSaddleMetal.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.horseRightSaddleMetal.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.muleLeftChest.rotationPointY = this.muleRightChest.rotationPointY;
         this.horseSaddleFront.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.horseSaddleBack.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.horseLeftSaddleRope.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.horseRightSaddleRope.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.horseLeftSaddleMetal.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.horseRightSaddleMetal.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.muleLeftChest.rotationPointZ = this.muleRightChest.rotationPointZ;
         this.horseSaddleBottom.rotateAngleX = this.body.rotateAngleX;
         this.horseSaddleFront.rotateAngleX = this.body.rotateAngleX;
         this.horseSaddleBack.rotateAngleX = this.body.rotateAngleX;
         this.horseLeftRein.rotationPointY = this.head.rotationPointY;
         this.horseRightRein.rotationPointY = this.head.rotationPointY;
         this.horseFaceRopes.rotationPointY = this.head.rotationPointY;
         this.horseLeftFaceMetal.rotationPointY = this.head.rotationPointY;
         this.horseRightFaceMetal.rotationPointY = this.head.rotationPointY;
         this.horseLeftRein.rotationPointZ = this.head.rotationPointZ;
         this.horseRightRein.rotationPointZ = this.head.rotationPointZ;
         this.horseFaceRopes.rotationPointZ = this.head.rotationPointZ;
         this.horseLeftFaceMetal.rotationPointZ = this.head.rotationPointZ;
         this.horseRightFaceMetal.rotationPointZ = this.head.rotationPointZ;
         this.horseLeftRein.rotateAngleX = var12;
         this.horseRightRein.rotateAngleX = var12;
         this.horseFaceRopes.rotateAngleX = this.head.rotateAngleX;
         this.horseLeftFaceMetal.rotateAngleX = this.head.rotateAngleX;
         this.horseRightFaceMetal.rotateAngleX = this.head.rotateAngleX;
         this.horseFaceRopes.rotateAngleY = this.head.rotateAngleY;
         this.horseLeftFaceMetal.rotateAngleY = this.head.rotateAngleY;
         this.horseLeftRein.rotateAngleY = this.head.rotateAngleY;
         this.horseRightFaceMetal.rotateAngleY = this.head.rotateAngleY;
         this.horseRightRein.rotateAngleY = this.head.rotateAngleY;
         if (var20) {
            this.horseLeftSaddleRope.rotateAngleX = (float) (-Math.PI / 3);
            this.horseLeftSaddleMetal.rotateAngleX = (float) (-Math.PI / 3);
            this.horseRightSaddleRope.rotateAngleX = (float) (-Math.PI / 3);
            this.horseRightSaddleMetal.rotateAngleX = (float) (-Math.PI / 3);
            this.horseLeftSaddleRope.rotateAngleZ = 0.0F;
            this.horseLeftSaddleMetal.rotateAngleZ = 0.0F;
            this.horseRightSaddleRope.rotateAngleZ = 0.0F;
            this.horseRightSaddleMetal.rotateAngleZ = 0.0F;
         } else {
            this.horseLeftSaddleRope.rotateAngleX = var23 / 3.0F;
            this.horseLeftSaddleMetal.rotateAngleX = var23 / 3.0F;
            this.horseRightSaddleRope.rotateAngleX = var23 / 3.0F;
            this.horseRightSaddleMetal.rotateAngleX = var23 / 3.0F;
            this.horseLeftSaddleRope.rotateAngleZ = var23 / 5.0F;
            this.horseLeftSaddleMetal.rotateAngleZ = var23 / 5.0F;
            this.horseRightSaddleRope.rotateAngleZ = -var23 / 5.0F;
            this.horseRightSaddleMetal.rotateAngleZ = -var23 / 5.0F;
         }
      }

      var24 = -1.3089F + var3 * 1.5F;
      if (var24 > 0.0F) {
         var24 = 0.0F;
      }

      if (var18) {
         this.tailBase.rotateAngleY = MathHelper.cos(var21 * 0.7F);
         var24 = 0.0F;
      } else {
         this.tailBase.rotateAngleY = 0.0F;
      }

      this.tailMiddle.rotateAngleY = this.tailBase.rotateAngleY;
      this.tailTip.rotateAngleY = this.tailBase.rotateAngleY;
      this.tailMiddle.rotationPointY = this.tailBase.rotationPointY;
      this.tailTip.rotationPointY = this.tailBase.rotationPointY;
      this.tailMiddle.rotationPointZ = this.tailBase.rotationPointZ;
      this.tailTip.rotationPointZ = this.tailBase.rotationPointZ;
      this.tailBase.rotateAngleX = var24;
      this.tailMiddle.rotateAngleX = var24;
      this.tailTip.rotateAngleX = -0.2618F + var24;
   }
}
