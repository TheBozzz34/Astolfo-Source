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

   public static Setting<?> getOption(Collection<Setting<?>> collection, String string) {
      return collection == null ? null : collection.stream().filter(setting -> setting.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
   }

   public static Map<String, Setting<?>> getSubOptions(Setting<?> setting) {
      if (setting == null) {
         return null;
      }

      List list = setting.getAttributes();
      return list != null
         ? (java.util.Map<java.lang.String,bypass.setting.Setting<?>>)(list.stream()
            .filter(object -> object instanceof SubSettingsAttribute)
            .map(object2 -> (SubSettingsAttribute)object2)
            .findFirst()
            .map((java.util.function.Function<SubSettingsAttribute, java.util.Map>)SubSettingsAttribute::getSettings)
            .orElse(null))
         : null;
   }

   public static Setting<?> getSubOption(Collection<Setting<?>> collection, String string, String string2) {
      return collection == null
         ? null
         : collection.stream()
            .filter(setting -> setting.getName().equalsIgnoreCase(string))
            .map(Setting::getAttributes)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .filter(settingAttribute -> settingAttribute instanceof SubSettingsAttribute)
            .map(settingAttribute2 -> (SubSettingsAttribute)settingAttribute2)
            .flatMap(subSettingsAttribute -> subSettingsAttribute.getSettings().values().stream())
            .filter(setting2 -> setting2.getName().equalsIgnoreCase(string2))
            .findFirst()
            .orElse(null);
   }

   public static Setting<?> I(Setting<?>[] settings, String string) {
      if (settings == null) {
         return null;
      }

      for (Setting setting : settings) {
         if (setting.getName().equalsIgnoreCase(string)) {
            return setting;
         }
      }

      return null;
   }

   public static Setting<?> getSubOption(Setting<?>[] settings, String string, String string2) {
      return settings == null
         ? null
         : IntStream.range(0, settings.length)
            .filter(i -> settings[i].getName().equalsIgnoreCase(string))
            .mapToObj(j -> settings[j].getAttributes())
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .filter(settingAttribute -> settingAttribute instanceof SubSettingsAttribute)
            .map(settingAttribute2 -> (SubSettingsAttribute)settingAttribute2)
            .flatMap(subSettingsAttribute -> subSettingsAttribute.getSettings().values().stream())
            .filter(setting -> setting.getName().equalsIgnoreCase(string2))
            .findFirst()
            .orElse(null);
   }

   public static Setting<?> J(Map<String, Setting<?>> map, String string) {
      if (map == null) {
         return null;
      } else {
         return map.containsKey(string) ? (Setting)map.get(string) : null;
      }
   }

   public static Setting<?> getSubOption(Map<String, Setting<?>> map, String string, String string2) {
      if (map == null) {
         return null;
      }

      if (map.containsKey(string)) {
         Setting setting2 = (Setting)map.get(string);
         List list = setting2.getAttributes();
         if (list != null) {
            return (bypass.setting.Setting<?>)(list.stream()
               .filter(object -> object instanceof SubSettingsAttribute)
               .map(object2 -> (SubSettingsAttribute)object2)
               .filter(object3 -> ((bypass.setting.attribute.SubSettingsAttribute)object3).getSettings().containsKey(string2))
               .findFirst()
               .map(object4 -> ((bypass.setting.attribute.SubSettingsAttribute)object4).getSettings().get(string2))
               .orElse(null));
         }
      }

      return null;
   }
}
