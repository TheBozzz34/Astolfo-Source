package bypass.setting;

import bypass.setting.attribute.SettingAttribute;
import bypass.util.TypeUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class StringSetting extends Setting<String> implements Comparable<StringSetting> {
   public StringSetting() {
      super("", "", "", SettingType.UNDEFINED);
   }

   public StringSetting(String string, String string2, String string3) {
      super(string, string2, string3, SettingType.STRING);
      if (!TypeUtil.c(string3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public StringSetting(String string, String string2, String string3, List<SettingAttribute> list) {
      super(string, string2, string3, SettingType.STRING, list);
      if (!TypeUtil.c(string3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public StringSetting(String string, String string2, String string3, SettingAttribute... settingAttributes) {
      super(string, string2, string3, SettingType.STRING, Arrays.asList(settingAttributes));
      if (!TypeUtil.c(string3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public static int compare(String string, String string2) {
      return Integer.compare(string.length(), string2.length());
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof StringSetting && Objects.equals(this.value, ((StringSetting)object).getValue()) && super.equals(object);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + (this.value == null ? 0 : this.value.hashCode());
   }

   public int compareTo(StringSetting stringSetting) {
      return compare(this.value, stringSetting.value);
   }
}
