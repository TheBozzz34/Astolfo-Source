package net.hypixel.api.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.api.reply.BoostersReply;

public class BoostersTypeAdapterFactory<T extends BoostersReply.Booster> extends CustomizedTypeAdapterFactory<T> {
   private static final long a = 69917661911809L;

   public BoostersTypeAdapterFactory(Class<T> var1) {
      super(var1);
   }

   @Override
   public void afterRead(JsonElement var1) {
      JsonObject var5 = var1.getAsJsonObject();
      JsonElement var6 = var5.get("stacked");
      if (var6 != null && var6.isJsonPrimitive() && var6.getAsJsonPrimitive().isBoolean()) {
         var5.addProperty("queuedToStack", var6.getAsJsonPrimitive().getAsBoolean());
         var5.remove("stacked");
      }
   }
}
