package net.minecraft.block.properties;

import com.google.common.base.Objects;

public abstract class PropertyHelper<T extends Comparable<T>> implements IProperty<T> {
   private final Class<T> valueClass;
   private final String name;

   public PropertyHelper(String name, Class<T> valueClass) {
      this.valueClass = valueClass;
      this.name = name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Class<T> getValueClass() {
      return this.valueClass;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("name", this.name).add("clazz", this.valueClass).add("values", this.getAllowedValues()).toString();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         PropertyHelper propertyHelper = (PropertyHelper)object;
         return this.valueClass.equals(propertyHelper.valueClass) && this.name.equals(propertyHelper.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.valueClass.hashCode() + this.name.hashCode();
   }
}
