package net.hypixel.api.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDTypeAdapter implements JsonDeserializer<UUID>, JsonSerializer<UUID> {
   private static final long a = 84128820052948L;

   public JsonElement serialize(UUID var1, Type var2, JsonSerializationContext var3) {
      return new JsonPrimitive(var1.toString());
   }

   public UUID deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      String var7 = var1.getAsString();
      return var7.contains("-")
         ? UUID.fromString(var7)
         : UUID.fromString(
            var7.substring(0, 8) + "-" + var7.substring(8, 12) + "-" + var7.substring(12, 16) + "-" + var7.substring(16, 20) + "-" + var7.substring(20, 32)
         );
   }
}
