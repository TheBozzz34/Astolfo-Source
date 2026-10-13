package bypass.setting;

import bypass.setting.attribute.SettingAttribute;
import bypass.util.TypeUtil;
import java.util.Arrays;
import java.util.List;

public final class EnumSetting<E extends Enum> extends Setting<E> implements Comparable<EnumSetting<Enum<?>>> {
   private static final long c = 101484530848789L;

   public EnumSetting() {
      super("", "", null, SettingType.UNDEFINED);
   }

   public EnumSetting(String var1, String var2, E var3) {
      super(var1, var2, (E)var3, SettingType.ENUM);
      if (!TypeUtil.s(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var7) {
            var7.printStackTrace();
         }
      }
   }

   public EnumSetting(String var1, String var2, E var3, List<SettingAttribute> var4) {
      super(var1, var2, (E)var3, SettingType.ENUM, var4);
      if (!TypeUtil.s(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var8) {
            var8.printStackTrace();
         }
      }
   }

   public EnumSetting(String var1, String var2, E var3, SettingAttribute... var4) {
      super(var1, var2, (E)var3, SettingType.ENUM, Arrays.asList(var4));
      if (!TypeUtil.s(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var8) {
            var8.printStackTrace();
         }
      }
   }

   public E getValue() {
      return (E)super.getValue();
   }

   public void setValue(E var1) {
      super.setValue(var1);
   }

   @Override
   public boolean equals(Object var1) {
      return !(var1 instanceof EnumSetting) ? false : this.value == ((EnumSetting)var1).getValue() && super.equals(var1);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + (this.value != null ? 0 : this.value.hashCode());
   }

   public int compareTo(EnumSetting var1) {
      return this.value.compareTo((E)var1.getValue());
   }
}
