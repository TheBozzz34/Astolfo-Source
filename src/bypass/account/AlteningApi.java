package bypass.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public final class AlteningApi {
   private String d;
   private final String T = "http://api.thealtening.com/v1/";
   private final Gson D = new GsonBuilder().setPrettyPrinting().create();

   public AlteningApi(String var1) {
      this.d = var1;
   }

   public AlteningLicense t() throws IOException {
      URLConnection var1 = new URL(this.t("http://api.thealtening.com/v1/" + "license")).openConnection();
      String var2 = new String(StreamReader.o().M(var1.getInputStream()));
      return this.D.fromJson(var2, AlteningLicense.class);
   }

   public AlteningAccountData i(AlteningLicense var1) throws IOException {
      HttpURLConnection var2 = (HttpURLConnection)new URL(this.t("http://api.thealtening.com/v1/" + "generate")).openConnection();
      InputStream var3 = var2.getInputStream();
      String var4 = new String(StreamReader.o().M(var3));
      if (var1.f()) {
         var3.close();
         var2.disconnect();
         return this.D.fromJson(var4, AlteningAccountData.class);
      }

      if (var3 != null) {
         var3.close();
      }

      if (var2 != null) {
         var2.disconnect();
      }

      return null;
   }

   public boolean C(AlteningAccountData var1) throws IOException {
      URLConnection var2 = new URL(this.r("http://api.thealtening.com/v1/" + "favorite", var1)).openConnection();
      String var3 = new String(StreamReader.o().M(var2.getInputStream()));
      return var3.isEmpty();
   }

   public boolean L(AlteningAccountData var1) throws IOException {
      URLConnection var2 = new URL(this.r("http://api.thealtening.com/v1/" + "private", var1)).openConnection();
      String var3 = new String(StreamReader.o().M(var2.getInputStream()));
      return var3.isEmpty();
   }

   private String t(String var1) {
      return var1 + "?token=" + this.d;
   }

   private String r(String var1, AlteningAccountData var2) {
      return var1 + "?token=" + this.d + "&acctoken=" + var2.f();
   }

   public String O() {
      return this.d;
   }

   public void d(String var1) {
      this.d = var1;
   }
}
