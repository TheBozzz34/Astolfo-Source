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
   private static ShaderGroup shaderGroup;
   private static Minecraft mc = Minecraft.getMinecraft();
   private static Framebuffer framebuffer;
   private static int lastScaleFactor;
   private static int lastScaledWidth;
   private static int lastScaledHeight;
   private static ResourceLocation blurShaderLocation = new ResourceLocation("shaders/post/blur.json");

   public static void initShader() {
      try {
         shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), blurShaderLocation);
         shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
         framebuffer = shaderGroup.mainFramebuffer;
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private static void setUniforms(float f, float g, float h, float f2) {
      shaderGroup.getShaders().get(0).getShaderManager().getShaderUniform("Radius").set(f);
      shaderGroup.getShaders().get(1).getShaderManager().getShaderUniform("Radius").set(f);
      shaderGroup.getShaders().get(0).getShaderManager().getShaderUniform("Opacity").set(f2);
      shaderGroup.getShaders().get(1).getShaderManager().getShaderUniform("Opacity").set(f2);
      shaderGroup.getShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(g, h);
      shaderGroup.getShaders().get(1).getShaderManager().getShaderUniform("BlurDir").set(h, g);
   }

   public static void blurAreaDirectional(int i, int j, int k, int l, float f, float g, float h) {
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int m = scaledResolution.getScaleFactor();
      int n = scaledResolution.getScaledWidth();
      int i2 = scaledResolution.getScaledHeight();
      if (lastScaleFactor != m || lastScaledWidth != n || lastScaledHeight != i2 || framebuffer == null || shaderGroup == null) {
         initShader();
      }

      lastScaleFactor = m;
      lastScaledWidth = n;
      lastScaledHeight = i2;
      if (OpenGlHelper.isFramebufferEnabled()) {
         framebuffer.framebufferClear();
         GL11.glScissor(i * m, mc.displayHeight - j * m - l * m, k * m, l * m);
         GL11.glEnable(3089);
         setUniforms(f, g, h, 1.0F);
         framebuffer.bindFramebuffer(true);
         shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
         mc.getFramebuffer().bindFramebuffer(true);
         GL11.glDisable(3089);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableBlend();
         GL11.glScalef(m, m, 0.0F);
      }
   }

   public static void blurAreaWithLighting(int i, int j, int k, int l, float f) {
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int m = scaledResolution.getScaleFactor();
      int n = scaledResolution.getScaledWidth();
      int i2 = scaledResolution.getScaledHeight();
      if (lastScaleFactor != m || lastScaledWidth != n || lastScaledHeight != i2 || framebuffer == null || shaderGroup == null) {
         initShader();
      }

      lastScaleFactor = m;
      lastScaledWidth = n;
      lastScaledHeight = i2;
      framebuffer.framebufferClear();
      GL11.glScissor(i * m, mc.displayHeight - j * m - l * m, k * m, l * m);
      GL11.glEnable(3089);
      setUniforms(f, 1.0F, 0.0F, 1.0F);
      framebuffer.bindFramebuffer(true);
      shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
      mc.getFramebuffer().bindFramebuffer(true);
      GL11.glDisable(3089);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
      GlStateManager.disableBlend();
      GL11.glScalef(m, m, 0.0F);
      RenderHelper.enableGUIStandardItemLighting();
   }

   public static void blurRegion(float f, float g, float h, float f2, float f3, float f4, float f5) {
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (lastScaleFactor != i || lastScaledWidth != j || lastScaledHeight != k || framebuffer == null || shaderGroup == null) {
         initShader();
      }

      lastScaleFactor = i;
      lastScaledWidth = j;
      lastScaledHeight = k;
      GL11.glScissor((int)(f * i), (int)(mc.displayHeight - g * i - f2 * i) + 1, (int)(h * i), (int)(f2 * i));
      GL11.glEnable(3089);
      setUniforms(f3, f4, f5, 1.0F);
      framebuffer.bindFramebuffer(true);
      shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
      mc.getFramebuffer().bindFramebuffer(true);
      GL11.glDisable(3089);
   }

   public static void blurRegionWithOpacity(float f, float g, float h, float f2, float f3, float f4, float f5, float f6) {
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (lastScaleFactor != i || lastScaledWidth != j || lastScaledHeight != k || framebuffer == null || shaderGroup == null) {
         initShader();
      }

      lastScaleFactor = i;
      lastScaledWidth = j;
      lastScaledHeight = k;
      GL11.glScissor((int)(f * i), (int)(mc.displayHeight - g * i - f2 * i) + 1, (int)(h * i), (int)(f2 * i));
      GL11.glEnable(3089);
      setUniforms(f3, f5, f6, f4);
      framebuffer.bindFramebuffer(true);
      shaderGroup.loadShaderGroupBlended(mc.timer.renderPartialTicks);
      mc.getFramebuffer().bindFramebuffer(true);
      GL11.glDisable(3089);
   }

   public static void blurRegionAlt(float f, float g, float h, float f2, float f3, float f4, float f5) {
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (lastScaleFactor != i || lastScaledWidth != j || lastScaledHeight != k || framebuffer == null || shaderGroup == null) {
         initShader();
      }

      lastScaleFactor = i;
      lastScaledWidth = j;
      lastScaledHeight = k;
      GL11.glScissor((int)(f * i), (int)(mc.displayHeight - g * i - f2 * i) + 1, (int)(h * i), (int)(f2 * i));
      GL11.glEnable(3089);
      setUniforms(f3, f4, f5, 1.0F);
      framebuffer.bindFramebuffer(true);
      shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
      mc.getFramebuffer().bindFramebuffer(true);
      GL11.glDisable(3089);
   }

   public static void blurArea(int i, int j, int k, int l, float f) {
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int m = scaledResolution.getScaleFactor();
      int n = scaledResolution.getScaledWidth();
      int i2 = scaledResolution.getScaledHeight();
      if (lastScaleFactor != m || lastScaledWidth != n || lastScaledHeight != i2 || framebuffer == null || shaderGroup == null) {
         initShader();
      }

      lastScaleFactor = m;
      lastScaledWidth = n;
      lastScaledHeight = i2;
      GL11.glScissor(i * m, mc.displayHeight - j * m - l * m, k * m, l * m);
      GL11.glEnable(3089);
      setUniforms(f, 1.0F, 0.0F, 1.0F);
      framebuffer.bindFramebuffer(true);
      shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
      mc.getFramebuffer().bindFramebuffer(true);
      GL11.glDisable(3089);
   }

   public static void blurScreen(float f) {
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (lastScaleFactor != i || lastScaledWidth != j || lastScaledHeight != k || framebuffer == null || shaderGroup == null) {
         initShader();
      }

      lastScaleFactor = i;
      lastScaledWidth = j;
      lastScaledHeight = k;
      setUniforms(f, 0.0F, 1.0F, 1.0F);
      framebuffer.bindFramebuffer(true);
      shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
      mc.getFramebuffer().bindFramebuffer(true);
   }

   public static void blurScreenWithOpacity(float f, float g) {
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int i = scaledResolution.getScaleFactor();
      int j = scaledResolution.getScaledWidth();
      int k = scaledResolution.getScaledHeight();
      if (lastScaleFactor != i || lastScaledWidth != j || lastScaledHeight != k || framebuffer == null || shaderGroup == null) {
         initShader();
      }

      lastScaleFactor = i;
      lastScaledWidth = j;
      lastScaledHeight = k;
      setUniforms(f, 0.0F, 1.0F, g);
      framebuffer.bindFramebuffer(true);
      shaderGroup.loadShaderGroupBlended(mc.timer.renderPartialTicks);
      mc.getFramebuffer().bindFramebuffer(true);
   }
}
