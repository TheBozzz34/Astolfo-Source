package net.hypixel.api.reply.skyblock;

import com.google.gson.JsonObject;
import net.hypixel.api.reply.AbstractReply;

public class ResourceReply extends AbstractReply {
   private long lastUpdated;
   private JsonObject response;

   public ResourceReply(JsonObject response) {
      this.response = response;
      this.success = response.has("success") && response.get("success").getAsBoolean();
      this.cause = response.has("cause") ? response.get("cause").getAsString() : null;
      this.lastUpdated = response.has("lastUpdated") ? response.get("lastUpdated").getAsLong() : -1L;
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
