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
      } catch (Exception var1) {
         var1.printStackTrace();
      }
   }

   private static void L(float var0, float var1, float var2, float var3) {
      e.O().get(0).getShaderManager().getShaderUniform("Radius").set(var0);
      e.O().get(1).getShaderManager().getShaderUniform("Radius").set(var0);
      e.O().get(0).getShaderManager().getShaderUniform("Opacity").set(var3);
      e.O().get(1).getShaderManager().getShaderUniform("Opacity").set(var3);
      e.O().get(0).getShaderManager().getShaderUniform("BlurDir").set(var1, var2);
      e.O().get(1).getShaderManager().getShaderUniform("BlurDir").set(var2, var1);
   }

   public static void Y(int var0, int var1, int var2, int var3, float var4, float var5, float var6) {
      long var7 = a ^ 57838412458280L;
      ScaledResolution var10 = new ScaledResolution(n);
      int var11 = var10.getScaleFactor();
      int var12 = var10.getScaledWidth();
      int var13 = var10.getScaledHeight();
      if (G != var11 || q != var12 || S != var13 || d == null || e == null) {
         g();
      }

      G = var11;
      q = var12;
      S = var13;
      if (OpenGlHelper.isFramebufferEnabled()) {
         d.framebufferClear();
         GL11.glScissor(var0 * var11, n.displayHeight - var1 * var11 - var3 * var11, var2 * var11, var3 * var11);
         long var10000 = 1287124361882384940L ^ var7;
         GL11.glEnable(3089);
         L(var4, var5, var6, 1.0F);
         var10000 = 1287124361882384940L ^ var7;
         d.bindFramebuffer(true);
         e.t(n.timer.renderPartialTicks);
         n.getFramebuffer().bindFramebuffer(true);
         GL11.glDisable(3089);
         GlStateManager.enableBlend();
         var10000 = 8940495287954356560L ^ var7;
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableBlend();
         GL11.glScalef(var11, var11, 0.0F);
      }
   }

   public static void w(int var0, int var1, int var2, int var3, float var4) {
      long var5 = a ^ 22400517269975L;
      ScaledResolution var8 = new ScaledResolution(n);
      int var9 = var8.getScaleFactor();
      int var10 = var8.getScaledWidth();
      int var11 = var8.getScaledHeight();
      if (G != var9 || q != var10 || S != var11 || d == null || e == null) {
         g();
      }

      G = var9;
      q = var10;
      long var10000 = 1287158734628895443L ^ var5;
      S = var11;
      d.framebufferClear();
      GL11.glScissor(var0 * var9, n.displayHeight - var1 * var9 - var3 * var9, var2 * var9, var3 * var9);
      GL11.glEnable(3089);
      L(var4, 1.0F, 0.0F, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      var10000 = 1287158734628895443L ^ var5;
      GL11.glDisable(3089);
      GlStateManager.enableBlend();
      var10000 = 4894574615400667843L ^ var5;
      GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
      GlStateManager.disableBlend();
      GL11.glScalef(var9, var9, 0.0F);
      RenderHelper.enableGUIStandardItemLighting();
   }

   public static void A(float var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      long var7 = a ^ 61147154447872L;
      ScaledResolution var10 = new ScaledResolution(n);
      int var11 = var10.getScaleFactor();
      int var12 = var10.getScaledWidth();
      int var13 = var10.getScaledHeight();
      if (G != var11 || q != var12 || S != var13 || d == null || e == null) {
         g();
      }

      G = var11;
      q = var12;
      long var10000 = 1287127686722628868L ^ var7;
      S = var13;
      GL11.glScissor((int)(var0 * var11), (int)(n.displayHeight - var1 * var11 - var3 * var11) + 1, (int)(var2 * var11), (int)(var3 * var11));
      GL11.glEnable(3089);
      L(var4, var5, var6, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      var10000 = 1287127686722628868L ^ var7;
      GL11.glDisable(3089);
   }

   public static void J(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      long var8 = a ^ 3062644644846L;
      ScaledResolution var11 = new ScaledResolution(n);
      int var12 = var11.getScaleFactor();
      int var13 = var11.getScaledWidth();
      int var14 = var11.getScaledHeight();
      if (G != var12 || q != var13 || S != var14 || d == null || e == null) {
         g();
      }

      G = var12;
      q = var13;
      long var10000 = 1287183531941808362L ^ var8;
      S = var14;
      GL11.glScissor((int)(var0 * var12), (int)(n.displayHeight - var1 * var12 - var3 * var12) + 1, (int)(var2 * var12), (int)(var3 * var12));
      GL11.glEnable(3089);
      L(var4, var6, var7, var5);
      d.bindFramebuffer(true);
      e.o(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      var10000 = 1287183531941808362L ^ var8;
      GL11.glDisable(3089);
   }

   public static void S(float var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      long var7 = a ^ 5792117520143L;
      ScaledResolution var10 = new ScaledResolution(n);
      int var11 = var10.getScaleFactor();
      int var12 = var10.getScaledWidth();
      int var13 = var10.getScaledHeight();
      if (G != var11 || q != var12 || S != var13 || d == null || e == null) {
         g();
      }

      G = var11;
      q = var12;
      long var10000 = 4267422571065264998L ^ var7;
      S = var13;
      GL11.glScissor((int)(var0 * var11), (int)(n.displayHeight - var1 * var11 - var3 * var11) + 1, (int)(var2 * var11), (int)(var3 * var11));
      GL11.glEnable(3089);
      L(var4, var5, var6, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      var10000 = 1287177473802118155L ^ var7;
      GL11.glDisable(3089);
   }

   public static void F(int var0, int var1, int var2, int var3, float var4) {
      long var5 = a ^ 728747750180L;
      ScaledResolution var8 = new ScaledResolution(n);
      int var9 = var8.getScaleFactor();
      int var10 = var8.getScaledWidth();
      int var11 = var8.getScaledHeight();
      if (G != var9 || q != var10 || S != var11 || d == null || e == null) {
         g();
      }

      G = var9;
      q = var10;
      long var10000 = 1287181746357887008L ^ var5;
      S = var11;
      GL11.glScissor(var0 * var9, n.displayHeight - var1 * var9 - var3 * var9, var2 * var9, var3 * var9);
      GL11.glEnable(3089);
      L(var4, 1.0F, 0.0F, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
      var10000 = 1287181746357887008L ^ var5;
      GL11.glDisable(3089);
   }

   public static void n(float var0) {
      ScaledResolution var4 = new ScaledResolution(n);
      int var5 = var4.getScaleFactor();
      int var6 = var4.getScaledWidth();
      int var7 = var4.getScaledHeight();
      if (G != var5 || q != var6 || S != var7 || d == null || e == null) {
         g();
      }

      G = var5;
      q = var6;
      S = var7;
      L(var0, 0.0F, 1.0F, 1.0F);
      d.bindFramebuffer(true);
      e.t(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
   }

   public static void j(float var0, float var1) {
      ScaledResolution var5 = new ScaledResolution(n);
      int var6 = var5.getScaleFactor();
      int var7 = var5.getScaledWidth();
      int var8 = var5.getScaledHeight();
      if (G != var6 || q != var7 || S != var8 || d == null || e == null) {
         g();
      }

      G = var6;
      q = var7;
      S = var8;
      L(var0, 0.0F, 1.0F, var1);
      d.bindFramebuffer(true);
      e.o(n.timer.renderPartialTicks);
      n.getFramebuffer().bindFramebuffer(true);
   }
}
