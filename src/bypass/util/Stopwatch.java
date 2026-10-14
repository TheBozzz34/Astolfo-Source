package bypass.util;

import net.minecraft.client.Minecraft;

public final class Stopwatch {
   private long q = this.now();

   public boolean hasElapsed(float f) {
      return (float)this.getElapsed() >= f;
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
