package bypass.util;

import java.util.Optional;

public enum ProxyType {
   HTTP("http"),
   SOCKS4("socks4"),
   SOCKS5("socks5");

   private final String protocolName;
   private static final ProxyType[] VALUES = new ProxyType[]{HTTP, SOCKS4, SOCKS5};

   ProxyType(String string2) {
      this.protocolName = string2;
   }

   public String getProtocolName() {
      return this.protocolName;
   }

   public static Optional<ProxyType> fromName(String string) {
      for (ProxyType proxyType : values()) {
         if (proxyType.name().equalsIgnoreCase(string)) {
            return Optional.of(proxyType);
         }
      }

      return Optional.empty();
   }
}
