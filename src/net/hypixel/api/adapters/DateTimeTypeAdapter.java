package net.hypixel.api.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import net.hypixel.api.util.Utilities;

public class DateTimeTypeAdapter implements JsonDeserializer<ZonedDateTime>, JsonSerializer<ZonedDateTime> {
   public JsonElement serialize(ZonedDateTime var1, Type var2, JsonSerializationContext var3) {
      return new JsonPrimitive(var1.toInstant().toEpochMilli());
   }

   public ZonedDateTime deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      return Utilities.getDateTime(Long.parseLong(var1.getAsString()));
   }
}
