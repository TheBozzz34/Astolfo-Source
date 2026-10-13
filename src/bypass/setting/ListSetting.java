package bypass.setting;

import java.util.ArrayList;
import java.util.List;

public final class ListSetting extends Setting<List<Setting<?>>> {
   private static final long c = 116953105784105L;

   public ListSetting(String var1, String var2, List<Setting<?>> var3) {
      super(var1, var2, var3, SettingType.LIST);
   }

   public ListSetting(String var1, String var2) {
      super(var1, var2, new ArrayList<>(), SettingType.LIST);
   }

   public List<Setting<?>> getSettings() {
      return this.getValue();
   }

   public Setting<?> getSetting(int var1) {
      return this.getValue().get(var1);
   }

   public Setting<?> find(Setting<?> var1) {
      for (Setting var6 : this.getValue()) {
         if (var6.equals(var1)) {
            return var6;
         }
      }

      return null;
   }
}
