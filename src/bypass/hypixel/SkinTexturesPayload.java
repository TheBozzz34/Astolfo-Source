package bypass.hypixel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkinTexturesPayload {
   @SerializedName("timestamp")
   @Expose
   private Long timestamp;
   @SerializedName("profileId")
   @Expose
   private String profileId;
   @SerializedName("profileName")
   @Expose
   private String profileName;
   @SerializedName("signatureRequired")
   @Expose
   private Boolean signatureRequired;
   @SerializedName("textures")
   @Expose
   private SkinTextures textures;

   public Long getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(Long long2) {
      this.timestamp = long2;
   }

   public String getProfileId() {
      return this.profileId;
   }

   public void setProfileId(String string) {
      this.profileId = string;
   }

   public String getProfileName() {
      return this.profileName;
   }

   public void setProfileName(String string) {
      this.profileName = string;
   }

   public Boolean getSignatureRequired() {
      return this.signatureRequired;
   }

   public void setSignatureRequired(Boolean boolean2) {
      this.signatureRequired = boolean2;
   }

   public SkinTextures getTextures() {
      return this.textures;
   }

   public void setTextures(SkinTextures skinTextures) {
      this.textures = skinTextures;
   }
}
