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
   private static final long c = 61555123256517L;

   public NumberSetting() {
      super("", "", null, SettingType.UNDEFINED);
   }

   public NumberSetting(String var1, String var2, T var3) {
      super(var1, var2, (T)var3, SettingType.NUMBER);
      if (!TypeUtil.Z(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var7) {
            var7.printStackTrace();
         }
      }
   }

   public NumberSetting(String var1, String var2, T var3, T var4, T var5) {
      super(var1, var2, (T)var3, SettingType.NUMBER);
      this.min = (T)var4;
      this.max = (T)var5;
      if (!TypeUtil.Z(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var11) {
            var11.printStackTrace();
         }
      } else if (!TypeUtil.Z(var4)) {
         try {
            throw new InvalidValueException("Invalid minimum.");
         } catch (InvalidValueException var10) {
            var10.printStackTrace();
         }
      } else if (!TypeUtil.Z(var5)) {
         try {
            throw new InvalidValueException("Invalid maximum.");
         } catch (InvalidValueException var9) {
            var9.printStackTrace();
         }
      }
   }

   public NumberSetting(String var1, String var2, T var3, T var4, T var5, String var6) {
      super(var1, var2, (T)var3, SettingType.NUMBER);
      this.min = (T)var4;
      this.max = (T)var5;
      this.suffix = var6;
      if (!TypeUtil.Z(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var12) {
            var12.printStackTrace();
         }
      } else if (!TypeUtil.Z(var4)) {
         try {
            throw new InvalidValueException("Invalid minimum.");
         } catch (InvalidValueException var11) {
            var11.printStackTrace();
         }
      } else if (!TypeUtil.Z(var5)) {
         try {
            throw new InvalidValueException("Invalid maximum.");
         } catch (InvalidValueException var10) {
            var10.printStackTrace();
         }
      }
   }

   public NumberSetting(String var1, String var2, T var3, T var4, T var5, List<SettingAttribute> var6) {
      super(var1, var2, (T)var3, SettingType.NUMBER, var6);
      this.min = (T)var4;
      this.max = (T)var5;
      if (!TypeUtil.Z(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var13) {
            var13.printStackTrace();
         }
      } else if (!TypeUtil.Z(var4)) {
         try {
            throw new InvalidValueException("Invalid minimum.");
         } catch (InvalidValueException var12) {
            var12.printStackTrace();
         }
      } else if (!TypeUtil.Z(var5)) {
         try {
            throw new InvalidValueException("Invalid maximum.");
         } catch (InvalidValueException var11) {
            var11.printStackTrace();
         }
      }
   }

   public NumberSetting(String var1, String var2, T var3, T var4, T var5, SettingAttribute... var6) {
      super(var1, var2, (T)var3, SettingType.NUMBER, Arrays.asList(var6));
      this.min = (T)var4;
      this.max = (T)var5;
      if (!TypeUtil.Z(var3)) {
         try {
            throw new InvalidValueException("Invalid value.");
         } catch (InvalidValueException var13) {
            var13.printStackTrace();
         }
      } else if (!TypeUtil.Z(var4)) {
         try {
            throw new InvalidValueException("Invalid minimum.");
         } catch (InvalidValueException var12) {
            var12.printStackTrace();
         }
      } else if (!TypeUtil.Z(var5)) {
         try {
            throw new InvalidValueException("Invalid maximum.");
         } catch (InvalidValueException var11) {
            var11.printStackTrace();
         }
      }
   }

   public T getMin() {
      return this.min != null ? this.min : this.value;
   }

   public void setMin(T var1) {
      for (SettingAttribute var6 : this.getAttributes()) {
         if (var6 instanceof ReadOnlyAttribute) {
            return;
         }
      }

      if (var1 != null) {
         this.min = (T)var1;
      }
   }

   public T getMax() {
      return this.max != null ? this.max : this.value;
   }

   public void setMax(T var1) {
      for (SettingAttribute var6 : this.getAttributes()) {
         if (var6 instanceof ReadOnlyAttribute) {
            return;
         }
      }

      if (var1 != null) {
         this.max = (T)var1;
      }
   }

   public boolean isNaN() {
      return Double.isNaN(this.asDouble());
   }

   public boolean isInfinite() {
      return Double.isInfinite(this.asDouble());
   }

   public void setValue(T var1) {
      if (this.max != null | this.min != null) {
         if (var1.doubleValue() > this.max.doubleValue()) {
            var1 = this.max;
         } else if (var1.doubleValue() < this.min.doubleValue()) {
            var1 = this.min;
         }
      }

      super.setValue((T)var1);
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof NumberSetting
         && Double.doubleToLongBits(Double.valueOf(((NumberSetting)var1).value.toString())) == Double.doubleToLongBits(this.asDouble())
         && super.equals(var1);
   }

   @Override
   public int hashCode() {
      int var5 = 0;
      var5 = (byte)17;
      var5 = 37 * var5 + this.getClass().hashCode();
      var5 = (this.min != null ? 1 : 37) * var5 + (this.min != null ? 0 : this.getMin().hashCode());
      var5 = (this.max != null ? 1 : 37) * var5 + (this.max != null ? 0 : this.getMax().hashCode());
      return super.hashCode() + var5;
   }

   public int compareTo(NumberSetting<T> var1) {
      return this.compare(this.value, var1.value);
   }

   public int compare(T var1, T var2) {
      if (var1 == var2) {
         return 0;
      } else {
         return var1.doubleValue() < var2.doubleValue() ? -1 : 1;
      }
   }

   public String getSuffix() {
      return this.suffix;
   }
}
