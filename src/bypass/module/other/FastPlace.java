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

   public FastPlace(Category category) {
      super(category);
      this.addSettings(this.delay, this.onlyBlocks);
      this.setHidden(false);
   }

   @Override
   public void onDisable() {
      mc.rightClickDelayTimer = 4;
      super.onDisable();
   }
}
