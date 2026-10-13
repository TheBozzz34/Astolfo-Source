package net.hypixel.api.reply.skyblock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.api.reply.AbstractReply;

public class SkyBlockProfileReply extends AbstractReply {
   private JsonElement profile;
   private static final long a = 26323601111223L;

   public JsonObject getProfile() {
      return this.profile != null && !this.profile.isJsonNull() ? this.profile.getAsJsonObject() : null;
   }

   @Override
   public String toString() {
      return "SkyBlockProfileReply{profile=" + this.profile + "} " + super.toString();
   }
}
