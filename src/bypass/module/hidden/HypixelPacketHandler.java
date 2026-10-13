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
   private final Listener<PacketEvent> onPacket = var1 -> {
      long var2 = c ^ 111837101702005L;
      long var4 = var2 ^ 129927802570145L;
      if (var1.getDirection() == PacketDirection.RECEIVED) {
         if (var1.getPacket() instanceof S08PacketPlayerPosLook) {
            TeleportEvent var7 = new TeleportEvent((S08PacketPlayerPosLook)var1.getPacket());
            client.getEventBus().post(var7);
         }

         if (var1.getPacket() instanceof S3FPacketCustomPayload) {
            this.v.reset();
         }
      }

      if (var1.getDirection() == PacketDirection.OUTGOING
         && !Minecraft.c
         && mc.theWorld != null
         && mc.thePlayer != null
         && GameUtil.isOnHypixel()
         && !mc.isSingleplayer()
         && ServerData.I != null
         && ServerData.I.toLowerCase().endsWith("hypixel.net")) {
         if (var1.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity var9 = (C02PacketUseEntity)var1.getPacket();
            if (client.getModuleManager().get(AutoClicker.class).isEnabled()) {
               Entity var8 = var9.getEntityFromWorld(mc.theWorld);
               if (var8 instanceof EntityPlayer && BotDetector.w.contains(var8) && var9.getAction() == C02PacketUseEntity.Action.ATTACK) {
                  var1.cancel();
               }
            }
         }

         if (var1.getPacket() instanceof C0FPacketConfirmTransaction) {
            C0FPacketConfirmTransaction var10 = (C0FPacketConfirmTransaction)var1.getPacket();
            if (var10.getUid() < 0) {
               f = true;
            } else {
               S = var10.getUid();
            }
         }
      }
   };
   public static int r;
   @EventHandler(TickEvent.class)
   private Listener<TickEvent> onTick = var0 -> {
      r++;
      if (r >= (int)h) {
         r = 0;
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = var1 -> {
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
   private static final long c = 40595861373799L;
   private static final long h = -1237826179050766282L;

   public HypixelPacketHandler() {
      client.getEventBus().subscribe(this);
   }

   public static void F() {
      if (GameUtil.isInHypixelGame()) {
         try {
            PlayerCapabilities var2 = (PlayerCapabilities)mc.thePlayer.capabilities.getClass().newInstance();
            var2.isFlying = true;
            var2.allowFlying = true;
            mc.thePlayer.sendQueue.u(new C13PacketPlayerAbilities(var2));
         } catch (InstantiationException | IllegalAccessException var4) {
            var4.printStackTrace();
         }
      }
   }

   public void d() {
      while (this.x.peek() != null) {
         Packet var4 = this.x.poll();
         if (var4 != null) {
            mc.thePlayer.sendQueue.u(var4);
         }
      }

      this.x.clear();
   }
}
