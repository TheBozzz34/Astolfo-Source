package bypass.http;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class SimpleRequest extends AbstractRequest {
   private static final Map<String, String> K = new HashMap<>();
   private static final long b = 12224250608946L;

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
      return HttpMethod.GET.toString();
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
      String var5 = var1.toString();
      if (!var5.endsWith("?")) {
         var5 = var5.concat("?");
      }

      var5 = var5.concat(this.D());
      return new URL(var5);
   }

   @Override
   public Map<String, String> U() {
      return K;
   }

   static {
      K.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
   }
}
