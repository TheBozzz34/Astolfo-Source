package bypass.http;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class BodyRequest extends AbstractRequest {
   private static final Map<String, String> h = new HashMap<>();
   private static final long b = 88286633402691L;

   @Override
   public String D() throws Exception {
      if (this.m.isEmpty()) {
         return "";
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (Entry entry : this.m.entrySet()) {
         stringBuilder.append((String)entry.getKey()).append("=").append((String)entry.getValue()).append("&");
      }

      stringBuilder.substring(0, stringBuilder.lastIndexOf("&"));
      return stringBuilder.toString();
   }

   @Override
   public String t() {
      return HttpMethod.POST.toString();
   }

   @Override
   public ContentType V() {
      return ContentType.URL_ENCODED;
   }

   @Override
   public int d() {
      try {
         return this.D().length();
      } catch (Exception exception) {
         exception.printStackTrace();
         return -1;
      }
   }

   @Override
   public URL m(URL url) throws Exception {
      return url;
   }

   @Override
   public Map<String, String> U() {
      h.clear();
      h.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
      h.put("Content-Type", this.V().S());
      h.put("Content-Length", String.valueOf(this.d()));
      return h;
   }
}
