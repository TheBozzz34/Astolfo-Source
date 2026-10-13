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

   public AuthHttp(String var1) throws MalformedURLException, IOException {
      this.C = (HttpURLConnection)new URL(var1).openConnection();
      this.C.setDoOutput(true);
      this.C.setDoInput(true);
      this.C.setConnectTimeout(5000);
      this.C.setReadTimeout(5000);
   }

   public void z(String var1, String var2) {
      this.C.setRequestProperty(var1, var2);
   }

   public void C(String var1) throws IOException {
      this.C.setRequestMethod("POST");
      byte[] var5 = var1.getBytes(StandardCharsets.UTF_8);

      try (OutputStream var6 = this.C.getOutputStream()) {
         var6.write(var5);
      }
   }

   public void e(Map<Object, Object> var1) throws IOException {
      StringJoiner var5 = new StringJoiner("&");

      for (Entry var7 : var1.entrySet()) {
         var5.add(URLEncoder.encode(var7.getKey().toString(), "UTF-8") + "=" + URLEncoder.encode(var7.getValue().toString(), "UTF-8"));
      }

      this.C(var5.toString());
   }

   public void Y() throws ProtocolException {
      this.C.setRequestMethod("GET");
   }

   public int s() throws IOException {
      return this.C.getResponseCode();
   }

   public String K() throws IOException {
      StringBuilder var4 = new StringBuilder();

      int var7;
      try (InputStreamReader var5 = new InputStreamReader(this.C.getInputStream(), StandardCharsets.UTF_8)) {
         while ((var7 = var5.read()) >= 0) {
            var4.append((char)var7);
         }
      }

      return var4.toString();
   }

   public String q() throws IOException {
      StringBuilder var4 = new StringBuilder();

      int var7;
      try (InputStreamReader var5 = new InputStreamReader(this.C.getErrorStream(), StandardCharsets.UTF_8)) {
         while ((var7 = var5.read()) >= 0) {
            var4.append((char)var7);
         }
      }

      return var4.toString();
   }
}
