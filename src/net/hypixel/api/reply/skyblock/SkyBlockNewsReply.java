package net.hypixel.api.reply.skyblock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.hypixel.api.reply.AbstractReply;

public class SkyBlockNewsReply extends AbstractReply {
   private JsonElement items;
   private static final long a = 124233703570038L;

   public JsonArray getItems() {
      return this.items != null && !this.items.isJsonNull() ? this.items.getAsJsonArray() : null;
   }

   @Override
   public String toString() {
      return "SkyBlockNewsReply{items=" + this.items + "} " + super.toString();
   }
}
