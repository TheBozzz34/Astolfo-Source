package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;

public class ModelWither extends ModelBase {
   private ModelRenderer[] field_82905_a;
   private ModelRenderer[] field_82904_b;

   public ModelWither(float f) {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.field_82905_a = new ModelRenderer[3];
      this.field_82905_a[0] = new ModelRenderer(this, 0, 16);
      this.field_82905_a[0].addBox(-10.0F, 3.9F, -0.5F, 20, 3, 3, f);
      this.field_82905_a[1] = new ModelRenderer(this).setTextureSize(this.textureWidth, this.textureHeight);
      this.field_82905_a[1].setRotationPoint(-2.0F, 6.9F, -0.5F);
      this.field_82905_a[1].setTextureOffset(0, 22).addBox(0.0F, 0.0F, 0.0F, 3, 10, 3, f);
      this.field_82905_a[1].setTextureOffset(24, 22).addBox(-4.0F, 1.5F, 0.5F, 11, 2, 2, f);
      this.field_82905_a[1].setTextureOffset(24, 22).addBox(-4.0F, 4.0F, 0.5F, 11, 2, 2, f);
      this.field_82905_a[1].setTextureOffset(24, 22).addBox(-4.0F, 6.5F, 0.5F, 11, 2, 2, f);
      this.field_82905_a[2] = new ModelRenderer(this, 12, 22);
      this.field_82905_a[2].addBox(0.0F, 0.0F, 0.0F, 3, 6, 3, f);
      this.field_82904_b = new ModelRenderer[3];
      this.field_82904_b[0] = new ModelRenderer(this, 0, 0);
      this.field_82904_b[0].addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, f);
      this.field_82904_b[1] = new ModelRenderer(this, 32, 0);
      this.field_82904_b[1].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
      this.field_82904_b[1].rotationPointX = -8.0F;
      this.field_82904_b[1].rotationPointY = 4.0F;
      this.field_82904_b[2] = new ModelRenderer(this, 32, 0);
      this.field_82904_b[2].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
      this.field_82904_b[2].rotationPointX = 10.0F;
      this.field_82904_b[2].rotationPointY = 4.0F;
   }

   @Override
   public void render(Entity entityIn, float f, float g, float h, float f2, float f3, float scale) {
      this.setRotationAngles(f, g, h, f2, f3, scale, entityIn);

      for (ModelRenderer modelRenderer : this.field_82904_b) {
         modelRenderer.render(scale);
      }

      for (ModelRenderer modelRenderer2 : this.field_82905_a) {
         modelRenderer2.render(scale);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      float f = MathHelper.cos(ageInTicks * 0.1F);
      this.field_82905_a[1].rotateAngleX = (0.065F + 0.05F * f) * (float) Math.PI;
      this.field_82905_a[2]
         .setRotationPoint(
            -2.0F, 6.9F + MathHelper.cos(this.field_82905_a[1].rotateAngleX) * 10.0F, -0.5F + MathHelper.sin(this.field_82905_a[1].rotateAngleX) * 10.0F
         );
      this.field_82905_a[2].rotateAngleX = (0.265F + 0.1F * f) * (float) Math.PI;
      this.field_82904_b[0].rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.field_82904_b[0].rotateAngleX = headPitch / (180.0F / (float)Math.PI);
   }

   @Override
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float f, float g, float partialTickTime) {
      EntityWither entityWither = (EntityWither)entitylivingbaseIn;

      for (int i = 1; i < 3; i++) {
         this.field_82904_b[i].rotateAngleY = (entityWither.func_82207_a(i - 1) - entitylivingbaseIn.renderYawOffset) / (180.0F / (float)Math.PI);
         this.field_82904_b[i].rotateAngleX = entityWither.func_82210_r(i - 1) / (180.0F / (float)Math.PI);
      }
   }
}
