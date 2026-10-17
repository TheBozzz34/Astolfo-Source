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
   public static Stopwatch inventoryActionTimer = new Stopwatch();
   public static boolean inventoryOpen;
   private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> {
      inventoryOpen = false;
      this.scheduler.shutdownNow();
      this.scheduler = Executors.newScheduledThreadPool(1);
      UnusedTeleportCommand.riderGodTeleports = 0;
   };
   private final Packet<?> closeInventoryPacket = new C0DPacketCloseWindow(0);
   private boolean unusedFlag;
   private Stopwatch unusedTimer = new Stopwatch();
   private final Queue<Packet<?>> delayedClickPackets = new LinkedList<>();
   @EventHandler(value = PacketEvent.class, priority = EventPriority.HIGHEST)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         boolean bl = InventoryManager.general.getSetting("Spoof-GUI").asBoolean();
         boolean bl2 = InventoryManager.general.getSetting("Auto-Stop").asBoolean();
         int i = InventoryManager.general.getSetting("Drop-Delay").asInt();
         if (bl2 && packetEvent.getPacket() instanceof C03PacketPlayer) {
            if (!GameUtil.isMoving(mc.thePlayer)) {
               Packet packet = this.delayedClickPackets.poll();
               if (packet != null) {
                  this.scheduler.schedule(() -> {
                     if (!inventoryOpen) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                     }
                  }, 50L, TimeUnit.MILLISECONDS);
                  this.scheduler.schedule(() -> {
                     if (inventoryOpen) {
                        mc.thePlayer.sendQueue.addToSendQueue(packet);
                     }
                  }, 55 + i, TimeUnit.MILLISECONDS);
               }
            } else if (inventoryOpen) {
               this.scheduler.schedule(() -> {
                  if (inventoryOpen) {
                     mc.thePlayer.sendQueue.addToSendQueue(this.closeInventoryPacket);
                  }
               }, 50L, TimeUnit.MILLISECONDS);
            }
         }

         if (packetEvent.getPacket() instanceof C0EPacketClickWindow) {
            C0EPacketClickWindow c0EPacketClickWindow = (C0EPacketClickWindow)packetEvent.getPacket();
            int j = c0EPacketClickWindow.getWindowId();
            if (j == 0) {
               if (bl2) {
                  if (GameUtil.isMoving(mc.thePlayer)) {
                     this.delayedClickPackets.offer(packetEvent.getPacket());
                     packetEvent.cancel();
                  }
               } else if (bl) {
                  mc.thePlayer.sendQueue.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                  mc.thePlayer.sendQueue.sendPacketNoEvent(packetEvent.getPacket());
                  mc.thePlayer.sendQueue.addToSendQueue(this.closeInventoryPacket);
                  packetEvent.cancel();
               }
            }

            inventoryActionTimer.reset();
         }

         if (packetEvent.getPacket() instanceof C0DPacketCloseWindow) {
            C0DPacketCloseWindow c0DPacketCloseWindow = (C0DPacketCloseWindow)packetEvent.getPacket();
            if (c0DPacketCloseWindow.getWindowId() == 0) {
               if (!inventoryOpen) {
                  packetEvent.cancel();
               }

               inventoryOpen = false;
            }
         }

         if (packetEvent.getPacket() instanceof C16PacketClientStatus) {
            C16PacketClientStatus c16PacketClientStatus = (C16PacketClientStatus)packetEvent.getPacket();
            if (c16PacketClientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
               if (inventoryOpen) {
                  packetEvent.cancel();
               }

               inventoryOpen = true;
            }
         }

         if (packetEvent.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement = (C08PacketPlayerBlockPlacement)packetEvent.getPacket();
            if (c08PacketPlayerBlockPlacement.getPlacedBlockDirection() != 255 && mc.theWorld.getBlockState(c08PacketPlayerBlockPlacement.getPosition()).getBlock() instanceof BlockChest) {
               inventoryActionTimer.reset();
            }
         }
      } else if (packetEvent.getPacket() instanceof S2EPacketCloseWindow) {
         inventoryOpen = false;
      }
   };

   public InventoryPacketHandler() {
      client.getEventBus().subscribe(this);
   }
}
