package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelMinecart extends ModelBase {
   public ModelRenderer[] sideModels = new ModelRenderer[7];
   private static final long a = 108610084127670L;

   public ModelMinecart() {
      this.sideModels[0] = new ModelRenderer(this, 0, 10);
      this.sideModels[1] = new ModelRenderer(this, 0, 0);
      this.sideModels[2] = new ModelRenderer(this, 0, 0);
      this.sideModels[3] = new ModelRenderer(this, 0, 0);
      this.sideModels[4] = new ModelRenderer(this, 0, 0);
      this.sideModels[5] = new ModelRenderer(this, 44, 10);
      byte var3 = 20;
      byte var4 = 8;
      byte var5 = 16;
      byte var6 = 4;
      this.sideModels[0].addBox(-var3 / 2, -var5 / 2, -1.0F, var3, var5, 2, 0.0F);
      this.sideModels[0].setRotationPoint(0.0F, var6, 0.0F);
      this.sideModels[5].addBox(-var3 / 2 + 1, -var5 / 2 + 1, -1.0F, var3 - 2, var5 - 2, 1, 0.0F);
      this.sideModels[5].setRotationPoint(0.0F, var6, 0.0F);
      this.sideModels[1].addBox(-var3 / 2 + 2, -var4 - 1, -1.0F, var3 - 4, var4, 2, 0.0F);
      this.sideModels[1].setRotationPoint(-var3 / 2 + 1, var6, 0.0F);
      this.sideModels[2].addBox(-var3 / 2 + 2, -var4 - 1, -1.0F, var3 - 4, var4, 2, 0.0F);
      this.sideModels[2].setRotationPoint(var3 / 2 - 1, var6, 0.0F);
      this.sideModels[3].addBox(-var3 / 2 + 2, -var4 - 1, -1.0F, var3 - 4, var4, 2, 0.0F);
      this.sideModels[3].setRotationPoint(0.0F, var6, -var5 / 2 + 1);
      this.sideModels[4].addBox(-var3 / 2 + 2, -var4 - 1, -1.0F, var3 - 4, var4, 2, 0.0F);
      this.sideModels[4].setRotationPoint(0.0F, var6, var5 / 2 - 1);
      this.sideModels[0].rotateAngleX = (float) (Math.PI / 2);
      this.sideModels[1].rotateAngleY = (float) (Math.PI * 3.0 / 2.0);
      this.sideModels[2].rotateAngleY = (float) (Math.PI / 2);
      this.sideModels[3].rotateAngleY = (float) Math.PI;
      this.sideModels[5].rotateAngleX = (float) (-Math.PI / 2);
   }

   @Override
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.sideModels[5].rotationPointY = 4.0F - var4;

      for (int var11 = 0; var11 < 6; var11++) {
         this.sideModels[var11].render(var7);
      }
   }
}
