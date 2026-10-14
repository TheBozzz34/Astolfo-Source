package bypass.setting;

import bypass.setting.attribute.ReadOnlyAttribute;
import bypass.setting.attribute.SettingAttribute;
import bypass.util.TypeUtil;
import java.util.Arrays;
import java.util.List;

public final class NumberSetting<T extends Number> extends Setting<T> implements Comparable<NumberSetting<T>> {
   private T min;
   private T max;
   private String suffix = "";

   public NumberSetting() {
      super("", "", null, SettingType.UNDEFINED);
   }

   public NumberSetting(String string, String string2, T number) {
      super(string, string2, (T)number, SettingType.NUMBER);
      if (!TypeUtil.Z(number)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      }
   }

   public NumberSetting(String string, String string2, T number, T number2, T number3) {
      super(string, string2, (T)number, SettingType.NUMBER);
      this.min = (T)number2;
      this.max = (T)number3;
      if (!TypeUtil.Z(number)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      } else if (!TypeUtil.Z(number2)) {
         try {
            throw new InvalidValueException("Invalid minimum.");
         } catch (InvalidValueException invalidValueException2) {
            invalidValueException2.printStackTrace();
         }
      } else if (!TypeUtil.Z(number3)) {
         try {
            throw new InvalidValueException("Invalid maximum.");
         } catch (InvalidValueException invalidValueException3) {
            invalidValueException3.printStackTrace();
         }
      }
   }

   public NumberSetting(String string, String string2, T number, T number2, T number3, String string3) {
      super(string, string2, (T)number, SettingType.NUMBER);
      this.min = (T)number2;
      this.max = (T)number3;
      this.suffix = string3;
      if (!TypeUtil.Z(number)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      } else if (!TypeUtil.Z(number2)) {
         try {
            throw new InvalidValueException("Invalid minimum.");
         } catch (InvalidValueException invalidValueException2) {
            invalidValueException2.printStackTrace();
         }
      } else if (!TypeUtil.Z(number3)) {
         try {
            throw new InvalidValueException("Invalid maximum.");
         } catch (InvalidValueException invalidValueException3) {
            invalidValueException3.printStackTrace();
         }
      }
   }

   public NumberSetting(String string, String string2, T number, T number2, T number3, List<SettingAttribute> list) {
      super(string, string2, (T)number, SettingType.NUMBER, list);
      this.min = (T)number2;
      this.max = (T)number3;
      if (!TypeUtil.Z(number)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      } else if (!TypeUtil.Z(number2)) {
         try {
            throw new InvalidValueException("Invalid minimum.");
         } catch (InvalidValueException invalidValueException2) {
            invalidValueException2.printStackTrace();
         }
      } else if (!TypeUtil.Z(number3)) {
         try {
            throw new InvalidValueException("Invalid maximum.");
         } catch (InvalidValueException invalidValueException3) {
            invalidValueException3.printStackTrace();
         }
      }
   }

   public NumberSetting(String string, String string2, T number, T number2, T number3, SettingAttribute... settingAttributes) {
      super(string, string2, (T)number, SettingType.NUMBER, Arrays.asList(settingAttributes));
      this.min = (T)number2;
      this.max = (T)number3;
      if (!TypeUtil.Z(number)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException invalidValueException) {
            invalidValueException.printStackTrace();
         }
      } else if (!TypeUtil.Z(number2)) {
         try {
            throw new InvalidValueException("Invalid minimum.");
         } catch (InvalidValueException invalidValueException2) {
            invalidValueException2.printStackTrace();
         }
      } else if (!TypeUtil.Z(number3)) {
         try {
            throw new InvalidValueException("Invalid maximum.");
         } catch (InvalidValueException invalidValueException3) {
            invalidValueException3.printStackTrace();
         }
      }
   }

   public T getMin() {
      return this.min != null ? this.min : this.value;
   }

   public void setMin(T number) {
      for (SettingAttribute settingAttribute : this.getAttributes()) {
         if (settingAttribute instanceof ReadOnlyAttribute) {
            return;
         }
      }

      if (number != null) {
         this.min = (T)number;
      }
   }

   public T getMax() {
      return this.max != null ? this.max : this.value;
   }

   public void setMax(T number) {
      for (SettingAttribute settingAttribute : this.getAttributes()) {
         if (settingAttribute instanceof ReadOnlyAttribute) {
            return;
         }
      }

      if (number != null) {
         this.max = (T)number;
      }
   }

   public boolean isNaN() {
      return Double.isNaN(this.asDouble());
   }

   public boolean isInfinite() {
      return Double.isInfinite(this.asDouble());
   }

   public void setValue(T number) {
      if (this.max != null | this.min != null) {
         if (number.doubleValue() > this.max.doubleValue()) {
            number = this.max;
         } else if (number.doubleValue() < this.min.doubleValue()) {
            number = this.min;
         }
      }

      super.setValue((T)number);
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof NumberSetting
         && Double.doubleToLongBits(Double.valueOf(((NumberSetting)object).value.toString())) == Double.doubleToLongBits(this.asDouble())
         && super.equals(object);
   }

   @Override
   public int hashCode() {
      int i = 0;
      i = (byte)17;
      i = 37 * i + this.getClass().hashCode();
      i = (this.min != null ? 1 : 37) * i + (this.min != null ? 0 : this.getMin().hashCode());
      i = (this.max != null ? 1 : 37) * i + (this.max != null ? 0 : this.getMax().hashCode());
      return super.hashCode() + i;
   }

   public int compareTo(NumberSetting<T> numberSetting) {
      return this.compare(this.value, numberSetting.value);
   }

   public int compare(T number, T number2) {
      if (number == number2) {
         return 0;
      } else {
         return number.doubleValue() < number2.doubleValue() ? -1 : 1;
      }
   }

   public String getSuffix() {
      return this.suffix;
   }
}
