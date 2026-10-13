package bypass.setting;

import bypass.setting.attribute.SettingAttribute;
import bypass.util.TypeUtil;
import java.util.Arrays;
import java.util.List;

public final class BooleanSetting extends Setting<Boolean> implements Comparable<BooleanSetting> {
   private static final long c = 1945363167698L;

   public BooleanSetting() {
      super("", "", false, SettingType.UNDEFINED);
   }

   public BooleanSetting(String var1, String var2, boolean var3) {
      super(var1, var2, var3, SettingType.BOOLEAN);
      if (!TypeUtil.F(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var7) {
            var7.printStackTrace();
         }
      }
   }

   public BooleanSetting(String var1, String var2, Boolean var3) {
      super(var1, var2, var3, SettingType.BOOLEAN);
      if (!TypeUtil.F(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var7) {
            var7.printStackTrace();
         }
      }
   }

   public BooleanSetting(String var1, String var2, boolean var3, List<SettingAttribute> var4) {
      super(var1, var2, var3, SettingType.BOOLEAN, var4);
      if (!TypeUtil.F(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var8) {
            var8.printStackTrace();
         }
      }
   }

   public BooleanSetting(String var1, String var2, Boolean var3, List<SettingAttribute> var4) {
      super(var1, var2, var3, SettingType.BOOLEAN, var4);
      if (!TypeUtil.F(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var8) {
            var8.printStackTrace();
         }
      }
   }

   public BooleanSetting(String var1, String var2, boolean var3, SettingAttribute... var4) {
      super(var1, var2, var3, SettingType.BOOLEAN, Arrays.asList(var4));
      if (!TypeUtil.F(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var8) {
            var8.printStackTrace();
         }
      }
   }

   public BooleanSetting(String var1, String var2, Boolean var3, SettingAttribute... var4) {
      super(var1, var2, var3, SettingType.BOOLEAN, Arrays.asList(var4));
      if (!TypeUtil.F(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var8) {
            var8.printStackTrace();
         }
      }
   }

   public static int compare(boolean var0, boolean var1) {
      if (var0 == var1) {
         return 0;
      } else {
         return var0 ? 1 : -1;
      }
   }

   public void disable() {
      super.setValue(false);
   }

   public void enable() {
      super.setValue(true);
   }

   public void set(boolean var1) {
      super.setValue(var1);
   }

   public void setValue(Boolean var1) {
      super.setValue(var1);
   }

   public boolean isEnabled() {
      return this.value;
   }

   public boolean isDisabled() {
      return !this.value;
   }

   @Override
   public boolean equals(Object var1) {
      return !(var1 instanceof BooleanSetting) ? false : this.value == ((BooleanSetting)var1).getValue() && super.equals(var1);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + (this.value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode());
   }

   public int compareTo(BooleanSetting var1) {
      return compare(this.value, var1.value);
   }
}
