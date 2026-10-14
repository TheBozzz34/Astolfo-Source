package net.minecraft.client.shader;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class Framebuffer {
   public int framebufferTextureWidth;
   public int framebufferTextureHeight;
   public int framebufferWidth;
   public int framebufferHeight;
   public boolean useDepth;
   public int framebufferObject;
   public int framebufferTexture;
   public int depthBuffer;
   public float[] framebufferColor;
   public int framebufferFilter;
   private static final long a = 68170991417652L;

   public Framebuffer(int i, int j, boolean bl) {
      this.useDepth = bl;
      this.framebufferObject = -1;
      this.framebufferTexture = -1;
      this.depthBuffer = -1;
      this.framebufferColor = new float[4];
      this.framebufferColor[0] = 1.0F;
      this.framebufferColor[1] = 1.0F;
      this.framebufferColor[2] = 1.0F;
      this.framebufferColor[3] = 0.0F;
      this.createBindFramebuffer(i, j);
   }

   public void createBindFramebuffer(int width, int height) {
      if (!OpenGlHelper.isFramebufferEnabled()) {
         this.framebufferWidth = width;
         this.framebufferHeight = height;
      } else {
         GlStateManager.enableDepth();
         if (this.framebufferObject >= 0) {
            this.deleteFramebuffer();
         }

         this.createFramebuffer(width, height);
         this.checkFramebufferComplete();
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
      }
   }

   public void deleteFramebuffer() {
      if (OpenGlHelper.isFramebufferEnabled()) {
         this.unbindFramebufferTexture();
         this.unbindFramebuffer();
         if (this.depthBuffer > -1) {
            OpenGlHelper.glDeleteRenderbuffers(this.depthBuffer);
            this.depthBuffer = -1;
         }

         if (this.framebufferTexture > -1) {
            TextureUtil.deleteTexture(this.framebufferTexture);
            this.framebufferTexture = -1;
         }

         if (this.framebufferObject > -1) {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
            OpenGlHelper.glDeleteFramebuffers(this.framebufferObject);
            this.framebufferObject = -1;
         }
      }
   }

   public void createFramebuffer(int width, int height) {
      long l = a ^ 75151553719345L;
      this.framebufferWidth = width;
      this.framebufferHeight = height;
      this.framebufferTextureWidth = width;
      this.framebufferTextureHeight = height;
      if (!OpenGlHelper.isFramebufferEnabled()) {
         this.framebufferClear();
      } else {
         this.framebufferObject = OpenGlHelper.glGenFramebuffers();
         this.framebufferTexture = TextureUtil.glGenTextures();
         if (this.useDepth) {
            this.depthBuffer = OpenGlHelper.glGenRenderbuffers();
         }

         this.setFramebufferFilter(9728);
         GlStateManager.bindTexture(this.framebufferTexture);
         long m = 5524770439045699323L ^ l;
         GL11.glTexImage2D(3553, 0, 32856, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, 6408, 5121, (ByteBuffer)null);
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
         OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, this.framebufferTexture, 0);
         if (this.useDepth) {
            OpenGlHelper.glBindRenderbuffer(OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
            OpenGlHelper.glRenderbufferStorage(OpenGlHelper.GL_RENDERBUFFER, 33190, this.framebufferTextureWidth, this.framebufferTextureHeight);
            OpenGlHelper.glFramebufferRenderbuffer(
               OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer
            );
         }

         this.framebufferClear();
         this.unbindFramebufferTexture();
      }
   }

   public void setFramebufferFilter(int i) {
      long l = a ^ 103765071115624L;
      if (OpenGlHelper.isFramebufferEnabled()) {
         this.framebufferFilter = i;
         GlStateManager.bindTexture(this.framebufferTexture);
         long m = 1915925770676843379L ^ l;
         GL11.glTexParameterf(3553, 10241, i);
         GL11.glTexParameterf(3553, 10240, i);
         GL11.glTexParameterf(3553, 10242, 10496.0F);
         GL11.glTexParameterf(3553, 10243, 10496.0F);
         GlStateManager.bindTexture(0);
      }
   }

   public void checkFramebufferComplete() {
      int i = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);
      if (i != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE) {
         if (i == OpenGlHelper.GL_FB_INCOMPLETE_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
         } else if (i == OpenGlHelper.GL_FB_INCOMPLETE_MISS_ATTACH) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
         } else if (i == OpenGlHelper.GL_FB_INCOMPLETE_DRAW_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
         } else if (i == OpenGlHelper.GL_FB_INCOMPLETE_READ_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
         } else {
            throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
         }
      }
   }

   public void bindFramebufferTexture() {
      if (OpenGlHelper.isFramebufferEnabled()) {
         GlStateManager.bindTexture(this.framebufferTexture);
      }
   }

   public void unbindFramebufferTexture() {
      if (OpenGlHelper.isFramebufferEnabled()) {
         GlStateManager.bindTexture(0);
      }
   }

   public void bindFramebuffer(boolean bl) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
         if (bl) {
            GlStateManager.viewport(0, 0, this.framebufferWidth, this.framebufferHeight);
         }
      }
   }

   public void unbindFramebuffer() {
      if (OpenGlHelper.isFramebufferEnabled()) {
         OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
      }
   }

   public void setFramebufferColor(float f, float g, float h, float f2) {
      this.framebufferColor[0] = f;
      this.framebufferColor[1] = g;
      this.framebufferColor[2] = h;
      this.framebufferColor[3] = f2;
   }

   public void framebufferRender(int i, int j) {
      this.framebufferRenderExt(i, j, true);
   }

   public void framebufferRenderExt(int i, int j, boolean bl) {
      long l = a ^ 89014826740534L;
      if (OpenGlHelper.isFramebufferEnabled()) {
         GlStateManager.colorMask(true, true, true, false);
         GlStateManager.disableDepth();
         long m = 8526040424913307669L ^ l;
         GlStateManager.depthMask(false);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         m = 802263974487195951L ^ l;
         GlStateManager.ortho(0.0, i, j, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GlStateManager.viewport(0, 0, i, j);
         GlStateManager.enableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableAlpha();
         if (bl) {
            GlStateManager.disableBlend();
            GlStateManager.enableColorMaterial();
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindFramebufferTexture();
         float f = i;
         float g = j;
         float h = (float)this.framebufferWidth / this.framebufferTextureWidth;
         float f2 = (float)this.framebufferHeight / this.framebufferTextureHeight;
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer.pos(0.0, g, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
         worldRenderer.pos(f, g, 0.0).tex(h, 0.0).color(255, 255, 255, 255).endVertex();
         worldRenderer.pos(f, 0.0, 0.0).tex(h, f2).color(255, 255, 255, 255).endVertex();
         worldRenderer.pos(0.0, 0.0, 0.0).tex(0.0, f2).color(255, 255, 255, 255).endVertex();
         tessellator.draw();
         this.unbindFramebufferTexture();
         GlStateManager.depthMask(true);
         GlStateManager.colorMask(true, true, true, true);
      }
   }

   public void framebufferClear() {
      this.bindFramebuffer(true);
      GlStateManager.clearColor(this.framebufferColor[0], this.framebufferColor[1], this.framebufferColor[2], this.framebufferColor[3]);
      short s = 16384;
      if (this.useDepth) {
         GlStateManager.clearDepth(1.0);
         s |= 256;
      }

      GlStateManager.clear(s);
      this.unbindFramebuffer();
   }
}
