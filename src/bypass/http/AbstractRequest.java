package bypass.http;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequest implements Request {
   public final Map<String, String> parameters = new HashMap<>();

   @Override
   public Request addParameter(String string, String string2) throws Exception {
      String string3 = URLEncoder.encode(string, CHARSET).trim();
      String string4 = URLEncoder.encode(string2, CHARSET).trim();
      this.parameters.put(string3, string4);
      return this;
   }

   @Override
   public Request removeParameter(String string) throws Exception {
      String string2 = URLEncoder.encode(string, CHARSET).trim();
      this.parameters.remove(string2);
      return this;
   }

   @Override
   public Map<String, String> getParameters() {
      return Collections.unmodifiableMap(this.parameters);
   }

   public abstract URL buildUrl(URL url) throws Exception;

   public abstract Map<String, String> getHeaders();
}
