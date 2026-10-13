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
   private static final int M = 7;
   public static int N;
   private static TextureManager k;
   private static ResourceLocation T;
   private static final long a = 94544970112253L;

   public static void h(TextureManager var0) {
      long var1 = a ^ 4908631775905L;
      if (k == null) {
         k = var0;
      }

      ScaledResolution var4 = new ScaledResolution(Minecraft.getMinecraft());
      int var5 = var4.getScaleFactor();
      Framebuffer var6 = new Framebuffer(var4.getScaledWidth() * var5, var4.getScaledHeight() * var5, true);
      var6.bindFramebuffer(false);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      long var10000 = 2036982545121047278L ^ var1;
      GlStateManager.ortho(0.0, var4.getScaledWidth(), var4.getScaledHeight(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      GlStateManager.disableDepth();
      GlStateManager.enableTexture2D();
      if (T == null) {
         T = new ResourceLocation("client/splash.png");
      }

      var0.bindTexture(T);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawScaledCustomSizeModalRect(0, 0, 0.0F, 0.0F, 1920, 1080, var4.getScaledWidth(), var4.getScaledHeight(), 1920.0F, 1080.0F);
      h();
      var10000 = 608870794812234667L ^ var1;
      var6.unbindFramebuffer();
      var6.framebufferRender(var4.getScaledWidth() * var5, var4.getScaledHeight() * var5);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      Minecraft.getMinecraft().updateDisplay();
   }

   public static void Y() {
      if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().getLanguageManager() != null) {
         h(Minecraft.getMinecraft().getTextureManager());
      }
   }

   public static void e(int var0) {
      N = var0;
      Y();
   }

   private static void h() {
      if (Minecraft.getMinecraft().gameSettings != null && Minecraft.getMinecraft().getTextureManager() != null) {
         ScaledResolution var3 = new ScaledResolution(Minecraft.getMinecraft());
         double var4 = N;
         int var6 = var3.getScaledWidth() / 2;
         int var7 = var3.getScaledHeight() / 2;
         int var8 = var6 - 45;
         int var9 = var6 + 45;
         double var10 = var8 + (var9 - var8) * (var4 / 7.0 * 100.0 * 0.01F);
         Gui.drawRect(var8, var7 - 5, var9, var7 + 5, new Color(0, 0, 0, 50).getRGB());
         Gui.drawRect(var8, var7 - 5, (int)var10, var7 + 5, new Color(167, 120, 255, 247).getRGB());
      }
   }

   static {
      long var14 = a ^ 97181672479201L;
      long var10000 = var14 ^ 7506636150211785286L;
   }
}
