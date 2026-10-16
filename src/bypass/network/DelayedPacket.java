package bypass.network;

import net.minecraft.network.Packet;

public class DelayedPacket {
   private float delay;
   private final Packet<?> packet;

   public DelayedPacket(float f, Packet<?> packet) {
      this.delay = f;
      this.packet = packet;
   }
}
