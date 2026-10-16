package bypass.account;

import com.google.gson.annotations.SerializedName;

public class AlteningAccountData {
   @SerializedName("token")
   private String token;
   @SerializedName("username")
   private String username;
   @SerializedName("expires")
   private String expires;
   @SerializedName("limit")
   private boolean limit;
   @SerializedName("skin")
   private String skin;

   public String getToken() {
      return this.token;
   }

   public String getUsername() {
      return this.username;
   }

   public String getExpires() {
      return this.expires;
   }

   public boolean isLimited() {
      return this.limit;
   }

   public String getSkin() {
      return this.skin;
   }
}
