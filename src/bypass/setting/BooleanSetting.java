package bypass.setting;

import bypass.setting.attribute.SettingAttribute;
import bypass.util.TypeUtil;
import java.util.Arrays;
import java.util.List;

public final class BooleanSetting extends Setting<Boolean> implements Comparable<BooleanSetting> {
   public BooleanSetting() {
      super("", "", false, SettingType.UNDEFINED);
   }

   public BooleanSetting(String string, String string2, boolean bl) {
      super(string, string2, bl, SettingType.BOOLEAN);
      if (!TypeUtil.isBoolean(bl)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public BooleanSetting(String string, String string2, Boolean boolean2) {
      super(string, string2, boolean2, SettingType.BOOLEAN);
      if (!TypeUtil.isBoolean(boolean2)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public BooleanSetting(String string, String string2, boolean bl, List<SettingAttribute> list) {
      super(string, string2, bl, SettingType.BOOLEAN, list);
      if (!TypeUtil.isBoolean(bl)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public BooleanSetting(String string, String string2, Boolean boolean2, List<SettingAttribute> list) {
      super(string, string2, boolean2, SettingType.BOOLEAN, list);
      if (!TypeUtil.isBoolean(boolean2)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public BooleanSetting(String string, String string2, boolean bl, SettingAttribute... settingAttributes) {
      super(string, string2, bl, SettingType.BOOLEAN, Arrays.asList(settingAttributes));
      if (!TypeUtil.isBoolean(bl)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public BooleanSetting(String string, String string2, Boolean boolean2, SettingAttribute... settingAttributes) {
      super(string, string2, boolean2, SettingType.BOOLEAN, Arrays.asList(settingAttributes));
      if (!TypeUtil.isBoolean(boolean2)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public static int compare(boolean bl, boolean bl2) {
      if (bl == bl2) {
         return 0;
      } else {
         return bl ? 1 : -1;
      }
   }

   public void disable() {
      super.setValue(false);
   }

   public void enable() {
      super.setValue(true);
   }

   public void set(boolean bl) {
      super.setValue(bl);
   }

   public void setValue(Boolean boolean2) {
      super.setValue(boolean2);
   }

   public boolean isEnabled() {
      return this.value;
   }

   public boolean isDisabled() {
      return !this.value;
   }

   @Override
   public boolean equals(Object object) {
      return !(object instanceof BooleanSetting) ? false : this.value == ((BooleanSetting)object).getValue() && super.equals(object);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + (this.value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode());
   }

   public int compareTo(BooleanSetting booleanSetting) {
      return compare(this.value, booleanSetting.value);
   }
}
