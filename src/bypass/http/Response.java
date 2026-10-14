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

   private Response(int i, String string, Map<String, List<String>> map) {
      this.G = i;
      this.d = string;
      this.a = map;
   }

   public static final Response g(HttpURLConnection httpURLConnection, boolean bl) throws IOException {
      String string = Z(httpURLConnection, bl);
      int i = httpURLConnection.getResponseCode();
      httpURLConnection.disconnect();
      return new Response(i, string, httpURLConnection.getHeaderFields());
   }

   public static String Z(URLConnection urlConnection, boolean bl) throws IOException {
      StringBuilder stringBuilder = new StringBuilder();
      HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
      InputStream inputStream = httpURLConnection.getResponseCode() < (int)c ? urlConnection.getInputStream() : httpURLConnection.getErrorStream();

      String string;
      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
         while ((string = bufferedReader.readLine()) != null) {
            stringBuilder.append(string);
            if (bl) {
               stringBuilder.append(R);
            }
         }
      }

      return stringBuilder.toString();
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
