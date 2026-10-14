package bypass.relation;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonObject;

public class Relation implements JsonSerializable<JsonObject> {
   private final String u;
   private final String P;
   private final boolean X;
   private static final long a = 53383391599147L;

   public Relation(String string, String string2, boolean bl) {
      this.u = string;
      this.P = string2.trim().equals("") ? string : string2;
      this.X = bl;
   }

   public String i() {
      return this.u;
   }

   public String m() {
      return this.P;
   }

   public boolean z() {
      return this.X;
   }

   @Override
   public String c() {
      return this.i();
   }

   public JsonObject V() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("nickname", this.m());
      jsonObject.addProperty("protected", this.z());
      return jsonObject;
   }
}
