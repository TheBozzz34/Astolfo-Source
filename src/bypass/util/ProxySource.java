package bypass.util;

import java.util.EnumSet;
import java.util.Set;

public interface ProxySource {
   Set<HostAndPort> scrap(EnumSet<ProxyType> enumSet);

   void z();
}
