package bypass.util;

import java.util.Optional;

public enum ProxyType {
   HTTP("http"),
   SOCKS4("socks4"),
   SOCKS5("socks5");

   private final String N;
   private static final ProxyType[] t = new ProxyType[]{HTTP, SOCKS4, SOCKS5};
   private static final long a = 75514600320246L;

   ProxyType(String var3) {
      this.N = var3;
   }

   public String R() {
      return this.N;
   }

   public static Optional<ProxyType> a(String var0) {
      for (ProxyType var7 : values()) {
         if (var7.name().equalsIgnoreCase(var0)) {
            return Optional.of(var7);
         }
      }

      return Optional.empty();
   }
}
