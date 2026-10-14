package bypass.module.player;

import bypass.event.CancellableEvent;
import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.game.MouseClickEvent;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MoveEvent;
import bypass.event.player.PushOutOfBlocksEvent;
import bypass.event.player.TeleportEvent;
import bypass.event.render.RenderModelEvent;
import bypass.event.render.SetupTerrainEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.movement.Flight;
import bypass.setting.BooleanSetting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class Freecam extends Module {
   private Entity z;
   public static final BooleanSetting view = new BooleanSetting("View", "Let's you spectate other players", true);
   private final BooleanSetting staticFlight = new BooleanSetting("Static-Flight", "Doesnt use the ingame minecraft flying", false);
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> this.setEnabled(false);
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (packetEvent.getPacket() instanceof C0BPacketEntityAction) {
            C0BPacketEntityAction c0BPacketEntityAction = (C0BPacketEntityAction)packetEvent.getPacket();
            if (c0BPacketEntityAction.getAction() == C0BPacketEntityAction.Action.OPEN_INVENTORY) {
               packetEvent.cancel();
            }
         } else if (packetEvent.getPacket() instanceof C08PacketPlayerBlockPlacement
            || packetEvent.getPacket() instanceof C03PacketPlayer
            || packetEvent.getPacket() instanceof C02PacketUseEntity
            || packetEvent.getPacket() instanceof C07PacketPlayerDigging) {
            packetEvent.cancel();
         }
      }
   };
   @EventHandler(TeleportEvent.class)
   private Listener<TeleportEvent> onTeleport = teleportEvent -> {
      if (teleportEvent.y()) {
         teleportEvent.b(mc.theWorld.getEntityByID(-1337));
         teleportEvent.cancel();
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      long l = c ^ 51117225364289L;
      long m = l ^ 128323223319382L;
      if (!client.getModuleManager().get(Flight.class).isEnabled() && !this.staticFlight.getValue()) {
         mc.thePlayer.capabilities.isFlying = true;
      }

      if (this.staticFlight.getValue()) {
         mc.thePlayer.motionY = 0.0;
         if (mc.inGameHasFocus) {
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
               mc.thePlayer.motionY = 3.0;
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
               mc.thePlayer.motionY = -3.0;
            }
         }
      }

      mc.thePlayer.noClip = true;
   };
   @EventHandler(RenderModelEvent.class)
   private Listener<RenderModelEvent> onRenderModel = renderModelEvent -> {
      if (renderModelEvent.getState() == EventState.PRE && renderModelEvent.getEntity().getEntityId() == -1337) {
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, (mc.thePlayer.getDistanceToEntity(renderModelEvent.getEntity()) - 0.2F) / 2.0F);
      }

      if (renderModelEvent.getEntity() == this.z) {
         renderModelEvent.cancel();
      }
   };
   @EventHandler(MouseClickEvent.class)
   private Listener<MouseClickEvent> onMouseClick = mouseClickEvent -> {
      if (mouseClickEvent.button == 1) {
         if (view.getValue()) {
            view.set(false);
         } else {
            Entity entity = mc.objectMouseOver.entityHit;
            if (entity instanceof EntityPlayer) {
               this.z = entity;
               view.set(true);
            }
         }
      }

      if (view.getValue()) {
         mouseClickEvent.cancel();
      }
   };
   @EventHandler(SetupTerrainEvent.class)
   private Listener<SetupTerrainEvent> onSetupTerrain = setupTerrainEvent -> {
      setupTerrainEvent.cancel();
      if (view.getValue() && this.z != null) {
         if (Mouse.isButtonDown(0)) {
            mc.thePlayer.setPosition(this.z.posX, this.z.posY, this.z.posZ);
            this.z.rotationYaw = mc.thePlayer.rotationYaw;
            this.z.rotationPitch = mc.thePlayer.rotationPitch;
            this.z.setRotationYawHead(mc.thePlayer.getRotationYawHead());
         } else {
            mc.thePlayer.z(this.z);
         }
      }
   };
   @EventHandler(PushOutOfBlocksEvent.class)
   private Listener<PushOutOfBlocksEvent> onPushOutOfBlocks = CancellableEvent::cancel;
   private static final long c = 120671400248740L;

   public Freecam(Category category) {
      super(category);
      this.addSettings(view, this.staticFlight);
   }

   @Override
   public void onEnable() {
      if (mc.thePlayer != null) {
         this.f(-1337);
         mc.thePlayer.onGround = false;
         mc.thePlayer.motionY = 0.2;
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      Entity entity = mc.theWorld.removeEntityFromWorld(-1337);
      if (entity != null) {
         mc.thePlayer.z(entity);
      }

      if (mc.thePlayer != null) {
         mc.thePlayer.noClip = true;
         mc.thePlayer.capabilities.isFlying = false;
         mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0.0;
      }

      super.onDisable();
   }

   private EntityOtherPlayerMP f(int i) {
      if (mc.theWorld == null) {
         return null;
      }

      if (i > 0) {
         i *= -1;
      }

      EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
      entityOtherPlayerMP.z(mc.thePlayer);
      entityOtherPlayerMP.rotationYawHead = mc.thePlayer.rotationYawHead;
      entityOtherPlayerMP.inventory.copyInventory(mc.thePlayer.inventory);
      mc.theWorld.addEntityToWorld(i, entityOtherPlayerMP);
      return entityOtherPlayerMP;
   }
}
