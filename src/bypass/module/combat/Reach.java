package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.ReachEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.NumberSetting;
import bypass.util.MathUtil;
import bypass.util.RandomUtils;
import net.minecraft.util.EnumChatFormatting;

public final class Reach extends Module {
   private final NumberSetting<Double> minBlocks = new NumberSetting<>("Min-Blocks", "Randomizes reach on hit", 3.25, 3.0, 6.0);
   private final NumberSetting<Double> maxBlocks = new NumberSetting<>("Max-Blocks", "Randomizes reach on hit", 3.65, 3.0, 6.0);
   public double s;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      this.p();
      if (mc.thePlayer.swingProgressInt == 3) {
         this.s = RandomUtils.nextDouble(this.minBlocks.asDouble(), this.maxBlocks.asDouble());
      }
   };
   @EventHandler(ReachEvent.class)
   private Listener<ReachEvent> onReach = reachEvent -> reachEvent.setReach(this.s);
   private static final long c = 17216868200059L;

   public Reach(Category category) {
      super(category);
      this.addSettings(this.minBlocks, this.maxBlocks);
   }

   @Override
   public void p() {
      this.setDisplayName(
         this.getName()
            + " "
            + EnumChatFormatting.GRAY
            + MathUtil.ONE_DECIMAL.format(this.minBlocks.getValue())
            + "-"
            + MathUtil.ONE_DECIMAL.format(this.maxBlocks.getValue())
      );
      super.p();
   }
}
