package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.monster.EntityEnderman;

public class LayerHeldBlock implements LayerRenderer<EntityEnderman> {
   private final RenderEnderman endermanRenderer;
   private static final long a = 129397234309130L;

   public LayerHeldBlock(RenderEnderman var1) {
      this.endermanRenderer = var1;
   }

   public void doRenderLayer(EntityEnderman var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      IBlockState var11 = var1.getHeldBlockState();
      if (var11.getBlock().getMaterial() != Material.air) {
         BlockRendererDispatcher var12 = Minecraft.getMinecraft().getBlockRendererDispatcher();
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.6875F, -0.75F);
         GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.25F, 0.1875F, 0.25F);
         float var13 = 0.5F;
         GlStateManager.scale(-var13, -var13, var13);
         int var14 = var1.getBrightnessForRender(var4);
         int var15 = var14 % 65536;
         int var16 = var14 / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var15 / 1.0F, var16 / 1.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.endermanRenderer.bindTexture(TextureMap.locationBlocksTexture);
         var12.renderBlockBrightness(var11, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
