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

   public Timer(float tps) {
      this.ticksPerSecond = tps;
      this.lastSyncSysClock = Minecraft.getSystemTime();
      this.lastSyncHRClock = System.nanoTime() / 1000000L;
   }

   public void updateTimer() {
      long l = Minecraft.getSystemTime();
      long m = l - this.lastSyncSysClock;
      long n = System.nanoTime() / 1000000L;
      double d = n / 1000.0;
      if (m <= 1000L && m >= 0L) {
         this.counter += m;
         if (this.counter > 1000L) {
            long l2 = n - this.lastSyncHRClock;
            double e = (double)this.counter / l2;
            this.timeSyncAdjustment = this.timeSyncAdjustment + (e - this.timeSyncAdjustment) * 0.2F;
            this.lastSyncHRClock = n;
            this.counter = 0L;
         }

         if (this.counter < 0L) {
            this.lastSyncHRClock = n;
         }
      } else {
         this.lastHRTime = d;
      }

      this.lastSyncSysClock = l;
      double d2 = (d - this.lastHRTime) * this.timeSyncAdjustment;
      this.lastHRTime = d;
      d2 = MathHelper.clamp_double(d2, 0.0, 1.0);
      this.elapsedPartialTicks = (float)(this.elapsedPartialTicks + d2 * this.timerSpeed * this.ticksPerSecond);
      this.elapsedTicks = (int)this.elapsedPartialTicks;
      this.elapsedPartialTicks = this.elapsedPartialTicks - this.elapsedTicks;
      if (this.elapsedTicks > 10) {
         this.elapsedTicks = 10;
      }

      this.renderPartialTicks = this.elapsedPartialTicks;
   }
}
