package bypass.module.other;

import bypass.event.CancellableEvent;
import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.network.ResourcePackEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.notification.NotificationType;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;

public final class PackSpoofer extends Module {
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var0 -> {
      long var1x = c ^ 118589060737201L;
      long var3 = var1x ^ 133442491471647L;
      if (var0.getDirection() == PacketDirection.RECEIVED && var0.getPacket() instanceof S48PacketResourcePackSend && mc.thePlayer != null) {
         S48PacketResourcePackSend var6 = (S48PacketResourcePackSend)var0.getPacket();
         mc.thePlayer.sendQueue.addToSendQueue(new C19PacketResourcePackStatus(var6.r() == null ? "" : var6.r(), C19PacketResourcePackStatus.Action.ACCEPTED));
         mc.thePlayer
            .sendQueue
            .addToSendQueue(new C19PacketResourcePackStatus(var6.r() == null ? "" : var6.r(), C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
         client.getNotificationManager().i("Pack spoofed", String.format("Pack spoofed (%s - %s)", var6.R(), var6.r()), NotificationType.SUCCESS, (int)h).K();
         var0.cancel();
      }
   };
   @EventHandler(ResourcePackEvent.class)
   private Listener<ResourcePackEvent> onResourcePack = CancellableEvent::cancel;
   private static final long c = 93086512628469L;
   private static final long h = 6783079568975596972L;

   public PackSpoofer(Category var1) {
      super(var1);
      this.setHidden(false);
   }
}
