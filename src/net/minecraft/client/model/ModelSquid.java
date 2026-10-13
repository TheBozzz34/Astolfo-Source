package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelSquid extends ModelBase {
   public ModelRenderer squidBody;
   public ModelRenderer[] squidTentacles = new ModelRenderer[8];
   private static final long a = 56936205590424L;

   public ModelSquid() {
      byte var4 = -16;
      this.squidBody = new ModelRenderer(this, 0, 0);
      this.squidBody.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
      this.squidBody.rotationPointY += 24 + var4;

      for (int var5 = 0; var5 < this.squidTentacles.length; var5++) {
         this.squidTentacles[var5] = new ModelRenderer(this, 48, 0);
         double var6 = var5 * Math.PI * 2.0 / this.squidTentacles.length;
         float var8 = (float)Math.cos(var6) * 5.0F;
         float var9 = (float)Math.sin(var6) * 5.0F;
         this.squidTentacles[var5].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
         this.squidTentacles[var5].rotationPointX = var8;
         this.squidTentacles[var5].rotationPointZ = var9;
         this.squidTentacles[var5].rotationPointY = 31 + var4;
         var6 = var5 * Math.PI * -2.0 / this.squidTentacles.length + (Math.PI / 2);
         this.squidTentacles[var5].rotateAngleY = (float)var6;
      }
   }

   @Override
   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      for (ModelRenderer var14 : this.squidTentacles) {
         var14.rotateAngleX = var3;
      }
   }

   @Override
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
      this.squidBody.render(var7);

      for (int var11 = 0; var11 < this.squidTentacles.length; var11++) {
         this.squidTentacles[var11].render(var7);
      }
   }
}
