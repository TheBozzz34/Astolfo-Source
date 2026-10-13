package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelBoat extends ModelBase {
   public ModelRenderer[] boatSides = new ModelRenderer[5];
   private static final long a = 129750229137508L;

   public ModelBoat() {
      this.boatSides[0] = new ModelRenderer(this, 0, 8);
      this.boatSides[1] = new ModelRenderer(this, 0, 0);
      this.boatSides[2] = new ModelRenderer(this, 0, 0);
      this.boatSides[3] = new ModelRenderer(this, 0, 0);
      this.boatSides[4] = new ModelRenderer(this, 0, 0);
      byte var3 = 24;
      byte var4 = 6;
      byte var5 = 20;
      byte var6 = 4;
      this.boatSides[0].addBox(-var3 / 2, -var5 / 2 + 2, -3.0F, var3, var5 - 4, 4, 0.0F);
      this.boatSides[0].setRotationPoint(0.0F, var6, 0.0F);
      this.boatSides[1].addBox(-var3 / 2 + 2, -var4 - 1, -1.0F, var3 - 4, var4, 2, 0.0F);
      this.boatSides[1].setRotationPoint(-var3 / 2 + 1, var6, 0.0F);
      this.boatSides[2].addBox(-var3 / 2 + 2, -var4 - 1, -1.0F, var3 - 4, var4, 2, 0.0F);
      this.boatSides[2].setRotationPoint(var3 / 2 - 1, var6, 0.0F);
      this.boatSides[3].addBox(-var3 / 2 + 2, -var4 - 1, -1.0F, var3 - 4, var4, 2, 0.0F);
      this.boatSides[3].setRotationPoint(0.0F, var6, -var5 / 2 + 1);
      this.boatSides[4].addBox(-var3 / 2 + 2, -var4 - 1, -1.0F, var3 - 4, var4, 2, 0.0F);
      this.boatSides[4].setRotationPoint(0.0F, var6, var5 / 2 - 1);
      this.boatSides[0].rotateAngleX = (float) (Math.PI / 2);
      this.boatSides[1].rotateAngleY = (float) (Math.PI * 3.0 / 2.0);
      this.boatSides[2].rotateAngleY = (float) (Math.PI / 2);
      this.boatSides[3].rotateAngleY = (float) Math.PI;
   }

   @Override
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      for (int var11 = 0; var11 < 5; var11++) {
         this.boatSides[var11].render(var7);
      }
   }
}
