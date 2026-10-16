package bypass.hypixel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkinTextures {
   @SerializedName("SKIN")
   @Expose
   private SkinTexture skin;

   public SkinTexture getSkin() {
      return this.skin;
   }

   public void setSkin(SkinTexture skinTexture) {
      this.skin = skinTexture;
   }
}
