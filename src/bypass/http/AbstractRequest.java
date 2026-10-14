package bypass.http;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequest implements Request {
   public final Map<String, String> m = new HashMap<>();

   @Override
   public Request m(String string, String string2) throws Exception {
      String string3 = URLEncoder.encode(string, e).trim();
      String string4 = URLEncoder.encode(string2, e).trim();
      this.m.put(string3, string4);
      return this;
   }

   @Override
   public Request y(String string) throws Exception {
      String string2 = URLEncoder.encode(string, e).trim();
      this.m.remove(string2);
      return this;
   }

   @Override
   public Map<String, String> I() {
      return Collections.unmodifiableMap(this.m);
   }

   public abstract URL m(URL url) throws Exception;

   public abstract Map<String, String> U();
}
