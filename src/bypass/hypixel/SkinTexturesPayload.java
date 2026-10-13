package bypass.hypixel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkinTexturesPayload {
   @SerializedName("timestamp")
   @Expose
   private Long t;
   @SerializedName("profileId")
   @Expose
   private String n;
   @SerializedName("profileName")
   @Expose
   private String r;
   @SerializedName("signatureRequired")
   @Expose
   private Boolean c;
   @SerializedName("textures")
   @Expose
   private SkinTextures b;

   public Long S() {
      return this.t;
   }

   public void o(Long var1) {
      this.t = var1;
   }

   public String o() {
      return this.n;
   }

   public void W(String var1) {
      this.n = var1;
   }

   public String g() {
      return this.r;
   }

   public void F(String var1) {
      this.r = var1;
   }

   public Boolean D() {
      return this.c;
   }

   public void H(Boolean var1) {
      this.c = var1;
   }

   public SkinTextures Y() {
      return this.b;
   }

   public void P(SkinTextures var1) {
      this.b = var1;
   }
}
