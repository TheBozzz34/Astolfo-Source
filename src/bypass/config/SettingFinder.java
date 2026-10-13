package bypass.config;

import bypass.setting.Setting;
import bypass.setting.attribute.SubSettingsAttribute;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class SettingFinder {
   private static final long a = 84781534054506L;

   public static Setting<?> getOption(Collection<Setting<?>> var0, String var1) {
      return var0 == null ? null : var0.stream().filter(var1x -> var1x.getName().equalsIgnoreCase(var1)).findFirst().orElse(null);
   }

   public static Map<String, Setting<?>> getSubOptions(Setting<?> var0) {
      if (var0 == null) {
         return null;
      }

      List var4 = var0.getAttributes();
      return var4 != null
         ? (java.util.Map<java.lang.String,bypass.setting.Setting<?>>)(var4.stream()
            .filter(var0x -> var0x instanceof SubSettingsAttribute)
            .map(var0x -> (SubSettingsAttribute)var0x)
            .findFirst()
            .map((java.util.function.Function<SubSettingsAttribute, java.util.Map>)SubSettingsAttribute::getSettings)
            .orElse(null))
         : null;
   }

   public static Setting<?> getSubOption(Collection<Setting<?>> var0, String var1, String var2) {
      return var0 == null
         ? null
         : var0.stream()
            .filter(var1x -> var1x.getName().equalsIgnoreCase(var1))
            .map(Setting::getAttributes)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .filter(var0x -> var0x instanceof SubSettingsAttribute)
            .map(var0x -> (SubSettingsAttribute)var0x)
            .flatMap(var0x -> var0x.getSettings().values().stream())
            .filter(var1x -> var1x.getName().equalsIgnoreCase(var2))
            .findFirst()
            .orElse(null);
   }

   public static Setting<?> I(Setting<?>[] var0, String var1) {
      if (var0 == null) {
         return null;
      }

      for (Setting var8 : var0) {
         if (var8.getName().equalsIgnoreCase(var1)) {
            return var8;
         }
      }

      return null;
   }

   public static Setting<?> getSubOption(Setting<?>[] var0, String var1, String var2) {
      return var0 == null
         ? null
         : IntStream.range(0, var0.length)
            .filter(var2x -> var0[var2x].getName().equalsIgnoreCase(var1))
            .mapToObj(var1x -> var0[var1x].getAttributes())
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .filter(var0x -> var0x instanceof SubSettingsAttribute)
            .map(var0x -> (SubSettingsAttribute)var0x)
            .flatMap(var0x -> var0x.getSettings().values().stream())
            .filter(var1x -> var1x.getName().equalsIgnoreCase(var2))
            .findFirst()
            .orElse(null);
   }

   public static Setting<?> J(Map<String, Setting<?>> var0, String var1) {
      if (var0 == null) {
         return null;
      } else {
         return var0.containsKey(var1) ? (Setting)var0.get(var1) : null;
      }
   }

   public static Setting<?> getSubOption(Map<String, Setting<?>> var0, String var1, String var2) {
      if (var0 == null) {
         return null;
      }

      if (var0.containsKey(var1)) {
         Setting var6 = (Setting)var0.get(var1);
         List var7 = var6.getAttributes();
         if (var7 != null) {
            return (bypass.setting.Setting<?>)(var7.stream()
               .filter(var0x -> var0x instanceof SubSettingsAttribute)
               .map(var0x -> (SubSettingsAttribute)var0x)
               .filter(var1x -> ((bypass.setting.attribute.SubSettingsAttribute)var1x).getSettings().containsKey(var2))
               .findFirst()
               .map(var1x -> ((bypass.setting.attribute.SubSettingsAttribute)var1x).getSettings().get(var2))
               .orElse(null));
         }
      }

      return null;
   }
}
