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
   public void fromJson(JsonElement json) {
      if (json.isJsonArray()) {
         for (JsonElement jsonElement : json.getAsJsonArray()) {
            this.add(jsonElement.getAsString());
         }
      }
   }

   @Override
   public JsonElement getSerializableElement() {
      JsonArray jsonArray = new JsonArray();

      for (String string : this) {
         jsonArray.add(new JsonPrimitive(string));
      }

      return jsonArray;
   }

   @Override
   public Set<String> delegate() {
      return this.underlyingSet;
   }
}
