package bypass.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BlurShader {
   private static ShaderGroup e;
   private static Minecraft n = Minecraft.getMinecraft();
   private static Framebuffer d;
   private static int G;
   private static int q;
   private static int S;
   private static ResourceLocation N = new ResourceLocation("shaders/post/blur.json");

   public static void g() {
      try {
         e = new ShaderGroup(n.getTextureManager(), n.getResourceManager(), n.getFramebuffer(), N);
         e.createBindFramebuffers(n.displayWidth, n.displayHeight);
         d = e.mainFramebuffer;
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private static void L(float f, float g, float h, float f2) {
      e.O().get(0).getShaderManager().getShaderUniform("Radius").set(f);
      e.O().get(1).getShaderManager().getShaderUniform("Radius").set(f);
      e.O().get(0).getShaderManager().getShaderUniform("Opacity").set(f2);
      e.O().get(1).getShaderManager().getShaderUniform("Opacity").set(f2);
      e.O().get(0).getShaderManager().getShaderUniform("BlurDir").set(g, h);
      e.O().get(1).getShaderManager().getShaderUniform("BlurDir").set(h, g);
   }

   public static void Y(int i, int j, int k, int l, float f, float g, float h) {
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int m = scaledResolution.getScaleFactor();
      int i2 = scaledResolution.getScaledWidth();
      int i3 = scaledResolution.getScaledHeight();
      if (G != m || q != i2 || S != i3 || d == null || e == null) {
         g();
      }

      G = m;
      q = i2;
      S = i3;
      if (OpenGlHelper.isFramebufferEnabled()) {
         d.framebufferClear();
         GL11.glScissor(i * m, n.displayHeight - j * m - l * m, k * m, l * m);
         GL11.glEnable(3089);
         L(f, g, h, 1.0F);
         d.bindFramebuffer(true);
         e.t(n.timer.renderPartialTicks);
         n.getFramebuffer().bindFramebuffer(true);
         GL11.glDisable(3089);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableBlend();
         GL11.glScalef(m, m, 0.0F);
      }
   }

   public static void w(int i, int j, int k, int l, float f) {
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int m = scaledResolution.getScaleFactor();
      int i2 = scaledResolution.getScaledWidth();
      int i3 = scaledResolution.getScaledHeight();
      if (G != m || q != i2 || S != i3 || d == null || e == null) {
         g();
      }

      G = m;
      q = i2;
      S = i3;
      d.framebufferClear();
      GL11.glScissor(i * m, n.displayHeight - j * m - l * m, k * m, l * m);
      GL11.glEnable(3089);
      L(f, 1.0F, 0.0F, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      GL11.glDisable(3089);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
      GlStateManager.disableBlend();
      GL11.glScalef(m, m, 0.0F);
      RenderHelper.enableGUIStandardItemLighting();
   }

   public static void A(float f, float g, float h, float f2, float f3, float f4, float f5) {
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (G != i || q != j || S != k || d == null || e == null) {
         g();
      }

      G = i;
      q = j;
      S = k;
      GL11.glScissor((int)(f * i), (int)(n.displayHeight - g * i - f2 * i) + 1, (int)(h * i), (int)(f2 * i));
      GL11.glEnable(3089);
      L(f3, f4, f5, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      GL11.glDisable(3089);
   }

   public static void J(float f, float g, float h, float f2, float f3, float f4, float f5, float f6) {
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (G != i || q != j || S != k || d == null || e == null) {
         g();
      }

      G = i;
      q = j;
      S = k;
      GL11.glScissor((int)(f * i), (int)(n.displayHeight - g * i - f2 * i) + 1, (int)(h * i), (int)(f2 * i));
      GL11.glEnable(3089);
      L(f3, f5, f6, f4);
      d.bindFramebuffer(true);
      e.o(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      GL11.glDisable(3089);
   }

   public static void S(float f, float g, float h, float f2, float f3, float f4, float f5) {
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (G != i || q != j || S != k || d == null || e == null) {
         g();
      }

      G = i;
      q = j;
      S = k;
      GL11.glScissor((int)(f * i), (int)(n.displayHeight - g * i - f2 * i) + 1, (int)(h * i), (int)(f2 * i));
      GL11.glEnable(3089);
      L(f3, f4, f5, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      GL11.glDisable(3089);
   }

   public static void F(int i, int j, int k, int l, float f) {
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int m = scaledResolution.getScaleFactor();
      int i2 = scaledResolution.getScaledWidth();
      int i3 = scaledResolution.getScaledHeight();
      if (G != m || q != i2 || S != i3 || d == null || e == null) {
         g();
      }

      G = m;
      q = i2;
      S = i3;
      GL11.glScissor(i * m, n.displayHeight - j * m - l * m, k * m, l * m);
      GL11.glEnable(3089);
      L(f, 1.0F, 0.0F, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      GL11.glDisable(3089);
   }

   public static void n(float f) {
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (G != i || q != j || S != k || d == null || e == null) {
         g();
      }

      G = i;
      q = j;
      S = k;
      L(f, 0.0F, 1.0F, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
   }

   public static void j(float f, float g) {
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i = scaledResolution.getScaleFactor();
      int k = scaledResolution.getScaledWidth();
      int l = scaledResolution.getScaledHeight();
      if (G != i || q != k || S != l || d == null || e == null) {
         g();
      }

      G = i;
      q = k;
      S = l;
      L(f, 0.0F, 1.0F, g);
      d.bindFramebuffer(true);
      e.o(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
   }
}
