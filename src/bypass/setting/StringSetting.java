package bypass.setting;

import bypass.setting.attribute.SettingAttribute;
import bypass.util.TypeUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class StringSetting extends Setting<String> implements Comparable<StringSetting> {
   private static final long c = 85438779180938L;

   public StringSetting() {
      super("", "", "", SettingType.UNDEFINED);
   }

   public StringSetting(String var1, String var2, String var3) {
      super(var1, var2, var3, SettingType.STRING);
      if (!TypeUtil.c(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var7) {
            var7.printStackTrace();
         }
      }
   }

   public StringSetting(String var1, String var2, String var3, List<SettingAttribute> var4) {
      super(var1, var2, var3, SettingType.STRING, var4);
      if (!TypeUtil.c(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var8) {
            var8.printStackTrace();
         }
      }
   }

   public StringSetting(String var1, String var2, String var3, SettingAttribute... var4) {
      super(var1, var2, var3, SettingType.STRING, Arrays.asList(var4));
      if (!TypeUtil.c(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var8) {
            var8.printStackTrace();
         }
      }
   }

   public static int compare(String var0, String var1) {
      return Integer.compare(var0.length(), var1.length());
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof StringSetting && Objects.equals(this.value, ((StringSetting)var1).getValue()) && super.equals(var1);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + (this.value == null ? 0 : this.value.hashCode());
   }

   public int compareTo(StringSetting var1) {
      return compare(this.value, var1.value);
   }
}
