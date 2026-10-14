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
   private static final long a = 14389213620998L;

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
      long m = a ^ 57838412458280L;
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i2 = scaledResolution.getScaleFactor();
      int i3 = scaledResolution.getScaledWidth();
      int i4 = scaledResolution.getScaledHeight();
      if (G != i2 || q != i3 || S != i4 || d == null || e == null) {
         g();
      }

      G = i2;
      q = i3;
      S = i4;
      if (OpenGlHelper.isFramebufferEnabled()) {
         d.framebufferClear();
         GL11.glScissor(i * i2, n.displayHeight - j * i2 - l * i2, k * i2, l * i2);
         long l2 = 1287124361882384940L ^ m;
         GL11.glEnable(3089);
         L(f, g, h, 1.0F);
         l2 = 1287124361882384940L ^ m;
         d.bindFramebuffer(true);
         e.t(n.timer.renderPartialTicks);
         n.getFramebuffer().bindFramebuffer(true);
         GL11.glDisable(3089);
         GlStateManager.enableBlend();
         l2 = 8940495287954356560L ^ m;
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableBlend();
         GL11.glScalef(i2, i2, 0.0F);
      }
   }

   public static void w(int i, int j, int k, int l, float f) {
      long m = a ^ 22400517269975L;
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i2 = scaledResolution.getScaleFactor();
      int i3 = scaledResolution.getScaledWidth();
      int i4 = scaledResolution.getScaledHeight();
      if (G != i2 || q != i3 || S != i4 || d == null || e == null) {
         g();
      }

      G = i2;
      q = i3;
      long l2 = 1287158734628895443L ^ m;
      S = i4;
      d.framebufferClear();
      GL11.glScissor(i * i2, n.displayHeight - j * i2 - l * i2, k * i2, l * i2);
      GL11.glEnable(3089);
      L(f, 1.0F, 0.0F, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      l2 = 1287158734628895443L ^ m;
      GL11.glDisable(3089);
      GlStateManager.enableBlend();
      l2 = 4894574615400667843L ^ m;
      GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
      GlStateManager.disableBlend();
      GL11.glScalef(i2, i2, 0.0F);
      RenderHelper.enableGUIStandardItemLighting();
   }

   public static void A(float f, float g, float h, float f2, float f3, float f4, float f5) {
      long l = a ^ 61147154447872L;
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (G != i || q != j || S != k || d == null || e == null) {
         g();
      }

      G = i;
      q = j;
      long m = 1287127686722628868L ^ l;
      S = k;
      GL11.glScissor((int)(f * i), (int)(n.displayHeight - g * i - f2 * i) + 1, (int)(h * i), (int)(f2 * i));
      GL11.glEnable(3089);
      L(f3, f4, f5, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      m = 1287127686722628868L ^ l;
      GL11.glDisable(3089);
   }

   public static void J(float f, float g, float h, float f2, float f3, float f4, float f5, float f6) {
      long l = a ^ 3062644644846L;
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (G != i || q != j || S != k || d == null || e == null) {
         g();
      }

      G = i;
      q = j;
      long m = 1287183531941808362L ^ l;
      S = k;
      GL11.glScissor((int)(f * i), (int)(n.displayHeight - g * i - f2 * i) + 1, (int)(h * i), (int)(f2 * i));
      GL11.glEnable(3089);
      L(f3, f5, f6, f4);
      d.bindFramebuffer(true);
      e.o(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      m = 1287183531941808362L ^ l;
      GL11.glDisable(3089);
   }

   public static void S(float f, float g, float h, float f2, float f3, float f4, float f5) {
      long l = a ^ 5792117520143L;
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (G != i || q != j || S != k || d == null || e == null) {
         g();
      }

      G = i;
      q = j;
      long m = 4267422571065264998L ^ l;
      S = k;
      GL11.glScissor((int)(f * i), (int)(n.displayHeight - g * i - f2 * i) + 1, (int)(h * i), (int)(f2 * i));
      GL11.glEnable(3089);
      L(f3, f4, f5, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      m = 1287177473802118155L ^ l;
      GL11.glDisable(3089);
   }

   public static void F(int i, int j, int k, int l, float f) {
      long m = a ^ 728747750180L;
      ScaledResolution scaledResolution = new ScaledResolution(n);
      int i2 = scaledResolution.getScaleFactor();
      int i3 = scaledResolution.getScaledWidth();
      int i4 = scaledResolution.getScaledHeight();
      if (G != i2 || q != i3 || S != i4 || d == null || e == null) {
         g();
      }

      G = i2;
      q = i3;
      long l2 = 1287181746357887008L ^ m;
      S = i4;
      GL11.glScissor(i * i2, n.displayHeight - j * i2 - l * i2, k * i2, l * i2);
      GL11.glEnable(3089);
      L(f, 1.0F, 0.0F, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      l2 = 1287181746357887008L ^ m;
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
