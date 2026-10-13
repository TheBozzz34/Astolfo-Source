package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import shadersmod.client.Shaders;

public class LayerEndermanEyes implements LayerRenderer<EntityEnderman> {
   private static final ResourceLocation RES_ENDERMAN_EYES = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
   private final RenderEnderman endermanRenderer;
   private static final long a = 21112279589448L;

   public LayerEndermanEyes(RenderEnderman var1) {
      this.endermanRenderer = var1;
   }

   public void doRenderLayer(EntityEnderman var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.endermanRenderer.bindTexture(RES_ENDERMAN_EYES);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(1, 1);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(!var1.isInvisible());
      char var12 = '\uf0f0';
      int var13 = var12 % 65536;
      int var14 = var12 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var13 / 1.0F, var14 / 1.0F);
      GlStateManager.enableLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (Config.isShaders()) {
         Shaders.s();
      }

      this.endermanRenderer.getMainModel().render(var1, var2, var3, var5, var6, var7, var8);
      this.endermanRenderer.setLightmap(var1, var4);
      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
