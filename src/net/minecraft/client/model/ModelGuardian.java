package net.minecraft.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ModelGuardian extends ModelBase {
   private ModelRenderer guardianBody;
   private ModelRenderer guardianEye;
   private ModelRenderer[] guardianSpines;
   private ModelRenderer[] guardianTail;
   private static final long a = 127669368844850L;

   public ModelGuardian() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.guardianSpines = new ModelRenderer[12];
      this.guardianBody = new ModelRenderer(this);
      this.guardianBody.setTextureOffset(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16);
      this.guardianBody.setTextureOffset(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12);
      this.guardianBody.setTextureOffset(0, 28).addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
      this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12);
      this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);

      for (int var4 = 0; var4 < this.guardianSpines.length; var4++) {
         this.guardianSpines[var4] = new ModelRenderer(this, 0, 0);
         this.guardianSpines[var4].addBox(-1.0F, -4.5F, -1.0F, 2, 9, 2);
         this.guardianBody.addChild(this.guardianSpines[var4]);
      }

      this.guardianEye = new ModelRenderer(this, 8, 0);
      this.guardianEye.addBox(-1.0F, 15.0F, 0.0F, 2, 2, 1);
      this.guardianBody.addChild(this.guardianEye);
      this.guardianTail = new ModelRenderer[3];
      this.guardianTail[0] = new ModelRenderer(this, 40, 0);
      this.guardianTail[0].addBox(-2.0F, 14.0F, 7.0F, 4, 4, 8);
      this.guardianTail[1] = new ModelRenderer(this, 0, 54);
      this.guardianTail[1].addBox(0.0F, 14.0F, 0.0F, 3, 3, 7);
      this.guardianTail[2] = new ModelRenderer(this);
      this.guardianTail[2].setTextureOffset(41, 32).addBox(0.0F, 14.0F, 0.0F, 2, 2, 6);
      this.guardianTail[2].setTextureOffset(25, 19).addBox(1.0F, 10.5F, 3.0F, 1, 9, 9);
      this.guardianBody.addChild(this.guardianTail[0]);
      this.guardianTail[0].addChild(this.guardianTail[1]);
      this.guardianTail[1].addChild(this.guardianTail[2]);
   }

   public int func_178706_a() {
      return 54;
   }

   @Override
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
      this.guardianBody.render(var7);
   }

   @Override
   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      EntityGuardian var11 = (EntityGuardian)var7;
      float var12 = var3 - var11.ticksExisted;
      this.guardianBody.rotateAngleY = var4 / (180.0F / (float)Math.PI);
      this.guardianBody.rotateAngleX = var5 / (180.0F / (float)Math.PI);
      float[] var13 = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
      float[] var14 = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
      float[] var15 = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
      float[] var16 = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
      float[] var17 = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
      float[] var18 = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
      float var19 = (1.0F - var11.func_175469_o(var12)) * 0.55F;

      for (int var20 = 0; var20 < 12; var20++) {
         this.guardianSpines[var20].rotateAngleX = (float) Math.PI * var13[var20];
         this.guardianSpines[var20].rotateAngleY = (float) Math.PI * var14[var20];
         this.guardianSpines[var20].rotateAngleZ = (float) Math.PI * var15[var20];
         this.guardianSpines[var20].rotationPointX = var16[var20] * (1.0F + MathHelper.cos(var3 * 1.5F + var20) * 0.01F - var19);
         this.guardianSpines[var20].rotationPointY = 16.0F + var17[var20] * (1.0F + MathHelper.cos(var3 * 1.5F + var20) * 0.01F - var19);
         this.guardianSpines[var20].rotationPointZ = var18[var20] * (1.0F + MathHelper.cos(var3 * 1.5F + var20) * 0.01F - var19);
      }

      this.guardianEye.rotationPointZ = -8.25F;
      Entity var29 = Minecraft.getMinecraft().getRenderViewEntity();
      if (var11.hasTargetedEntity()) {
         var29 = var11.getTargetedEntity();
      }

      if (var29 != null) {
         Vec3 var21 = var29.getPositionEyes(0.0F);
         Vec3 var22 = var7.getPositionEyes(0.0F);
         double var23 = var21.yCoord - var22.yCoord;
         if (var23 > 0.0) {
            this.guardianEye.rotationPointY = 0.0F;
         } else {
            this.guardianEye.rotationPointY = 1.0F;
         }

         Vec3 var25 = var7.getLook(0.0F);
         var25 = new Vec3(var25.xCoord, 0.0, var25.zCoord);
         Vec3 var26 = new Vec3(var22.xCoord - var21.xCoord, 0.0, var22.zCoord - var21.zCoord).normalize().rotateYaw((float) (Math.PI / 2));
         double var27 = var25.dotProduct(var26);
         this.guardianEye.rotationPointX = MathHelper.sqrt_float((float)Math.abs(var27)) * 2.0F * (float)Math.signum(var27);
      }

      this.guardianEye.showModel = true;
      float var30 = var11.func_175471_a(var12);
      this.guardianTail[0].rotateAngleY = MathHelper.sin(var30) * (float) Math.PI * 0.05F;
      this.guardianTail[1].rotateAngleY = MathHelper.sin(var30) * (float) Math.PI * 0.1F;
      this.guardianTail[1].rotationPointX = -1.5F;
      this.guardianTail[1].rotationPointY = 0.5F;
      this.guardianTail[1].rotationPointZ = 14.0F;
      this.guardianTail[2].rotateAngleY = MathHelper.sin(var30) * (float) Math.PI * 0.15F;
      this.guardianTail[2].rotationPointX = 0.5F;
      this.guardianTail[2].rotationPointY = 0.5F;
      this.guardianTail[2].rotationPointZ = 6.0F;
   }
}
