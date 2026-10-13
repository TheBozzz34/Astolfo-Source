package bypass.module.visuals;

import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;

public final class Scoreboard extends Module {
   public static final NumberSetting<Float> position = new NumberSetting<>("Position", "Position of scoreboard", 150.0F, -200.0F, 200.0F);
   public static final BooleanSetting remove = new BooleanSetting("Remove", "Removes scoreboard", false);

   public Scoreboard(Category var1) {
      super(var1);
      this.addSettings(position, remove);
      this.setHidden(false);
   }
}
