package bypass.hypixel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkinTexture {
   @SerializedName("url")
   @Expose
   private String G;

   public String j() {
      return this.G;
   }

   public void h(String var1) {
      this.G = var1;
   }
}
