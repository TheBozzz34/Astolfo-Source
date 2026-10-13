package net.minecraft.util;

import bypass.util.Stopwatch;
import net.minecraft.client.Minecraft;

public class Timer {
   public float ticksPerSecond;
   private double lastHRTime;
   public int elapsedTicks;
   public float renderPartialTicks;
   public float timerSpeed = 1.0F;
   public float elapsedPartialTicks;
   private long lastSyncSysClock;
   private long lastSyncHRClock;
   private long counter;
   private double timeSyncAdjustment = 1.0;
   public static Stopwatch q = new Stopwatch();
   private static final long b = 4549079364528L;

   public Timer(float var1) {
      this.ticksPerSecond = var1;
      this.lastSyncSysClock = Minecraft.getSystemTime();
      this.lastSyncHRClock = System.nanoTime() / 1000000L;
   }

   public void updateTimer() {
      long var4 = Minecraft.getSystemTime();
      long var6 = var4 - this.lastSyncSysClock;
      long var8 = System.nanoTime() / 1000000L;
      double var10 = var8 / 1000.0;
      if (var6 <= 1000L && var6 >= 0L) {
         this.counter += var6;
         if (this.counter > 1000L) {
            long var12 = var8 - this.lastSyncHRClock;
            double var14 = (double)this.counter / var12;
            this.timeSyncAdjustment = this.timeSyncAdjustment + (var14 - this.timeSyncAdjustment) * 0.2F;
            this.lastSyncHRClock = var8;
            this.counter = 0L;
         }

         if (this.counter < 0L) {
            this.lastSyncHRClock = var8;
         }
      } else {
         this.lastHRTime = var10;
      }

      this.lastSyncSysClock = var4;
      double var16 = (var10 - this.lastHRTime) * this.timeSyncAdjustment;
      this.lastHRTime = var10;
      var16 = MathHelper.clamp_double(var16, 0.0, 1.0);
      this.elapsedPartialTicks = (float)(this.elapsedPartialTicks + var16 * this.timerSpeed * this.ticksPerSecond);
      this.elapsedTicks = (int)this.elapsedPartialTicks;
      this.elapsedPartialTicks = this.elapsedPartialTicks - this.elapsedTicks;
      if (this.elapsedTicks > 10) {
         this.elapsedTicks = 10;
      }

      this.renderPartialTicks = this.elapsedPartialTicks;
   }
}
