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
   private ArrayList<Packet<?>> packetQueue = new ArrayList<>();
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = updateEvent -> this.updateDisplayName();
   private int unusedCounter;
   private boolean notFalling;
   private double fallenDistance;
   private Stopwatch unusedTimer = new Stopwatch();
   private boolean unusedFlagA;
   private boolean unusedFlagB;
   private double lastFallDistance;
   private boolean unusedFlagC;
   private boolean unusedFlagD;
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
      }
   };
   @EventHandler(MoveEvent.class)
   private final Listener<MoveEvent> onMove = moveEvent -> {};
   @EventHandler(value = MotionEvent.class, priority = EventPriority.HIGH)
   private final Listener<MotionEvent> onMotion = motionEvent -> {
      NoFallMode noFallMode = this.mode.getValue();
      if (motionEvent.getState().equals(EventState.PRE)) {
         if (motionEvent.getMinY() < motionEvent.getLastY()) {
            this.fallenDistance = this.fallenDistance + (motionEvent.getLastY() - motionEvent.getMinY());
         }

         if (mc.thePlayer.isCollidedVertically || mc.thePlayer.onGround) {
            this.fallenDistance = 0.0;
         }

         if (GameUtil.scoreboardTitleContains("PIT") || GameUtil.scoreboardTitleContains("UHC CHAMPIONS") && mc.thePlayer.getMaxHealth() == 20.0F) {
            return;
         }

         if (noFallMode == NoFallMode.NO_GROUND) {
            motionEvent.setOnGround(false);
            return;
         }

         if (this.shouldPreventFallDefault()) {
            switch (noFallMode) {
               case SPOOF:
                  if (GameUtil.isOnHypixel()) {
                     if (this.fallenDistance > 2.5) {
                        motionEvent.setOnGround(true);
                        this.fallenDistance = 0.0;
                     }
                  } else {
                     motionEvent.setOnGround(true);
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
            this.notFalling = true;
            this.unusedCounter = 0;
         }

         if (noFallMode == NoFallMode.PACKET
            && P_boolean()
            && mc.thePlayer.posY > 0.0
            && this.fallenDistance >= 2.5 + (mc.thePlayer.isPotionActive(Potion.jump) ? 0.3 : 0.0)) {
            mc.thePlayer.sendQueue.sendPacketNoEvent(new C03PacketPlayer(true));
            this.fallenDistance = 0.0;
         }

         this.lastFallDistance = mc.thePlayer.fallDistance;
      }
   };

   public NoFall(Category category) {
      super(category);
      this.addSettings(this.mode);
   }

   private boolean shouldPreventFallDefault() {
      return this.shouldPreventFall(3.0F);
   }

   private boolean shouldPreventFall(float f) {
      return mc.thePlayer.fallDistance >= f
         && !mc.thePlayer.isSpectator()
         && !mc.thePlayer.capabilities.isCreativeMode
         && !mc.thePlayer.capabilities.allowFlying
         && mc.thePlayer.posY > 0.0
         && P_boolean()
         && !client.getModuleManager().get(RiderGod.class).isEnabled();
   }

   public static double X_double() {
      for (int i = (int)mc.thePlayer.posY; i > 0; i--) {
         Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock();
         if (block != Blocks.air) {
            return mc.thePlayer.posY - i;
         }
      }

      return -999.0;
   }

   public static boolean P_boolean() {
      for (int i = (int)mc.thePlayer.posY; i >= 0; i--) {
         Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock();
         if (block != Blocks.air) {
            return true;
         }
      }

      return false;
   }

   public static boolean u_boolean() {
      for (double d = (int)mc.thePlayer.posY; d >= 0.0; d--) {
         Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, d, mc.thePlayer.posZ)).getBlock();
         if (block != Blocks.air) {
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
   public void updateDisplayName() {
      this.setDisplayName("No Fall " + EnumChatFormatting.GRAY + NoFallMode.displayNameOf(this.mode.getValue()));
      super.updateDisplayName();
   }
}
