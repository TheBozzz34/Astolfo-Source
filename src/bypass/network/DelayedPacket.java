package bypass.network;

import net.minecraft.network.Packet;

public class DelayedPacket {
   private float g;
   private final Packet<?> A;

   public DelayedPacket(float var1, Packet<?> var2) {
      this.g = var1;
      this.A = var2;
   }
}
