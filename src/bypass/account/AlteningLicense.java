package bypass.account;

import com.google.gson.annotations.SerializedName;

public class AlteningLicense {
   @SerializedName("username")
   private String t;
   @SerializedName("premium")
   private boolean v;
   @SerializedName("premium_name")
   private String q;
   @SerializedName("expires")
   private String C;

   public String V() {
      return this.t;
   }

   public boolean f() {
      return this.v;
   }

   public String A() {
      return this.q;
   }

   public String Q() {
      return this.C;
   }
}
