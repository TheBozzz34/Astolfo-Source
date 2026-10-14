package bypass.setting;

import bypass.setting.attribute.SettingAttribute;
import bypass.util.TypeUtil;
import java.util.Arrays;
import java.util.List;

public final class EnumSetting<E extends Enum> extends Setting<E> implements Comparable<EnumSetting<Enum<?>>> {
   public EnumSetting() {
      super("", "", null, SettingType.UNDEFINED);
   }

   public EnumSetting(String string, String string2, E enum2) {
      super(string, string2, (E)enum2, SettingType.ENUM);
      if (!TypeUtil.s(enum2)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public EnumSetting(String string, String string2, E enum2, List<SettingAttribute> list) {
      super(string, string2, (E)enum2, SettingType.ENUM, list);
      if (!TypeUtil.s(enum2)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public EnumSetting(String string, String string2, E enum2, SettingAttribute... settingAttributes) {
      super(string, string2, (E)enum2, SettingType.ENUM, Arrays.asList(settingAttributes));
      if (!TypeUtil.s(enum2)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public E getValue() {
      return (E)super.getValue();
   }

   public void setValue(E enum2) {
      super.setValue(enum2);
   }

   @Override
   public boolean equals(Object object) {
      return !(object instanceof EnumSetting) ? false : this.value == ((EnumSetting)object).getValue() && super.equals(object);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + (this.value != null ? 0 : this.value.hashCode());
   }

   public int compareTo(EnumSetting enumSetting) {
      return this.value.compareTo((E)enumSetting.getValue());
   }
}
