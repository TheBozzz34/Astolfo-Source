package net.minecraft.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.src.Config;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;

public class LoadingScreenRenderer implements IProgressUpdate {
   private String message = "";
   private Minecraft mc;
   private String currentlyDisplayedText = "";
   private long systemTime = Minecraft.getSystemTime();
   private boolean loadingSuccess;
   private ScaledResolution scaledResolution;
   private Framebuffer framebuffer;
   private static final long a = 83726422598529L;

   public LoadingScreenRenderer(Minecraft mcIn) {
      this.mc = mcIn;
      this.scaledResolution = new ScaledResolution(mcIn);
      this.framebuffer = new Framebuffer(mcIn.displayWidth, mcIn.displayHeight, false);
      this.framebuffer.setFramebufferFilter(9728);
   }

   @Override
   public void resetProgressAndMessage(String message) {
      this.loadingSuccess = false;
      this.displayString(message);
   }

   @Override
   public void displaySavingString(String message) {
      this.loadingSuccess = true;
      this.displayString(message);
   }

   private void displayString(String message) {
      this.currentlyDisplayedText = message;
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         if (OpenGlHelper.isFramebufferEnabled()) {
            int i = this.scaledResolution.getScaleFactor();
            GlStateManager.ortho(0.0, this.scaledResolution.getScaledWidth() * i, this.scaledResolution.getScaledHeight() * i, 0.0, 100.0, 300.0);
         } else {
            ScaledResolution scaledResolution2 = new ScaledResolution(this.mc);
            GlStateManager.ortho(0.0, scaledResolution2.getScaledWidth_double(), scaledResolution2.getScaledHeight_double(), 0.0, 100.0, 300.0);
         }

         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -200.0F);
      }
   }

   @Override
   public void displayLoadingString(String message) {
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         this.systemTime = 0L;
         this.message = message;
         this.setLoadingProgress(-1);
         this.systemTime = 0L;
      }
   }

   @Override
   public void setLoadingProgress(int progress) {
      long l = a ^ 46248809135117L;
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         long m = Minecraft.getSystemTime();
         if (m - this.systemTime >= 100L) {
            this.systemTime = m;
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            int i = scaledResolution.getScaleFactor();
            int j = scaledResolution.getScaledWidth();
            int k = scaledResolution.getScaledHeight();
            if (OpenGlHelper.isFramebufferEnabled()) {
               this.framebuffer.framebufferClear();
            } else {
               GlStateManager.clear(256);
            }

            this.framebuffer.bindFramebuffer(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            long n = 5613537673383833317L ^ l;
            GlStateManager.ortho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, 100.0, 300.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);
            if (!OpenGlHelper.isFramebufferEnabled()) {
               GlStateManager.clear(16640);
            }

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
            float f = 32.0F;
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(0.0, k, 0.0).tex(0.0, k / f).color(64, 64, 64, 255).endVertex();
            worldRenderer.pos(j, k, 0.0).tex(j / f, k / f).color(64, 64, 64, 255).endVertex();
            worldRenderer.pos(j, 0.0, 0.0).tex(j / f, 0.0).color(64, 64, 64, 255).endVertex();
            worldRenderer.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(64, 64, 64, 255).endVertex();
            tessellator.draw();
            if (progress >= 0) {
               byte b = 100;
               byte b2 = 2;
               int i2 = j / 2 - b / 2;
               int i3 = k / 2 + 16;
               GlStateManager.disableTexture2D();
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
               worldRenderer.pos(i2, i3, 0.0).color(128, 128, 128, 255).endVertex();
               worldRenderer.pos(i2, i3 + b2, 0.0).color(128, 128, 128, 255).endVertex();
               worldRenderer.pos(i2 + b, i3 + b2, 0.0).color(128, 128, 128, 255).endVertex();
               worldRenderer.pos(i2 + b, i3, 0.0).color(128, 128, 128, 255).endVertex();
               worldRenderer.pos(i2, i3, 0.0).color(128, 255, 128, 255).endVertex();
               worldRenderer.pos(i2, i3 + b2, 0.0).color(128, 255, 128, 255).endVertex();
               worldRenderer.pos(i2 + progress, i3 + b2, 0.0).color(128, 255, 128, 255).endVertex();
               worldRenderer.pos(i2 + progress, i3, 0.0).color(128, 255, 128, 255).endVertex();
               tessellator.draw();
               GlStateManager.enableTexture2D();
            }

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            this.mc
               .fontRendererObj
               .drawStringWithShadow(
                  this.currentlyDisplayedText, (j - this.mc.fontRendererObj.getStringWidth(this.currentlyDisplayedText)) / 2, k / 2 - 4 - 16, 16777215
               );
            this.mc
               .fontRendererObj
               .drawStringWithShadow(this.message, (j - this.mc.fontRendererObj.getStringWidth(this.message)) / 2, k / 2 - 4 + 8, 16777215);
            this.framebuffer.unbindFramebuffer();
            if (OpenGlHelper.isFramebufferEnabled() && Config.isFastRender()) {
               this.framebuffer.framebufferRender(j * i, k * i);
            }

            this.mc.updateDisplay();

            try {
               Thread.yield();
            } catch (Exception exception) {
            }
         }
      }
   }

   @Override
   public void setDoneWorking() {
   }
}
