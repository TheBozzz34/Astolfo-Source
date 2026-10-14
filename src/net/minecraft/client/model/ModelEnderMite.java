package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelEnderMite extends ModelBase {
   private static final int[][] field_178716_a = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
   private static final int[][] field_178714_b = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
   private static final int field_178715_c = field_178716_a.length;
   private final ModelRenderer[] field_178713_d = new ModelRenderer[field_178715_c];

   public ModelEnderMite() {
      float f = -3.5F;

      for (int i = 0; i < this.field_178713_d.length; i++) {
         this.field_178713_d[i] = new ModelRenderer(this, field_178714_b[i][0], field_178714_b[i][1]);
         this.field_178713_d[i]
            .addBox(
               field_178716_a[i][0] * -0.5F,
               0.0F,
               field_178716_a[i][2] * -0.5F,
               field_178716_a[i][0],
               field_178716_a[i][1],
               field_178716_a[i][2]
            );
         this.field_178713_d[i].setRotationPoint(0.0F, 24 - field_178716_a[i][1], f);
         if (i < this.field_178713_d.length - 1) {
            f += (field_178716_a[i][2] + field_178716_a[i + 1][2]) * 0.5F;
         }
      }
   }

   @Override
   public void render(Entity entityIn, float f, float g, float h, float f2, float f3, float scale) {
      this.setRotationAngles(f, g, h, f2, f3, scale, entityIn);

      for (int i = 0; i < this.field_178713_d.length; i++) {
         this.field_178713_d[i].render(scale);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      for (int i = 0; i < this.field_178713_d.length; i++) {
         this.field_178713_d[i].rotateAngleY = MathHelper.cos(ageInTicks * 0.9F + i * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.01F
            * (1 + Math.abs(i - 2));
         this.field_178713_d[i].rotationPointX = MathHelper.sin(ageInTicks * 0.9F + i * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.1F
            * Math.abs(i - 2);
      }
   }
}
