package bypass.hypixel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkinTextures {
   @SerializedName("SKIN")
   @Expose
   private SkinTexture T;

   public SkinTexture d() {
      return this.T;
   }

   public void F(SkinTexture skinTexture) {
      this.T = skinTexture;
   }
}
