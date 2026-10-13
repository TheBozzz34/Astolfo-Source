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
import bypass.setting.Setting;
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
   private final Rotator s = new Rotator(false, 15.0F);
   private final EnumSetting<AnnoyMode> mode = new EnumSetting<>("Mode", "Set your annoy mode", AnnoyMode.CRAZY);
   private final NumberSetting<Integer> spam = new NumberSetting<>("Spam", "Amount of rod spam it will do", 2, 0, 5);
   private final NumberSetting<Float> range = new NumberSetting<>("Range", "Distance until it activates the hting", 4.0F, 0.0F, 6.0F);
   private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Spin-bot Speed", 15.0F, 0.0F, 50.0F);
   private final NumberSetting<Float> pitch = new NumberSetting<>("Pitch", "Pitch", 90.0F, -180.0F, 180.0F);
   private final BooleanSetting sneak = new BooleanSetting("Sneak", "Random sneak", true);
   private boolean Q;
   private float c;
   private float H;
   private float t;
   private float B;
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         switch ((AnnoyMode)this.mode.getValue()) {
            case CRAZY:
               EntityPlayer var9 = this.k();
               if (var9 != null) {
                  Vector3d var12 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                  Vector3d var13 = new Vector3d(var9.posX, var9.posY + var9.height / 1.0, var9.posZ);
                  Rotation var14 = this.s.rotationTo(var13, var12);
                  this.g(var14.getYaw(), var14.getPitch(), var1x);
               } else if (this.speed.getValue() > 0.0F) {
                  this.g(MathHelper.wrapAngleTo180_float(var1x.getLastYaw() + this.speed.getValue()), this.pitch.getValue(), var1x);
               } else {
                  this.g(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw), this.pitch.getValue(), var1x);
               }

               if (this.sneak.getValue()) {
                  if (!this.Q) {
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                     this.Q = true;
                  } else {
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                     this.Q = false;
                  }
               }
               break;
            case WITCH:
               EntityPlayer var5 = this.k();
               if (var5 != null) {
                  Vector3d var11 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                  Vector3d var7 = new Vector3d(var5.posX, var5.posY + var5.height / 1.0, var5.posZ);
                  Rotation var8 = this.s.rotationTo(var7, var11);
                  this.g(var8.getYaw(), var8.getPitch(), var1x);
                  if (this.sneak.getValue()) {
                     this.Q = true;
                     mc.gameSettings.keyBindSneak.pressed = true;
                  }
               } else {
                  this.g(mc.thePlayer.rotationYaw + 180.0F, 89.0F, var1x);
                  if (this.sneak.getValue()) {
                     this.Q = false;
                     mc.gameSettings.keyBindSneak.pressed = false;
                  }
               }
               break;
            case DEATH:
               if (mc.thePlayer.getHealth() == 0.0F) {
                  double var6 = Math.random() * 16.0;
                  if (var6 > 8.0) {
                     var6 = Math.random();
                  } else {
                     var6 = -Math.random();
                  }

                  mc.thePlayer.setPosition(mc.thePlayer.posX + var6, mc.thePlayer.posY + 2.0, mc.thePlayer.posZ + var6);
                  mc.thePlayer.rotationYaw += 20.0F;
                  mc.thePlayer.sendQueue.u(new C0APacketAnimation());
               }
         }

         if (!this.sneak.getValue() && this.Q) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            this.Q = false;
         }
      }
   };
   @EventHandler(PlayerModelVisibilityEvent.class)
   private Listener<PlayerModelVisibilityEvent> onPlayerModelVisibility = var1x -> {
      if (this.Q && var1x.getEntity() == mc.thePlayer) {
         var1x.cancel();
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      this.p();
      if (var1x.getState() == EventState.PRE
         && this.mode.getValue() == AnnoyMode.CRAZY
         && mc.thePlayer.getHeldItem() != null
         && mc.thePlayer.getHeldItem().getItem() instanceof ItemFishingRod) {
         for (int var5 = 0; var5 < this.spam.getValue(); var5++) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
         }
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = var1x -> {
      if (var1x.getEntity() == mc.thePlayer && !this.mode.getValue().equals(AnnoyMode.DEATH)) {
      }
   };
   private static final long e = 10548188722617L;
   private static final long v = -3774130557772890106L;

   public Annoy(Category var1) {
      super(var1);
      Setting[] var10001 = new Setting[(int)v];
      var10001[0] = this.mode;
      var10001[1] = this.range;
      var10001[2] = this.speed;
      var10001[3] = this.pitch;
      var10001[4] = this.sneak;
      var10001[5] = this.spam;
      this.addSettings(var10001);
   }

   @Override
   public void p() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + AnnoyMode.u(this.mode.getValue()));
      super.p();
   }

   private void g(float var1, float var2, MotionEvent var3) {
      this.H = var3.getLastYaw();
      this.B = var3.getLastPitch();
      var3.setYaw(var1);
      var3.setPitch(var2);
      this.c = var3.getYaw();
      this.t = var3.getPitch();
   }

   private EntityPlayer k() {
      float var4 = this.range.getValue();
      EntityPlayer var5 = null;

      for (EntityPlayer var7 : mc.theWorld.playerEntities) {
         if (var7 != mc.thePlayer) {
            float var8 = mc.thePlayer.getDistanceToEntity(var7);
            if (var8 < var4) {
               var5 = var7;
               var4 = var8;
            }
         }
      }

      return var5;
   }

   @Override
   public void onDisable() {
      if (mc.thePlayer != null) {
         if (this.Q) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
         }

         this.Q = false;
         mc.gameSettings.keyBindSneak.pressed = false;
      }

      this.t = this.c = this.H = this.B = 0.0F;
      super.onDisable();
   }
}
