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
   private final Queue<Packet<?>> j = new LinkedBlockingQueue<>();
   private final Stopwatch s = new Stopwatch();
   public static EntityOtherPlayerMP H;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING) {
         if (var1x.getPacket() instanceof C03PacketPlayer) {
            this.j.add(var1x.getPacket());
            var1x.cancel();
            return;
         }

         if (var1x.getPacket() instanceof C07PacketPlayerDigging
            || var1x.getPacket() instanceof C08PacketPlayerBlockPlacement
            || var1x.getPacket() instanceof C0BPacketEntityAction
            || var1x.getPacket() instanceof C0APacketAnimation
            || var1x.getPacket() instanceof C02PacketUseEntity) {
            this.j.add(var1x.getPacket());
            var1x.cancel();
         }
      }
   };
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      this.p();
      if (var1x.getState() == EventState.PRE && this.g_boolean() && this.s.hasElapsed(this.g())) {
         if (mc.theWorld != null && mc.thePlayer != null) {
            this.h();
         }

         this.s.reset();
      }
   };
   @EventHandler(RenderModelEvent.class)
   private Listener<RenderModelEvent> onRenderModel = var0 -> {
      if (var0.getState() == EventState.PRE && var0.getEntity().getEntityId() == -420) {
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, (mc.thePlayer.getDistanceToEntity(var0.getEntity()) - 0.2F) / 2.0F);
      }
   };
   @EventHandler(TeleportEvent.class)
   private Listener<TeleportEvent> onTeleport = var1x -> {
      if (var1x.y()) {
         Entity var5 = mc.theWorld.getEntityByID(-420);
         if (H != null && var5 != null) {
            var1x.b(var5);
            var1x.cancel();
         }
      } else {
         var1x.i(this);
      }
   };
   private static final long c = 85402930600385L;

   public Blink(Category var1) {
      super(var1);
      this.addSettings(this.pulsing, this.entity);
   }

   @Override
   public void p() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + this.j.size());
      super.p();
   }

   @Override
   public void onEnable() {
      if (mc.theWorld != null && mc.thePlayer != null && this.entity.getValue()) {
         H = new EntityOtherPlayerMP(mc.thePlayer.getEntityWorld(), new GameProfile(UUID.randomUUID(), mc.thePlayer.getName()));
         H.worldObj = mc.thePlayer.getEntityWorld();
         H.entityUniqueID = mc.thePlayer.getUniqueID();
         H.z(mc.thePlayer);
         H.inventory = mc.thePlayer.inventory;
         H.capabilities = mc.thePlayer.capabilities;
         mc.theWorld.addEntityToWorld(-420, H);
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (mc.theWorld != null && mc.thePlayer != null) {
         while (true) {
            Packet var4;
            if ((var4 = this.j.poll()) == null) {
               this.j.clear();
               mc.theWorld.removeEntityFromWorld(-420);
               break;
            }

            mc.thePlayer.sendQueue.u(var4);
         }
      } else {
         this.setEnabled(false);
      }

      super.onDisable();
   }

   private boolean g_boolean() {
      return this.pulsing.getSetting("Pulse").asBoolean();
   }

   private int g() {
      return this.pulsing.getSetting("Delay").asInt();
   }

   private void h() {
      Packet var4;
      while ((var4 = this.j.poll()) != null) {
         mc.thePlayer.sendQueue.u(var4);
      }

      if (this.entity.getValue() && H != null) {
         H.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
      }
   }
}
