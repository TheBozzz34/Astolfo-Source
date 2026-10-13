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
      long var3 = System.currentTimeMillis();
      long var5 = getMemoryUsed();
      boolean var7 = false;
      if (var5 < memLast) {
         double var8 = memDiff / 1000000.0;
         double var10 = memTimeDiffMs / 1000.0;
         int var12 = (int)(var8 / var10);
         if (var12 > 0) {
            memMbSec = var12;
         }

         memTimeStartMs = var3;
         memStart = var5;
         memTimeDiffMs = 0L;
         memDiff = 0L;
         var7 = true;
      } else {
         memTimeDiffMs = var3 - memTimeStartMs;
         memDiff = var5 - memStart;
      }

      memTimeLast = var3;
      memLast = var5;
      return var7;
   }

   private static long getMemoryUsed() {
      Runtime var0 = Runtime.getRuntime();
      return var0.totalMemory() - var0.freeMemory();
   }

   public static void updateLagometer() {
      if (mc == null) {
         mc = Minecraft.getMinecraft();
         gameSettings = mc.gameSettings;
         profiler = mc.mcProfiler;
      }

      if (gameSettings.n && (gameSettings.ZH || gameSettings.A_)) {
         active = true;
         long var3 = System.nanoTime();
         if (m == -1L) {
            m = var3;
         } else {
            int var5 = numRecordedFrameTimes & timesFrame.length - 1;
            numRecordedFrameTimes++;
            boolean var6 = updateMemoryAllocation();
            timesFrame[var5] = var3 - m - v;
            timesTick[var5] = timerTick.timeNano;
            timesScheduledExecutables[var5] = timerScheduledExecutables.timeNano;
            timesChunkUpload[var5] = timerChunkUpload.timeNano;
            timesChunkUpdate[var5] = timerChunkUpdate.timeNano;
            timesVisibility[var5] = timerVisibility.timeNano;
            timesTerrain[var5] = timerTerrain.timeNano;
            timesServer[var5] = timerServer.timeNano;
            gcs[var5] = var6;
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

   public static void showLagometer(ScaledResolution var0) {
      long var1 = a ^ 126159213800109L;
      if (gameSettings != null && (gameSettings.ZH || gameSettings.A_)) {
         long var4 = System.nanoTime();
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.pushMatrix();
         long var10000 = 6172834651753333208L ^ var1;
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, mc.displayWidth, mc.displayHeight, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         Tessellator var6 = Tessellator.getInstance();
         WorldRenderer var7 = var6.getWorldRenderer();
         var7.begin(1, DefaultVertexFormats.POSITION_COLOR);

         for (int var8 = 0; var8 < timesFrame.length; var8++) {
            int var9 = (var8 - numRecordedFrameTimes & timesFrame.length - 1) * 100 / timesFrame.length;
            var9 += 155;
            float var10 = mc.displayHeight;
            long var11 = 0L;
            if (gcs[var8]) {
               renderTime(var8, timesFrame[var8], var9, var9 / 2, 0, var10, var7);
            } else {
               renderTime(var8, timesFrame[var8], var9, var9, var9, var10, var7);
               var10 -= (float)renderTime(var8, timesServer[var8], var9 / 2, var9 / 2, var9 / 2, var10, var7);
               var10 -= (float)renderTime(var8, timesTerrain[var8], 0, var9, 0, var10, var7);
               var10 -= (float)renderTime(var8, timesVisibility[var8], var9, var9, 0, var10, var7);
               var10 -= (float)renderTime(var8, timesChunkUpdate[var8], var9, 0, 0, var10, var7);
               var10 -= (float)renderTime(var8, timesChunkUpload[var8], var9, 0, var9, var10, var7);
               var10 -= (float)renderTime(var8, timesScheduledExecutables[var8], 0, 0, var9, var10, var7);
               float var13 = var10 - (float)renderTime(var8, timesTick[var8], 0, var9, var9, var10, var7);
            }
         }

         renderTimeDivider(0, timesFrame.length, 33333333L, 196, 196, 196, mc.displayHeight, var7);
         renderTimeDivider(0, timesFrame.length, 16666666L, 196, 196, 196, mc.displayHeight, var7);
         var6.draw();
         GlStateManager.enableTexture2D();
         int var18 = mc.displayHeight - 80;
         int var20 = mc.displayHeight - 160;
         mc.fontRendererObj.D("30", 2.0F, var20 + 1, -8947849);
         mc.fontRendererObj.D("30", 1.0F, var20, -3881788);
         mc.fontRendererObj.D("60", 2.0F, var18 + 1, -8947849);
         mc.fontRendererObj.D("60", 1.0F, var18, -3881788);
         GlStateManager.matrixMode(5889);
         GlStateManager.popMatrix();
         var10000 = 2959871147967154618L ^ var1;
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         float var27 = 1.0F - (float)((System.currentTimeMillis() - memTimeStartMs) / 1000.0);
         var27 = Config.limit(var27, 0.0F, 1.0F);
         int var29 = (int)(170.0F + var27 * 85.0F);
         int var12 = (int)(100.0F + var27 * 55.0F);
         int var30 = (int)(10.0F + var27 * 10.0F);
         int var14 = var29 << 16 | var12 << 8 | var30;
         int var15 = 512 / var0.getScaleFactor() + 2;
         int var16 = mc.displayHeight / var0.getScaleFactor() - 8;
         GuiIngame var17 = mc.ingameGUI;
         GuiIngame.drawRect(var15 - 1, var16 - 1, var15 + 50, var16 + 10, -1605349296);
         mc.fontRendererObj.D(" " + memMbSec + " MB/s", var15, var16, var14);
         v = System.nanoTime() - var4;
      }
   }

   private static long renderTime(int var0, long var1, int var3, int var4, int var5, float var6, WorldRenderer var7) {
      long var11 = var1 / 200000L;
      if (var11 < 3L) {
         return 0L;
      }

      var7.pos(var0 + 0.5F, var6 - (float)var11 + 0.5F, 0.0).color(var3, var4, var5, 255).endVertex();
      var7.pos(var0 + 0.5F, var6 + 0.5F, 0.0).color(var3, var4, var5, 255).endVertex();
      return var11;
   }

   private static long renderTimeDivider(int var0, int var1, long var2, int var4, int var5, int var6, float var7, WorldRenderer var8) {
      long var12 = var2 / 200000L;
      if (var12 < 3L) {
         return 0L;
      }

      var8.pos(var0 + 0.5F, var7 - (float)var12 + 0.5F, 0.0).color(var4, var5, var6, 255).endVertex();
      var8.pos(var1 + 0.5F, var7 - (float)var12 + 0.5F, 0.0).color(var4, var5, var6, 255).endVertex();
      return var12;
   }

   public static boolean isActive() {
      return active;
   }

   static {
      long var20 = a ^ 132449037101838L;
      long var10000 = 2466116030147702028L ^ var20;
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

      public static void A(Lagometer.TimerNano var0) {
         var0.reset();
      }
   }
}
