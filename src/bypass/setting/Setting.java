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

   public Setting(String string, String string2, T object, SettingType settingType) {
      this.name = string;
      this.description = string2;
      this.value = (T)object;
      this.valueClass = object.getClass();
      this.type = settingType;
      Parsers parsers = new Parsers();
      this.parser = parsers.get(this.getType().x());
      this.previousValue = (T)object;
      this.attributes = new ArrayList<>();
   }

   public Setting(String string, String string2, T object, SettingType settingType, List<SettingAttribute> list) {
      this(string, string2, (T)object, settingType);
      this.attributes = list != null ? list : new ArrayList<>();
      this.attributes.sort((settingAttribute, settingAttribute2) -> {
         int i = settingAttribute.getPriority().h();
         int j = settingAttribute2.getPriority().h();
         return Integer.compare(i, j);
      });
   }

   public Setting(String string, String string2, T object, SettingType settingType, SettingAttribute[] settingAttributes) {
      this(string, string2, (T)object, settingType, Arrays.asList(settingAttributes));
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
   public void setValue(T object) {
      for (SettingAttribute settingAttribute : this.getAttributes()) {
         if (settingAttribute instanceof ReadOnlyAttribute) {
            ReadOnlyAttribute readOnlyAttribute = (ReadOnlyAttribute)settingAttribute;
            if (!readOnlyAttribute.isCancelled()) {
               return;
            }
         } else if (settingAttribute instanceof ChangeListenerAttribute) {
            ChangeListenerAttribute changeListenerAttribute = (ChangeListenerAttribute)settingAttribute;
            if (!changeListenerAttribute.isCancelled()) {
               changeListenerAttribute.onChange(SettingPart.VALUE);
            }
         }
      }

      this.previousValue = this.value;
      this.value = (T)object;
   }

   public Class<?> getValueClass() {
      return this.valueClass;
   }

   public T getPreviousValue() {
      return this.previousValue;
   }

   @Override
   public void parse(String string) {
      if (this.parser != null) {
         Optional optional = this.parser.parse(string, this.getValueClass());
         optional.ifPresent((java.util.function.Consumer<T>)this::setValue);
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
   public void setName(String string) {
      for (SettingAttribute settingAttribute : this.getAttributes()) {
         if (settingAttribute instanceof ReadOnlyAttribute) {
            ReadOnlyAttribute readOnlyAttribute = (ReadOnlyAttribute)settingAttribute;
            if (!readOnlyAttribute.isCancelled()) {
               return;
            }
         } else if (settingAttribute instanceof ChangeListenerAttribute) {
            ChangeListenerAttribute changeListenerAttribute = (ChangeListenerAttribute)settingAttribute;
            if (!changeListenerAttribute.isCancelled()) {
               changeListenerAttribute.onChange(SettingPart.LABEL);
            }
         }
      }

      if (string != null && string != "") {
         this.name = string;
      }
   }

   @Override
   public String getDescription() {
      return this.description != null ? this.description : "";
   }

   public void setDescription(String string) {
      for (SettingAttribute settingAttribute : this.getAttributes()) {
         if (settingAttribute instanceof ReadOnlyAttribute) {
            ReadOnlyAttribute readOnlyAttribute = (ReadOnlyAttribute)settingAttribute;
            if (!readOnlyAttribute.isCancelled()) {
               return;
            }
         } else if (settingAttribute instanceof ChangeListenerAttribute) {
            ChangeListenerAttribute changeListenerAttribute = (ChangeListenerAttribute)settingAttribute;
            if (!changeListenerAttribute.isCancelled()) {
               changeListenerAttribute.onChange(SettingPart.DESCRIPTION);
            }
         }
      }

      if (string != null) {
         this.description = string;
      }
   }

   @Override
   public int hashCode() {
      int i = 3;
      i = 14 * i + this.name.hashCode();
      i = 7 * i + this.type.hashCode();
      return 14 * i + this.attributes.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof Setting)) {
         return false;
      }

      Setting setting = (Setting)object;
      return this.name == setting.name && this.value == setting.value && this.type == setting.type && this.attributes == setting.attributes;
   }

   public Setting<T> clone() {
      return new Setting<>(this.getName(), this.getDescription(), this.getValue(), this.getType() == null ? SettingType.UNDEFINED : this.getType());
   }
}
