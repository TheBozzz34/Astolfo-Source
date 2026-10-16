package bypass.loader.check;

import java.util.Arrays;
import java.util.List;

public class JvmArgumentPropertyPatterns {
   private final List<String> patterns = Arrays.asList(
      "useSystemProxies",
      "socksProxyHost",
      "socksProxyPort",
      "proxyHost",
      "http.proxyHost",
      "https.proxyHost",
      "socksProxyHost",
      "http.proxyPort",
      "https.proxyPort"
   );

   public List<String> getPatterns() {
      return this.patterns;
   }
}
