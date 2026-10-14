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

   public LayerArrow(RendererLivingEntity rendererLivingEntity) {
      this.field_177168_a = rendererLivingEntity;
   }

   @Override
   public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      int i = entitylivingbaseIn.getArrowCountInEntity();
      if (i > 0) {
         EntityArrow entityArrow = new EntityArrow(entitylivingbaseIn.worldObj, entitylivingbaseIn.posX, entitylivingbaseIn.posY, entitylivingbaseIn.posZ);
         Random random = new Random(entitylivingbaseIn.getEntityId());
         RenderHelper.disableStandardItemLighting();

         for (int j = 0; j < i; j++) {
            GlStateManager.pushMatrix();
            ModelRenderer modelRenderer = this.field_177168_a.getMainModel().getRandomModelBox(random);
            ModelBox modelBox = modelRenderer.cubeList.get(random.nextInt(modelRenderer.cubeList.size()));
            modelRenderer.postRender(0.0625F);
            float f4 = random.nextFloat();
            float f5 = random.nextFloat();
            float f6 = random.nextFloat();
            float f7 = (modelBox.posX1 + (modelBox.posX2 - modelBox.posX1) * f4) / 16.0F;
            float f8 = (modelBox.posY1 + (modelBox.posY2 - modelBox.posY1) * f5) / 16.0F;
            float f9 = (modelBox.posZ1 + (modelBox.posZ2 - modelBox.posZ1) * f6) / 16.0F;
            GlStateManager.translate(f7, f8, f9);
            f4 = f4 * 2.0F - 1.0F;
            f5 = f5 * 2.0F - 1.0F;
            f6 = f6 * 2.0F - 1.0F;
            f4 *= -1.0F;
            f5 *= -1.0F;
            f6 *= -1.0F;
            float f10 = MathHelper.sqrt_float(f4 * f4 + f6 * f6);
            entityArrow.prevRotationYaw = entityArrow.rotationYaw = (float)(Math.atan2(f4, f6) * 180.0 / Math.PI);
            entityArrow.prevRotationPitch = entityArrow.rotationPitch = (float)(Math.atan2(f5, f10) * 180.0 / Math.PI);
            double d = 0.0;
            double e = 0.0;
            double d2 = 0.0;
            this.field_177168_a.getRenderManager().renderEntityWithPosYaw(entityArrow, d, e, d2, 0.0F, partialTicks);
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
