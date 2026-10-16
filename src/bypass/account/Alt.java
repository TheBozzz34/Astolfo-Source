package bypass.account;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;

public class Alt implements JsonSerializable<JsonObject> {
   public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
   public String username;
   public final String password;
   public final Session.Type sessionType;
   public String displayName;
   public String accessToken;
   public String clientToken;
   public boolean banned;
   public long banExpiry = -1L;
   public boolean failedLogin;
   public boolean favorite;
   public boolean microsoft;
   public String refreshToken;

   public Alt(String string, String string2) {
      this(string, string2, string);
   }

   public Alt(String string, String string2, String string3) {
      this(string, string2, string3, AltLogin.AUTH.generateClientToken(), null);
   }

   public Alt(String string, String string2, String string3, String string4, String string5) {
      this.username = string.trim();
      this.password = string2;
      this.sessionType = AltLogin.AUTH.getSessionType(string);
      this.clientToken = string4;
      this.displayName = string3.trim().length() > 1 ? string3 : this.username;
      this.accessToken = string5;
   }

   public boolean V_boolean() {
      return !StringUtils.isNullOrEmpty(this.password) || this.isAlteningAlt() || this.microsoft;
   }

   public boolean isAlteningAlt() {
      return this.username.endsWith("@alt.com");
   }

   @Override
   public String getKey() {
      return this.username;
   }

   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("password", Base64.getEncoder().encodeToString(this.password.getBytes()));
      jsonObject.addProperty("display", this.displayName);
      jsonObject.addProperty("accessToken", this.accessToken);
      jsonObject.addProperty("clientToken", this.clientToken);
      jsonObject.addProperty("banned", this.banned);
      jsonObject.addProperty("failedLogin", this.failedLogin);
      jsonObject.addProperty("favorite", this.favorite);
      jsonObject.addProperty("microsoft", this.microsoft);
      if (this.microsoft) {
         jsonObject.addProperty("refreshToken", this.refreshToken);
      }

      if (this.banned) {
         jsonObject.addProperty("banExpiry", this.banExpiry);
      }

      return jsonObject;
   }

   @Override
   public int hashCode() {
      return this.username.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         Alt alt = (Alt)object;
         return alt.username.equalsIgnoreCase(this.username);
      } else {
         return false;
      }
   }
}
