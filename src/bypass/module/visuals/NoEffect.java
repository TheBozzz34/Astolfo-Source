package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.event.render.HurtCameraEvent;
import bypass.event.render.RenderOverlaysEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import net.minecraft.potion.Potion;

public final class NoEffect extends Module {
   private final BooleanSetting hurtCam = new BooleanSetting("Hurt-Cam", "Disables hurtcam effect", true);
   private final BooleanSetting overlay = new BooleanSetting("Overlay", "Disables pumpkin overlay and fire", false);
   private final BooleanSetting weather = new BooleanSetting("Weather", "Disables weather", true);
   private final BooleanSetting potions = new BooleanSetting("Potions", "Disables blindness", true);
   public static final BooleanSetting noFog = new BooleanSetting("No-Fog", "Disables fog", true);
   @EventHandler(HurtCameraEvent.class)
   private Listener<HurtCameraEvent> onHurtCamera = var1x -> {
      if (this.hurtCam.getValue()) {
         var1x.cancel();
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      if (mc.theWorld != null && this.weather.getValue()) {
         mc.theWorld.V(0.0F);
      }

      if (this.potions.getValue() && mc.thePlayer.isPotionActive(Potion.blindness)) {
         mc.thePlayer.removePotionEffect(Potion.blindness.id);
      }
   };
   @EventHandler(RenderOverlaysEvent.class)
   private Listener<RenderOverlaysEvent> onRenderOverlays = var1x -> {
      if (this.overlay.getValue()) {
         var1x.cancel();
      }
   };
   private static final long d = 48021696634082L;

   public NoEffect(Category var1) {
      super(var1);
      this.setHidden(false);
      this.addSettings(this.hurtCam, this.overlay, this.weather, this.potions, noFog);
   }
}
