package bypass.util;

import java.util.Optional;

public enum ProxyType {
   HTTP("http"),
   SOCKS4("socks4"),
   SOCKS5("socks5");

   private final String N;
   private static final ProxyType[] t = new ProxyType[]{HTTP, SOCKS4, SOCKS5};
   private static final long a = 75514600320246L;

   ProxyType(String string2) {
      this.N = string2;
   }

   public String R() {
      return this.N;
   }

   public static Optional<ProxyType> a(String string) {
      for (ProxyType proxyType : values()) {
         if (proxyType.name().equalsIgnoreCase(string)) {
            return Optional.of(proxyType);
         }
      }

      return Optional.empty();
   }
}
