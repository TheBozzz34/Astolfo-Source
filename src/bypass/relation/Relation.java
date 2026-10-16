package bypass.relation;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonObject;

public class Relation implements JsonSerializable<JsonObject> {
   private final String name;
   private final String nickname;
   private final boolean friend;

   public Relation(String string, String string2, boolean bl) {
      this.name = string;
      this.nickname = string2.trim().equals("") ? string : string2;
      this.friend = bl;
   }

   public String getName() {
      return this.name;
   }

   public String getNickname() {
      return this.nickname;
   }

   public boolean isFriend() {
      return this.friend;
   }

   @Override
   public String getKey() {
      return this.getName();
   }

   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("nickname", this.getNickname());
      jsonObject.addProperty("protected", this.isFriend());
      return jsonObject;
   }
}
