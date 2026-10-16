package bypass.setting;

import java.util.ArrayList;
import java.util.List;

public class ListSettingBuilder implements SettingBuilder<ListSetting> {
   private final String name;
   private String description;
   private List<Setting<?>> settings;

   public ListSettingBuilder(String string) {
      this.name = string;
   }

   public ListSettingBuilder setDescription(String string) {
      this.description = string;
      return this;
   }

   public ListSettingBuilder withEmptyList() {
      this.settings = new ArrayList<>();
      return this;
   }

   public ListSettingBuilder setSettings(List<Setting<?>> list) {
      this.settings = list;
      return this;
   }

   public ListSettingBuilder addSetting(Setting<?> setting) {
      this.settings.add(setting);
      return this;
   }

   public ListSetting build() {
      return new ListSetting(this.name, this.description != null ? this.description : "none", this.settings);
   }
}
