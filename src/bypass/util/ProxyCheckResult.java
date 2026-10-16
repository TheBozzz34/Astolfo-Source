package bypass.util;

public class ProxyCheckResult {
   private final HostAndPort proxy;
   private final boolean reachable;

   public ProxyCheckResult(HostAndPort hostAndPort, boolean bl) {
      this.proxy = hostAndPort;
      this.reachable = bl;
   }

   public HostAndPort getProxy() {
      return this.proxy;
   }

   public boolean isReachable() {
      return this.reachable;
   }
}
