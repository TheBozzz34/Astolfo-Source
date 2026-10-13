package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class Shader {
   private final ShaderManager manager;
   public final Framebuffer framebufferIn;
   public final Framebuffer framebufferOut;
   private final List<Object> listAuxFramebuffers = Lists.newArrayList();
   private final List<String> listAuxNames = Lists.newArrayList();
   private final List<Integer> listAuxWidths = Lists.newArrayList();
   private final List<Integer> listAuxHeights = Lists.newArrayList();
   private Matrix4f projectionMatrix;
   private static final long a = 38205687845358L;

   public Shader(IResourceManager var1, String var2, Framebuffer var3, Framebuffer var4) throws IOException {
      this.manager = new ShaderManager(var1, var2);
      this.framebufferIn = var3;
      this.framebufferOut = var4;
   }

   public void deleteShader() {
      this.manager.deleteShader();
   }

   public void addAuxFramebuffer(String var1, Object var2, int var3, int var4) {
      this.listAuxNames.add(this.listAuxNames.size(), var1);
      this.listAuxFramebuffers.add(this.listAuxFramebuffers.size(), var2);
      this.listAuxWidths.add(this.listAuxWidths.size(), var3);
      this.listAuxHeights.add(this.listAuxHeights.size(), var4);
   }

   private void preLoadShader() {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.disableDepth();
      GlStateManager.disableAlpha();
      GlStateManager.disableFog();
      GlStateManager.disableLighting();
      GlStateManager.disableColorMaterial();
      GlStateManager.enableTexture2D();
      GlStateManager.bindTexture(0);
   }

   public void setProjectionMatrix(Matrix4f var1) {
      this.projectionMatrix = var1;
   }

   public void loadShader(float var1) {
      this.preLoadShader();
      this.framebufferIn.unbindFramebuffer();
      float var6 = this.framebufferOut.framebufferTextureWidth;
      float var7 = this.framebufferOut.framebufferTextureHeight;
      GlStateManager.viewport(0, 0, (int)var6, (int)var7);
      this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

      for (int var8 = 0; var8 < this.listAuxFramebuffers.size(); var8++) {
         this.manager.addSamplerTexture(this.listAuxNames.get(var8), this.listAuxFramebuffers.get(var8));
         this.manager.getShaderUniformOrDefault("AuxSize" + var8).set(this.listAuxWidths.get(var8).intValue(), this.listAuxHeights.get(var8).intValue());
      }

      this.manager.getShaderUniformOrDefault("ProjMat").set(this.projectionMatrix);
      this.manager.getShaderUniformOrDefault("InSize").set(this.framebufferIn.framebufferTextureWidth, this.framebufferIn.framebufferTextureHeight);
      this.manager.getShaderUniformOrDefault("OutSize").set(var6, var7);
      this.manager.getShaderUniformOrDefault("Time").set(var1);
      Minecraft var13 = Minecraft.getMinecraft();
      this.manager.getShaderUniformOrDefault("ScreenSize").set(var13.displayWidth, var13.displayHeight);
      this.manager.useShader();
      this.framebufferOut.framebufferClear();
      this.framebufferOut.bindFramebuffer(false);
      GlStateManager.depthMask(false);
      GlStateManager.colorMask(true, true, true, true);
      Tessellator var9 = Tessellator.getInstance();
      WorldRenderer var10 = var9.getWorldRenderer();
      var10.begin(7, DefaultVertexFormats.POSITION_COLOR);
      var10.pos(0.0, var7, 500.0).color(255, 255, 255, 255).endVertex();
      var10.pos(var6, var7, 500.0).color(255, 255, 255, 255).endVertex();
      var10.pos(var6, 0.0, 500.0).color(255, 255, 255, 255).endVertex();
      var10.pos(0.0, 0.0, 500.0).color(255, 255, 255, 255).endVertex();
      var9.draw();
      GlStateManager.depthMask(true);
      GlStateManager.colorMask(true, true, true, true);
      this.manager.endShader();
      this.framebufferOut.unbindFramebuffer();
      this.framebufferIn.unbindFramebufferTexture();

      for (Object var12 : this.listAuxFramebuffers) {
         if (var12 instanceof Framebuffer) {
            ((Framebuffer)var12).unbindFramebufferTexture();
         }
      }
   }

   public void Q(float var1) {
      GL11.glEnable(3042);
      this.framebufferIn.unbindFramebuffer();
      float var5 = this.framebufferOut.framebufferTextureWidth;
      float var6 = this.framebufferOut.framebufferTextureHeight;
      GlStateManager.viewport(0, 0, (int)var5, (int)var6);
      this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

      for (int var7 = 0; var7 < this.listAuxFramebuffers.size(); var7++) {
         this.manager.addSamplerTexture(this.listAuxNames.get(var7), this.listAuxFramebuffers.get(var7));
         this.manager.getShaderUniformOrDefault("AuxSize" + var7).set(this.listAuxWidths.get(var7).intValue(), this.listAuxHeights.get(var7).intValue());
      }

      this.manager.getShaderUniformOrDefault("ProjMat").set(this.projectionMatrix);
      this.manager.getShaderUniformOrDefault("InSize").set(this.framebufferIn.framebufferTextureWidth, this.framebufferIn.framebufferTextureHeight);
      this.manager.getShaderUniformOrDefault("OutSize").set(var5, var6);
      this.manager.getShaderUniformOrDefault("Time").set(var1);
      Minecraft var12 = Minecraft.getMinecraft();
      this.manager.getShaderUniformOrDefault("ScreenSize").set(var12.displayWidth, var12.displayHeight);
      this.manager.useShader();
      this.framebufferOut.bindFramebuffer(false);
      Tessellator var8 = Tessellator.getInstance();
      WorldRenderer var9 = var8.getWorldRenderer();
      var9.begin(7, DefaultVertexFormats.POSITION_TEX);
      var9.pos(0.0, var6, 500.0).endVertex();
      var9.pos(var5, var6, 500.0).endVertex();
      var9.pos(var5, 0.0, 500.0).endVertex();
      var9.pos(0.0, 0.0, 500.0).endVertex();
      var8.draw();
      this.manager.endShader();
      this.framebufferOut.unbindFramebuffer();
      this.framebufferIn.unbindFramebufferTexture();

      for (Object var11 : this.listAuxFramebuffers) {
         if (var11 instanceof Framebuffer) {
            ((Framebuffer)var11).unbindFramebufferTexture();
         }
      }
   }

   public ShaderManager getShaderManager() {
      return this.manager;
   }
}
