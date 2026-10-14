package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.MathUtil;
import net.minecraft.util.EnumChatFormatting;

public final class Timer extends Module {
   private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Speed of timer", 1.0F, 0.1F, 10.0F);
   private final BooleanSetting tick = new BooleanSetting("Tick", "Dynamic timer", false);
   private final BooleanSetting onlyMoving = new BooleanSetting("Only-Moving", "Only when moving", true);
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      this.p();
      if (this.onlyMoving.getValue() && !GameUtil.isMoving(mc.thePlayer)) {
         mc.timer.timerSpeed = 1.0F;
      } else if (this.tick.getValue()) {
         if (mc.thePlayer.ticksExisted % 2 == 0) {
            mc.timer.timerSpeed = this.speed.getValue();
         } else {
            mc.timer.timerSpeed = 1.0F;
         }
      } else {
         mc.timer.timerSpeed = this.speed.getValue();
      }
   };
   private static final long c = 43346108680049L;

   public Timer(Category category) {
      super(category);
      this.addSettings(this.tick, this.speed, this.onlyMoving);
   }

   @Override
   public void onDisable() {
      mc.timer.timerSpeed = 1.0F;
      super.onDisable();
      mc.timer.timerSpeed = 1.0F;
   }

   @Override
   public void p() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + MathUtil.ONE_DECIMAL.format(this.speed.getValue()));
      super.p();
   }
}
