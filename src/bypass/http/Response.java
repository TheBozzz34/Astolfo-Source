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
   private static final String LINE_SEPARATOR = System.lineSeparator();
   private final int statusCode;
   private final String body;
   private final Map<String, List<String>> headers;

   private Response(int i, String string, Map<String, List<String>> map) {
      this.statusCode = i;
      this.body = string;
      this.headers = map;
   }

   public static final Response fromConnection(HttpURLConnection httpURLConnection, boolean bl) throws IOException {
      String string = readBody(httpURLConnection, bl);
      int i = httpURLConnection.getResponseCode();
      httpURLConnection.disconnect();
      return new Response(i, string, httpURLConnection.getHeaderFields());
   }

   public static String readBody(URLConnection urlConnection, boolean bl) throws IOException {
      StringBuilder stringBuilder = new StringBuilder();
      HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
      InputStream inputStream = httpURLConnection.getResponseCode() < 400 ? urlConnection.getInputStream() : httpURLConnection.getErrorStream();

      String string;
      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
         while ((string = bufferedReader.readLine()) != null) {
            stringBuilder.append(string);
            if (bl) {
               stringBuilder.append(LINE_SEPARATOR);
            }
         }
      }

      return stringBuilder.toString();
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public String getBody() {
      return this.body;
   }

   public Map<String, List<String>> getHeaders() {
      return this.headers;
   }
}
