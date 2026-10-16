package bypass.http;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class SimpleRequest extends AbstractRequest {
   private static final Map<String, String> defaultHeaders = new HashMap<>();

   @Override
   public String getBody() throws Exception {
      if (this.parameters.isEmpty()) {
         return "";
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (Entry entry : this.parameters.entrySet()) {
         stringBuilder.append((String)entry.getKey()).append("=").append((String)entry.getValue()).append("&");
      }

      stringBuilder.substring(0, stringBuilder.lastIndexOf("&"));
      return stringBuilder.toString();
   }

   @Override
   public String getMethod() {
      return HttpMethod.GET.toString();
   }

   @Override
   public ContentType getContentType() {
      return ContentType.URL_ENCODED;
   }

   @Override
   public int getContentLength() {
      try {
         return this.getBody().length();
      } catch (Exception exception) {
         exception.printStackTrace();
         return -1;
      }
   }

   @Override
   public URL buildUrl(URL url) throws Exception {
      String string = url.toString();
      if (!string.endsWith("?")) {
         string = string.concat("?");
      }

      string = string.concat(this.getBody());
      return new URL(string);
   }

   @Override
   public Map<String, String> getHeaders() {
      return defaultHeaders;
   }

   static {
      defaultHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
   }
}
