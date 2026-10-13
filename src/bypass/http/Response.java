package bypass.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public final class Response {
   private static final String R = System.lineSeparator();
   private final int G;
   private final String d;
   private final Map<String, List<String>> a;
   private static final long b = 6451728891889L;
   private static final long c = -6498816265136635504L;

   private Response(int var1, String var2, Map<String, List<String>> var3) {
      this.G = var1;
      this.d = var2;
      this.a = var3;
   }

   public static final Response g(HttpURLConnection var0, boolean var1) throws IOException {
      String var2 = Z(var0, var1);
      int var3 = var0.getResponseCode();
      var0.disconnect();
      return new Response(var3, var2, var0.getHeaderFields());
   }

   public static String Z(URLConnection var0, boolean var1) throws IOException {
      StringBuilder var5 = new StringBuilder();
      HttpURLConnection var6 = (HttpURLConnection)var0;
      InputStream var7 = var6.getResponseCode() < (int)c ? var0.getInputStream() : var6.getErrorStream();

      String var10;
      try (BufferedReader var8 = new BufferedReader(new InputStreamReader(var7))) {
         while ((var10 = var8.readLine()) != null) {
            var5.append(var10);
            if (var1) {
               var5.append(R);
            }
         }
      }

      return var5.toString();
   }

   public int P() {
      return this.G;
   }

   public String u() {
      return this.d;
   }

   public Map<String, List<String>> k() {
      return this.a;
   }
}
