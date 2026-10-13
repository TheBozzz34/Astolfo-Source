package net.hypixel.api.reply.skyblock;

import com.google.gson.JsonObject;
import net.hypixel.api.reply.AbstractReply;

public class ResourceReply extends AbstractReply {
   private long lastUpdated;
   private JsonObject response;
   private static final long a = 112206433772806L;

   public ResourceReply(JsonObject var1) {
      this.response = var1;
      this.success = var1.has("success") && var1.get("success").getAsBoolean();
      this.cause = var1.has("cause") ? var1.get("cause").getAsString() : null;
      this.lastUpdated = var1.has("lastUpdated") ? var1.get("lastUpdated").getAsLong() : -1L;
   }

   public JsonObject getResponse() {
      return this.response != null && !this.response.isJsonNull() ? this.response.getAsJsonObject() : null;
   }

   public long getLastUpdated() {
      return this.lastUpdated;
   }

   @Override
   public String toString() {
      return "ResourceReply{lastUpdated=" + this.lastUpdated + ", response=" + this.response + "} " + super.toString();
   }
}
