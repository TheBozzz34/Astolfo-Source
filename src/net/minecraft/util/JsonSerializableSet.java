package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Set;

public class JsonSerializableSet extends ForwardingSet<String> implements IJsonSerializable {
   private final Set<String> underlyingSet = Sets.newHashSet();
   private static final long a = 97107483040496L;

   @Override
   public void fromJson(JsonElement var1) {
      if (var1.isJsonArray()) {
         for (JsonElement var6 : var1.getAsJsonArray()) {
            this.add(var6.getAsString());
         }
      }
   }

   @Override
   public JsonElement getSerializableElement() {
      JsonArray var4 = new JsonArray();

      for (String var6 : this) {
         var4.add(new JsonPrimitive(var6));
      }

      return var4;
   }

   @Override
   public Set<String> delegate() {
      return this.underlyingSet;
   }
}
