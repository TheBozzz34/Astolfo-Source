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

   SettingType(String var3, int var4, Class<?> var5) {
      this.E = var4;
      this.R = var3;
      this.q = var5;
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

   public static SettingType U(String var0) {
      for (SettingType var7 : values()) {
         if (var7.l().equalsIgnoreCase(var0)) {
            return var7;
         }
      }

      return null;
   }

   public static Class<?> S(String var0) {
      for (SettingType var8 : values()) {
         if (var8.l().equalsIgnoreCase(var0)) {
            return var8.x();
         }
      }

      return null;
   }
}
