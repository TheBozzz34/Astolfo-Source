package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.TeleportEvent;
import bypass.event.render.RenderModelEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.Stopwatch;
import com.mojang.authlib.GameProfile;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public final class Blink extends Module {
   private final MapSetting pulsing = new MapSettingBuilder("Pulsing")
      .ordered()
      .add("Delay", new NumberSetting<>("Delay", "Delay for pulsation", 150, 0, 1000))
      .add("Pulse", new BooleanSetting("Pulse", "Sets pulse mode", false))
      .build();
   private final BooleanSetting entity = new BooleanSetting("Entity", "Shows serversided entity", true);
   private final Queue<Packet<?>> packetQueue = new LinkedBlockingQueue<>();
   private final Stopwatch pulseTimer = new Stopwatch();
   public static EntityOtherPlayerMP fakePlayer;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (packetEvent.getPacket() instanceof C03PacketPlayer) {
            this.packetQueue.add(packetEvent.getPacket());
            packetEvent.cancel();
            return;
         }

         if (packetEvent.getPacket() instanceof C07PacketPlayerDigging
            || packetEvent.getPacket() instanceof C08PacketPlayerBlockPlacement
            || packetEvent.getPacket() instanceof C0BPacketEntityAction
            || packetEvent.getPacket() instanceof C0APacketAnimation
            || packetEvent.getPacket() instanceof C02PacketUseEntity) {
            this.packetQueue.add(packetEvent.getPacket());
            packetEvent.cancel();
         }
      }
   };
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      this.updateDisplayName();
      if (motionEvent.getState() == EventState.PRE && this.g_boolean() && this.pulseTimer.hasElapsed(this.getPulseDelay())) {
         if (mc.theWorld != null && mc.thePlayer != null) {
            this.flushPackets();
         }

         this.pulseTimer.reset();
      }
   };
   @EventHandler(RenderModelEvent.class)
   private Listener<RenderModelEvent> onRenderModel = renderModelEvent -> {
      if (renderModelEvent.getState() == EventState.PRE && renderModelEvent.getEntity().getEntityId() == -420) {
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, (mc.thePlayer.getDistanceToEntity(renderModelEvent.getEntity()) - 0.2F) / 2.0F);
      }
   };
   @EventHandler(TeleportEvent.class)
   private Listener<TeleportEvent> onTeleport = teleportEvent -> {
      if (teleportEvent.isLagback()) {
         Entity entity = mc.theWorld.getEntityByID(-420);
         if (fakePlayer != null && entity != null) {
            teleportEvent.applyToEntity(entity);
            teleportEvent.cancel();
         }
      } else {
         teleportEvent.disableModule(this);
      }
   };

   public Blink(Category category) {
      super(category);
      this.addSettings(this.pulsing, this.entity);
   }

   @Override
   public void updateDisplayName() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + this.packetQueue.size());
      super.updateDisplayName();
   }

   @Override
   public void onEnable() {
      if (mc.theWorld != null && mc.thePlayer != null && this.entity.getValue()) {
         fakePlayer = new EntityOtherPlayerMP(mc.thePlayer.getEntityWorld(), new GameProfile(UUID.randomUUID(), mc.thePlayer.getName()));
         fakePlayer.worldObj = mc.thePlayer.getEntityWorld();
         fakePlayer.entityUniqueID = mc.thePlayer.getUniqueID();
         fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
         fakePlayer.inventory = mc.thePlayer.inventory;
         fakePlayer.capabilities = mc.thePlayer.capabilities;
         mc.theWorld.addEntityToWorld(-420, fakePlayer);
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (mc.theWorld != null && mc.thePlayer != null) {
         while (true) {
            Packet packet;
            if ((packet = this.packetQueue.poll()) == null) {
               this.packetQueue.clear();
               mc.theWorld.removeEntityFromWorld(-420);
               break;
            }

            mc.thePlayer.sendQueue.u(packet);
         }
      } else {
         this.setEnabled(false);
      }

      super.onDisable();
   }

   private boolean g_boolean() {
      return this.pulsing.getSetting("Pulse").asBoolean();
   }

   private int getPulseDelay() {
      return this.pulsing.getSetting("Delay").asInt();
   }

   private void flushPackets() {
      Packet packet;
      while ((packet = this.packetQueue.poll()) != null) {
         mc.thePlayer.sendQueue.u(packet);
      }

      if (this.entity.getValue() && fakePlayer != null) {
         fakePlayer.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
      }
   }
}
