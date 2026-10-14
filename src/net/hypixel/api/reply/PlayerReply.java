package net.hypixel.api.reply;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class PlayerReply extends AbstractReply {
   @SerializedName("player")
   private JsonElement player;

   public JsonObject getPlayer() {
      return this.player != null && !this.player.isJsonNull() ? this.player.getAsJsonObject() : null;
   }

   @Override
   public String toString() {
      return "PlayerReply{player=" + this.player + "} " + super.toString();
   }
}
