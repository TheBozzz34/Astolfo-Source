package bypass.util;

import java.util.EnumSet;
import java.util.Set;

public interface ProxySource {
   Set<HostAndPort> scrap(EnumSet<ProxyType> var1);

   void z();
}
