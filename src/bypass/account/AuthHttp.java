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
   public HttpURLConnection C;
   private static final long a = 58800123819467L;

   public AuthHttp(String string) throws MalformedURLException, IOException {
      this.C = (HttpURLConnection)new URL(string).openConnection();
      this.C.setDoOutput(true);
      this.C.setDoInput(true);
      this.C.setConnectTimeout(5000);
      this.C.setReadTimeout(5000);
   }

   public void z(String string, String string2) {
      this.C.setRequestProperty(string, string2);
   }

   public void C(String string) throws IOException {
      this.C.setRequestMethod("POST");
      byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

      try (OutputStream outputStream = this.C.getOutputStream()) {
         outputStream.write(bytes);
      }
   }

   public void e(Map<Object, Object> map) throws IOException {
      StringJoiner stringJoiner = new StringJoiner("&");

      for (Entry entry : map.entrySet()) {
         stringJoiner.add(URLEncoder.encode(entry.getKey().toString(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
      }

      this.C(stringJoiner.toString());
   }

   public void Y() throws ProtocolException {
      this.C.setRequestMethod("GET");
   }

   public int s() throws IOException {
      return this.C.getResponseCode();
   }

   public String K() throws IOException {
      StringBuilder stringBuilder = new StringBuilder();

      int i;
      try (InputStreamReader inputStreamReader = new InputStreamReader(this.C.getInputStream(), StandardCharsets.UTF_8)) {
         while ((i = inputStreamReader.read()) >= 0) {
            stringBuilder.append((char)i);
         }
      }

      return stringBuilder.toString();
   }

   public String q() throws IOException {
      StringBuilder stringBuilder = new StringBuilder();

      int i;
      try (InputStreamReader inputStreamReader = new InputStreamReader(this.C.getErrorStream(), StandardCharsets.UTF_8)) {
         while ((i = inputStreamReader.read()) >= 0) {
            stringBuilder.append((char)i);
         }
      }

      return stringBuilder.toString();
   }
}
