package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import shadersmod.client.Shaders;

public class LayerSpiderEyes implements LayerRenderer<EntitySpider> {
   private static final ResourceLocation SPIDER_EYES = new ResourceLocation("textures/entity/spider_eyes.png");
   private final RenderSpider spiderRenderer;

   public LayerSpiderEyes(RenderSpider spiderRendererIn) {
      this.spiderRenderer = spiderRendererIn;
   }

   public void doRenderLayer(EntitySpider entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      this.spiderRenderer.bindTexture(SPIDER_EYES);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(1, 1);
      if (entitylivingbaseIn.isInvisible()) {
         GlStateManager.depthMask(false);
      } else {
         GlStateManager.depthMask(true);
      }

      char c = '\uf0f0';
      int i = c % 65536;
      int j = c / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i / 1.0F, j / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (Config.isShaders()) {
         Shaders.beginSpiderEyes();
      }

      this.spiderRenderer.getMainModel().render(entitylivingbaseIn, f, g, h, f2, f3, scale);
      int k = entitylivingbaseIn.getBrightnessForRender(partialTicks);
      i = k % 65536;
      j = k / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i / 1.0F, j / 1.0F);
      this.spiderRenderer.setLightmap(entitylivingbaseIn, partialTicks);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
