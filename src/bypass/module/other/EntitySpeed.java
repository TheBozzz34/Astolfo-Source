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
   private Listener<UpdateEvent> onUpdate = var1 -> {
      if (mc.thePlayer.E()) {
         if (mc.thePlayer.ridingEntity instanceof EntityHorse) {
            EntityHorse var5 = (EntityHorse)mc.thePlayer.ridingEntity;
            var5.setHorseSaddled(true);
            float var6 = this.speed.getValue();
            double var7 = mc.thePlayer.movementInput.moveForward;
            double var9 = mc.thePlayer.movementInput.moveStrafe;
            float var11 = mc.thePlayer.rotationYaw;
            if (var7 == 0.0 && var9 == 0.0) {
               mc.thePlayer.ridingEntity.motionX = mc.thePlayer.ridingEntity.motionZ = 0.0;
            } else {
               if (var7 != 0.0) {
                  if (var9 > 0.0) {
                     var11 += var7 > 0.0 ? -45 : 45;
                  } else if (var9 < 0.0) {
                     var11 += var7 > 0.0 ? 45 : -45;
                  }

                  var9 = 0.0;
                  if (var7 > 0.0) {
                     var7 = 1.0;
                  } else if (var7 < 0.0) {
                     var7 = -1.0;
                  }
               }

               double var12 = Math.cos(Math.toRadians(var11 + 90.0F));
               double var14 = Math.sin(Math.toRadians(var11 + 90.0F));
               mc.thePlayer.ridingEntity.motionX = var7 * var6 * var12 + var9 * var6 * var14;
               mc.thePlayer.ridingEntity.motionZ = var7 * var6 * var14 - var9 * var6 * var12;
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
   private static final long c = 120985874079522L;

   public EntitySpeed() {
      super("EntitySpeed", Category.OTHER);
      this.addSettings(this.speed, this.vertical);
   }
}
