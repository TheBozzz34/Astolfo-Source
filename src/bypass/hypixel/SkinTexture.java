package bypass.hypixel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkinTexture {
   @SerializedName("url")
   @Expose
   private String url;

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String string) {
      this.url = string;
   }
}
