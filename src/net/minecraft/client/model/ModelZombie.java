package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelZombie extends ModelBiped {
   private static final long c = 15740149137335L;

   public ModelZombie() {
      this(0.0F, false);
   }

   public ModelZombie(float var1, float var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public ModelZombie(float var1, boolean var2) {
      super(var1, 0.0F, 64, var2 ? 32 : 64);
   }

   @Override
   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.setRotationAngles(var1, var2, var3, var4, var5, var6, var7);
      float var8 = MathHelper.sin(this.swingProgress * (float) Math.PI);
      float var9 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightArm.rotateAngleY = -(0.1F - var8 * 0.6F);
      this.bipedLeftArm.rotateAngleY = 0.1F - var8 * 0.6F;
      this.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2);
      this.bipedLeftArm.rotateAngleX = (float) (-Math.PI / 2);
      this.bipedRightArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
      this.bipedLeftArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
      this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + (MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F);
      this.bipedLeftArm.rotateAngleZ = this.bipedLeftArm.rotateAngleZ - (MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F);
      this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX + MathHelper.sin(var3 * 0.067F) * 0.05F;
      this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX - MathHelper.sin(var3 * 0.067F) * 0.05F;
   }
}
