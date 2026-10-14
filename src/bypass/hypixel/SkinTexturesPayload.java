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

   public void o(Long long2) {
      this.t = long2;
   }

   public String o() {
      return this.n;
   }

   public void W(String string) {
      this.n = string;
   }

   public String g() {
      return this.r;
   }

   public void F(String string) {
      this.r = string;
   }

   public Boolean D() {
      return this.c;
   }

   public void H(Boolean boolean2) {
      this.c = boolean2;
   }

   public SkinTextures Y() {
      return this.b;
   }

   public void P(SkinTextures skinTextures) {
      this.b = skinTextures;
   }
}
