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
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.RandomUtils;
import bypass.util.Stopwatch;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

public final class Flight extends Module {
   public static final EnumSetting<FlightMode> mode = new EnumSetting<>("Mode", "Set your fly modes", FlightMode.VANILLA);
   private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Speed of fly", 1.0F, 0.5F, 10.0F);
   public static final BooleanSetting bobbing = new BooleanSetting("Bobbing", "View bobbing", true);
   private final BooleanSetting antiKick = new BooleanSetting("Anti-Kick", "Doesnt let the server kick your for flying", true);
   public static final MapSetting boost = new MapSettingBuilder("Boost")
      .ordered()
      .description("boost")
      .add("Disabler", new EnumSetting<>("Disabler", "Disables hypixel checks", FlightBoostDisablerMode.NONE))
      .build();
   private final MapSetting custom = new MapSettingBuilder("Custom")
      .ordered()
      .description("Custom flight")
      .add("Motion", new NumberSetting<>("Motion", "Vertical motion", 0.0, -3.0, 3.0))
      .add("Spoof", new EnumSetting<>("Spoof", "ground spoof mode", FlightCustomSpoofMode.NONE))
      .add("Tick-Speed", new BooleanSetting("Tick-Speed", "thing", false))
      .add("Push-Up", new BooleanSetting("Push-Up", "goes up if onground", false))
      .add("Cancel-Packets", new BooleanSetting("Cancel-Packets", "cancels some packets", false))
      .add("Tick-Y", new BooleanSetting("Tick-Y", "sets y every tick", false))
      .add("Tick-Y-V", new NumberSetting<>("Tick-Y-V", "Vertical pos", 0.3, 0.0, 3.0))
      .add("Speed", new NumberSetting<>("Speed", "Speed of custom fly", 1.0F, 0.1F, 10.0F))
      .add("Timer", new NumberSetting<>("Timer", "Timer", 1.0F, 0.1F, 5.0F))
      .add("Tick-Timer", new BooleanSetting("Tick-Timer", "tick da timer", false))
      .add("Tick-Rate", new NumberSetting<>("Tick-Rate", "Rate of tick actioning", 2, 2, 16))
      .add("Stop", new BooleanSetting("Stop", "Stops when disabled", true))
      .build();
   private boolean unusedFlag;
   private boolean unusedFlag2;
   private double baseSpeed;
   private double lastDistance;
   private int unusedInt;
   private final Stopwatch unusedTimer = new Stopwatch();
   private final Stopwatch unusedTimer2 = new Stopwatch();
   private final Stopwatch antiKickTimer = new Stopwatch();
   private final Stopwatch unusedTimer3 = new Stopwatch();
   private final Stopwatch unusedTimer4 = new Stopwatch();
   private boolean respawned;
   private double groundDistance;
   public static boolean slotSpoofed;
   private boolean slotSwitched;
   private Item unusedItem;
   private boolean cancellingPackets;
   private int cancelledPackets;
   private int boostStage;
   private int sentinelTicks;
   private double gwenSpeed;
   private boolean gwenReversed;
   private boolean gwenBoosted;
   private boolean holdPackets;
   private FlightMode enabledMode;
   private final Queue<Packet<?>> heldPackets = new LinkedBlockingQueue<>();
   private boolean unusedFlag3;
   private int tickCounter;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      this.p();
      FlightMode flightMode = mode.getValue();
      switch (updateEvent.getState()) {
         case PRE:
            switch (flightMode) {
               case VANILLA:
                  mc.thePlayer.motionX = 0.0;
                  mc.thePlayer.motionY = 0.0;
                  mc.thePlayer.motionZ = 0.0;
                  if (mc.inGameHasFocus) {
                     if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                        mc.thePlayer.motionY = this.speed.getValue() / 2.0F;
                     }

                     if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                        mc.thePlayer.motionY = -this.speed.getValue() / 2.0F;
                     }
                  }

                  if (this.antiKick.getValue()) {
                     this.findGroundDistance();
                     mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                     if (this.groundDistance <= 290.0 && this.antiKickTimer.hasElapsed(500.0F)
                        || this.groundDistance > 290.0 && this.antiKickTimer.hasElapsed(100.0F)) {
                        this.antiKick();
                        this.antiKickTimer.reset();
                        return;
                     }
                  }

                  return;
               case SENTINEL:
               case CUSTOM:
               case FAITHFUL:
               default:
                  return;
               case AAC_3:
                  if (this.tickCounter == 2) {
                     mc.thePlayer.motionY = 0.1;
                  } else if (this.tickCounter > 2) {
                     this.tickCounter = 0;
                  }

                  this.tickCounter++;
                  return;
               case AAC_3312:
                  if (mc.thePlayer.posY < -70.0) {
                     mc.thePlayer.motionY = 10.0;
                  }

                  mc.timer.timerSpeed = 1.0F;
                  if (Keyboard.isKeyDown(29)) {
                     mc.timer.timerSpeed = 0.2F;
                     mc.rightClickDelayTimer = 0;
                  }

                  return;
               case AAC_3313:
                  if (mc.thePlayer.isDead) {
                     this.respawned = true;
                  }

                  if (this.respawned || mc.thePlayer.onGround) {
                     this.respawned = false;
                     mc.thePlayer.motionY = 10.0;
                     mc.thePlayer.onGround = false;
                  }

                  mc.timer.timerSpeed = 1.0F;
                  if (Keyboard.isKeyDown(29)) {
                     mc.timer.timerSpeed = 0.2F;
                     mc.rightClickDelayTimer = 0;
                  }

                  return;
               case MC_CENTRAL:
                  boolean bl = mc.thePlayer.ticksExisted % 2 == 0;
                  mc.thePlayer.motionY = 0.0;
                  if (bl) {
                     mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-9, mc.thePlayer.posZ);
                     if (GameUtil.isMoving(mc.thePlayer)) {
                        mc.timer.timerSpeed = 20.0F;
                        return;
                     }
                  } else {
                     mc.timer.timerSpeed = 1.0F;
                  }

                  return;
            }
         case POST:
            if (!bobbing.getValue()) {
               mc.thePlayer.distanceWalkedModified = 0.0F;
            }
      }
   };
   private double yOffset;
   private int unusedInt2;
   private int unusedInt3;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.HIGHEST)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         this.lastDistance = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
         FlightMode flightMode = mode.getValue();
         switch (flightMode) {
            case SENTINEL:
               if (!this.cancellingPackets) {
                  switch (this.boostStage) {
                     case 0:
                        motionEvent.setMinY(motionEvent.getMinY() + 0.6 + Math.random() / 100.0);
                        break;
                     case 1:
                        motionEvent.setMinY(motionEvent.getMinY() + 0.2 + Math.random() / 100.0);
                        break;
                     case 2:
                        motionEvent.setMinY(motionEvent.getMinY() + 1.0 - Math.random() / 10000.0);
                        break;
                     default:
                        this.boostStage = 0;
                  }

                  this.boostStage++;
               }
               break;
            case CUSTOM:
               float f = this.custom.getSetting("Speed").asFloat();
               boolean bl = GameUtil.isMoving(mc.thePlayer);
               int i = this.custom.getSetting("Tick-Rate").asInt();
               if (this.custom.getSetting("Tick-Timer").asBoolean()) {
                  if (mc.thePlayer.ticksExisted % i == 0) {
                     mc.timer.timerSpeed = this.custom.getSetting("Timer").asFloat();
                  } else {
                     mc.timer.timerSpeed = 1.0F;
                  }
               } else {
                  mc.timer.timerSpeed = this.custom.getSetting("Timer").asFloat();
               }

               mc.thePlayer.motionY = this.custom.getSetting("Motion").asDouble();
               if (mc.thePlayer.onGround && this.custom.getSetting("Push-Up").asBoolean()) {
                  mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ);
               }

               if (this.custom.getSetting("Tick-Speed").asBoolean()) {
                  if (mc.thePlayer.ticksExisted % i == 0 && bl) {
                     mc.thePlayer.r(f);
                  } else {
                     mc.thePlayer.r(0.0);
                  }
               } else if (f != 1.0F) {
                  if (bl) {
                     mc.thePlayer.r(f);
                  } else {
                     mc.thePlayer.r(0.0);
                  }
               }

               if (this.custom.getSetting("Tick-Y").asBoolean()) {
                  if (mc.thePlayer.ticksExisted % i == 0) {
                     mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + this.custom.getSetting("Tick-Y-V").asDouble(), mc.thePlayer.posZ);
                  } else {
                     mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY - this.custom.getSetting("Tick-Y-V").asDouble(), mc.thePlayer.posZ);
                  }
               }

               FlightCustomSpoofMode flightCustomSpoofMode = (FlightCustomSpoofMode)this.custom.getSetting("Spoof").getValue();
               switch (flightCustomSpoofMode) {
                  case ALWAYS:
                     motionEvent.setOnGround(true);
                     break;
                  case RANDOM:
                     motionEvent.setOnGround(RandomUtils.RANDOM.nextBoolean());
               }

               if (this.custom.getSetting("Cancel-Packets").asBoolean() && mc.thePlayer.ticksExisted % 2 == 0) {
                  motionEvent.cancel();
               }
            case FAITHFUL:
               mc.thePlayer.motionY = 0.0;
               mc.thePlayer.r(this.speed.getValue().floatValue());
               if (mc.inGameHasFocus) {
                  if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                     mc.thePlayer.motionY = this.speed.getValue().floatValue();
                  }

                  if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                     mc.thePlayer.motionY = -this.speed.getValue();
                  }
               }

               motionEvent.setOnGround(true);
         }
      }
   };
   @EventHandler(Block.CollisionBoxEvent.class)
   private Listener<Block.CollisionBoxEvent> onCollisionBox = collisionBoxEvent -> {
      if (mode.getValue() == FlightMode.COLLISION && !mc.thePlayer.isSneaking()) {
         collisionBoxEvent.setBoundingBox(new AxisAlignedBB(-2.0, -1.0, -2.0, 2.0, 1.0, 2.0).offset(collisionBoxEvent.getX(), collisionBoxEvent.getY(), collisionBoxEvent.getZ()));
      }
   };
   private boolean unusedFlag4;
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      float f = mc.thePlayer.movementInput.moveForward;
      float g = mc.thePlayer.movementInput.moveStrafe;
      float h = mc.thePlayer.rotationYaw;
      FlightMode flightMode = mode.getValue();
      if (mode.getValue() == FlightMode.GWEN && moveEvent.getState() == EventState.PRE) {
         boolean bl = (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && mc.thePlayer.onGround;
         if (findEmptyHotbarSlot() == -10) {
            GameUtil.setMoveSpeed(moveEvent, 0.0, 90.0F);
            return;
         }

         if (!this.gwenBoosted) {
            mc.thePlayer.sendQueue.u(new C09PacketHeldItemChange(findEmptyHotbarSlot()));
            clickBlockBelow();
            GameUtil.setMoveSpeed(moveEvent, this.gwenReversed ? -this.gwenSpeed : this.gwenSpeed, 90.0F);
            this.gwenReversed = !this.gwenReversed;
            if (bl && mc.thePlayer.ticksExisted % 2 == 0) {
               this.gwenSpeed += 0.105;
            }

            if (this.gwenSpeed >= Math.min(this.speed.getValue(), 5.0F) * 1.3 && bl) {
               moveEvent.setY(mc.thePlayer.motionY = 0.425F);
               GameUtil.setMoveSpeed(moveEvent, 0.0, 90.0F);
               this.gwenBoosted = true;
            }
         } else {
            mc.thePlayer.sendQueue.u(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            if (mc.thePlayer.fallDistance == 0.0F) {
               moveEvent.setY(mc.thePlayer.motionY += 0.038);
            } else if (mc.thePlayer.fallDistance <= 1.4) {
               moveEvent.setY(mc.thePlayer.motionY += 0.032);
            }

            GameUtil.setMoveSpeed(
               moveEvent,
               this.gwenSpeed = this.gwenSpeed * (this.gwenSpeed < 1.0 ? 0.99 : (this.gwenSpeed < 2.0 ? 0.985 : (this.gwenSpeed < 2.5 ? 0.972 : 0.97))),
               90.0F
            );
            if (bl) {
               this.gwenBoosted = false;
            }
         }
      }

      if (mode.getValue() == FlightMode.SENTINEL) {
         if (!this.cancellingPackets) {
            moveEvent.setY(mc.thePlayer.motionY = 0.0);
            mc.timer.timerSpeed = 0.31F;
            if (GameUtil.isMoving(mc.thePlayer) && this.sentinelTicks == 2) {
               GameUtil.setMoveSpeed(moveEvent, 2.45, 90.0F);
               this.sentinelTicks = 0;
            } else {
               GameUtil.setMoveSpeed(moveEvent, 0.25, 90.0F);
               this.sentinelTicks++;
            }
         } else {
            GameUtil.setMoveSpeed(moveEvent, 0.0, 90.0F);
         }
      }

      if (flightMode == FlightMode.VANILLA && moveEvent.getState() == EventState.PRE && GameUtil.isMoving(mc.thePlayer)) {
         GameUtil.setMoveSpeed(moveEvent, this.speed.getValue().floatValue(), 88.3883F);
      }
   };
   @EventHandler(value = PacketEvent.class, priority = EventPriority.HIGH)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      FlightMode flightMode = mode.getValue();
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         switch (flightMode) {
            case VANILLA:
               if (packetEvent.getPacket() instanceof C03PacketPlayer && GameUtil.isOnHypixel()) {
                  C03PacketPlayer c03PacketPlayer = (C03PacketPlayer)packetEvent.getPacket();
                  if (mc.thePlayer != null && this.heldPackets.size() >= 40) {
                     this.releasePackets();
                  }

                  if (c03PacketPlayer.isMoving()) {
                     this.heldPackets.offer(packetEvent.getPacket());
                     packetEvent.cancel();
                  }
               }
               break;
            case SENTINEL:
               if (this.cancellingPackets) {
                  this.cancelledPackets++;
               }

               if (this.cancelledPackets == 15) {
                  this.cancellingPackets = false;
               } else {
                  packetEvent.cancel();
               }
         }
      }
   };
   private static final long cb = 73121372369804L;

   public Flight(Category category) {
      super(category);
      this.B(-1193283);
      this.addSettings(mode, boost, this.custom, bobbing, this.antiKick, this.speed);
   }

   private void releasePackets() {
      Packet packet;
      while ((packet = this.heldPackets.poll()) != null) {
         mc.thePlayer.sendQueue.u(packet);
      }
   }

   public static float getSafeFallHeight() {
      PotionEffect potionEffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
      int i = potionEffect != null ? potionEffect.getAmplifier() + 1 : 0;
      return mc.thePlayer.getMaxFallHeight() + i;
   }

   public void findGroundDistance() {
      double d = 1.0;
      AxisAlignedBB axisAlignedBB = mc.thePlayer.getEntityBoundingBox().expand(0.0625, 0.0625, 0.0625);

      for (this.groundDistance = 0.0; this.groundDistance < mc.thePlayer.posY; this.groundDistance += d) {
         AxisAlignedBB axisAlignedBB2 = axisAlignedBB.offset(0.0, -this.groundDistance, 0.0);
         if (mc.theWorld.X(axisAlignedBB2)) {
            if (d < 0.0625) {
               break;
            }

            this.groundDistance -= d;
            d /= 2.0;
         }
      }
   }

   public static int findEmptyHotbarSlot() {
      for (int i = 0; i < 8; i++) {
         if (mc.thePlayer.inventory.mainInventory[i] == null) {
            return i;
         }
      }

      return -10;
   }

   public static void clickBlockBelow() {
      BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY - 1.0, mc.thePlayer.posZ);
      Vec3 vec3 = new Vec3(blockPos).addVector(0.4, 0.4, 0.4);
      mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, null, blockPos, EnumFacing.UP, vec3.e(0.4));
   }

   @Override
   public void p() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + FlightMode.A(mode.getValue()));
      super.p();
   }

   @Override
   public void onEnable() {
      if (mc.thePlayer != null) {
         this.enabledMode = mode.getValue();
         if (GameUtil.isInHypixelGame() && this.enabledMode == FlightMode.VANILLA) {
            this.holdPackets = true;
         }
      }

      this.gwenSpeed = 0.0;
      this.gwenBoosted = false;
      this.gwenReversed = false;
      this.unusedInt2 = 0;
      if (this.yOffset != 0.0 && mc.thePlayer != null) {
         mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + this.yOffset, mc.thePlayer.posZ);
      }

      this.yOffset = 0.0;
      this.unusedFlag4 = this.unusedFlag2 = false;
      this.baseSpeed = Speed.getBaseSpeed();
      this.lastDistance = 0.0;
      this.unusedTimer2.reset();
      this.unusedTimer3.reset();
      this.unusedTimer4.reset();
      super.onEnable();
      if (mode.getValue() == FlightMode.SENTINEL && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
         mc.thePlayer
            .sendQueue
            .addToSendQueue(
               new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - RandomUtils.nextDouble(0.1, 0.9), mc.thePlayer.posZ, false)
            );
         mc.thePlayer
            .sendQueue
            .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-7F, mc.thePlayer.posZ, true));
         this.cancellingPackets = true;
      }
   }

   @Override
   public void onDisable() {
      FlightMode flightMode = mode.getValue();
      if (mode.getValue() == FlightMode.SENTINEL && mc.thePlayer != null) {
         mc.thePlayer.r(0.25);
         mc.thePlayer.motionY = -0.2F;
      }

      if (mode.getValue() == FlightMode.CUSTOM && mc.thePlayer != null && this.custom.getSetting("Stop").asBoolean()) {
         mc.thePlayer.r(0.0);
      }

      if (mode.getValue() == FlightMode.GWEN) {
         mc.thePlayer.r(0.0);
      }

      if (mc.thePlayer != null && (this.slotSwitched || slotSpoofed)) {
         mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
      }

      if (mc.thePlayer != null && flightMode == FlightMode.VANILLA && this.holdPackets && !this.heldPackets.isEmpty()) {
         this.releasePackets();
      }

      this.unusedFlag = this.holdPackets = false;
      mc.timer.timerSpeed = 1.0F;
      this.unusedInt = 0;
      this.lastDistance = this.baseSpeed = 0.0;
      this.yOffset = 0.0;
      this.unusedInt3 = 0;
      slotSpoofed = false;
      this.slotSwitched = false;
      this.unusedFlag3 = true;
      this.cancellingPackets = false;
      this.cancelledPackets = this.sentinelTicks = 0;
      this.boostStage = -1;
      super.onDisable();
   }

   public void antiKick() {
      if (!(this.groundDistance > 300.0)) {
         double d = mc.thePlayer.posY - this.groundDistance;
         if (!(d <= 0.0)) {
            double e = mc.thePlayer.posY;

            while (e > d) {
               e -= 8.0;
               if (e < d) {
                  e = d;
               }

               C03PacketPlayer.C04PacketPlayerPosition c04PacketPlayerPosition = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, e, mc.thePlayer.posZ, true);
               mc.thePlayer.sendQueue.addToSendQueue(c04PacketPlayerPosition);
            }

            e = d;

            while (e < mc.thePlayer.posY) {
               e += 8.0;
               if (e > mc.thePlayer.posY) {
                  e = mc.thePlayer.posY;
               }

               C03PacketPlayer.C04PacketPlayerPosition c04PacketPlayerPosition2 = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, e, mc.thePlayer.posZ, true);
               mc.thePlayer.sendQueue.addToSendQueue(c04PacketPlayerPosition2);
            }
         }
      }
   }
}
