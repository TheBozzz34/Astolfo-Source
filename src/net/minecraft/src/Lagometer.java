package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.profiler.Profiler;
import org.lwjgl.opengl.GL11;

public class Lagometer {
   private static Minecraft mc;
   private static GameSettings gameSettings;
   private static Profiler profiler;
   public static boolean active = false;
   public static Lagometer.TimerNano timerTick = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerScheduledExecutables = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerChunkUpload = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerChunkUpdate = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerVisibility = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerTerrain = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerServer = new Lagometer.TimerNano();
   private static long[] timesFrame = new long[512];
   private static long[] timesTick = new long[512];
   private static long[] timesScheduledExecutables = new long[512];
   private static long[] timesChunkUpload = new long[512];
   private static long[] timesChunkUpdate = new long[512];
   private static long[] timesVisibility = new long[512];
   private static long[] timesTerrain = new long[512];
   private static long[] timesServer = new long[512];
   private static boolean[] gcs = new boolean[512];
   private static int numRecordedFrameTimes = 0;
   private static long prevFrameTimeNano = -1L;
   private static long renderTimeNano = 0L;
   private static long memTimeStartMs = System.currentTimeMillis();
   private static long memStart = getMemoryUsed();
   private static long memTimeLast = memTimeStartMs;
   private static long memLast = memStart;
   private static long memTimeDiffMs = 1L;
   private static long memDiff = 0L;
   private static int memMbSec = 0;

   public static boolean updateMemoryAllocation() {
      long l = System.currentTimeMillis();
      long m = getMemoryUsed();
      boolean bl = false;
      if (m < memLast) {
         double d = memDiff / 1000000.0;
         double e = memTimeDiffMs / 1000.0;
         int i = (int)(d / e);
         if (i > 0) {
            memMbSec = i;
         }

         memTimeStartMs = l;
         memStart = m;
         memTimeDiffMs = 0L;
         memDiff = 0L;
         bl = true;
      } else {
         memTimeDiffMs = l - memTimeStartMs;
         memDiff = m - memStart;
      }

      memTimeLast = l;
      memLast = m;
      return bl;
   }

   private static long getMemoryUsed() {
      Runtime r = Runtime.getRuntime();
      return r.totalMemory() - r.freeMemory();
   }

   public static void updateLagometer() {
      if (mc == null) {
         mc = Minecraft.getMinecraft();
         gameSettings = mc.gameSettings;
         profiler = mc.mcProfiler;
      }

      if (gameSettings.showDebugInfo && (gameSettings.ofLagometer || gameSettings.showLagometer)) {
         active = true;
         long l = System.nanoTime();
         if (prevFrameTimeNano == -1L) {
            prevFrameTimeNano = l;
         } else {
            int i = numRecordedFrameTimes & timesFrame.length - 1;
            numRecordedFrameTimes++;
            boolean bl = updateMemoryAllocation();
            timesFrame[i] = l - prevFrameTimeNano - renderTimeNano;
            timesTick[i] = timerTick.timeNano;
            timesScheduledExecutables[i] = timerScheduledExecutables.timeNano;
            timesChunkUpload[i] = timerChunkUpload.timeNano;
            timesChunkUpdate[i] = timerChunkUpdate.timeNano;
            timesVisibility[i] = timerVisibility.timeNano;
            timesTerrain[i] = timerTerrain.timeNano;
            timesServer[i] = timerServer.timeNano;
            gcs[i] = bl;
            Lagometer.TimerNano.resetTimer(timerTick);
            Lagometer.TimerNano.resetTimer(timerScheduledExecutables);
            Lagometer.TimerNano.resetTimer(timerVisibility);
            Lagometer.TimerNano.resetTimer(timerChunkUpdate);
            Lagometer.TimerNano.resetTimer(timerChunkUpload);
            Lagometer.TimerNano.resetTimer(timerTerrain);
            Lagometer.TimerNano.resetTimer(timerServer);
            prevFrameTimeNano = System.nanoTime();
         }
      } else {
         active = false;
         prevFrameTimeNano = -1L;
      }
   }

   public static void showLagometer(ScaledResolution scaledResolution) {
      if (gameSettings != null && (gameSettings.ofLagometer || gameSettings.showLagometer)) {
         long l = System.nanoTime();
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.pushMatrix();
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, mc.displayWidth, mc.displayHeight, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);

         for (int i = 0; i < timesFrame.length; i++) {
            int j = (i - numRecordedFrameTimes & timesFrame.length - 1) * 100 / timesFrame.length;
            j += 155;
            float f = mc.displayHeight;
            long m = 0L;
            if (gcs[i]) {
               renderTime(i, timesFrame[i], j, j / 2, 0, f, worldRenderer);
            } else {
               renderTime(i, timesFrame[i], j, j, j, f, worldRenderer);
               f -= (float)renderTime(i, timesServer[i], j / 2, j / 2, j / 2, f, worldRenderer);
               f -= (float)renderTime(i, timesTerrain[i], 0, j, 0, f, worldRenderer);
               f -= (float)renderTime(i, timesVisibility[i], j, j, 0, f, worldRenderer);
               f -= (float)renderTime(i, timesChunkUpdate[i], j, 0, 0, f, worldRenderer);
               f -= (float)renderTime(i, timesChunkUpload[i], j, 0, j, f, worldRenderer);
               f -= (float)renderTime(i, timesScheduledExecutables[i], 0, 0, j, f, worldRenderer);
               float g = f - (float)renderTime(i, timesTick[i], 0, j, j, f, worldRenderer);
            }
         }

         renderTimeDivider(0, timesFrame.length, 33333333L, 196, 196, 196, mc.displayHeight, worldRenderer);
         renderTimeDivider(0, timesFrame.length, 16666666L, 196, 196, 196, mc.displayHeight, worldRenderer);
         tessellator.draw();
         GlStateManager.enableTexture2D();
         int k = mc.displayHeight - 80;
         int n = mc.displayHeight - 160;
         mc.fontRendererObj.drawStringNoShadow("30", 2.0F, n + 1, -8947849);
         mc.fontRendererObj.drawStringNoShadow("30", 1.0F, n, -3881788);
         mc.fontRendererObj.drawStringNoShadow("60", 2.0F, k + 1, -8947849);
         mc.fontRendererObj.drawStringNoShadow("60", 1.0F, k, -3881788);
         GlStateManager.matrixMode(5889);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         float h = 1.0F - (float)((System.currentTimeMillis() - memTimeStartMs) / 1000.0);
         h = Config.limit(h, 0.0F, 1.0F);
         int i2 = (int)(170.0F + h * 85.0F);
         int i3 = (int)(100.0F + h * 55.0F);
         int i4 = (int)(10.0F + h * 10.0F);
         int i5 = i2 << 16 | i3 << 8 | i4;
         int i6 = 512 / scaledResolution.getScaleFactor() + 2;
         int i7 = mc.displayHeight / scaledResolution.getScaleFactor() - 8;
         GuiIngame guiIngame = mc.ingameGUI;
         GuiIngame.drawRect(i6 - 1, i7 - 1, i6 + 50, i7 + 10, -1605349296);
         mc.fontRendererObj.drawStringNoShadow(" " + memMbSec + " MB/s", i6, i7, i5);
         renderTimeNano = System.nanoTime() - l;
      }
   }

   private static long renderTime(int frameNum, long time, int r, int g, int b, float baseHeight, WorldRenderer tessellator) {
      long l = time / 200000L;
      if (l < 3L) {
         return 0L;
      }

      tessellator.pos(frameNum + 0.5F, baseHeight - (float)l + 0.5F, 0.0).color(r, g, b, 255).endVertex();
      tessellator.pos(frameNum + 0.5F, baseHeight + 0.5F, 0.0).color(r, g, b, 255).endVertex();
      return l;
   }

   private static long renderTimeDivider(int frameStart, int frameEnd, long time, int r, int g, int b, float baseHeight, WorldRenderer tessellator) {
      long l = time / 200000L;
      if (l < 3L) {
         return 0L;
      }

      tessellator.pos(frameStart + 0.5F, baseHeight - (float)l + 0.5F, 0.0).color(r, g, b, 255).endVertex();
      tessellator.pos(frameEnd + 0.5F, baseHeight - (float)l + 0.5F, 0.0).color(r, g, b, 255).endVertex();
      return l;
   }

   public static boolean isActive() {
      return active;
   }

   public static class TimerNano {
      public long timeStartNano = 0L;
      public long timeNano = 0L;

      public void start() {
         if (Lagometer.active && this.timeStartNano == 0L) {
            this.timeStartNano = System.nanoTime();
         }
      }

      public void end() {
         if (Lagometer.active && this.timeStartNano != 0L) {
            this.timeNano = this.timeNano + (System.nanoTime() - this.timeStartNano);
            this.timeStartNano = 0L;
         }
      }

      private void reset() {
         this.timeNano = 0L;
         this.timeStartNano = 0L;
      }

      public static void resetTimer(Lagometer.TimerNano timerNano) {
         timerNano.reset();
      }
   }
}
