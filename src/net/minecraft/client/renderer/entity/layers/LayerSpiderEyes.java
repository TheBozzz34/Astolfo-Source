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
   private static final long a = 58136076305710L;

   public LayerSpiderEyes(RenderSpider var1) {
      this.spiderRenderer = var1;
   }

   public void doRenderLayer(EntitySpider var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.spiderRenderer.bindTexture(SPIDER_EYES);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(1, 1);
      if (var1.isInvisible()) {
         GlStateManager.depthMask(false);
      } else {
         GlStateManager.depthMask(true);
      }

      char var13 = '\uf0f0';
      int var14 = var13 % 65536;
      int var15 = var13 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var14 / 1.0F, var15 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (Config.isShaders()) {
         Shaders.s();
      }

      this.spiderRenderer.getMainModel().render(var1, var2, var3, var5, var6, var7, var8);
      int var16 = var1.getBrightnessForRender(var4);
      var14 = var16 % 65536;
      var15 = var16 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var14 / 1.0F, var15 / 1.0F);
      this.spiderRenderer.setLightmap(var1, var4);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
