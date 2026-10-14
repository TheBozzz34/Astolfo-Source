package bypass.util;

public class ProxyCheckResult {
   private final HostAndPort I;
   private final boolean F;

   public ProxyCheckResult(HostAndPort hostAndPort, boolean bl) {
      this.I = hostAndPort;
      this.F = bl;
   }

   public HostAndPort q() {
      return this.I;
   }

   public boolean r() {
      return this.F;
   }
}
