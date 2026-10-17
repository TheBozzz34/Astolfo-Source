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
   private final Queue<Packet<?>> pendingPackets = new LinkedList<>();
   private final Queue<Packet<?>> delayedPackets = new LinkedList<>();
   private final Queue<Packet<?>> queuedPackets = new LinkedList<>();
   private final Stopwatch unusedTimer = new Stopwatch();
   private final Stopwatch customPayloadTimer = new Stopwatch();
   private final Stopwatch worldJoinTimer = new Stopwatch();
   private final Stopwatch worldLoadTimer = new Stopwatch();
   private final Stopwatch spareTimer = new Stopwatch();
   private static final List<Packet<?>> trackedPackets = new CopyOnWriteArrayList<>();
   private final Queue<Packet<?>> blockingPacketQueue = new LinkedBlockingQueue<>();
   private static short lastNegativeTransactionUid;
   private static short lastTransactionUid;
   public static int packetCounter;
   public int pendingId;
   private static boolean sentNegativeTransaction;
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
         if (packetEvent.getPacket() instanceof S08PacketPlayerPosLook) {
            TeleportEvent teleportEvent = new TeleportEvent((S08PacketPlayerPosLook)packetEvent.getPacket());
            client.getEventBus().post(teleportEvent);
         }

         if (packetEvent.getPacket() instanceof S3FPacketCustomPayload) {
            this.customPayloadTimer.reset();
         }
      }

      if (packetEvent.getDirection() == PacketDirection.OUTGOING
         && !Minecraft.hypixelInHostsFile
         && mc.theWorld != null
         && mc.thePlayer != null
         && GameUtil.isOnHypixel()
         && !mc.isSingleplayer()
         && ServerData.currentServerIp != null
         && ServerData.currentServerIp.toLowerCase().endsWith("hypixel.net")) {
         if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)packetEvent.getPacket();
            if (client.getModuleManager().get(AutoClicker.class).isEnabled()) {
               Entity entity = c02PacketUseEntity.getEntityFromWorld(mc.theWorld);
               if (entity instanceof EntityPlayer && BotDetector.bots.contains(entity) && c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
                  packetEvent.cancel();
               }
            }
         }

         if (packetEvent.getPacket() instanceof C0FPacketConfirmTransaction) {
            C0FPacketConfirmTransaction c0FPacketConfirmTransaction = (C0FPacketConfirmTransaction)packetEvent.getPacket();
            if (c0FPacketConfirmTransaction.getUid() < 0) {
               sentNegativeTransaction = true;
            } else {
               lastTransactionUid = c0FPacketConfirmTransaction.getUid();
            }
         }
      }
   };
   public static int tickCycle;
   @EventHandler(TickEvent.class)
   private Listener<TickEvent> onTick = tickEvent -> {
      tickCycle++;
      if (tickCycle >= 54) {
         tickCycle = 0;
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> {
      this.worldJoinTimer.reset();
      this.delayedPackets.clear();
      this.pendingPackets.clear();
      packetCounter = 0;
      this.pendingId = -1;
      this.queuedPackets.clear();
      this.customPayloadTimer.reset();
      PlayerListTracker.playerStats.clear();
      PlayerListTracker.checkedPlayers.clear();
      PlayerListTracker.requestedPlayers.clear();
      trackedPackets.clear();
      this.worldLoadTimer.reset();
      lastTransactionUid = 0;
      lastNegativeTransactionUid = 0;
      sentNegativeTransaction = false;
   };

   public HypixelPacketHandler() {
      client.getEventBus().subscribe(this);
   }

   public static void sendFlyingAbilities() {
      if (GameUtil.isInHypixelGame()) {
         try {
            PlayerCapabilities playerCapabilities = (PlayerCapabilities)mc.thePlayer.capabilities.getClass().newInstance();
            playerCapabilities.isFlying = true;
            playerCapabilities.allowFlying = true;
            mc.thePlayer.sendQueue.sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
         } catch (InstantiationException | IllegalAccessException value) {
            value.printStackTrace();
         }
      }
   }

   public void flushQueuedPackets() {
      while (this.queuedPackets.peek() != null) {
         Packet packet = this.queuedPackets.poll();
         if (packet != null) {
            mc.thePlayer.sendQueue.sendPacketNoEvent(packet);
         }
      }

      this.queuedPackets.clear();
   }
}
