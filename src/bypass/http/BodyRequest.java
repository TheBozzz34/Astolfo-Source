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

      StringBuilder var4 = new StringBuilder();

      for (Entry var6 : this.m.entrySet()) {
         var4.append((String)var6.getKey()).append("=").append((String)var6.getValue()).append("&");
      }

      var4.substring(0, var4.lastIndexOf("&"));
      return var4.toString();
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
      } catch (Exception var2) {
         var2.printStackTrace();
         return -1;
      }
   }

   @Override
   public URL m(URL var1) throws Exception {
      return var1;
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
