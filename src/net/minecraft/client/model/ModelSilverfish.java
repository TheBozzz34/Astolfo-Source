package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSilverfish extends ModelBase {
   private ModelRenderer[] silverfishBodyParts = new ModelRenderer[7];
   private ModelRenderer[] silverfishWings;
   private float[] field_78170_c = new float[7];
   private static final int[][] silverfishBoxLength = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   private static final int[][] silverfishTexturePositions = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};
   private static final long a = 111100839408858L;

   public ModelSilverfish() {
      float var4 = -3.5F;

      for (int var5 = 0; var5 < this.silverfishBodyParts.length; var5++) {
         this.silverfishBodyParts[var5] = new ModelRenderer(this, silverfishTexturePositions[var5][0], silverfishTexturePositions[var5][1]);
         this.silverfishBodyParts[var5]
            .addBox(
               silverfishBoxLength[var5][0] * -0.5F,
               0.0F,
               silverfishBoxLength[var5][2] * -0.5F,
               silverfishBoxLength[var5][0],
               silverfishBoxLength[var5][1],
               silverfishBoxLength[var5][2]
            );
         this.silverfishBodyParts[var5].setRotationPoint(0.0F, 24 - silverfishBoxLength[var5][1], var4);
         this.field_78170_c[var5] = var4;
         if (var5 < this.silverfishBodyParts.length - 1) {
            var4 += (silverfishBoxLength[var5][2] + silverfishBoxLength[var5 + 1][2]) * 0.5F;
         }
      }

      this.silverfishWings = new ModelRenderer[3];
      this.silverfishWings[0] = new ModelRenderer(this, 20, 0);
      this.silverfishWings[0].addBox(-5.0F, 0.0F, silverfishBoxLength[2][2] * -0.5F, 10, 8, silverfishBoxLength[2][2]);
      this.silverfishWings[0].setRotationPoint(0.0F, 16.0F, this.field_78170_c[2]);
      this.silverfishWings[1] = new ModelRenderer(this, 20, 11);
      this.silverfishWings[1].addBox(-3.0F, 0.0F, silverfishBoxLength[4][2] * -0.5F, 6, 4, silverfishBoxLength[4][2]);
      this.silverfishWings[1].setRotationPoint(0.0F, 20.0F, this.field_78170_c[4]);
      this.silverfishWings[2] = new ModelRenderer(this, 20, 18);
      this.silverfishWings[2].addBox(-3.0F, 0.0F, silverfishBoxLength[4][2] * -0.5F, 6, 5, silverfishBoxLength[1][2]);
      this.silverfishWings[2].setRotationPoint(0.0F, 19.0F, this.field_78170_c[1]);
   }

   @Override
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);

      for (int var11 = 0; var11 < this.silverfishBodyParts.length; var11++) {
         this.silverfishBodyParts[var11].render(var7);
      }

      for (int var12 = 0; var12 < this.silverfishWings.length; var12++) {
         this.silverfishWings[var12].render(var7);
      }
   }

   @Override
   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      for (int var11 = 0; var11 < this.silverfishBodyParts.length; var11++) {
         this.silverfishBodyParts[var11].rotateAngleY = MathHelper.cos(var3 * 0.9F + var11 * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.05F
            * (1 + Math.abs(var11 - 2));
         this.silverfishBodyParts[var11].rotationPointX = MathHelper.sin(var3 * 0.9F + var11 * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.2F
            * Math.abs(var11 - 2);
      }

      this.silverfishWings[0].rotateAngleY = this.silverfishBodyParts[2].rotateAngleY;
      this.silverfishWings[1].rotateAngleY = this.silverfishBodyParts[4].rotateAngleY;
      this.silverfishWings[1].rotationPointX = this.silverfishBodyParts[4].rotationPointX;
      this.silverfishWings[2].rotateAngleY = this.silverfishBodyParts[1].rotateAngleY;
      this.silverfishWings[2].rotationPointX = this.silverfishBodyParts[1].rotationPointX;
   }
}
