package bypass.http;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequest implements Request {
   public final Map<String, String> m = new HashMap<>();
   private static final long a = 72336621491796L;

   @Override
   public Request m(String var1, String var2) throws Exception {
      String var7 = URLEncoder.encode(var1, e).trim();
      String var8 = URLEncoder.encode(var2, e).trim();
      this.m.put(var7, var8);
      return this;
   }

   @Override
   public Request y(String var1) throws Exception {
      String var5 = URLEncoder.encode(var1, e).trim();
      this.m.remove(var5);
      return this;
   }

   @Override
   public Map<String, String> I() {
      return Collections.unmodifiableMap(this.m);
   }

   public abstract URL m(URL var1) throws Exception;

   public abstract Map<String, String> U();
}
