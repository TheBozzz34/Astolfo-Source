package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;

public class LayerArrow implements LayerRenderer<EntityLivingBase> {
   private final RendererLivingEntity field_177168_a;
   private static final long a = 109437198992812L;

   public LayerArrow(RendererLivingEntity var1) {
      this.field_177168_a = var1;
   }

   @Override
   public void doRenderLayer(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      int var12 = var1.getArrowCountInEntity();
      if (var12 > 0) {
         EntityArrow var13 = new EntityArrow(var1.worldObj, var1.posX, var1.posY, var1.posZ);
         Random var14 = new Random(var1.getEntityId());
         RenderHelper.disableStandardItemLighting();

         for (int var15 = 0; var15 < var12; var15++) {
            GlStateManager.pushMatrix();
            ModelRenderer var16 = this.field_177168_a.getMainModel().getRandomModelBox(var14);
            ModelBox var17 = var16.cubeList.get(var14.nextInt(var16.cubeList.size()));
            var16.postRender(0.0625F);
            float var18 = var14.nextFloat();
            float var19 = var14.nextFloat();
            float var20 = var14.nextFloat();
            float var21 = (var17.posX1 + (var17.posX2 - var17.posX1) * var18) / 16.0F;
            float var22 = (var17.posY1 + (var17.posY2 - var17.posY1) * var19) / 16.0F;
            float var23 = (var17.posZ1 + (var17.posZ2 - var17.posZ1) * var20) / 16.0F;
            GlStateManager.translate(var21, var22, var23);
            var18 = var18 * 2.0F - 1.0F;
            var19 = var19 * 2.0F - 1.0F;
            var20 = var20 * 2.0F - 1.0F;
            var18 *= -1.0F;
            var19 *= -1.0F;
            var20 *= -1.0F;
            float var24 = MathHelper.sqrt_float(var18 * var18 + var20 * var20);
            var13.prevRotationYaw = var13.rotationYaw = (float)(Math.atan2(var18, var20) * 180.0 / Math.PI);
            var13.prevRotationPitch = var13.rotationPitch = (float)(Math.atan2(var19, var24) * 180.0 / Math.PI);
            double var25 = 0.0;
            double var27 = 0.0;
            double var29 = 0.0;
            this.field_177168_a.getRenderManager().renderEntityWithPosYaw(var13, var25, var27, var29, 0.0F, var4);
            GlStateManager.popMatrix();
         }

         RenderHelper.enableStandardItemLighting();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
