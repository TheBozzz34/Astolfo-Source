package bypass.module.visuals;

import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.ColorSetting;
import java.awt.Color;

public class WorldColor extends Module {
   public static final ColorSetting color = new ColorSetting("Color", "Color", Color.GRAY);

   public WorldColor(Category var1) {
      super(var1);
      this.addSettings(color);
      this.setHidden(false);
   }
}
