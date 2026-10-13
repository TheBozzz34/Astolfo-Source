package bypass.util;

import net.minecraft.client.Minecraft;

public final class Stopwatch {
   private long q = this.now();
   private static final long a = 18497897150866L;

   public boolean hasElapsed(float var1) {
      return (float)this.getElapsed() >= var1;
   }

   public long getElapsed() {
      return this.now() - this.q;
   }

   public void reset() {
      this.q = this.now();
   }

   public long now() {
      return Minecraft.getSystemTime();
   }
}
