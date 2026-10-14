package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public final class FullBright extends Module {
   private final EnumSetting<FullBrightMode> mode = new EnumSetting<>("Mode", "Set your animation mode", FullBrightMode.WORLD);
   private final NumberSetting<Float> bright = new NumberSetting<>("Bright", "Bright level", 1.0F, 0.1F, 1.0F);
   private boolean S;
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (mc.theWorld != null) {
         if (this.mode.getValue() == FullBrightMode.WORLD) {
            if (this.S) {
               mc.thePlayer.removePotionEffect(Potion.nightVision.id);
               this.S = false;
            }

            for (int i = 0; i < 16; i++) {
               if (mc.theWorld.provider.lightBrightnessTable[i] != this.bright.getValue()) {
                  mc.theWorld.provider.lightBrightnessTable[i] = this.bright.getValue();
               }
            }
         } else {
            PotionEffect potionEffect = new PotionEffect(Potion.nightVision.id, 7000, 1);
            potionEffect.setPotionDurationMax(true);
            mc.thePlayer.addPotionEffect(potionEffect);
            this.S = true;
         }
      }
   };

   public FullBright(Category category) {
      super(category);
      this.setHidden(false);
      this.addSettings(this.mode, this.bright);
   }

   @Override
   public void onDisable() {
      if (mc.theWorld != null) {
         mc.theWorld.provider.generateLightBrightnessTable();
      }

      if (mc.thePlayer != null && this.S) {
         mc.thePlayer.removePotionEffect(Potion.nightVision.id);
      }

      this.S = false;
      super.onDisable();
   }
}
