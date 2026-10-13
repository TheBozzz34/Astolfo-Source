package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import shadersmod.client.Shaders;

public class LayerEnderDragonEyes implements LayerRenderer<EntityDragon> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
   private final RenderDragon dragonRenderer;
   private static final long b = 21489159829862L;

   public LayerEnderDragonEyes(RenderDragon var1) {
      this.dragonRenderer = var1;
   }

   public void doRenderLayer(EntityDragon var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      long var9 = b ^ 93840939186788L;
      this.dragonRenderer.bindTexture(TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(1, 1);
      GlStateManager.disableLighting();
      GlStateManager.depthFunc(514);
      char var12 = '\uf0f0';
      int var13 = var12 % 65536;
      int var14 = var12 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var13 / 1.0F, var14 / 1.0F);
      GlStateManager.enableLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (Config.isShaders()) {
         Shaders.s();
      }

      this.dragonRenderer.getMainModel().render(var1, var2, var3, var5, var6, var7, var8);
      this.dragonRenderer.setLightmap(var1, var4);
      long var10000 = 3730157493441269419L ^ var9;
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.depthFunc(515);
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
