package net.hypixel.api.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.hypixel.api.util.GameType;

public class GameTypeTypeAdapter implements JsonDeserializer<GameType>, JsonSerializer<GameType> {
   public JsonElement serialize(GameType src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toString());
   }

   public GameType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      String raw = json.getAsString();

      try {
         return GameType.fromId(Integer.parseInt(raw));
      } catch (NumberFormatException numberFormatException) {
         return GameType.valueOf(raw);
      }
   }
}
