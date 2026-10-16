package bypass.account;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Map.Entry;

public class AuthHttp {
   public HttpURLConnection connection;

   public AuthHttp(String string) throws MalformedURLException, IOException {
      this.connection = (HttpURLConnection)new URL(string).openConnection();
      this.connection.setDoOutput(true);
      this.connection.setDoInput(true);
      this.connection.setConnectTimeout(5000);
      this.connection.setReadTimeout(5000);
   }

   public void setHeader(String string, String string2) {
      this.connection.setRequestProperty(string, string2);
   }

   public void post(String string) throws IOException {
      this.connection.setRequestMethod("POST");
      byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

      try (OutputStream outputStream = this.connection.getOutputStream()) {
         outputStream.write(bytes);
      }
   }

   public void postForm(Map<Object, Object> map) throws IOException {
      StringJoiner stringJoiner = new StringJoiner("&");

      for (Entry entry : map.entrySet()) {
         stringJoiner.add(URLEncoder.encode(entry.getKey().toString(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
      }

      this.post(stringJoiner.toString());
   }

   public void setGetMethod() throws ProtocolException {
      this.connection.setRequestMethod("GET");
   }

   public int getResponseCode() throws IOException {
      return this.connection.getResponseCode();
   }

   public String readResponse() throws IOException {
      StringBuilder stringBuilder = new StringBuilder();

      int i;
      try (InputStreamReader inputStreamReader = new InputStreamReader(this.connection.getInputStream(), StandardCharsets.UTF_8)) {
         while ((i = inputStreamReader.read()) >= 0) {
            stringBuilder.append((char)i);
         }
      }

      return stringBuilder.toString();
   }

   public String readErrorResponse() throws IOException {
      StringBuilder stringBuilder = new StringBuilder();

      int i;
      try (InputStreamReader inputStreamReader = new InputStreamReader(this.connection.getErrorStream(), StandardCharsets.UTF_8)) {
         while ((i = inputStreamReader.read()) >= 0) {
            stringBuilder.append((char)i);
         }
      }

      return stringBuilder.toString();
   }
}
