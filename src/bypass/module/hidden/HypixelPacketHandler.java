package bypass.module.hidden;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.game.TickEvent;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.TeleportEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.HiddenModule;
import bypass.module.combat.AutoClicker;
import bypass.util.GameUtil;
import bypass.util.Stopwatch;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public final class HypixelPacketHandler extends HiddenModule {
   private final Queue<Packet<?>> e = new LinkedList<>();
   private final Queue<Packet<?>> G = new LinkedList<>();
   private final Queue<Packet<?>> x = new LinkedList<>();
   private final Stopwatch B = new Stopwatch();
   private final Stopwatch v = new Stopwatch();
   private final Stopwatch Z = new Stopwatch();
   private final Stopwatch E = new Stopwatch();
   private final Stopwatch T = new Stopwatch();
   private static final List<Packet<?>> Q = new CopyOnWriteArrayList<>();
   private final Queue<Packet<?>> U = new LinkedBlockingQueue<>();
   private static short m;
   private static short S;
   public static int u;
   public int P;
   private static boolean f;
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
         if (packetEvent.getPacket() instanceof S08PacketPlayerPosLook) {
            TeleportEvent teleportEvent = new TeleportEvent((S08PacketPlayerPosLook)packetEvent.getPacket());
            client.getEventBus().post(teleportEvent);
         }

         if (packetEvent.getPacket() instanceof S3FPacketCustomPayload) {
            this.v.reset();
         }
      }

      if (packetEvent.getDirection() == PacketDirection.OUTGOING
         && !Minecraft.c
         && mc.theWorld != null
         && mc.thePlayer != null
         && GameUtil.isOnHypixel()
         && !mc.isSingleplayer()
         && ServerData.I != null
         && ServerData.I.toLowerCase().endsWith("hypixel.net")) {
         if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)packetEvent.getPacket();
            if (client.getModuleManager().get(AutoClicker.class).isEnabled()) {
               Entity entity = c02PacketUseEntity.getEntityFromWorld(mc.theWorld);
               if (entity instanceof EntityPlayer && BotDetector.w.contains(entity) && c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
                  packetEvent.cancel();
               }
            }
         }

         if (packetEvent.getPacket() instanceof C0FPacketConfirmTransaction) {
            C0FPacketConfirmTransaction c0FPacketConfirmTransaction = (C0FPacketConfirmTransaction)packetEvent.getPacket();
            if (c0FPacketConfirmTransaction.getUid() < 0) {
               f = true;
            } else {
               S = c0FPacketConfirmTransaction.getUid();
            }
         }
      }
   };
   public static int r;
   @EventHandler(TickEvent.class)
   private Listener<TickEvent> onTick = tickEvent -> {
      r++;
      if (r >= 54) {
         r = 0;
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> {
      this.Z.reset();
      this.G.clear();
      this.e.clear();
      u = 0;
      this.P = -1;
      this.x.clear();
      this.v.reset();
      PlayerListTracker.playerStats.clear();
      PlayerListTracker.checkedPlayers.clear();
      PlayerListTracker.requestedPlayers.clear();
      Q.clear();
      this.E.reset();
      S = 0;
      m = 0;
      f = false;
   };

   public HypixelPacketHandler() {
      client.getEventBus().subscribe(this);
   }

   public static void F() {
      if (GameUtil.isInHypixelGame()) {
         try {
            PlayerCapabilities playerCapabilities = (PlayerCapabilities)mc.thePlayer.capabilities.getClass().newInstance();
            playerCapabilities.isFlying = true;
            playerCapabilities.allowFlying = true;
            mc.thePlayer.sendQueue.u(new C13PacketPlayerAbilities(playerCapabilities));
         } catch (InstantiationException | IllegalAccessException value) {
            value.printStackTrace();
         }
      }
   }

   public void d() {
      while (this.x.peek() != null) {
         Packet packet = this.x.poll();
         if (packet != null) {
            mc.thePlayer.sendQueue.u(packet);
         }
      }

      this.x.clear();
   }
}
