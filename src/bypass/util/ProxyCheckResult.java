package bypass.util;

public class ProxyCheckResult {
   private final HostAndPort I;
   private final boolean F;

   public ProxyCheckResult(HostAndPort var1, boolean var2) {
      this.I = var1;
      this.F = var2;
   }

   public HostAndPort q() {
      return this.I;
   }

   public boolean r() {
      return this.F;
   }
}
