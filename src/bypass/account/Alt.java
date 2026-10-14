package bypass.account;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;

public class Alt implements JsonSerializable<JsonObject> {
   public static final DateFormat M = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
   public String j;
   public final String z;
   public final Session.Type T;
   public String o;
   public String y;
   public String U;
   public boolean N;
   public long m = -1L;
   public boolean I;
   public boolean Y;
   public boolean F;
   public String Z;
   private static final long a = 126169934704290L;

   public Alt(String string, String string2) {
      this(string, string2, string);
   }

   public Alt(String string, String string2, String string3) {
      this(string, string2, string3, AltLogin.AUTH.x(), null);
   }

   public Alt(String string, String string2, String string3, String string4, String string5) {
      this.j = string.trim();
      this.z = string2;
      this.T = AltLogin.AUTH.t(string);
      this.U = string4;
      this.o = string3.trim().length() > 1 ? string3 : this.j;
      this.y = string5;
   }

   public boolean V_boolean() {
      return !StringUtils.isNullOrEmpty(this.z) || this.D() || this.F;
   }

   public boolean D() {
      return this.j.endsWith("@alt.com");
   }

   @Override
   public String c() {
      return this.j;
   }

   public JsonObject V() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("password", Base64.getEncoder().encodeToString(this.z.getBytes()));
      jsonObject.addProperty("display", this.o);
      jsonObject.addProperty("accessToken", this.y);
      jsonObject.addProperty("clientToken", this.U);
      jsonObject.addProperty("banned", this.N);
      jsonObject.addProperty("failedLogin", this.I);
      jsonObject.addProperty("favorite", this.Y);
      jsonObject.addProperty("microsoft", this.F);
      if (this.F) {
         jsonObject.addProperty("refreshToken", this.Z);
      }

      if (this.N) {
         jsonObject.addProperty("banExpiry", this.m);
      }

      return jsonObject;
   }

   @Override
   public int hashCode() {
      return this.j.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         Alt alt = (Alt)object;
         return alt.j.equalsIgnoreCase(this.j);
      } else {
         return false;
      }
   }
}
