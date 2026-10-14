package bypass.setting.attribute;

import bypass.setting.Setting;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SubSettingsAttribute extends SettingAttribute {
   public Map<String, Setting<?>> settings = new TreeMap<>((string, string2) -> {
      int i = string.length();
      int j = string2.length();
      return Integer.compare(i, j);
   });

   public SubSettingsAttribute(List<Setting<?>> list) {
      super(AttributePriority.NORMAL);
      if (list != null) {
         for (Setting setting : list) {
            this.settings.put(setting.getName().replaceAll(" ", "").toLowerCase(), setting);
         }
      }
   }

   public SubSettingsAttribute(Setting<?>... settings) {
      this(Arrays.asList(settings));
   }

   public Map<String, Setting<?>> getSettings() {
      return this.settings;
   }
}
