package net.hypixel.api.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.api.reply.BoostersReply;

public class BoostersTypeAdapterFactory<T extends BoostersReply.Booster> extends CustomizedTypeAdapterFactory<T> {
   public BoostersTypeAdapterFactory(Class<T> customizedClass) {
      super(customizedClass);
   }

   @Override
   public void afterRead(JsonElement json) {
      JsonObject jsonObject = json.getAsJsonObject();
      JsonElement jsonElement = jsonObject.get("stacked");
      if (jsonElement != null && jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isBoolean()) {
         jsonObject.addProperty("queuedToStack", jsonElement.getAsJsonPrimitive().getAsBoolean());
         jsonObject.remove("stacked");
      }
   }
}
