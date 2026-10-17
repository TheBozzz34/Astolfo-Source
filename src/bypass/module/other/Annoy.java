package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.MotionEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.PlayerModelVisibilityEvent;
import bypass.event.render.RenderLivingEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.util.Rotation;
import bypass.util.Rotator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vector3d;

public final class Annoy extends Module {
   private final Rotator rotator = new Rotator(false, 15.0F);
   private final EnumSetting<AnnoyMode> mode = new EnumSetting<>("Mode", "Set your annoy mode", AnnoyMode.CRAZY);
   private final NumberSetting<Integer> spam = new NumberSetting<>("Spam", "Amount of rod spam it will do", 2, 0, 5);
   private final NumberSetting<Float> range = new NumberSetting<>("Range", "Distance until it activates the hting", 4.0F, 0.0F, 6.0F);
   private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Spin-bot Speed", 15.0F, 0.0F, 50.0F);
   private final NumberSetting<Float> pitch = new NumberSetting<>("Pitch", "Pitch", 90.0F, -180.0F, 180.0F);
   private final BooleanSetting sneak = new BooleanSetting("Sneak", "Random sneak", true);
   private boolean sneaking;
   private float appliedYaw;
   private float lastYaw;
   private float appliedPitch;
   private float lastPitch;
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         switch ((AnnoyMode)this.mode.getValue()) {
            case CRAZY:
               EntityPlayer entityPlayer = this.getClosestPlayer();
               if (entityPlayer != null) {
                  Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                  Vector3d vector3d2 = new Vector3d(entityPlayer.posX, entityPlayer.posY + entityPlayer.height / 1.0, entityPlayer.posZ);
                  Rotation rotation = this.rotator.rotationTo(vector3d2, vector3d);
                  this.applyRotation(rotation.getYaw(), rotation.getPitch(), motionEvent);
               } else if (this.speed.getValue() > 0.0F) {
                  this.applyRotation(MathHelper.wrapAngleTo180_float(motionEvent.getLastYaw() + this.speed.getValue()), this.pitch.getValue(), motionEvent);
               } else {
                  this.applyRotation(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw), this.pitch.getValue(), motionEvent);
               }

               if (this.sneak.getValue()) {
                  if (!this.sneaking) {
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                     this.sneaking = true;
                  } else {
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                     this.sneaking = false;
                  }
               }
               break;
            case WITCH:
               EntityPlayer entityPlayer2 = this.getClosestPlayer();
               if (entityPlayer2 != null) {
                  Vector3d vector3d3 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                  Vector3d vector3d4 = new Vector3d(entityPlayer2.posX, entityPlayer2.posY + entityPlayer2.height / 1.0, entityPlayer2.posZ);
                  Rotation rotation2 = this.rotator.rotationTo(vector3d4, vector3d3);
                  this.applyRotation(rotation2.getYaw(), rotation2.getPitch(), motionEvent);
                  if (this.sneak.getValue()) {
                     this.sneaking = true;
                     mc.gameSettings.keyBindSneak.pressed = true;
                  }
               } else {
                  this.applyRotation(mc.thePlayer.rotationYaw + 180.0F, 89.0F, motionEvent);
                  if (this.sneak.getValue()) {
                     this.sneaking = false;
                     mc.gameSettings.keyBindSneak.pressed = false;
                  }
               }
               break;
            case DEATH:
               if (mc.thePlayer.getHealth() == 0.0F) {
                  double d = Math.random() * 16.0;
                  if (d > 8.0) {
                     d = Math.random();
                  } else {
                     d = -Math.random();
                  }

                  mc.thePlayer.setPosition(mc.thePlayer.posX + d, mc.thePlayer.posY + 2.0, mc.thePlayer.posZ + d);
                  mc.thePlayer.rotationYaw += 20.0F;
                  mc.thePlayer.sendQueue.sendPacketNoEvent(new C0APacketAnimation());
               }
         }

         if (!this.sneak.getValue() && this.sneaking) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            this.sneaking = false;
         }
      }
   };
   @EventHandler(PlayerModelVisibilityEvent.class)
   private Listener<PlayerModelVisibilityEvent> onPlayerModelVisibility = playerModelVisibilityEvent -> {
      if (this.sneaking && playerModelVisibilityEvent.getEntity() == mc.thePlayer) {
         playerModelVisibilityEvent.cancel();
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      this.updateDisplayName();
      if (updateEvent.getState() == EventState.PRE
         && this.mode.getValue() == AnnoyMode.CRAZY
         && mc.thePlayer.getHeldItem() != null
         && mc.thePlayer.getHeldItem().getItem() instanceof ItemFishingRod) {
         for (int i = 0; i < this.spam.getValue(); i++) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
         }
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = renderLivingEvent -> {
      if (renderLivingEvent.getEntity() == mc.thePlayer && !this.mode.getValue().equals(AnnoyMode.DEATH)) {
      }
   };

   public Annoy(Category category) {
      super(category);
      this.addSettings(this.mode, this.range, this.speed, this.pitch, this.sneak, this.spam);
   }

   @Override
   public void updateDisplayName() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + AnnoyMode.getLabel(this.mode.getValue()));
      super.updateDisplayName();
   }

   private void applyRotation(float f, float g, MotionEvent motionEvent) {
      this.lastYaw = motionEvent.getLastYaw();
      this.lastPitch = motionEvent.getLastPitch();
      motionEvent.setYaw(f);
      motionEvent.setPitch(g);
      this.appliedYaw = motionEvent.getYaw();
      this.appliedPitch = motionEvent.getPitch();
   }

   private EntityPlayer getClosestPlayer() {
      float f = this.range.getValue();
      EntityPlayer entityPlayer = null;

      for (EntityPlayer entityPlayer2 : mc.theWorld.playerEntities) {
         if (entityPlayer2 != mc.thePlayer) {
            float g = mc.thePlayer.getDistanceToEntity(entityPlayer2);
            if (g < f) {
               entityPlayer = entityPlayer2;
               f = g;
            }
         }
      }

      return entityPlayer;
   }

   @Override
   public void onDisable() {
      if (mc.thePlayer != null) {
         if (this.sneaking) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
         }

         this.sneaking = false;
         mc.gameSettings.keyBindSneak.pressed = false;
      }

      this.appliedPitch = this.appliedYaw = this.lastYaw = this.lastPitch = 0.0F;
      super.onDisable();
   }
}
