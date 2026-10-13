package bypass.module.visuals;

import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.NumberSetting;

public class MoreParticles extends Module {
   public static final NumberSetting<Integer> enchantAmount = new NumberSetting<>("Enchant-Amount", "Amount of particles displayed", 1, 0, 20);
   public static final NumberSetting<Integer> criticalAmount = new NumberSetting<>("Critical-Amount", "Amount of particles displayed", 1, 0, 20);

   public MoreParticles(Category var1) {
      super(var1);
      this.addSettings(enchantAmount, criticalAmount);
   }
}
