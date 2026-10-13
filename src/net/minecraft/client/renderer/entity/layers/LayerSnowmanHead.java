package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class LayerSnowmanHead implements LayerRenderer<EntitySnowman> {
   private final RenderSnowMan snowManRenderer;
   private static final long a = 38796040581052L;

   public LayerSnowmanHead(RenderSnowMan var1) {
      this.snowManRenderer = var1;
   }

   public void doRenderLayer(EntitySnowman var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.isInvisible()) {
         GlStateManager.pushMatrix();
         this.snowManRenderer.getMainModel().head.postRender(0.0625F);
         float var12 = 0.625F;
         GlStateManager.translate(0.0F, -0.34375F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.scale(var12, -var12, -var12);
         Minecraft.getMinecraft().getItemRenderer().renderItem(var1, new ItemStack(Blocks.pumpkin, 1), ItemCameraTransforms.TransformType.HEAD);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
