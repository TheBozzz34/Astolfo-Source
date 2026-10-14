package bypass.setting;

import java.util.ArrayList;
import java.util.List;

public class ListSettingBuilder implements SettingBuilder<ListSetting> {
   private final String k;
   private String Y;
   private List<Setting<?>> R;

   public ListSettingBuilder(String string) {
      this.k = string;
   }

   public ListSettingBuilder X(String string) {
      this.Y = string;
      return this;
   }

   public ListSettingBuilder R() {
      this.R = new ArrayList<>();
      return this;
   }

   public ListSettingBuilder n(List<Setting<?>> list) {
      this.R = list;
      return this;
   }

   public ListSettingBuilder p(Setting<?> setting) {
      this.R.add(setting);
      return this;
   }

   public ListSetting build() {
      return new ListSetting(this.k, this.Y != null ? this.Y : "none", this.R);
   }
}
