package net.hypixel.api.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public abstract class CustomizedTypeAdapterFactory<C> implements TypeAdapterFactory {
   private final Class<C> customizedClass;
   private static final long b = 80157036539332L;

   public CustomizedTypeAdapterFactory(Class<C> var1) {
      this.customizedClass = var1;
   }

   @Override
   public final <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
      return (TypeAdapter<T>)(var2.getRawType() == this.customizedClass ? this.customizeMyClassAdapter(var1, (com.google.gson.reflect.TypeToken)(var2)) : null);
   }

   private TypeAdapter<C> customizeMyClassAdapter(Gson var1, TypeToken<C> var2) {
      final TypeAdapter var3 = var1.getDelegateAdapter(this, var2);
      final TypeAdapter var4 = var1.getAdapter(JsonElement.class);
      return new TypeAdapter<C>() {
         private static final long a = 51159902679273L;

         @Override
         public void write(JsonWriter var1, C var2x) throws IOException {
            JsonElement var7 = var3.toJsonTree(var2x);
            CustomizedTypeAdapterFactory.this.beforeWrite(var2x, var7);
            var4.write(var1, var7);
         }

         @Override
         public C read(JsonReader var1) throws IOException {
            JsonElement var5 = (JsonElement)var4.read(var1);
            CustomizedTypeAdapterFactory.this.afterRead(var5);
            return (C)var3.fromJsonTree(var5);
         }
      };
   }

   public void beforeWrite(C var1, JsonElement var2) {
   }

   public void afterRead(JsonElement var1) {
   }
}
