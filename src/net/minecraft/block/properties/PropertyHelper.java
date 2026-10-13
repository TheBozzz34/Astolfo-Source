package net.minecraft.block.properties;

import com.google.common.base.Objects;

public abstract class PropertyHelper<T extends Comparable<T>> implements IProperty<T> {
   private final Class<T> valueClass;
   private final String name;
   private static final long c = 133247674161653L;
   private static final long i = -7933460149455290337L;

   public PropertyHelper(String var1, Class<T> var2) {
      this.valueClass = var2;
      this.name = var1;
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
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         PropertyHelper var5 = (PropertyHelper)var1;
         return this.valueClass.equals(var5.valueClass) && this.name.equals(var5.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return (int)i * this.valueClass.hashCode() + this.name.hashCode();
   }
}
