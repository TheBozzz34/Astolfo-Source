package bypass.setting;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapSettingBuilder implements SettingBuilder<MapSetting> {
   private final String name;
   private String description;
   private Map<String, Setting<?>> settings;

   public MapSettingBuilder(String string) {
      this.name = string;
   }

   public MapSettingBuilder description(String string) {
      this.description = string;
      return this;
   }

   public MapSettingBuilder ordered() {
      this.settings = new LinkedHashMap<>();
      return this;
   }

   public MapSettingBuilder settings(Map<String, Setting<?>> map) {
      this.settings = map;
      return this;
   }

   public MapSettingBuilder add(String string, Setting<?> setting) {
      this.settings.put(string, setting);
      return this;
   }

   public MapSetting build() {
      return new MapSetting(this.name, this.description != null ? this.description : "none", this.settings);
   }
}
