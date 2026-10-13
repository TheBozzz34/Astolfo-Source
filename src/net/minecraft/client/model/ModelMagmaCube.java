package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;

public class ModelMagmaCube extends ModelBase {
   public ModelRenderer[] segments = new ModelRenderer[8];
   public ModelRenderer core;
   private static final long a = 59114420738080L;

   public ModelMagmaCube() {
      for (int var4 = 0; var4 < this.segments.length; var4++) {
         byte var5 = 0;
         int var6 = var4;
         if (var4 == 2) {
            var5 = 24;
            var6 = 10;
         } else if (var4 == 3) {
            var5 = 24;
            var6 = 19;
         }

         this.segments[var4] = new ModelRenderer(this, var5, var6);
         this.segments[var4].addBox(-4.0F, 16 + var4, -4.0F, 8, 1, 8);
      }

      this.core = new ModelRenderer(this, 0, 16);
      this.core.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
   }

   @Override
   public void setLivingAnimations(EntityLivingBase var1, float var2, float var3, float var4) {
      EntityMagmaCube var8 = (EntityMagmaCube)var1;
      float var9 = var8.prevSquishFactor + (var8.squishFactor - var8.prevSquishFactor) * var4;
      if (var9 < 0.0F) {
         var9 = 0.0F;
      }

      for (int var10 = 0; var10 < this.segments.length; var10++) {
         this.segments[var10].rotationPointY = -(4 - var10) * var9 * 1.7F;
      }
   }

   @Override
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
      this.core.render(var7);

      for (int var11 = 0; var11 < this.segments.length; var11++) {
         this.segments[var11].render(var7);
      }
   }
}
