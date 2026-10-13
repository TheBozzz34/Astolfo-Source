package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelWitch extends ModelVillager {
   public boolean field_82900_g;
   private ModelRenderer field_82901_h = new ModelRenderer(this).setTextureSize(64, 128);
   private ModelRenderer witchHat;
   private static final long b = 39182929443546L;

   public ModelWitch(float var1) {
      super(var1, 0.0F, 64, 128);
      this.field_82901_h.setRotationPoint(0.0F, -2.0F, 0.0F);
      this.field_82901_h.setTextureOffset(0, 0).addBox(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
      this.villagerNose.addChild(this.field_82901_h);
      this.witchHat = new ModelRenderer(this).setTextureSize(64, 128);
      this.witchHat.setRotationPoint(-5.0F, -10.03125F, -5.0F);
      this.witchHat.setTextureOffset(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
      this.villagerHead.addChild(this.witchHat);
      ModelRenderer var4 = new ModelRenderer(this).setTextureSize(64, 128);
      var4.setRotationPoint(1.75F, -4.0F, 2.0F);
      var4.setTextureOffset(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
      var4.rotateAngleX = -0.05235988F;
      var4.rotateAngleZ = 0.02617994F;
      this.witchHat.addChild(var4);
      ModelRenderer var5 = new ModelRenderer(this).setTextureSize(64, 128);
      var5.setRotationPoint(1.75F, -4.0F, 2.0F);
      var5.setTextureOffset(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
      var5.rotateAngleX = -0.10471976F;
      var5.rotateAngleZ = 0.05235988F;
      var4.addChild(var5);
      ModelRenderer var6 = new ModelRenderer(this).setTextureSize(64, 128);
      var6.setRotationPoint(1.75F, -2.0F, 2.0F);
      var6.setTextureOffset(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
      var6.rotateAngleX = (float) (-Math.PI / 15);
      var6.rotateAngleZ = 0.10471976F;
      var5.addChild(var6);
   }

   @Override
   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.setRotationAngles(var1, var2, var3, var4, var5, var6, var7);
      this.villagerNose.offsetX = this.villagerNose.offsetY = this.villagerNose.offsetZ = 0.0F;
      float var11 = 0.01F * (var7.getEntityId() % 10);
      this.villagerNose.rotateAngleX = MathHelper.sin(var7.ticksExisted * var11) * 4.5F * (float) Math.PI / 180.0F;
      this.villagerNose.rotateAngleY = 0.0F;
      this.villagerNose.rotateAngleZ = MathHelper.cos(var7.ticksExisted * var11) * 2.5F * (float) Math.PI / 180.0F;
      if (this.field_82900_g) {
         this.villagerNose.rotateAngleX = -0.9F;
         this.villagerNose.offsetZ = -0.09375F;
         this.villagerNose.offsetY = 0.1875F;
      }
   }
}
