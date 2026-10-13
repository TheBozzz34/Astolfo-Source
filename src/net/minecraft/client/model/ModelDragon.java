package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;

public class ModelDragon extends ModelBase {
   private ModelRenderer head;
   private ModelRenderer spine;
   private ModelRenderer jaw;
   private ModelRenderer body;
   private ModelRenderer rearLeg;
   private ModelRenderer frontLeg;
   private ModelRenderer rearLegTip;
   private ModelRenderer frontLegTip;
   private ModelRenderer rearFoot;
   private ModelRenderer frontFoot;
   private ModelRenderer wing;
   private ModelRenderer wingTip;
   private float partialTicks;
   private static final long c = 22390894328198L;

   public ModelDragon(float var1) {
      this.textureWidth = 256;
      this.textureHeight = 256;
      this.setTextureOffset("body.body", 0, 0);
      this.setTextureOffset("wing.skin", -56, 88);
      this.setTextureOffset("wingtip.skin", -56, 144);
      this.setTextureOffset("rearleg.main", 0, 0);
      this.setTextureOffset("rearfoot.main", 112, 0);
      this.setTextureOffset("rearlegtip.main", 196, 0);
      this.setTextureOffset("head.upperhead", 112, 30);
      this.setTextureOffset("wing.bone", 112, 88);
      this.setTextureOffset("head.upperlip", 176, 44);
      this.setTextureOffset("jaw.jaw", 176, 65);
      this.setTextureOffset("frontleg.main", 112, 104);
      this.setTextureOffset("wingtip.bone", 112, 136);
      this.setTextureOffset("frontfoot.main", 144, 104);
      this.setTextureOffset("neck.box", 192, 104);
      this.setTextureOffset("frontlegtip.main", 226, 138);
      this.setTextureOffset("body.scale", 220, 53);
      this.setTextureOffset("head.scale", 0, 0);
      this.setTextureOffset("neck.scale", 48, 0);
      this.setTextureOffset("head.nostril", 112, 0);
      float var4 = -16.0F;
      this.head = new ModelRenderer(this, "head");
      this.head.addBox("upperlip", -6.0F, -1.0F, -8.0F + var4, 12, 5, 16);
      this.head.addBox("upperhead", -8.0F, -8.0F, 6.0F + var4, 16, 16, 16);
      this.head.mirror = true;
      this.head.addBox("scale", -5.0F, -12.0F, 12.0F + var4, 2, 4, 6);
      this.head.addBox("nostril", -5.0F, -3.0F, -6.0F + var4, 2, 2, 4);
      this.head.mirror = false;
      this.head.addBox("scale", 3.0F, -12.0F, 12.0F + var4, 2, 4, 6);
      this.head.addBox("nostril", 3.0F, -3.0F, -6.0F + var4, 2, 2, 4);
      this.jaw = new ModelRenderer(this, "jaw");
      this.jaw.setRotationPoint(0.0F, 4.0F, 8.0F + var4);
      this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16);
      this.head.addChild(this.jaw);
      this.spine = new ModelRenderer(this, "neck");
      this.spine.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10);
      this.spine.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6);
      this.body = new ModelRenderer(this, "body");
      this.body.setRotationPoint(0.0F, 4.0F, 8.0F);
      this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64);
      this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12);
      this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12);
      this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12);
      this.wing = new ModelRenderer(this, "wing");
      this.wing.setRotationPoint(-12.0F, 5.0F, 2.0F);
      this.wing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8);
      this.wing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
      this.wingTip = new ModelRenderer(this, "wingtip");
      this.wingTip.setRotationPoint(-56.0F, 0.0F, 0.0F);
      this.wingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4);
      this.wingTip.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
      this.wing.addChild(this.wingTip);
      this.frontLeg = new ModelRenderer(this, "frontleg");
      this.frontLeg.setRotationPoint(-12.0F, 20.0F, 2.0F);
      this.frontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8);
      this.frontLegTip = new ModelRenderer(this, "frontlegtip");
      this.frontLegTip.setRotationPoint(0.0F, 20.0F, -1.0F);
      this.frontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6);
      this.frontLeg.addChild(this.frontLegTip);
      this.frontFoot = new ModelRenderer(this, "frontfoot");
      this.frontFoot.setRotationPoint(0.0F, 23.0F, 0.0F);
      this.frontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16);
      this.frontLegTip.addChild(this.frontFoot);
      this.rearLeg = new ModelRenderer(this, "rearleg");
      this.rearLeg.setRotationPoint(-16.0F, 16.0F, 42.0F);
      this.rearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16);
      this.rearLegTip = new ModelRenderer(this, "rearlegtip");
      this.rearLegTip.setRotationPoint(0.0F, 32.0F, -4.0F);
      this.rearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12);
      this.rearLeg.addChild(this.rearLegTip);
      this.rearFoot = new ModelRenderer(this, "rearfoot");
      this.rearFoot.setRotationPoint(0.0F, 31.0F, 4.0F);
      this.rearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24);
      this.rearLegTip.addChild(this.rearFoot);
   }

   @Override
   public void setLivingAnimations(EntityLivingBase var1, float var2, float var3, float var4) {
      this.partialTicks = var4;
   }

   @Override
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.pushMatrix();
      EntityDragon var11 = (EntityDragon)var1;
      float var12 = var11.prevAnimTime + (var11.animTime - var11.prevAnimTime) * this.partialTicks;
      this.jaw.rotateAngleX = (float)(Math.sin(var12 * (float) Math.PI * 2.0F) + 1.0) * 0.2F;
      float var13 = (float)(Math.sin(var12 * (float) Math.PI * 2.0F - 1.0F) + 1.0);
      var13 = (var13 * var13 * 1.0F + var13 * 2.0F) * 0.05F;
      GlStateManager.translate(0.0F, var13 - 2.0F, -3.0F);
      GlStateManager.rotate(var13 * 2.0F, 1.0F, 0.0F, 0.0F);
      float var14 = -30.0F;
      float var15 = 0.0F;
      float var16 = 1.5F;
      double[] var17 = var11.getMovementOffsets(6, this.partialTicks);
      float var18 = this.updateRotations(var11.getMovementOffsets(5, this.partialTicks)[0] - var11.getMovementOffsets(10, this.partialTicks)[0]);
      float var19 = this.updateRotations(var11.getMovementOffsets(5, this.partialTicks)[0] + var18 / 2.0F);
      var14 += 2.0F;
      float var20 = var12 * (float) Math.PI * 2.0F;
      var14 = 20.0F;
      float var21 = -12.0F;

      for (int var22 = 0; var22 < 5; var22++) {
         double[] var23 = var11.getMovementOffsets(5 - var22, this.partialTicks);
         float var24 = (float)Math.cos(var22 * 0.45F + var20) * 0.15F;
         this.spine.rotateAngleY = this.updateRotations(var23[0] - var17[0]) * (float) Math.PI / 180.0F * var16;
         this.spine.rotateAngleX = var24 + (float)(var23[1] - var17[1]) * (float) Math.PI / 180.0F * var16 * 5.0F;
         this.spine.rotateAngleZ = -this.updateRotations(var23[0] - var19) * (float) Math.PI / 180.0F * var16;
         this.spine.rotationPointY = var14;
         this.spine.rotationPointZ = var21;
         this.spine.rotationPointX = var15;
         var14 = (float)(var14 + Math.sin(this.spine.rotateAngleX) * 10.0);
         var21 = (float)(var21 - Math.cos(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
         var15 = (float)(var15 - Math.sin(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
         this.spine.render(var7);
      }

      this.head.rotationPointY = var14;
      this.head.rotationPointZ = var21;
      this.head.rotationPointX = var15;
      double[] var33 = var11.getMovementOffsets(0, this.partialTicks);
      this.head.rotateAngleY = this.updateRotations(var33[0] - var17[0]) * (float) Math.PI / 180.0F * 1.0F;
      this.head.rotateAngleZ = -this.updateRotations(var33[0] - var19) * (float) Math.PI / 180.0F * 1.0F;
      this.head.render(var7);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-var18 * var16 * 1.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(0.0F, -1.0F, 0.0F);
      this.body.rotateAngleZ = 0.0F;
      this.body.render(var7);

      for (int var35 = 0; var35 < 2; var35++) {
         GlStateManager.enableCull();
         float var37 = var12 * (float) Math.PI * 2.0F;
         this.wing.rotateAngleX = 0.125F - (float)Math.cos(var37) * 0.2F;
         this.wing.rotateAngleY = 0.25F;
         this.wing.rotateAngleZ = (float)(Math.sin(var37) + 0.125) * 0.8F;
         this.wingTip.rotateAngleZ = -((float)(Math.sin(var37 + 2.0F) + 0.5)) * 0.75F;
         this.rearLeg.rotateAngleX = 1.0F + var13 * 0.1F;
         this.rearLegTip.rotateAngleX = 0.5F + var13 * 0.1F;
         this.rearFoot.rotateAngleX = 0.75F + var13 * 0.1F;
         this.frontLeg.rotateAngleX = 1.3F + var13 * 0.1F;
         this.frontLegTip.rotateAngleX = -0.5F - var13 * 0.1F;
         this.frontFoot.rotateAngleX = 0.75F + var13 * 0.1F;
         this.wing.render(var7);
         this.frontLeg.render(var7);
         this.rearLeg.render(var7);
         GlStateManager.scale(-1.0F, 1.0F, 1.0F);
         if (var35 == 0) {
            GlStateManager.cullFace(1028);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.cullFace(1029);
      GlStateManager.disableCull();
      float var36 = -((float)Math.sin(var12 * (float) Math.PI * 2.0F)) * 0.0F;
      var20 = var12 * (float) Math.PI * 2.0F;
      var14 = 10.0F;
      var21 = 60.0F;
      var15 = 0.0F;
      var17 = var11.getMovementOffsets(11, this.partialTicks);

      for (int var38 = 0; var38 < 12; var38++) {
         var33 = var11.getMovementOffsets(12 + var38, this.partialTicks);
         var36 = (float)(var36 + Math.sin(var38 * 0.45F + var20) * 0.05F);
         this.spine.rotateAngleY = (this.updateRotations(var33[0] - var17[0]) * var16 + 180.0F) * (float) Math.PI / 180.0F;
         this.spine.rotateAngleX = var36 + (float)(var33[1] - var17[1]) * (float) Math.PI / 180.0F * var16 * 5.0F;
         this.spine.rotateAngleZ = this.updateRotations(var33[0] - var19) * (float) Math.PI / 180.0F * var16;
         this.spine.rotationPointY = var14;
         this.spine.rotationPointZ = var21;
         this.spine.rotationPointX = var15;
         var14 = (float)(var14 + Math.sin(this.spine.rotateAngleX) * 10.0);
         var21 = (float)(var21 - Math.cos(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
         var15 = (float)(var15 - Math.sin(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
         this.spine.render(var7);
      }

      GlStateManager.popMatrix();
   }

   private float updateRotations(double var1) {
      while (var1 >= 180.0) {
         var1 -= 360.0;
      }

      while (var1 < -180.0) {
         var1 += 360.0;
      }

      return (float)var1;
   }
}
