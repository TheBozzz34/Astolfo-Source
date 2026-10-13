package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBlaze extends ModelBase {
   private ModelRenderer[] blazeSticks = new ModelRenderer[12];
   private ModelRenderer blazeHead;
   private static final long b = 109507654920530L;

   public ModelBlaze() {
      for (int var4 = 0; var4 < this.blazeSticks.length; var4++) {
         this.blazeSticks[var4] = new ModelRenderer(this, 0, 16);
         this.blazeSticks[var4].addBox(0.0F, 0.0F, 0.0F, 2, 8, 2);
      }

      this.blazeHead = new ModelRenderer(this, 0, 0);
      this.blazeHead.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
   }

   @Override
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
      this.blazeHead.render(var7);

      for (int var11 = 0; var11 < this.blazeSticks.length; var11++) {
         this.blazeSticks[var11].render(var7);
      }
   }

   @Override
   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      float var11 = var3 * (float) Math.PI * -0.1F;

      for (int var12 = 0; var12 < 4; var12++) {
         this.blazeSticks[var12].rotationPointY = -2.0F + MathHelper.cos((var12 * 2 + var3) * 0.25F);
         this.blazeSticks[var12].rotationPointX = MathHelper.cos(var11) * 9.0F;
         this.blazeSticks[var12].rotationPointZ = MathHelper.sin(var11) * 9.0F;
         var11++;
      }

      var11 = (float) (Math.PI / 4) + var3 * (float) Math.PI * 0.03F;

      for (int var15 = 4; var15 < 8; var15++) {
         this.blazeSticks[var15].rotationPointY = 2.0F + MathHelper.cos((var15 * 2 + var3) * 0.25F);
         this.blazeSticks[var15].rotationPointX = MathHelper.cos(var11) * 7.0F;
         this.blazeSticks[var15].rotationPointZ = MathHelper.sin(var11) * 7.0F;
         var11++;
      }

      var11 = 0.47123894F + var3 * (float) Math.PI * -0.05F;

      for (int var16 = 8; var16 < 12; var16++) {
         this.blazeSticks[var16].rotationPointY = 11.0F + MathHelper.cos((var16 * 1.5F + var3) * 0.5F);
         this.blazeSticks[var16].rotationPointX = MathHelper.cos(var11) * 5.0F;
         this.blazeSticks[var16].rotationPointZ = MathHelper.sin(var11) * 5.0F;
         var11++;
      }

      this.blazeHead.rotateAngleY = var4 / (180.0F / (float)Math.PI);
      this.blazeHead.rotateAngleX = var5 / (180.0F / (float)Math.PI);
   }
}
