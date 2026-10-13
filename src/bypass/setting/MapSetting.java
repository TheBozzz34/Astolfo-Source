package bypass.setting;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MapSetting extends Setting<Map<String, Setting<?>>> {
   public MapSetting(String var1, String var2, Map<String, Setting<?>> var3) {
      super(var1, var2, var3, SettingType.MAP);
   }

   public MapSetting(String var1, String var2) {
      super(var1, var2, new LinkedHashMap<>(), SettingType.MAP);
   }

   public Map<String, Setting<?>> getSettings() {
      return this.getValue();
   }

   public Setting<?> getSetting(String var1) {
      return this.getValue().get(var1);
   }
}
