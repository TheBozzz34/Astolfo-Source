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
   public JsonElement serialize(GameType var1, Type var2, JsonSerializationContext var3) {
      return new JsonPrimitive(var1.toString());
   }

   public GameType deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      String var4 = var1.getAsString();

      try {
         return GameType.fromId(Integer.parseInt(var4));
      } catch (NumberFormatException var6) {
         return GameType.valueOf(var4);
      }
   }
}
