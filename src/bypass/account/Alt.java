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

   public Alt(String var1, String var2) {
      this(var1, var2, var1);
   }

   public Alt(String var1, String var2, String var3) {
      this(var1, var2, var3, AltLogin.AUTH.x(), null);
   }

   public Alt(String var1, String var2, String var3, String var4, String var5) {
      this.j = var1.trim();
      this.z = var2;
      this.T = AltLogin.AUTH.t(var1);
      this.U = var4;
      this.o = var3.trim().length() > 1 ? var3 : this.j;
      this.y = var5;
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
      JsonObject var4 = new JsonObject();
      var4.addProperty("password", Base64.getEncoder().encodeToString(this.z.getBytes()));
      var4.addProperty("display", this.o);
      var4.addProperty("accessToken", this.y);
      var4.addProperty("clientToken", this.U);
      var4.addProperty("banned", this.N);
      var4.addProperty("failedLogin", this.I);
      var4.addProperty("favorite", this.Y);
      var4.addProperty("microsoft", this.F);
      if (this.F) {
         var4.addProperty("refreshToken", this.Z);
      }

      if (this.N) {
         var4.addProperty("banExpiry", this.m);
      }

      return var4;
   }

   @Override
   public int hashCode() {
      return this.j.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && var1.getClass() == this.getClass()) {
         Alt var5 = (Alt)var1;
         return var5.j.equalsIgnoreCase(this.j);
      } else {
         return false;
      }
   }
}
