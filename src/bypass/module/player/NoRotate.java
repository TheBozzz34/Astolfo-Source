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
   private Listener<PacketEvent> onPacket = var0 -> {
      if (var0.getDirection() == PacketDirection.RECEIVED
         && var0.getPacket() instanceof S08PacketPlayerPosLook
         && mc.theWorld != null
         && mc.thePlayer != null
         && mc.getNetHandler().doneLoadingTerrain
         && mc.thePlayer.getHealth() > 0.0
         && mc.thePlayer.ticksExisted > 0) {
         S08PacketPlayerPosLook var4 = (S08PacketPlayerPosLook)var0.getPacket();
         double var5 = var4.getX();
         double var7 = var4.getY();
         double var9 = var4.getZ();
         float var11 = var4.getYaw();
         float var12 = var4.getPitch();
         if (var4.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
            var5 += mc.thePlayer.posX;
         }

         if (var4.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
            var7 += mc.thePlayer.posY;
         }

         if (var4.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
            var9 += mc.thePlayer.posZ;
         }

         if (var4.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
            var12 += mc.thePlayer.rotationPitch;
         }

         if (var4.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
            var11 += mc.thePlayer.rotationYaw;
         }

         mc.thePlayer.sendQueue.u(new C03PacketPlayer.C06PacketPlayerPosLook(var5, var7, var9, var11, var12, false));
         var4.P(mc.thePlayer.rotationYaw);
         var4.B(mc.thePlayer.rotationPitch);
      }
   };
   private static final long c = 52863605125408L;

   public NoRotate(Category var1) {
      super(var1);
      this.setHidden(false);
   }
}
