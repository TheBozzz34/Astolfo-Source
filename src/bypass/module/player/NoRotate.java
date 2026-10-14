package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public final class NoRotate extends Module {
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED
         && packetEvent.getPacket() instanceof S08PacketPlayerPosLook
         && mc.theWorld != null
         && mc.thePlayer != null
         && mc.getNetHandler().doneLoadingTerrain
         && mc.thePlayer.getHealth() > 0.0
         && mc.thePlayer.ticksExisted > 0) {
         S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook)packetEvent.getPacket();
         double d = s08PacketPlayerPosLook.getX();
         double e = s08PacketPlayerPosLook.getY();
         double d2 = s08PacketPlayerPosLook.getZ();
         float f = s08PacketPlayerPosLook.getYaw();
         float g = s08PacketPlayerPosLook.getPitch();
         if (s08PacketPlayerPosLook.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
            d += mc.thePlayer.posX;
         }

         if (s08PacketPlayerPosLook.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
            e += mc.thePlayer.posY;
         }

         if (s08PacketPlayerPosLook.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
            d2 += mc.thePlayer.posZ;
         }

         if (s08PacketPlayerPosLook.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
            g += mc.thePlayer.rotationPitch;
         }

         if (s08PacketPlayerPosLook.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
            f += mc.thePlayer.rotationYaw;
         }

         mc.thePlayer.sendQueue.u(new C03PacketPlayer.C06PacketPlayerPosLook(d, e, d2, f, g, false));
         s08PacketPlayerPosLook.P(mc.thePlayer.rotationYaw);
         s08PacketPlayerPosLook.B(mc.thePlayer.rotationPitch);
      }
   };

   public NoRotate(Category category) {
      super(category);
      this.setHidden(false);
   }
}
