package bypass.setting;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MapSetting extends Setting<Map<String, Setting<?>>> {
   public MapSetting(String string, String string2, Map<String, Setting<?>> map) {
      super(string, string2, map, SettingType.MAP);
   }

   public MapSetting(String string, String string2) {
      super(string, string2, new LinkedHashMap<>(), SettingType.MAP);
   }

   public Map<String, Setting<?>> getSettings() {
      return this.getValue();
   }

   public Setting<?> getSetting(String string) {
      return this.getValue().get(string);
   }
}
