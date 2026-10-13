package net.minecraft.block.properties;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.minecraft.util.IStringSerializable;

public class PropertyEnum<T extends Enum<T> & IStringSerializable> extends PropertyHelper<T> {
   private final ImmutableSet<T> allowedValues;
   private final Map<String, T> nameToValue = Maps.newHashMap();
   private static final long a = 107728236259007L;

   public PropertyEnum(String var1, Class<T> var2, Collection<T> var3) {
      super(var1, var2);
      this.allowedValues = ImmutableSet.copyOf(var3);

      for (Enum var7 : var3) {
         String var8 = ((IStringSerializable)var7).getName();
         if (this.nameToValue.containsKey(var8)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + var8 + "'");
         }

         this.nameToValue.put(var8, (T)var7);
      }
   }

   @Override
   public Collection<T> getAllowedValues() {
      return this.allowedValues;
   }

   public String getName(T var1) {
      return ((IStringSerializable)var1).getName();
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String var0, Class<T> var1) {
      return create(var0, var1, Predicates.alwaysTrue());
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String var0, Class<T> var1, Predicate<T> var2) {
      return create(var0, var1, Collections2.filter(Lists.newArrayList((T[])var1.getEnumConstants()), var2));
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String var0, Class<T> var1, T... var2) {
      return create(var0, var1, Lists.newArrayList((T[])var2));
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String var0, Class<T> var1, Collection<T> var2) {
      return new PropertyEnum<>(var0, var1, var2);
   }
}
