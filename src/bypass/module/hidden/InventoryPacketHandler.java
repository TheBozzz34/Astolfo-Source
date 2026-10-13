package bypass.module.hidden;

import bypass.command.UnusedTeleportCommand;
import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.HiddenModule;
import bypass.module.player.InventoryManager;
import bypass.util.GameUtil;
import bypass.util.Stopwatch;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.BlockChest;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

public final class InventoryPacketHandler extends HiddenModule {
   public static Stopwatch F = new Stopwatch();
   public static boolean u;
   private ScheduledExecutorService N = Executors.newScheduledThreadPool(1);
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = var1 -> {
      u = false;
      this.N.shutdownNow();
      this.N = Executors.newScheduledThreadPool(1);
      UnusedTeleportCommand.W = 0;
   };
   private final Packet<?> V = new C0DPacketCloseWindow(0);
   private boolean d;
   private Stopwatch w = new Stopwatch();
   private final Queue<Packet<?>> p = new LinkedList<>();
   @EventHandler(value = PacketEvent.class, priority = EventPriority.HIGHEST)
   private Listener<PacketEvent> onPacket = var1 -> {
      if (var1.getDirection() == PacketDirection.OUTGOING) {
         boolean var5 = InventoryManager.general.getSetting("Spoof-GUI").asBoolean();
         boolean var6 = InventoryManager.general.getSetting("Auto-Stop").asBoolean();
         int var7 = InventoryManager.general.getSetting("Drop-Delay").asInt();
         if (var6 && var1.getPacket() instanceof C03PacketPlayer) {
            if (!GameUtil.isMoving(mc.thePlayer)) {
               Packet var8 = this.p.poll();
               if (var8 != null) {
                  this.N.schedule(() -> {
                     if (!u) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                     }
                  }, 50L, TimeUnit.MILLISECONDS);
                  this.N.schedule(() -> {
                     if (u) {
                        mc.thePlayer.sendQueue.addToSendQueue(var8);
                     }
                  }, 55 + var7, TimeUnit.MILLISECONDS);
               }
            } else if (u) {
               this.N.schedule(() -> {
                  if (u) {
                     mc.thePlayer.sendQueue.addToSendQueue(this.V);
                  }
               }, 50L, TimeUnit.MILLISECONDS);
            }
         }

         if (var1.getPacket() instanceof C0EPacketClickWindow) {
            C0EPacketClickWindow var10 = (C0EPacketClickWindow)var1.getPacket();
            int var9 = var10.getWindowId();
            if (var9 == 0) {
               if (var6) {
                  if (GameUtil.isMoving(mc.thePlayer)) {
                     this.p.offer(var1.getPacket());
                     var1.cancel();
                  }
               } else if (var5) {
                  mc.thePlayer.sendQueue.u(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                  mc.thePlayer.sendQueue.u(var1.getPacket());
                  mc.thePlayer.sendQueue.addToSendQueue(this.V);
                  var1.cancel();
               }
            }

            F.reset();
         }

         if (var1.getPacket() instanceof C0DPacketCloseWindow) {
            C0DPacketCloseWindow var11 = (C0DPacketCloseWindow)var1.getPacket();
            if (var11.e() == 0) {
               if (!u) {
                  var1.cancel();
               }

               u = false;
            }
         }

         if (var1.getPacket() instanceof C16PacketClientStatus) {
            C16PacketClientStatus var12 = (C16PacketClientStatus)var1.getPacket();
            if (var12.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
               if (u) {
                  var1.cancel();
               }

               u = true;
            }
         }

         if (var1.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement var13 = (C08PacketPlayerBlockPlacement)var1.getPacket();
            if (var13.getPlacedBlockDirection() != 255 && mc.theWorld.getBlockState(var13.getPosition()).getBlock() instanceof BlockChest) {
               F.reset();
            }
         }
      } else if (var1.getPacket() instanceof S2EPacketCloseWindow) {
         u = false;
      }
   };
   private static final long e = 110913153721384L;

   public InventoryPacketHandler() {
      client.getEventBus().subscribe(this);
   }
}
