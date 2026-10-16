package bypass.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public final class AlteningApi {
   private String apiKey;
   private final String baseUrl = "http://api.thealtening.com/v1/";
   private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

   public AlteningApi(String string) {
      this.apiKey = string;
   }

   public AlteningLicense getLicense() throws IOException {
      URLConnection urlConnection = new URL(this.appendApiKey("http://api.thealtening.com/v1/" + "license")).openConnection();
      String string = new String(StreamReader.getInstance().readAllBytes(urlConnection.getInputStream()));
      return this.gson.fromJson(string, AlteningLicense.class);
   }

   public AlteningAccountData generateAccount(AlteningLicense alteningLicense) throws IOException {
      HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(this.appendApiKey("http://api.thealtening.com/v1/" + "generate")).openConnection();
      InputStream inputStream = httpURLConnection.getInputStream();
      String string = new String(StreamReader.getInstance().readAllBytes(inputStream));
      if (alteningLicense.isPremium()) {
         inputStream.close();
         httpURLConnection.disconnect();
         return this.gson.fromJson(string, AlteningAccountData.class);
      }

      if (inputStream != null) {
         inputStream.close();
      }

      if (httpURLConnection != null) {
         httpURLConnection.disconnect();
      }

      return null;
   }

   public boolean favoriteAccount(AlteningAccountData alteningAccountData) throws IOException {
      URLConnection urlConnection = new URL(this.buildAccountUrl("http://api.thealtening.com/v1/" + "favorite", alteningAccountData)).openConnection();
      String string = new String(StreamReader.getInstance().readAllBytes(urlConnection.getInputStream()));
      return string.isEmpty();
   }

   public boolean privateAccount(AlteningAccountData alteningAccountData) throws IOException {
      URLConnection urlConnection = new URL(this.buildAccountUrl("http://api.thealtening.com/v1/" + "private", alteningAccountData)).openConnection();
      String string = new String(StreamReader.getInstance().readAllBytes(urlConnection.getInputStream()));
      return string.isEmpty();
   }

   private String appendApiKey(String string) {
      return string + "?token=" + this.apiKey;
   }

   private String buildAccountUrl(String string, AlteningAccountData alteningAccountData) {
      return string + "?token=" + this.apiKey + "&acctoken=" + alteningAccountData.getToken();
   }

   public String getApiKey() {
      return this.apiKey;
   }

   public void setApiKey(String string) {
      this.apiKey = string;
   }
}
