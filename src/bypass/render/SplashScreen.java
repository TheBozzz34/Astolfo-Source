package bypass.render;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class SplashScreen {
   private static final int MAX_PROGRESS = 7;
   public static int progress;
   private static TextureManager textureManager;
   private static ResourceLocation splashTexture;

   public static void render(TextureManager textureManager2) {
      if (textureManager == null) {
         textureManager = textureManager2;
      }

      ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
      int i = scaledResolution.getScaleFactor();
      Framebuffer framebuffer = new Framebuffer(scaledResolution.getScaledWidth() * i, scaledResolution.getScaledHeight() * i, true);
      framebuffer.bindFramebuffer(false);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      GlStateManager.disableDepth();
      GlStateManager.enableTexture2D();
      if (splashTexture == null) {
         splashTexture = new ResourceLocation("client/splash.png");
      }

      textureManager2.bindTexture(splashTexture);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawScaledCustomSizeModalRect(0, 0, 0.0F, 0.0F, 1920, 1080, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 1920.0F, 1080.0F);
      drawProgressBar();
      framebuffer.unbindFramebuffer();
      framebuffer.framebufferRender(scaledResolution.getScaledWidth() * i, scaledResolution.getScaledHeight() * i);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      Minecraft.getMinecraft().updateDisplay();
   }

   public static void redraw() {
      if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().getLanguageManager() != null) {
         render(Minecraft.getMinecraft().getTextureManager());
      }
   }

   public static void setProgress(int i) {
      progress = i;
      redraw();
   }

   private static void drawProgressBar() {
      if (Minecraft.getMinecraft().gameSettings != null && Minecraft.getMinecraft().getTextureManager() != null) {
         ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
         double d = progress;
         int i = scaledResolution.getScaledWidth() / 2;
         int j = scaledResolution.getScaledHeight() / 2;
         int k = i - 45;
         int l = i + 45;
         double e = k + (l - k) * (d / 7.0 * 100.0 * 0.01F);
         Gui.drawRect(k, j - 5, l, j + 5, new Color(0, 0, 0, 50).getRGB());
         Gui.drawRect(k, j - 5, (int)e, j + 5, new Color(167, 120, 255, 247).getRGB());
      }
   }
}
