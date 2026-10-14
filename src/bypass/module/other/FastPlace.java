package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import net.minecraft.item.ItemBlock;

public final class FastPlace extends Module {
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Placing delay", 0, 0, 3, " ticks");
   private final BooleanSetting onlyBlocks = new BooleanSetting("Only-Blocks", "Only works with blocks", false);
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (mc.rightClickDelayTimer != this.delay.getValue()
         && mc.rightClickDelayTimer > this.delay.getValue()
         && (!this.onlyBlocks.getValue() || mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
         mc.rightClickDelayTimer = this.delay.getValue();
      }
   };
   private static final long c = 12114046300555L;
   private static final long j = 7721467113549856772L;

   public FastPlace(Category category) {
      super(category);
      this.addSettings(this.delay, this.onlyBlocks);
      this.setHidden(false);
   }

   @Override
   public void onDisable() {
      mc.rightClickDelayTimer = (int)j;
      super.onDisable();
   }
}
