package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import net.minecraft.client.gui.GuiGameOver;

public final class AutoRespawn extends Module {
   private final BooleanSetting instant = new BooleanSetting("Instant", "Instantly respawns", false);
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (this.instant.asBoolean() && mc.thePlayer.getHealth() == 0.0F
         || mc.currentScreen instanceof GuiGameOver && ((GuiGameOver)mc.currentScreen).enableButtonsTimer >= 20) {
         mc.thePlayer.respawnPlayer();
         mc.displayGuiScreen(null);
      }
   };

   public AutoRespawn(Category category) {
      super(category);
      this.setHidden(false);
      this.addSettings(this.instant);
   }
}
