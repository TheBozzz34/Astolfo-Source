package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;

public class ModelMagmaCube extends ModelBase {
   public ModelRenderer[] segments = new ModelRenderer[8];
   public ModelRenderer core;
   private static final long a = 59114420738080L;

   public ModelMagmaCube() {
      for (int i = 0; i < this.segments.length; i++) {
         byte b = 0;
         int j = i;
         if (i == 2) {
            b = 24;
            j = 10;
         } else if (i == 3) {
            b = 24;
            j = 19;
         }

         this.segments[i] = new ModelRenderer(this, b, j);
         this.segments[i].addBox(-4.0F, 16 + i, -4.0F, 8, 1, 8);
      }

      this.core = new ModelRenderer(this, 0, 16);
      this.core.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
   }

   @Override
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float f, float g, float partialTickTime) {
      EntityMagmaCube entityMagmaCube = (EntityMagmaCube)entitylivingbaseIn;
      float h = entityMagmaCube.prevSquishFactor + (entityMagmaCube.squishFactor - entityMagmaCube.prevSquishFactor) * partialTickTime;
      if (h < 0.0F) {
         h = 0.0F;
      }

      for (int i = 0; i < this.segments.length; i++) {
         this.segments[i].rotationPointY = -(4 - i) * h * 1.7F;
      }
   }

   @Override
   public void render(Entity entityIn, float f, float g, float h, float f2, float f3, float scale) {
      this.setRotationAngles(f, g, h, f2, f3, scale, entityIn);
      this.core.render(scale);

      for (int i = 0; i < this.segments.length; i++) {
         this.segments[i].render(scale);
      }
   }
}
