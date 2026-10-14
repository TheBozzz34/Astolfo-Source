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

   public AlteningApi(String string) {
      this.d = string;
   }

   public AlteningLicense t() throws IOException {
      URLConnection urlConnection = new URL(this.t("http://api.thealtening.com/v1/" + "license")).openConnection();
      String string = new String(StreamReader.o().M(urlConnection.getInputStream()));
      return this.D.fromJson(string, AlteningLicense.class);
   }

   public AlteningAccountData i(AlteningLicense alteningLicense) throws IOException {
      HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(this.t("http://api.thealtening.com/v1/" + "generate")).openConnection();
      InputStream inputStream = httpURLConnection.getInputStream();
      String string = new String(StreamReader.o().M(inputStream));
      if (alteningLicense.f()) {
         inputStream.close();
         httpURLConnection.disconnect();
         return this.D.fromJson(string, AlteningAccountData.class);
      }

      if (inputStream != null) {
         inputStream.close();
      }

      if (httpURLConnection != null) {
         httpURLConnection.disconnect();
      }

      return null;
   }

   public boolean C(AlteningAccountData alteningAccountData) throws IOException {
      URLConnection urlConnection = new URL(this.r("http://api.thealtening.com/v1/" + "favorite", alteningAccountData)).openConnection();
      String string = new String(StreamReader.o().M(urlConnection.getInputStream()));
      return string.isEmpty();
   }

   public boolean L(AlteningAccountData alteningAccountData) throws IOException {
      URLConnection urlConnection = new URL(this.r("http://api.thealtening.com/v1/" + "private", alteningAccountData)).openConnection();
      String string = new String(StreamReader.o().M(urlConnection.getInputStream()));
      return string.isEmpty();
   }

   private String t(String string) {
      return string + "?token=" + this.d;
   }

   private String r(String string, AlteningAccountData alteningAccountData) {
      return string + "?token=" + this.d + "&acctoken=" + alteningAccountData.f();
   }

   public String O() {
      return this.d;
   }

   public void d(String string) {
      this.d = string;
   }
}
