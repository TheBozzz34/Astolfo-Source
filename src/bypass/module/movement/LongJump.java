package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.player.Scaffold;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.RandomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public final class LongJump extends Module {
   private final EnumSetting<LongJumpMode> mode = new EnumSetting<>("Mode", "Set your longjump mode", LongJumpMode.NORMAL);
   private final NumberSetting<Double> boost = new NumberSetting<>("Boost", "Speed of fly", 2.5, 1.35, 16.0);
   private final NumberSetting<Float> jumpBoost = new NumberSetting<>("Jump-Boost", "Speed of fly", 1.0F, 1.0F, 8.0F);
   private final BooleanSetting autoScaffold = new BooleanSetting("Auto-Scaffold", "enables scaffold automatically", false);
   private final BooleanSetting autoDisable = new BooleanSetting("Auto-Disable", "disables longjump after one jump", true);
   private final double unusedMultiplier = 2.15;
   private static final double BASE_SPEED = 0.27999999999999997;
   private double moveSpeed = 0.27999999999999997;
   private boolean wasOnGround;
   private boolean jumped;
   private double lastDistance = 0.0;
   private int jumpTicks;
   private boolean safeBoosted;
   private boolean safeLanded;
   private int landedTicks;
   private boolean unusedSlotSpoofed;
   private Item unusedSpoofItem;
   private int unusedCounter;
   private double motionY;
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      long l = d ^ 43843006971240L;
      long n = l ^ 10171659452336L;
      if (moveEvent.getState() == EventState.PRE) {
         double e = mc.thePlayer.movementInput.moveForward;
         double d2 = mc.thePlayer.movementInput.moveStrafe;
         double d3 = mc.thePlayer.rotationYaw;
         switch ((LongJumpMode)this.mode.getValue()) {
            case WATCHDOG:
               if (mc.thePlayer.m(0.01) || this.jumpTicks > 0) {
                  switch (this.jumpTicks) {
                     case 0:
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.004 * Math.random(), mc.thePlayer.posZ);
                        selfDamage(3.0);
                        this.motionY = 0.42F;
                        this.moveSpeed = Speed.getBaseSpeed(1.0, false) * 2.14;
                        break;
                     case 1:
                        this.moveSpeed *= 0.8;
                        break;
                     default:
                        this.moveSpeed *= 0.95;
                  }

                  if (this.jumpTicks < 5) {
                     this.motionY -= 0.046;
                  } else {
                     this.motionY -= 0.066;
                  }

                  moveEvent.setY(mc.thePlayer.motionY = this.motionY);
                  this.jumpTicks++;
                  if (mc.thePlayer.m(0.01) && this.jumpTicks > 4) {
                     this.setEnabled(false);
                  }

                  Speed.setMotion(moveEvent, (float)d2, (float)e, 1.0F, this.moveSpeed);
               }
               break;
            case NORMAL:
               if (mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F || mc.theWorld == null) {
                  this.moveSpeed = 0.27999999999999997;
                  return;
               }

               if (mc.thePlayer.onGround) {
                  if (this.jumped && this.autoDisable.getValue()) {
                     this.moveSpeed = 0.27999999999999997;
                     this.setEnabled(false);
                  }

                  if (this.wasOnGround) {
                     this.moveSpeed *= 2.053232;
                     moveEvent.setY(mc.thePlayer.motionY = 0.40356524352 * Speed.getJumpBoostMultiplier());
                     this.jumped = true;
                  } else {
                     this.moveSpeed = this.boost.getValue() * 0.27999999999999997;
                  }
               } else if (this.wasOnGround) {
                  if (this.lastDistance < 2.147) {
                     this.lastDistance = 2.147;
                  }

                  this.moveSpeed = this.lastDistance - 0.66 * (this.lastDistance - 0.27999999999999997);
               } else {
                  this.moveSpeed = this.lastDistance - this.lastDistance / 155.0;
               }

               if (this.autoScaffold.getValue()
                  && mc.thePlayer.isCollidedHorizontally
                  && !mc.thePlayer.isCollidedVertically
                  && !client.getModuleManager().get(Scaffold.class).isEnabled()) {
                  client.getModuleManager().get(Scaffold.class).setEnabled(true);
                  this.setEnabled(false);
               }

               this.wasOnGround = mc.thePlayer.onGround;
               this.moveSpeed = Math.max(this.moveSpeed, 0.27999999999999997);
               if (e == 0.0 && d2 == 0.0) {
                  moveEvent.setX(0.0);
                  moveEvent.setZ(0.0);
               }

               if (e != 0.0 && d2 != 0.0) {
                  e *= Math.sin((Math.PI / 4) - Math.random() * 1.0E-4);
                  d2 *= Math.cos((Math.PI / 4) - Math.random() * 1.0E-4);
               }

               moveEvent.setX(e * this.moveSpeed * -Math.sin(Math.toRadians(d3)) + d2 * this.moveSpeed * Math.cos(Math.toRadians(d3)));
               moveEvent.setZ(e * this.moveSpeed * Math.cos(Math.toRadians(d3)) - d2 * this.moveSpeed * -Math.sin(Math.toRadians(d3)));
               break;
            case SAFE:
               if (!this.safeBoosted) {
                  this.landedTicks = 0;
               }

               if (mc.thePlayer.onGround && this.safeBoosted) {
                  this.safeLanded = true;
               }

               if (this.safeLanded) {
                  this.landedTicks++;
                  moveEvent.setX(0.0);
                  moveEvent.setZ(0.0);
                  if (this.landedTicks > (int)w) {
                     this.safeBoosted = false;
                     this.safeLanded = false;
                     mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                     mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                     this.setEnabled(false);
                  }
               }
         }
      }
   };
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         this.lastDistance = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
         if (this.mode.getValue() == LongJumpMode.SAFE
            && !this.safeBoosted
            && mc.gameSettings.keyBindJump.isKeyDown()
            && mc.thePlayer.movementInput.moveForward > 0.0F
            && mc.thePlayer.motionY == 0.33319999363422365) {
            double d = Math.toRadians(mc.thePlayer.rotationYaw);
            double e = 1.261;
            mc.thePlayer.motionX = -(Math.sin(d) * e);
            mc.thePlayer.motionZ = Math.cos(d) * e;
            this.safeBoosted = true;
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (this.mode.getValue() == LongJumpMode.SAFE && packetEvent.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer c03PacketPlayer = (C03PacketPlayer)packetEvent.getPacket();
            if (this.safeLanded && this.landedTicks == 2) {
               c03PacketPlayer.y += 0.40426326F;
               c03PacketPlayer.E(false);
            }
         }

         if (packetEvent.getPacket() instanceof C09PacketHeldItemChange && this.unusedSlotSpoofed && this.unusedSpoofItem != null) {
            packetEvent.cancel();
         }
      }
   };
   private static final long d = 28486289309547L;
   private static final long w = 6602843336573190150L;

   public LongJump(Category category) {
      super(category);
      this.addSettings(this.mode, this.jumpBoost, this.boost, this.autoScaffold, this.autoDisable);
   }

   public static void selfDamage(double d2) {
      double d = 0.0;

      while (d < d2) {
         double e;
         if (GameUtil.isOnHypixel()) {
            e = RandomUtils.nextFloat(0.03F, 0.06F);
         } else {
            e = 0.0625;
         }

         Minecraft.getMinecraft()
            .getNetHandler()
            .getNetworkManager()
            .sendPacket(
               new C03PacketPlayer.C04PacketPlayerPosition(
                  Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + e, Minecraft.getMinecraft().thePlayer.posZ, false
               )
            );
         Minecraft.getMinecraft()
            .getNetHandler()
            .getNetworkManager()
            .sendPacket(
               new C03PacketPlayer.C04PacketPlayerPosition(
                  Minecraft.getMinecraft().thePlayer.posX,
                  Minecraft.getMinecraft().thePlayer.posY + 0.012500048F,
                  Minecraft.getMinecraft().thePlayer.posZ,
                  false
               )
            );
         d += e - 0.012500048F;
      }

      mc.thePlayer.sendQueue.u(new C03PacketPlayer(true));
      mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
   }

   @Override
   public void onEnable() {
      this.jumpTicks = 0;
      this.moveSpeed = 0.0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.unusedSpoofItem != null && mc.thePlayer != null) {
         mc.thePlayer.sendQueue.u(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
      }

      this.unusedSpoofItem = null;
      this.unusedSlotSpoofed = false;
      this.unusedCounter = 0;
      this.lastDistance = 0.0;
      this.moveSpeed = 0.27999999999999997;
      this.wasOnGround = this.jumped = false;
      mc.timer.timerSpeed = 1.0F;
      this.safeLanded = false;
      this.safeBoosted = false;
      this.landedTicks = 0;
      super.onDisable();
   }
}
