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
   private double C;
   private double h;
   private double X;
   private String E = "";
   private int z;
   private boolean p;
   private boolean s;
   private BlockPos T;
   private final Stopwatch Q = new Stopwatch();
   public static int W;
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (packetEvent.getPacket() instanceof C0CPacketInput && mc.thePlayer.E()) {
            C0CPacketInput c0CPacketInput = (C0CPacketInput)packetEvent.getPacket();
            c0CPacketInput.d(true);
            return;
         }

         if (this.z == 1 && !this.Q.hasElapsed(6000.0F) || !this.p && packetEvent.getPacket() instanceof C03PacketPlayer) {
            packetEvent.cancel();
         }
      } else if (packetEvent.getPacket() instanceof S08PacketPlayerPosLook && !this.p) {
         this.p = true;
      }
   };
   @EventHandler(MotionEvent.class)
   private final Listener<MotionEvent> onMotion = motionEvent -> {
      long l = d ^ 57577220663433L;
      long n = l ^ 139774890254778L;
      if (this.p) {
         boolean bl = client.getModuleManager().get(RiderGod.class).isEnabled();
         if (!this.E.isEmpty()) {
            EntityPlayer entityPlayer = mc.theWorld.getPlayerEntityByName(this.E);
            if (entityPlayer != null) {
               if (entityPlayer.ridingEntity != null) {
                  this.C = entityPlayer.ridingEntity.posX;
                  this.h = entityPlayer.ridingEntity.posY;
                  this.X = entityPlayer.ridingEntity.posZ;
               } else {
                  this.C = entityPlayer.posX;
                  this.h = entityPlayer.posY;
                  this.X = entityPlayer.posZ;
               }
            }
         }

         if (this.s && mc.theWorld.getSpawnPoint() != null) {
            this.C = mc.theWorld.getSpawnPoint().getX();
            this.h = 128.0;
            this.X = mc.theWorld.getSpawnPoint().getZ();
            this.s = false;
         }

         this.T = mc.thePlayer.getPosition();
         double e = mc.thePlayer.posY - this.h;
         if (bl) {
            mc.thePlayer.setPosition(this.C, this.h, this.X);
         } else {
            boolean bl2 = e < -30.0 && mc.thePlayer.posY > 40.0 && this.E.isEmpty() && !ScoreboardTracker.r();
            if (bl2) {
               this.m(
                  InfiniteReach.Z(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.posX, mc.thePlayer.posY + 50.0, mc.thePlayer.posZ, 8.9),
                  bl
               );
               this.m(InfiniteReach.Z(mc.thePlayer.posX, mc.thePlayer.posY + 50.0, mc.thePlayer.posZ, this.C, this.h, this.X, 8.9), bl);
            } else {
               this.m(PathFinder.i(new Vector3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vector3(this.C, this.h, this.X), 8.9), bl);
            }
         }

         mc.thePlayer.setPosition(this.C, this.h, this.X);
         client.getEventBus().unsubscribe(this);
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
         mc.thePlayer.fallDistance = 0.0F;
         this.E = "";
         if (HUD.isPowerBranding()) {
            f("TP~~");
         } else {
            toasts.g("Teleported", "Successfully teleported");
         }

         if (bl) {
            W++;
         }
      }
   };
   @EventHandler(MoveEvent.class)
   private final Listener<MoveEvent> onMove = moveEvent -> {
      GameUtil.setMoveSpeed(moveEvent, 0.0, 90.0F);
      moveEvent.setY(mc.thePlayer.motionY = 0.0);
   };
   private static final long d = 136569738441856L;
   private static final long j = -8984061303844765666L;

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
                  this.s = true;
                  this.c("teammate");
                  client.getEventBus().subscribe(this);
               } else {
                  toasts.x("You must hold your compass to use this");
               }
            } else if (strings[1].equalsIgnoreCase("back")) {
               if (this.T != null) {
                  this.C = this.T.getX();
                  this.h = 128.0;
                  this.X = this.T.getZ();
                  this.c("back");
                  client.getEventBus().subscribe(this);
               } else {
                  toasts.x("Theres no registered last position");
               }
            } else {
               EntityPlayer entityPlayer = mc.theWorld.getPlayerEntityByName(strings[1]);
               boolean bl = false;
               if (entityPlayer != null) {
                  this.C = entityPlayer.posX;
                  this.h = entityPlayer.posY;
                  this.X = entityPlayer.posZ;
                  this.E = entityPlayer.getName();
               } else {
                  BlockPos blockPos = PlayerListTracker.lastPositions.get(strings[1]);
                  if (blockPos == null) {
                     toasts.x("Couldn't find this player");
                     break;
                  }

                  if (mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) > 2000.0) {
                     toasts.x(strings[1] + " last position was too far away");
                     break;
                  }

                  this.C = blockPos.getX();
                  this.h = 128.0;
                  this.X = blockPos.getZ();
                  this.E = strings[1];
                  bl = true;
               }

               if (this.o()) {
                  toasts.x("You must be on a boat to TP");
                  return;
               }

               client.getEventBus().subscribe(this);
               this.c(bl ? "tracked" : "found");
            }
            break;
         case 3:
            if (this.o()) {
               toasts.x("You must be on a boat to to TP");
               return;
            }

            try {
               double d = Double.parseDouble(strings[1]);
               double e = Double.parseDouble(strings[2]);
               this.C = d;
               this.h = 128.0;
               this.X = e;
               client.getEventBus().subscribe(this);
               this.c("");
            } catch (NumberFormatException numberFormatException) {
               ClientChat.CHAT.f("put cords correctly noob");
            }
            break;
         case 4:
            if (this.o()) {
               toasts.x("You must be on a boat to TP");
               return;
            }

            try {
               double d2 = Double.parseDouble(strings[1]);
               double d3 = Double.parseDouble(strings[2]);
               double d4 = Double.parseDouble(strings[3]);
               this.C = d2;
               this.h = d3;
               this.X = d4;
               client.getEventBus().subscribe(this);
               this.c("");
            } catch (NumberFormatException numberFormatException2) {
               ClientChat.CHAT.f("put cords correctly noob");
            }
            break;
         case 5:
            if (strings[1].equalsIgnoreCase("update")) {
               try {
                  double d5 = Double.parseDouble(strings[2]);
                  double d6 = Double.parseDouble(strings[3]);
                  double d7 = Double.parseDouble(strings[4]);
                  this.C = d5;
                  this.h = d6;
                  this.X = d7;
               } catch (NumberFormatException numberFormatException3) {
               }
            }
            break;
         default:
            ClientChat.CHAT.I(this.getName() + ": " + this.getUsage());
      }
   }

   private void c(String string) {
      long l = d ^ 23328835604355L;
      long m = l ^ 103860757841584L;
      this.z = 0;
      this.p = false;
      if (client.getModuleManager().get(RiderGod.class).isEnabled()) {
         this.p = true;
      }

      mc.thePlayer.motionX = 0.0;
      mc.thePlayer.motionZ = 0.0;
      if (HUD.isPowerBranding()) {
         if (string.equals("tracked") || string.equals("found")) {
            q(
               "LookTP",
               String.format(
                  "§a目标 §c%s §a距离 §c%s §b米 §a坐标§bX:§f%s §bY:§f%s §bZ:§f%s",
                  this.E,
                  (int)mc.thePlayer.getDistance(this.C, this.h, this.X),
                  (int)this.C,
                  (int)this.h,
                  (int)this.X
               )
            );
         }

         f(EnumChatFormatting.GREEN + "TP" + EnumChatFormatting.WHITE + " : " + EnumChatFormatting.AQUA + "Please wait Server Lag!");
      } else {
         switch (string) {
            case "tracked":
               toasts.B("Getting ready", "Teleporting to " + this.E + " last position soon");
               break;
            case "found":
               toasts.B("Getting ready", "Teleporting to " + this.E + " soon");
               break;
            case "teammate":
               toasts.B("Getting ready", "Teleporting to your teammates soon");
               break;
            case "back":
               toasts.B("Getting ready", "Teleporting to your last position");
               break;
            default:
               toasts.B("Getting ready", "Teleporting to [" + this.C + " " + this.h + " " + this.X + "] soon");
         }
      }
   }

   public static void q(String string, String string2) {
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

   public static void f() {
      mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42F, mc.thePlayer.posZ, false));
      mc.thePlayer
         .sendQueue
         .u(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42F + 0.33319999363422426, mc.thePlayer.posZ, false));
      mc.thePlayer
         .sendQueue
         .u(
            new C03PacketPlayer.C04PacketPlayerPosition(
               mc.thePlayer.posX, mc.thePlayer.posY + 0.42F + 0.33319999363422426 + 0.24813599859093927, mc.thePlayer.posZ, false
            )
         );
      mc.thePlayer
         .sendQueue
         .u(
            new C03PacketPlayer.C04PacketPlayerPosition(
               mc.thePlayer.posX, mc.thePlayer.posY + 0.42F + 0.33319999363422426 + 0.24813599859093927 + 0.1647732818260721, mc.thePlayer.posZ, false
            )
         );
   }

   public static void f(String string) {
      q("Power", string);
   }

   private void m(List<Vector3> list, boolean bl) {
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
            mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(vector3.getX(), vector3.getY(), vector3.getZ(), true));
            e = 0.0;
         } else {
            mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(vector3.getX(), vector3.getY(), vector3.getZ(), false));
         }

         d = vector3.getY();
      }
   }

   private boolean o() {
      return !GameUtil.isOnHypixel() || !mc.thePlayer.E() && mc.thePlayer.ticksExisted > (int)j;
   }
}
