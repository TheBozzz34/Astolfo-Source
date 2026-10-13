package bypass.relation;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonObject;

public class Relation implements JsonSerializable<JsonObject> {
   private final String u;
   private final String P;
   private final boolean X;
   private static final long a = 53383391599147L;

   public Relation(String var1, String var2, boolean var3) {
      this.u = var1;
      this.P = var2.trim().equals("") ? var1 : var2;
      this.X = var3;
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
      JsonObject var4 = new JsonObject();
      var4.addProperty("nickname", this.m());
      var4.addProperty("protected", this.z());
      return var4;
   }
}
