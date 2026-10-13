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
   private Listener<MoveEvent> onMove = var1x -> {
      long var2 = d ^ 43843006971240L;
      long var4 = var2 ^ 10171659452336L;
      if (var1x.getState() == EventState.PRE) {
         double var7 = mc.thePlayer.movementInput.moveForward;
         double var9 = mc.thePlayer.movementInput.moveStrafe;
         double var11 = mc.thePlayer.rotationYaw;
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

                  var1x.setY(mc.thePlayer.motionY = this.motionY);
                  this.jumpTicks++;
                  if (mc.thePlayer.m(0.01) && this.jumpTicks > 4) {
                     this.setEnabled(false);
                  }

                  Speed.setMotion(var1x, (float)var9, (float)var7, 1.0F, this.moveSpeed);
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
                     var1x.setY(mc.thePlayer.motionY = 0.40356524352 * Speed.getJumpBoostMultiplier());
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
               if (var7 == 0.0 && var9 == 0.0) {
                  var1x.setX(0.0);
                  var1x.setZ(0.0);
               }

               if (var7 != 0.0 && var9 != 0.0) {
                  var7 *= Math.sin((Math.PI / 4) - Math.random() * 1.0E-4);
                  var9 *= Math.cos((Math.PI / 4) - Math.random() * 1.0E-4);
               }

               var1x.setX(var7 * this.moveSpeed * -Math.sin(Math.toRadians(var11)) + var9 * this.moveSpeed * Math.cos(Math.toRadians(var11)));
               var1x.setZ(var7 * this.moveSpeed * Math.cos(Math.toRadians(var11)) - var9 * this.moveSpeed * -Math.sin(Math.toRadians(var11)));
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
                  var1x.setX(0.0);
                  var1x.setZ(0.0);
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
   private Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         this.lastDistance = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
         if (this.mode.getValue() == LongJumpMode.SAFE
            && !this.safeBoosted
            && mc.gameSettings.keyBindJump.isKeyDown()
            && mc.thePlayer.movementInput.moveForward > 0.0F
            && mc.thePlayer.motionY == 0.33319999363422365) {
            double var5 = Math.toRadians(mc.thePlayer.rotationYaw);
            double var7 = 1.261;
            mc.thePlayer.motionX = -(Math.sin(var5) * var7);
            mc.thePlayer.motionZ = Math.cos(var5) * var7;
            this.safeBoosted = true;
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING) {
         if (this.mode.getValue() == LongJumpMode.SAFE && var1x.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer var5 = (C03PacketPlayer)var1x.getPacket();
            if (this.safeLanded && this.landedTicks == 2) {
               var5.y += 0.40426326F;
               var5.E(false);
            }
         }

         if (var1x.getPacket() instanceof C09PacketHeldItemChange && this.unusedSlotSpoofed && this.unusedSpoofItem != null) {
            var1x.cancel();
         }
      }
   };
   private static final long d = 28486289309547L;
   private static final long w = 6602843336573190150L;

   public LongJump(Category var1) {
      super(var1);
      this.addSettings(this.mode, this.jumpBoost, this.boost, this.autoScaffold, this.autoDisable);
   }

   public static void selfDamage(double var0) {
      double var5 = 0.0;

      while (var5 < var0) {
         double var7;
         if (GameUtil.isOnHypixel()) {
            var7 = RandomUtils.nextFloat(0.03F, 0.06F);
         } else {
            var7 = 0.0625;
         }

         Minecraft.getMinecraft()
            .getNetHandler()
            .getNetworkManager()
            .sendPacket(
               new C03PacketPlayer.C04PacketPlayerPosition(
                  Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + var7, Minecraft.getMinecraft().thePlayer.posZ, false
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
         var5 += var7 - 0.012500048F;
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
