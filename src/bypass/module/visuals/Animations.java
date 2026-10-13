package bypass.module.visuals;

import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;

public final class Animations extends Module {
   public static final EnumSetting<AnimationsMode> mode = new EnumSetting<>("Mode", "Set your animation mode", AnimationsMode.DEFAULT);
   public static final NumberSetting<Float> spin = new NumberSetting<>("Spin", "Spin", 2.0F, 0.0F, 10.0F);
   public static final NumberSetting<Integer> speed = new NumberSetting<>("Speed", "Speed of the animation", 6, 1, 15);
   public static final MapSetting position = new MapSettingBuilder("Position")
      .ordered()
      .description("")
      .add("X", new NumberSetting<>("X", "X coordinate", 0.0F, -2.0F, 2.0F))
      .add("Y", new NumberSetting<>("Y", "Y coordinate", 0.0F, -2.0F, 2.0F))
      .add("Z", new NumberSetting<>("Z", "Z coordinate", 0.0F, -2.0F, 2.0F))
      .build();

   public Animations(Category var1) {
      super(var1);
      this.addSettings(position, mode, spin, speed);
      this.setHidden(false);
   }
}
