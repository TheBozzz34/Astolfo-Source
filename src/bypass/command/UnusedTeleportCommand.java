package bypass.command;

import bypass.ClientChat;
import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.module.exploits.RiderGod;
import bypass.module.hidden.PlayerListTracker;
import bypass.module.hidden.ScoreboardTracker;
import bypass.module.other.InfiniteReach;
import bypass.module.visuals.HUD;
import bypass.network.VehicleMovePacket;
import bypass.util.GameUtil;
import bypass.util.PathFinder;
import bypass.util.Stopwatch;
import bypass.util.Vector3;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public final class UnusedTeleportCommand extends Command {
   private double targetX;
   private double targetY;
   private double targetZ;
   private String targetName = "";
   private int stage;
   private boolean ready;
   private boolean toSpawnPoint;
   private BlockPos lastPosition;
   private final Stopwatch cooldown = new Stopwatch();
   public static int riderGodTeleports;
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (packetEvent.getPacket() instanceof C0CPacketInput && mc.thePlayer.isRiding()) {
            C0CPacketInput c0CPacketInput = (C0CPacketInput)packetEvent.getPacket();
            c0CPacketInput.setSneaking(true);
            return;
         }

         if (this.stage == 1 && !this.cooldown.hasElapsed(6000.0F) || !this.ready && packetEvent.getPacket() instanceof C03PacketPlayer) {
            packetEvent.cancel();
         }
      } else if (packetEvent.getPacket() instanceof S08PacketPlayerPosLook && !this.ready) {
         this.ready = true;
      }
   };
   @EventHandler(MotionEvent.class)
   private final Listener<MotionEvent> onMotion = motionEvent -> {
      if (this.ready) {
         boolean bl = client.getModuleManager().get(RiderGod.class).isEnabled();
         if (!this.targetName.isEmpty()) {
            EntityPlayer entityPlayer = mc.theWorld.getPlayerEntityByName(this.targetName);
            if (entityPlayer != null) {
               if (entityPlayer.ridingEntity != null) {
                  this.targetX = entityPlayer.ridingEntity.posX;
                  this.targetY = entityPlayer.ridingEntity.posY;
                  this.targetZ = entityPlayer.ridingEntity.posZ;
               } else {
                  this.targetX = entityPlayer.posX;
                  this.targetY = entityPlayer.posY;
                  this.targetZ = entityPlayer.posZ;
               }
            }
         }

         if (this.toSpawnPoint && mc.theWorld.getSpawnPoint() != null) {
            this.targetX = mc.theWorld.getSpawnPoint().getX();
            this.targetY = 128.0;
            this.targetZ = mc.theWorld.getSpawnPoint().getZ();
            this.toSpawnPoint = false;
         }

         this.lastPosition = mc.thePlayer.getPosition();
         double d = mc.thePlayer.posY - this.targetY;
         if (bl) {
            mc.thePlayer.setPosition(this.targetX, this.targetY, this.targetZ);
         } else {
            boolean bl2 = d < -30.0 && mc.thePlayer.posY > 40.0 && this.targetName.isEmpty() && !ScoreboardTracker.isInGame();
            if (bl2) {
               this.sendPathPackets(
                  InfiniteReach.createLinearPath(
                     mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.posX, mc.thePlayer.posY + 50.0, mc.thePlayer.posZ, 8.9
                  ),
                  bl
               );
               this.sendPathPackets(
                  InfiniteReach.createLinearPath(mc.thePlayer.posX, mc.thePlayer.posY + 50.0, mc.thePlayer.posZ, this.targetX, this.targetY, this.targetZ, 8.9),
                  bl
               );
            } else {
               this.sendPathPackets(
                  PathFinder.findPath(
                     new Vector3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vector3(this.targetX, this.targetY, this.targetZ), 8.9
                  ),
                  bl
               );
            }
         }

         mc.thePlayer.setPosition(this.targetX, this.targetY, this.targetZ);
         client.getEventBus().unsubscribe(this);
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
         mc.thePlayer.fallDistance = 0.0F;
         this.targetName = "";
         if (HUD.isPowerBranding()) {
            printPowerMessage("TP~~");
         } else {
            toasts.successMessage("Teleported", "Successfully teleported");
         }

         if (bl) {
            riderGodTeleports++;
         }
      }
   };
   @EventHandler(MoveEvent.class)
   private final Listener<MoveEvent> onMove = moveEvent -> {
      GameUtil.setMoveSpeed(moveEvent, 0.0, 90.0F);
      moveEvent.setY(mc.thePlayer.motionY = 0.0);
   };

   public UnusedTeleportCommand() {
      super("teleport", "<x> <y> <z>/<x> <z>");
      this.setAliases(Collections.singletonList("tp"));
   }

   @Override
   public void runCommand(String[] strings) {
      switch (strings.length) {
         case 2:
            if (strings[1].equalsIgnoreCase("cancel")) {
               client.getEventBus().unsubscribe(this);
            } else if (strings[1].equalsIgnoreCase("teammate")) {
               if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == Items.compass) {
                  this.toSpawnPoint = true;
                  this.prepareTeleport("teammate");
                  client.getEventBus().subscribe(this);
               } else {
                  toasts.danger("You must hold your compass to use this");
               }
            } else if (strings[1].equalsIgnoreCase("back")) {
               if (this.lastPosition != null) {
                  this.targetX = this.lastPosition.getX();
                  this.targetY = 128.0;
                  this.targetZ = this.lastPosition.getZ();
                  this.prepareTeleport("back");
                  client.getEventBus().subscribe(this);
               } else {
                  toasts.danger("Theres no registered last position");
               }
            } else {
               EntityPlayer entityPlayer = mc.theWorld.getPlayerEntityByName(strings[1]);
               boolean bl = false;
               if (entityPlayer != null) {
                  this.targetX = entityPlayer.posX;
                  this.targetY = entityPlayer.posY;
                  this.targetZ = entityPlayer.posZ;
                  this.targetName = entityPlayer.getName();
               } else {
                  BlockPos blockPos = PlayerListTracker.lastPositions.get(strings[1]);
                  if (blockPos == null) {
                     toasts.danger("Couldn't find this player");
                     break;
                  }

                  if (mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) > 2000.0) {
                     toasts.danger(strings[1] + " last position was too far away");
                     break;
                  }

                  this.targetX = blockPos.getX();
                  this.targetY = 128.0;
                  this.targetZ = blockPos.getZ();
                  this.targetName = strings[1];
                  bl = true;
               }

               if (this.cannotTeleport()) {
                  toasts.danger("You must be on a boat to TP");
                  return;
               }

               client.getEventBus().subscribe(this);
               this.prepareTeleport(bl ? "tracked" : "found");
            }
            break;
         case 3:
            if (this.cannotTeleport()) {
               toasts.danger("You must be on a boat to to TP");
               return;
            }

            try {
               double d = Double.parseDouble(strings[1]);
               double e = Double.parseDouble(strings[2]);
               this.targetX = d;
               this.targetY = 128.0;
               this.targetZ = e;
               client.getEventBus().subscribe(this);
               this.prepareTeleport("");
            } catch (NumberFormatException numberFormatException) {
               ClientChat.CHAT.printError("put cords correctly noob");
            }
            break;
         case 4:
            if (this.cannotTeleport()) {
               toasts.danger("You must be on a boat to TP");
               return;
            }

            try {
               double d2 = Double.parseDouble(strings[1]);
               double d3 = Double.parseDouble(strings[2]);
               double d4 = Double.parseDouble(strings[3]);
               this.targetX = d2;
               this.targetY = d3;
               this.targetZ = d4;
               client.getEventBus().subscribe(this);
               this.prepareTeleport("");
            } catch (NumberFormatException numberFormatException2) {
               ClientChat.CHAT.printError("put cords correctly noob");
            }
            break;
         case 5:
            if (strings[1].equalsIgnoreCase("update")) {
               try {
                  double d5 = Double.parseDouble(strings[2]);
                  double d6 = Double.parseDouble(strings[3]);
                  double d7 = Double.parseDouble(strings[4]);
                  this.targetX = d5;
                  this.targetY = d6;
                  this.targetZ = d7;
               } catch (NumberFormatException numberFormatException3) {
               }
            }
            break;
         default:
            ClientChat.CHAT.print(this.getName() + ": " + this.getUsage());
      }
   }

   private void prepareTeleport(String string) {
      this.stage = 0;
      this.ready = false;
      if (client.getModuleManager().get(RiderGod.class).isEnabled()) {
         this.ready = true;
      }

      mc.thePlayer.motionX = 0.0;
      mc.thePlayer.motionZ = 0.0;
      if (HUD.isPowerBranding()) {
         if (string.equals("tracked") || string.equals("found")) {
            printPrefixed(
               "LookTP",
               String.format(
                  "§a目标 §c%s §a距离 §c%s §b米 §a坐标§bX:§f%s §bY:§f%s §bZ:§f%s",
                  this.targetName,
                  (int)mc.thePlayer.getDistance(this.targetX, this.targetY, this.targetZ),
                  (int)this.targetX,
                  (int)this.targetY,
                  (int)this.targetZ
               )
            );
         }

         printPowerMessage(EnumChatFormatting.GREEN + "TP" + EnumChatFormatting.WHITE + " : " + EnumChatFormatting.AQUA + "Please wait Server Lag!");
      } else {
         switch (string) {
            case "tracked":
               toasts.infoMessage("Getting ready", "Teleporting to " + this.targetName + " last position soon");
               break;
            case "found":
               toasts.infoMessage("Getting ready", "Teleporting to " + this.targetName + " soon");
               break;
            case "teammate":
               toasts.infoMessage("Getting ready", "Teleporting to your teammates soon");
               break;
            case "back":
               toasts.infoMessage("Getting ready", "Teleporting to your last position");
               break;
            default:
               toasts.infoMessage("Getting ready", "Teleporting to [" + this.targetX + " " + this.targetY + " " + this.targetZ + "] soon");
         }
      }
   }

   public static void printPrefixed(String string, String string2) {
      mc.ingameGUI
         .getChatGUI()
         .printChatMessage(
            new ChatComponentText(
               EnumChatFormatting.DARK_GRAY
                  + "["
                  + EnumChatFormatting.RED
                  + string
                  + EnumChatFormatting.DARK_GRAY
                  + "]"
                  + EnumChatFormatting.WHITE
                  + " "
                  + string2
            )
         );
   }

   public static void sendFakeJump() {
      mc.thePlayer
         .sendQueue
         .sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42F, mc.thePlayer.posZ, false));
      mc.thePlayer
         .sendQueue
         .sendPacketNoEvent(
            new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42F + 0.33319999363422426, mc.thePlayer.posZ, false)
         );
      mc.thePlayer
         .sendQueue
         .sendPacketNoEvent(
            new C03PacketPlayer.C04PacketPlayerPosition(
               mc.thePlayer.posX, mc.thePlayer.posY + 0.42F + 0.33319999363422426 + 0.24813599859093927, mc.thePlayer.posZ, false
            )
         );
      mc.thePlayer
         .sendQueue
         .sendPacketNoEvent(
            new C03PacketPlayer.C04PacketPlayerPosition(
               mc.thePlayer.posX, mc.thePlayer.posY + 0.42F + 0.33319999363422426 + 0.24813599859093927 + 0.1647732818260721, mc.thePlayer.posZ, false
            )
         );
   }

   public static void printPowerMessage(String string) {
      printPrefixed("Power", string);
   }

   private void sendPathPackets(List<Vector3> list, boolean bl) {
      double d = mc.thePlayer.posY;
      double e = 0.0;

      for (Vector3 vector3 : list) {
         if (vector3.getY() < d) {
            e += d - vector3.getY();
         }

         if (bl) {
            mc.thePlayer
               .sendQueue
               .addToSendQueue(new VehicleMovePacket(vector3.getX(), vector3.getY(), vector3.getZ(), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
         } else if (e > 2.85) {
            mc.thePlayer.sendQueue.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector3.getX(), vector3.getY(), vector3.getZ(), true));
            e = 0.0;
         } else {
            mc.thePlayer.sendQueue.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector3.getX(), vector3.getY(), vector3.getZ(), false));
         }

         d = vector3.getY();
      }
   }

   private boolean cannotTeleport() {
      return !GameUtil.isOnHypixel() || !mc.thePlayer.isRiding() && mc.thePlayer.ticksExisted > 30;
   }
}
