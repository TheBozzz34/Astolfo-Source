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

   public CustomizedTypeAdapterFactory(Class<C> customizedClass) {
      this.customizedClass = customizedClass;
   }

   @Override
   public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeIn) {
      return (TypeAdapter<T>)(typeIn.getRawType() == this.customizedClass ? this.customizeMyClassAdapter(gson, (TypeToken<C>)typeIn) : null);
   }

   private TypeAdapter<C> customizeMyClassAdapter(Gson gson, TypeToken<C> typeIn) {
      final TypeAdapter<C> delegate = gson.getDelegateAdapter(this, typeIn);
      final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
      return new TypeAdapter<C>() {
         @Override
         public void write(JsonWriter out, C value) throws IOException {
            JsonElement jsonElement = delegate.toJsonTree((C)value);
            CustomizedTypeAdapterFactory.this.beforeWrite(value, jsonElement);
            elementAdapter.write(out, jsonElement);
         }

         @Override
         public C read(JsonReader in) throws IOException {
            JsonElement jsonElement2 = elementAdapter.read(in);
            CustomizedTypeAdapterFactory.this.afterRead(jsonElement2);
            return delegate.fromJsonTree(jsonElement2);
         }
      };
   }

   public void beforeWrite(C source, JsonElement toSerialize) {
   }

   public void afterRead(JsonElement deserialized) {
   }
}
