package bypass.setting;

import java.awt.Color;
import java.util.List;
import java.util.Map;

public enum SettingType {
   UNDEFINED("undefined", 0, null),
   BOOLEAN("boolean", 1, Boolean.class),
   NUMBER("number", 2, Number.class),
   STRING("string", 3, String.class),
   ENUM("enum", 4, Enum.class),
   MAP("map", 5, Map.class),
   LIST("list", 6, List.class),
   COLOR("color", 7, Color.class);

   private int E;
   private String R;
   private Class<?> q;
   private static final long a = 129022484490582L;

   SettingType(String string2, int j, Class<?> class2) {
      this.E = j;
      this.R = string2;
      this.q = class2;
   }

   public int J() {
      return this.E;
   }

   public String l() {
      return this.R;
   }

   public Class<?> x() {
      return this.q;
   }

   public static SettingType U(String string) {
      for (SettingType settingType : values()) {
         if (settingType.l().equalsIgnoreCase(string)) {
            return settingType;
         }
      }

      return null;
   }

   public static Class<?> S(String string) {
      for (SettingType settingType : values()) {
         if (settingType.l().equalsIgnoreCase(string)) {
            return settingType.x();
         }
      }

      return null;
   }
}
