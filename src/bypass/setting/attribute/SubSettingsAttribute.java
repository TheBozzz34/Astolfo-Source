package bypass.setting.attribute;

import bypass.setting.Setting;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SubSettingsAttribute extends SettingAttribute {
   public Map<String, Setting<?>> settings = new TreeMap<>((var0, var1x) -> {
      int var2 = var0.length();
      int var3 = var1x.length();
      return Integer.compare(var2, var3);
   });
   private static final long b = 134679304957640L;

   public SubSettingsAttribute(List<Setting<?>> var1) {
      super(AttributePriority.NORMAL);
      if (var1 != null) {
         for (Setting var7 : var1) {
            this.settings.put(var7.getName().replaceAll(" ", "").toLowerCase(), var7);
         }
      }
   }

   public SubSettingsAttribute(Setting<?>... var1) {
      this(Arrays.asList(var1));
   }

   public Map<String, Setting<?>> getSettings() {
      return this.settings;
   }
}
