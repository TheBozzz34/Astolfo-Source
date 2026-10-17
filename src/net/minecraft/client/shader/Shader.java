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

   public Shader(IResourceManager iResourceManager, String string, Framebuffer framebuffer, Framebuffer framebuffer2) throws IOException {
      this.manager = new ShaderManager(iResourceManager, string);
      this.framebufferIn = framebuffer;
      this.framebufferOut = framebuffer2;
   }

   public void deleteShader() {
      this.manager.deleteShader();
   }

   public void addAuxFramebuffer(String string, Object object, int i, int j) {
      this.listAuxNames.add(this.listAuxNames.size(), string);
      this.listAuxFramebuffers.add(this.listAuxFramebuffers.size(), object);
      this.listAuxWidths.add(this.listAuxWidths.size(), i);
      this.listAuxHeights.add(this.listAuxHeights.size(), j);
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

   public void setProjectionMatrix(Matrix4f matrix4f) {
      this.projectionMatrix = matrix4f;
   }

   public void loadShader(float f) {
      this.preLoadShader();
      this.framebufferIn.unbindFramebuffer();
      float g = this.framebufferOut.framebufferTextureWidth;
      float h = this.framebufferOut.framebufferTextureHeight;
      GlStateManager.viewport(0, 0, (int)g, (int)h);
      this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

      for (int i = 0; i < this.listAuxFramebuffers.size(); i++) {
         this.manager.addSamplerTexture(this.listAuxNames.get(i), this.listAuxFramebuffers.get(i));
         this.manager.getShaderUniformOrDefault("AuxSize" + i).set(this.listAuxWidths.get(i).intValue(), this.listAuxHeights.get(i).intValue());
      }

      this.manager.getShaderUniformOrDefault("ProjMat").set(this.projectionMatrix);
      this.manager.getShaderUniformOrDefault("InSize").set(this.framebufferIn.framebufferTextureWidth, this.framebufferIn.framebufferTextureHeight);
      this.manager.getShaderUniformOrDefault("OutSize").set(g, h);
      this.manager.getShaderUniformOrDefault("Time").set(f);
      Minecraft minecraft = Minecraft.getMinecraft();
      this.manager.getShaderUniformOrDefault("ScreenSize").set(minecraft.displayWidth, minecraft.displayHeight);
      this.manager.useShader();
      this.framebufferOut.framebufferClear();
      this.framebufferOut.bindFramebuffer(false);
      GlStateManager.depthMask(false);
      GlStateManager.colorMask(true, true, true, true);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(0.0, h, 500.0).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(g, h, 500.0).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(g, 0.0, 500.0).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(0.0, 0.0, 500.0).color(255, 255, 255, 255).endVertex();
      tessellator.draw();
      GlStateManager.depthMask(true);
      GlStateManager.colorMask(true, true, true, true);
      this.manager.endShader();
      this.framebufferOut.unbindFramebuffer();
      this.framebufferIn.unbindFramebufferTexture();

      for (Object object : this.listAuxFramebuffers) {
         if (object instanceof Framebuffer) {
            ((Framebuffer)object).unbindFramebufferTexture();
         }
      }
   }

   public void renderBlended(float f) {
      GL11.glEnable(3042);
      this.framebufferIn.unbindFramebuffer();
      float g = this.framebufferOut.framebufferTextureWidth;
      float h = this.framebufferOut.framebufferTextureHeight;
      GlStateManager.viewport(0, 0, (int)g, (int)h);
      this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

      for (int i = 0; i < this.listAuxFramebuffers.size(); i++) {
         this.manager.addSamplerTexture(this.listAuxNames.get(i), this.listAuxFramebuffers.get(i));
         this.manager.getShaderUniformOrDefault("AuxSize" + i).set(this.listAuxWidths.get(i).intValue(), this.listAuxHeights.get(i).intValue());
      }

      this.manager.getShaderUniformOrDefault("ProjMat").set(this.projectionMatrix);
      this.manager.getShaderUniformOrDefault("InSize").set(this.framebufferIn.framebufferTextureWidth, this.framebufferIn.framebufferTextureHeight);
      this.manager.getShaderUniformOrDefault("OutSize").set(g, h);
      this.manager.getShaderUniformOrDefault("Time").set(f);
      Minecraft minecraft = Minecraft.getMinecraft();
      this.manager.getShaderUniformOrDefault("ScreenSize").set(minecraft.displayWidth, minecraft.displayHeight);
      this.manager.useShader();
      this.framebufferOut.bindFramebuffer(false);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(0.0, h, 500.0).endVertex();
      worldRenderer.pos(g, h, 500.0).endVertex();
      worldRenderer.pos(g, 0.0, 500.0).endVertex();
      worldRenderer.pos(0.0, 0.0, 500.0).endVertex();
      tessellator.draw();
      this.manager.endShader();
      this.framebufferOut.unbindFramebuffer();
      this.framebufferIn.unbindFramebufferTexture();

      for (Object object : this.listAuxFramebuffers) {
         if (object instanceof Framebuffer) {
            ((Framebuffer)object).unbindFramebufferTexture();
         }
      }
   }

   public ShaderManager getShaderManager() {
      return this.manager;
   }
}
