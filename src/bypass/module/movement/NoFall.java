package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.exploits.RiderGod;
import bypass.setting.EnumSetting;
import bypass.util.GameUtil;
import bypass.util.Stopwatch;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

public final class NoFall extends Module {
   private final EnumSetting<NoFallMode> mode = new EnumSetting<>("Mode", "Set your nofall modes", NoFallMode.SPOOF);
   private ArrayList<Packet<?>> Q = new ArrayList<>();
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = var1x -> this.p();
   private int J;
   private boolean O;
   private double D;
   private Stopwatch v = new Stopwatch();
   private boolean t;
   private boolean X;
   private double N;
   private boolean Y;
   private boolean G;
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = var0 -> {
      if (var0.getDirection() == PacketDirection.RECEIVED) {
      }
   };
   @EventHandler(MoveEvent.class)
   private final Listener<MoveEvent> onMove = var0 -> {};
   @EventHandler(value = MotionEvent.class, priority = EventPriority.HIGH)
   private final Listener<MotionEvent> onMotion = var1x -> {
      NoFallMode var5 = this.mode.getValue();
      if (var1x.getState().equals(EventState.PRE)) {
         if (var1x.getMinY() < var1x.getLastY()) {
            this.D = this.D + (var1x.getLastY() - var1x.getMinY());
         }

         if (mc.thePlayer.isCollidedVertically || mc.thePlayer.onGround) {
            this.D = 0.0;
         }

         if (GameUtil.scoreboardTitleContains("PIT") || GameUtil.scoreboardTitleContains("UHC CHAMPIONS") && mc.thePlayer.getMaxHealth() == 20.0F) {
            return;
         }

         if (var5 == NoFallMode.NO_GROUND) {
            var1x.setOnGround(false);
            return;
         }

         if (this.h()) {
            switch (var5) {
               case SPOOF:
                  if (GameUtil.isOnHypixel()) {
                     if (this.D > 2.5) {
                        var1x.setOnGround(true);
                        this.D = 0.0;
                     }
                  } else {
                     var1x.setOnGround(true);
                  }
                  break;
               case AAC_3311:
                  if (mc.thePlayer.fallDistance > 2.0F) {
                     mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
                     mc.getNetHandler()
                        .addToSendQueue(
                           new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.001, mc.thePlayer.posZ, mc.thePlayer.onGround)
                        );
                     mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
                  }
                  break;
               case AAC_3315:
                  if (mc.thePlayer.fallDistance > 3.0F) {
                     if (!mc.isIntegratedServerRunning()) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, Double.NaN, mc.thePlayer.posZ, false));
                     }

                     mc.thePlayer.fallDistance = -9999.0F;
                  }
            }
         } else {
            this.O = true;
            this.J = 0;
         }

         if (var5 == NoFallMode.PACKET && P_boolean() && mc.thePlayer.posY > 0.0 && this.D >= 2.5 + (mc.thePlayer.isPotionActive(Potion.jump) ? 0.3 : 0.0)) {
            mc.thePlayer.sendQueue.u(new C03PacketPlayer(true));
            this.D = 0.0;
         }

         this.N = mc.thePlayer.fallDistance;
      }
   };
   private static final long c = 87600643255506L;

   public NoFall(Category var1) {
      super(var1);
      this.addSettings(this.mode);
   }

   private boolean h() {
      return this.D(3.0F);
   }

   private boolean D(float var1) {
      long var2 = c ^ 63802694004856L;
      long var4 = var2 ^ 73516519596313L;
      return mc.thePlayer.fallDistance >= var1
         && !mc.thePlayer.isSpectator()
         && !mc.thePlayer.capabilities.isCreativeMode
         && !mc.thePlayer.capabilities.allowFlying
         && mc.thePlayer.posY > 0.0
         && P_boolean()
         && !client.getModuleManager().get(RiderGod.class).isEnabled();
   }

   public static double X_double() {
      for (int var3 = (int)mc.thePlayer.posY; var3 > 0; var3--) {
         Block var4 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, var3, mc.thePlayer.posZ)).getBlock();
         if (var4 != Blocks.air) {
            return mc.thePlayer.posY - var3;
         }
      }

      return -999.0;
   }

   public static boolean P_boolean() {
      for (int var3 = (int)mc.thePlayer.posY; var3 >= 0; var3--) {
         Block var4 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, var3, mc.thePlayer.posZ)).getBlock();
         if (var4 != Blocks.air) {
            return true;
         }
      }

      return false;
   }

   public static boolean u_boolean() {
      for (double var3 = (int)mc.thePlayer.posY; var3 >= 0.0; var3--) {
         Block var5 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, var3, mc.thePlayer.posZ)).getBlock();
         if (var5 != Blocks.air) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void p() {
      this.setDisplayName("No Fall " + EnumChatFormatting.GRAY + NoFallMode.W(this.mode.getValue()));
      super.p();
   }
}
