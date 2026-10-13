package bypass.util;

public final class HostAndPort {
   public static final String R = ":";
   private final String host;
   private final int port;
   private static final long a = 86513441917687L;

   public HostAndPort(String var1, int var2) {
      this.host = var1;
      this.port = var2;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   @Override
   public int hashCode() {
      int var4 = 1;
      var4 = 37 * var4 + this.host.hashCode();
      return 37 * var4 + this.port;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1.getClass() != HostAndPort.class) {
         return false;
      }

      HostAndPort var5 = (HostAndPort)var1;
      return var5.host.equals(this.host) && var5.port == this.port;
   }

   @Override
   public String toString() {
      return super.toString();
   }
}
