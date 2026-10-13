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

   public LoadingScreenRenderer(Minecraft var1) {
      this.mc = var1;
      this.scaledResolution = new ScaledResolution(var1);
      this.framebuffer = new Framebuffer(var1.displayWidth, var1.displayHeight, false);
      this.framebuffer.setFramebufferFilter(9728);
   }

   @Override
   public void resetProgressAndMessage(String var1) {
      this.loadingSuccess = false;
      this.displayString(var1);
   }

   @Override
   public void displaySavingString(String var1) {
      this.loadingSuccess = true;
      this.displayString(var1);
   }

   private void displayString(String var1) {
      this.currentlyDisplayedText = var1;
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         if (OpenGlHelper.isFramebufferEnabled()) {
            int var5 = this.scaledResolution.getScaleFactor();
            GlStateManager.ortho(0.0, this.scaledResolution.getScaledWidth() * var5, this.scaledResolution.getScaledHeight() * var5, 0.0, 100.0, 300.0);
         } else {
            ScaledResolution var6 = new ScaledResolution(this.mc);
            GlStateManager.ortho(0.0, var6.getScaledWidth_double(), var6.getScaledHeight_double(), 0.0, 100.0, 300.0);
         }

         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -200.0F);
      }
   }

   @Override
   public void displayLoadingString(String var1) {
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         this.systemTime = 0L;
         this.message = var1;
         this.setLoadingProgress(-1);
         this.systemTime = 0L;
      }
   }

   @Override
   public void setLoadingProgress(int var1) {
      long var2 = a ^ 46248809135117L;
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         long var5 = Minecraft.getSystemTime();
         if (var5 - this.systemTime >= 100L) {
            this.systemTime = var5;
            ScaledResolution var7 = new ScaledResolution(this.mc);
            int var8 = var7.getScaleFactor();
            int var9 = var7.getScaledWidth();
            int var10 = var7.getScaledHeight();
            if (OpenGlHelper.isFramebufferEnabled()) {
               this.framebuffer.framebufferClear();
            } else {
               GlStateManager.clear(256);
            }

            this.framebuffer.bindFramebuffer(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            long var10000 = 5613537673383833317L ^ var2;
            GlStateManager.ortho(0.0, var7.getScaledWidth_double(), var7.getScaledHeight_double(), 0.0, 100.0, 300.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);
            if (!OpenGlHelper.isFramebufferEnabled()) {
               GlStateManager.clear(16640);
            }

            Tessellator var11 = Tessellator.getInstance();
            WorldRenderer var12 = var11.getWorldRenderer();
            this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
            float var13 = 32.0F;
            var12.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            var12.pos(0.0, var10, 0.0).tex(0.0, var10 / var13).color(64, 64, 64, 255).endVertex();
            var12.pos(var9, var10, 0.0).tex(var9 / var13, var10 / var13).color(64, 64, 64, 255).endVertex();
            var12.pos(var9, 0.0, 0.0).tex(var9 / var13, 0.0).color(64, 64, 64, 255).endVertex();
            var12.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(64, 64, 64, 255).endVertex();
            var11.draw();
            if (var1 >= 0) {
               byte var14 = 100;
               byte var15 = 2;
               int var16 = var9 / 2 - var14 / 2;
               int var17 = var10 / 2 + 16;
               GlStateManager.disableTexture2D();
               var12.begin(7, DefaultVertexFormats.POSITION_COLOR);
               var12.pos(var16, var17, 0.0).color(128, 128, 128, 255).endVertex();
               var12.pos(var16, var17 + var15, 0.0).color(128, 128, 128, 255).endVertex();
               var12.pos(var16 + var14, var17 + var15, 0.0).color(128, 128, 128, 255).endVertex();
               var12.pos(var16 + var14, var17, 0.0).color(128, 128, 128, 255).endVertex();
               var12.pos(var16, var17, 0.0).color(128, 255, 128, 255).endVertex();
               var12.pos(var16, var17 + var15, 0.0).color(128, 255, 128, 255).endVertex();
               var12.pos(var16 + var1, var17 + var15, 0.0).color(128, 255, 128, 255).endVertex();
               var12.pos(var16 + var1, var17, 0.0).color(128, 255, 128, 255).endVertex();
               var11.draw();
               GlStateManager.enableTexture2D();
            }

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            this.mc
               .fontRendererObj
               .drawStringWithShadow(
                  this.currentlyDisplayedText, (var9 - this.mc.fontRendererObj.getStringWidth(this.currentlyDisplayedText)) / 2, var10 / 2 - 4 - 16, 16777215
               );
            this.mc
               .fontRendererObj
               .drawStringWithShadow(this.message, (var9 - this.mc.fontRendererObj.getStringWidth(this.message)) / 2, var10 / 2 - 4 + 8, 16777215);
            this.framebuffer.unbindFramebuffer();
            if (OpenGlHelper.isFramebufferEnabled() && Config.isFastRender()) {
               this.framebuffer.framebufferRender(var9 * var8, var10 * var8);
            }

            this.mc.updateDisplay();

            try {
               Thread.yield();
            } catch (Exception var18) {
            }
         }
      }
   }

   @Override
   public void setDoneWorking() {
   }
}
