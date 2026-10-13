package bypass.setting;

import java.util.ArrayList;
import java.util.List;

public class ListSettingBuilder implements SettingBuilder<ListSetting> {
   private final String k;
   private String Y;
   private List<Setting<?>> R;
   private static final long a = 13104492072384L;

   public ListSettingBuilder(String var1) {
      this.k = var1;
   }

   public ListSettingBuilder X(String var1) {
      this.Y = var1;
      return this;
   }

   public ListSettingBuilder R() {
      this.R = new ArrayList<>();
      return this;
   }

   public ListSettingBuilder n(List<Setting<?>> var1) {
      this.R = var1;
      return this;
   }

   public ListSettingBuilder p(Setting<?> var1) {
      this.R.add(var1);
      return this;
   }

   public ListSetting build() {
      return new ListSetting(this.k, this.Y != null ? this.Y : "none", this.R);
   }
}
