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
   private static long m = -1L;
   private static long v = 0L;
   private static long memTimeStartMs = System.currentTimeMillis();
   private static long memStart = getMemoryUsed();
   private static long memTimeLast = memTimeStartMs;
   private static long memLast = memStart;
   private static long memTimeDiffMs = 1L;
   private static long memDiff = 0L;
   private static int memMbSec = 0;
   private static final long a = 136562733952441L;

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

      if (gameSettings.n && (gameSettings.ZH || gameSettings.A_)) {
         active = true;
         long l = System.nanoTime();
         if (m == -1L) {
            m = l;
         } else {
            int i = numRecordedFrameTimes & timesFrame.length - 1;
            numRecordedFrameTimes++;
            boolean bl = updateMemoryAllocation();
            timesFrame[i] = l - m - v;
            timesTick[i] = timerTick.timeNano;
            timesScheduledExecutables[i] = timerScheduledExecutables.timeNano;
            timesChunkUpload[i] = timerChunkUpload.timeNano;
            timesChunkUpdate[i] = timerChunkUpdate.timeNano;
            timesVisibility[i] = timerVisibility.timeNano;
            timesTerrain[i] = timerTerrain.timeNano;
            timesServer[i] = timerServer.timeNano;
            gcs[i] = bl;
            Lagometer.TimerNano.A(timerTick);
            Lagometer.TimerNano.A(timerScheduledExecutables);
            Lagometer.TimerNano.A(timerVisibility);
            Lagometer.TimerNano.A(timerChunkUpdate);
            Lagometer.TimerNano.A(timerChunkUpload);
            Lagometer.TimerNano.A(timerTerrain);
            Lagometer.TimerNano.A(timerServer);
            m = System.nanoTime();
         }
      } else {
         active = false;
         m = -1L;
      }
   }

   public static void showLagometer(ScaledResolution scaledResolution) {
      long l = a ^ 126159213800109L;
      if (gameSettings != null && (gameSettings.ZH || gameSettings.A_)) {
         long m = System.nanoTime();
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.pushMatrix();
         long n = 6172834651753333208L ^ l;
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
            long l2 = 0L;
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
         int i2 = mc.displayHeight - 160;
         mc.fontRendererObj.D("30", 2.0F, i2 + 1, -8947849);
         mc.fontRendererObj.D("30", 1.0F, i2, -3881788);
         mc.fontRendererObj.D("60", 2.0F, k + 1, -8947849);
         mc.fontRendererObj.D("60", 1.0F, k, -3881788);
         GlStateManager.matrixMode(5889);
         GlStateManager.popMatrix();
         n = 2959871147967154618L ^ l;
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         float h = 1.0F - (float)((System.currentTimeMillis() - memTimeStartMs) / 1000.0);
         h = Config.limit(h, 0.0F, 1.0F);
         int i3 = (int)(170.0F + h * 85.0F);
         int i4 = (int)(100.0F + h * 55.0F);
         int i5 = (int)(10.0F + h * 10.0F);
         int i6 = i3 << 16 | i4 << 8 | i5;
         int i7 = 512 / scaledResolution.getScaleFactor() + 2;
         int i8 = mc.displayHeight / scaledResolution.getScaleFactor() - 8;
         GuiIngame guiIngame = mc.ingameGUI;
         GuiIngame.drawRect(i7 - 1, i8 - 1, i7 + 50, i8 + 10, -1605349296);
         mc.fontRendererObj.D(" " + memMbSec + " MB/s", i7, i8, i6);
         v = System.nanoTime() - m;
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

   static {
      long l = a ^ 132449037101838L;
      long m = 2466116030147702028L ^ l;
   }

   public static class TimerNano {
      public long timeStartNano = 0L;
      public long timeNano = 0L;
      private static final long a = 17849212439077L;

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

      public static void A(Lagometer.TimerNano timerNano) {
         timerNano.reset();
      }
   }
}
