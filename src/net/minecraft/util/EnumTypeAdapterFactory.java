package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class EnumTypeAdapterFactory implements TypeAdapterFactory {
   private static final long a = 40530997590878L;

   @Override
   public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
      Class var6 = var2.getRawType();
      if (!var6.isEnum()) {
         return null;
      }

      final HashMap var7 = Maps.newHashMap();

      for (Object var11 : var6.getEnumConstants()) {
         var7.put(this.func_151232_a(var11), var11);
      }

      return new TypeAdapter<T>() {
         private static final long a = 61609283281043L;

         @Override
         public void write(JsonWriter var1, T var2x) throws IOException {
            if (var2x == null) {
               var1.nullValue();
            } else {
               var1.value(EnumTypeAdapterFactory.this.func_151232_a(var2x));
            }
         }

         @Override
         public T read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return (T)var7.get(var1.nextString());
            }
         }
      };
   }

   private String func_151232_a(Object var1) {
      return var1 instanceof Enum ? ((Enum)var1).name().toLowerCase(Locale.US) : var1.toString().toLowerCase(Locale.US);
   }
}
