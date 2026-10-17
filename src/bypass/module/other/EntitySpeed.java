package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.NumberSetting;
import net.minecraft.entity.passive.EntityHorse;

public class EntitySpeed extends Module {
   private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Move speed", 1.0F, 0.1F, 5.0F);
   private final NumberSetting<Float> vertical = new NumberSetting<>("Vertical", "Vertical move speed", 3.0F, 0.1F, 5.0F);
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (mc.thePlayer.isRiding()) {
         if (mc.thePlayer.ridingEntity instanceof EntityHorse) {
            EntityHorse entityHorse = (EntityHorse)mc.thePlayer.ridingEntity;
            entityHorse.setHorseSaddled(true);
            float f = this.speed.getValue();
            double d = mc.thePlayer.movementInput.moveForward;
            double e = mc.thePlayer.movementInput.moveStrafe;
            float g = mc.thePlayer.rotationYaw;
            if (d == 0.0 && e == 0.0) {
               mc.thePlayer.ridingEntity.motionX = mc.thePlayer.ridingEntity.motionZ = 0.0;
            } else {
               if (d != 0.0) {
                  if (e > 0.0) {
                     g += d > 0.0 ? -45 : 45;
                  } else if (e < 0.0) {
                     g += d > 0.0 ? 45 : -45;
                  }

                  e = 0.0;
                  if (d > 0.0) {
                     d = 1.0;
                  } else if (d < 0.0) {
                     d = -1.0;
                  }
               }

               double d2 = Math.cos(Math.toRadians(g + 90.0F));
               double d3 = Math.sin(Math.toRadians(g + 90.0F));
               mc.thePlayer.ridingEntity.motionX = d * f * d2 + e * f * d3;
               mc.thePlayer.ridingEntity.motionZ = d * f * d3 - e * f * d2;
            }

            mc.thePlayer.ridingEntity.motionY = 0.0;
            if (mc.thePlayer.movementInput.jump) {
               mc.thePlayer.ridingEntity.motionY = this.vertical.getValue().floatValue();
            }

            if (mc.thePlayer.movementInput.sneak) {
               mc.thePlayer.ridingEntity.motionY = -this.vertical.getValue();
            }
         }
      }
   };

   public EntitySpeed() {
      super("EntitySpeed", Category.OTHER);
      this.addSettings(this.speed, this.vertical);
   }
}
