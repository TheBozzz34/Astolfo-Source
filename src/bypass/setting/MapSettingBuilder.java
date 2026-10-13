package bypass.setting;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapSettingBuilder implements SettingBuilder<MapSetting> {
   private final String name;
   private String description;
   private Map<String, Setting<?>> settings;
   private static final long a = 61775804464101L;

   public MapSettingBuilder(String var1) {
      this.name = var1;
   }

   public MapSettingBuilder description(String var1) {
      this.description = var1;
      return this;
   }

   public MapSettingBuilder ordered() {
      this.settings = new LinkedHashMap<>();
      return this;
   }

   public MapSettingBuilder settings(Map<String, Setting<?>> var1) {
      this.settings = var1;
      return this;
   }

   public MapSettingBuilder add(String var1, Setting<?> var2) {
      this.settings.put(var1, var2);
      return this;
   }

   public MapSetting build() {
      return new MapSetting(this.name, this.description != null ? this.description : "none", this.settings);
   }
}
