package bypass.util;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GatherProxySource implements ProxySource {
   private final ExecutorService F;
   private String H = "http://gatherproxy.com/sockslist/country/?c=";
   private String[] u = new String[]{
      "United%20States",
      "Brazil",
      "Mexico",
      "Sweden",
      "Norway",
      "France",
      "Taiwan",
      "India",
      "Republic%20of%20Korea",
      "Denmark",
      "Russia",
      "China",
      "Ukraine",
      "Canada",
      "Austria",
      "Switzerland",
      "Germany",
      "Australia",
      "Finland",
      "Romania",
      "Poland",
      "Italy",
      "Netherlands",
      "United%20Kingdom",
      "Spain",
      "Singapore",
      "Belgium",
      "Portugal",
      "Sweden"
   };
   private static final long a = 48415676494622L;

   public GatherProxySource() {
      this.F = Executors.newFixedThreadPool(25);
   }

   @Override
   public Set<HostAndPort> scrap(EnumSet<ProxyType> var1) {
      return new HashSet<>();
   }

   @Override
   public void z() {
      this.F.shutdownNow();
   }
}
