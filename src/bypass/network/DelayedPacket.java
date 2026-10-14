package bypass.network;

import net.minecraft.network.Packet;

public class DelayedPacket {
   private float g;
   private final Packet<?> A;

   public DelayedPacket(float f, Packet<?> packet) {
      this.g = f;
      this.A = packet;
   }
}
