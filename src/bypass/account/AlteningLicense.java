package bypass.account;

import com.google.gson.annotations.SerializedName;

public class AlteningLicense {
   @SerializedName("username")
   private String username;
   @SerializedName("premium")
   private boolean premium;
   @SerializedName("premium_name")
   private String premiumName;
   @SerializedName("expires")
   private String expires;

   public String getUsername() {
      return this.username;
   }

   public boolean isPremium() {
      return this.premium;
   }

   public String getPremiumName() {
      return this.premiumName;
   }

   public String getExpires() {
      return this.expires;
   }
}
