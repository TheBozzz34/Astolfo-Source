package bypass.setting;

import bypass.setting.attribute.ChangeListenerAttribute;
import bypass.setting.attribute.ReadOnlyAttribute;
import bypass.setting.attribute.SettingAttribute;
import bypass.setting.attribute.SettingPart;
import bypass.util.SelfTyped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Setting<T> implements Value<T>, Serializable, SelfTyped<Setting<?>>, Cloneable {
   public T value;
   public Class<?> valueClass;
   public Parser parser;
   public T previousValue;
   public SettingType type;
   private String name;
   private String description;
   private List<SettingAttribute> attributes;
   private static final long a = 85974706251154L;

   public Setting(String var1, String var2, T var3, SettingType var4) {
      this.name = var1;
      this.description = var2;
      this.value = (T)var3;
      this.valueClass = var3.getClass();
      this.type = var4;
      Parsers var5 = new Parsers();
      this.parser = var5.get(this.getType().x());
      this.previousValue = (T)var3;
      this.attributes = new ArrayList<>();
   }

   public Setting(String var1, String var2, T var3, SettingType var4, List<SettingAttribute> var5) {
      this(var1, var2, (T)var3, var4);
      this.attributes = var5 != null ? var5 : new ArrayList<>();
      this.attributes.sort((var0, var1x) -> {
         int var2x = var0.getPriority().h();
         int var3x = var1x.getPriority().h();
         return Integer.compare(var2x, var3x);
      });
   }

   public Setting(String var1, String var2, T var3, SettingType var4, SettingAttribute[] var5) {
      this(var1, var2, (T)var3, var4, Arrays.asList(var5));
   }

   public Optional<Setting<T>> c_Optional() {
      return Optional.ofNullable(this);
   }

   public List<SettingAttribute> getAttributes() {
      return this.attributes;
   }

   public SettingType getType() {
      return this.type;
   }

   @Override
   public T getValue() {
      return this.value;
   }

   @Override
   public void setValue(T var1) {
      for (SettingAttribute var6 : this.getAttributes()) {
         if (var6 instanceof ReadOnlyAttribute) {
            ReadOnlyAttribute var7 = (ReadOnlyAttribute)var6;
            if (!var7.isCancelled()) {
               return;
            }
         } else if (var6 instanceof ChangeListenerAttribute) {
            ChangeListenerAttribute var8 = (ChangeListenerAttribute)var6;
            if (!var8.isCancelled()) {
               var8.onChange(SettingPart.VALUE);
            }
         }
      }

      this.previousValue = this.value;
      this.value = (T)var1;
   }

   public Class<?> getValueClass() {
      return this.valueClass;
   }

   public T getPreviousValue() {
      return this.previousValue;
   }

   @Override
   public void parse(String var1) {
      if (this.parser != null) {
         Optional var5 = this.parser.parse(var1, this.getValueClass());
         var5.ifPresent((java.util.function.Consumer<T>)this::setValue);
      } else {
         throw new UnsupportedOperationException("There is not parser for " + this.type.l() + " options");
      }
   }

   public byte asByte() {
      return Byte.valueOf(this.value.toString());
   }

   public String asString() {
      return String.valueOf(this.value.toString());
   }

   public boolean asBoolean() {
      return Boolean.valueOf(this.value.toString());
   }

   public double asDouble() {
      return Double.valueOf(this.value.toString());
   }

   public float asFloat() {
      return Float.valueOf(this.value.toString());
   }

   public int asInt() {
      return Integer.valueOf(this.value.toString());
   }

   public long asLong() {
      return Long.valueOf(this.value.toString());
   }

   public short asShort() {
      return Short.valueOf(this.value.toString());
   }

   @Override
   public String toString() {
      return String.format("%s", this.getClass().getSimpleName());
   }

   @Override
   public String getName() {
      return this.name != null ? this.name : "";
   }

   @Override
   public void setName(String var1) {
      for (SettingAttribute var6 : this.getAttributes()) {
         if (var6 instanceof ReadOnlyAttribute) {
            ReadOnlyAttribute var7 = (ReadOnlyAttribute)var6;
            if (!var7.isCancelled()) {
               return;
            }
         } else if (var6 instanceof ChangeListenerAttribute) {
            ChangeListenerAttribute var8 = (ChangeListenerAttribute)var6;
            if (!var8.isCancelled()) {
               var8.onChange(SettingPart.LABEL);
            }
         }
      }

      if (var1 != null && var1 != "") {
         this.name = var1;
      }
   }

   @Override
   public String getDescription() {
      return this.description != null ? this.description : "";
   }

   public void setDescription(String var1) {
      for (SettingAttribute var6 : this.getAttributes()) {
         if (var6 instanceof ReadOnlyAttribute) {
            ReadOnlyAttribute var7 = (ReadOnlyAttribute)var6;
            if (!var7.isCancelled()) {
               return;
            }
         } else if (var6 instanceof ChangeListenerAttribute) {
            ChangeListenerAttribute var8 = (ChangeListenerAttribute)var6;
            if (!var8.isCancelled()) {
               var8.onChange(SettingPart.DESCRIPTION);
            }
         }
      }

      if (var1 != null) {
         this.description = var1;
      }
   }

   @Override
   public int hashCode() {
      int var6 = 3;
      var6 = 14 * var6 + this.name.hashCode();
      var6 = 7 * var6 + this.type.hashCode();
      return 14 * var6 + this.attributes.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Setting)) {
         return false;
      }

      Setting var5 = (Setting)var1;
      return this.name == var5.name && this.value == var5.value && this.type == var5.type && this.attributes == var5.attributes;
   }

   public Setting<T> clone() {
      return new Setting<>(this.getName(), this.getDescription(), this.getValue(), this.getType() == null ? SettingType.UNDEFINED : this.getType());
   }
}
