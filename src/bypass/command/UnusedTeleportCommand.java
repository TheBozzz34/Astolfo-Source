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
   private final Listener<PacketEvent> onPacket = var1 -> {
      if (var1.getDirection() == PacketDirection.OUTGOING) {
         if (var1.getPacket() instanceof C0CPacketInput && mc.thePlayer.E()) {
            C0CPacketInput var5 = (C0CPacketInput)var1.getPacket();
            var5.d(true);
            return;
         }

         if (this.z == 1 && !this.Q.hasElapsed(6000.0F) || !this.p && var1.getPacket() instanceof C03PacketPlayer) {
            var1.cancel();
         }
      } else if (var1.getPacket() instanceof S08PacketPlayerPosLook && !this.p) {
         this.p = true;
      }
   };
   @EventHandler(MotionEvent.class)
   private final Listener<MotionEvent> onMotion = var1 -> {
      long var2 = d ^ 57577220663433L;
      long var4 = var2 ^ 139774890254778L;
      if (this.p) {
         boolean var7 = client.getModuleManager().get(RiderGod.class).isEnabled();
         if (!this.E.isEmpty()) {
            EntityPlayer var8 = mc.theWorld.getPlayerEntityByName(this.E);
            if (var8 != null) {
               if (var8.ridingEntity != null) {
                  this.C = var8.ridingEntity.posX;
                  this.h = var8.ridingEntity.posY;
                  this.X = var8.ridingEntity.posZ;
               } else {
                  this.C = var8.posX;
                  this.h = var8.posY;
                  this.X = var8.posZ;
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
         double var11 = mc.thePlayer.posY - this.h;
         if (var7) {
            mc.thePlayer.setPosition(this.C, this.h, this.X);
         } else {
            boolean var10 = var11 < -30.0 && mc.thePlayer.posY > 40.0 && this.E.isEmpty() && !ScoreboardTracker.r();
            if (var10) {
               this.m(
                  InfiniteReach.Z(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.posX, mc.thePlayer.posY + 50.0, mc.thePlayer.posZ, 8.9),
                  var7
               );
               this.m(InfiniteReach.Z(mc.thePlayer.posX, mc.thePlayer.posY + 50.0, mc.thePlayer.posZ, this.C, this.h, this.X, 8.9), var7);
            } else {
               this.m(PathFinder.i(new Vector3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vector3(this.C, this.h, this.X), 8.9), var7);
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

         if (var7) {
            W++;
         }
      }
   };
   @EventHandler(MoveEvent.class)
   private final Listener<MoveEvent> onMove = var0 -> {
      GameUtil.setMoveSpeed(var0, 0.0, 90.0F);
      var0.setY(mc.thePlayer.motionY = 0.0);
   };
   private static final long d = 136569738441856L;
   private static final long j = -8984061303844765666L;

   public UnusedTeleportCommand() {
      super("teleport", "<x> <y> <z>/<x> <z>");
      this.setAliases(Collections.singletonList("tp"));
   }

   @Override
   public void runCommand(String[] var1) {
      switch (var1.length) {
         case 2:
            if (var1[1].equalsIgnoreCase("cancel")) {
               client.getEventBus().unsubscribe(this);
            } else if (var1[1].equalsIgnoreCase("teammate")) {
               if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == Items.compass) {
                  this.s = true;
                  this.c("teammate");
                  client.getEventBus().subscribe(this);
               } else {
                  toasts.x("You must hold your compass to use this");
               }
            } else if (var1[1].equalsIgnoreCase("back")) {
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
               EntityPlayer var16 = mc.theWorld.getPlayerEntityByName(var1[1]);
               boolean var6 = false;
               if (var16 != null) {
                  this.C = var16.posX;
                  this.h = var16.posY;
                  this.X = var16.posZ;
                  this.E = var16.getName();
               } else {
                  BlockPos var19 = PlayerListTracker.lastPositions.get(var1[1]);
                  if (var19 == null) {
                     toasts.x("Couldn't find this player");
                     break;
                  }

                  if (mc.thePlayer.getDistance(var19.getX(), var19.getY(), var19.getZ()) > 2000.0) {
                     toasts.x(var1[1] + " last position was too far away");
                     break;
                  }

                  this.C = var19.getX();
                  this.h = 128.0;
                  this.X = var19.getZ();
                  this.E = var1[1];
                  var6 = true;
               }

               if (this.o()) {
                  toasts.x("You must be on a boat to TP");
                  return;
               }

               client.getEventBus().subscribe(this);
               this.c(var6 ? "tracked" : "found");
            }
            break;
         case 3:
            if (this.o()) {
               toasts.x("You must be on a boat to to TP");
               return;
            }

            try {
               double var15 = Double.parseDouble(var1[1]);
               double var18 = Double.parseDouble(var1[2]);
               this.C = var15;
               this.h = 128.0;
               this.X = var18;
               client.getEventBus().subscribe(this);
               this.c("");
            } catch (NumberFormatException var11) {
               ClientChat.CHAT.f("put cords correctly noob");
            }
            break;
         case 4:
            if (this.o()) {
               toasts.x("You must be on a boat to TP");
               return;
            }

            try {
               double var14 = Double.parseDouble(var1[1]);
               double var17 = Double.parseDouble(var1[2]);
               double var20 = Double.parseDouble(var1[3]);
               this.C = var14;
               this.h = var17;
               this.X = var20;
               client.getEventBus().subscribe(this);
               this.c("");
            } catch (NumberFormatException var12) {
               ClientChat.CHAT.f("put cords correctly noob");
            }
            break;
         case 5:
            if (var1[1].equalsIgnoreCase("update")) {
               try {
                  double var5 = Double.parseDouble(var1[2]);
                  double var7 = Double.parseDouble(var1[3]);
                  double var9 = Double.parseDouble(var1[4]);
                  this.C = var5;
                  this.h = var7;
                  this.X = var9;
               } catch (NumberFormatException var13) {
               }
            }
            break;
         default:
            ClientChat.CHAT.I(this.getName() + ": " + this.getUsage());
      }
   }

   private void c(String var1) {
      long var2 = d ^ 23328835604355L;
      long var4 = var2 ^ 103860757841584L;
      this.z = 0;
      this.p = false;
      if (client.getModuleManager().get(RiderGod.class).isEnabled()) {
         this.p = true;
      }

      mc.thePlayer.motionX = 0.0;
      mc.thePlayer.motionZ = 0.0;
      if (HUD.isPowerBranding()) {
         if (var1.equals("tracked") || var1.equals("found")) {
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
         switch (var1) {
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

   public static void q(String var0, String var1) {
      mc.ingameGUI
         .getChatGUI()
         .printChatMessage(
            new ChatComponentText(
               EnumChatFormatting.DARK_GRAY + "[" + EnumChatFormatting.RED + var0 + EnumChatFormatting.DARK_GRAY + "]" + EnumChatFormatting.WHITE + " " + var1
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

   public static void f(String var0) {
      q("Power", var0);
   }

   private void m(List<Vector3> var1, boolean var2) {
      double var6 = mc.thePlayer.posY;
      double var8 = 0.0;

      for (Vector3 var11 : var1) {
         if (var11.getY() < var6) {
            var8 += var6 - var11.getY();
         }

         if (var2) {
            mc.thePlayer
               .sendQueue
               .addToSendQueue(new VehicleMovePacket(var11.getX(), var11.getY(), var11.getZ(), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
         } else if (var8 > 2.85) {
            mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(var11.getX(), var11.getY(), var11.getZ(), true));
            var8 = 0.0;
         } else {
            mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(var11.getX(), var11.getY(), var11.getZ(), false));
         }

         var6 = var11.getY();
      }
   }

   private boolean o() {
      return !GameUtil.isOnHypixel() || !mc.thePlayer.E() && mc.thePlayer.ticksExisted > (int)j;
   }
}
