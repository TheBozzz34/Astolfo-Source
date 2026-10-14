package bypass.util;

public final class HostAndPort {
   public static final String R = ":";
   private final String host;
   private final int port;
   private static final long a = 86513441917687L;

   public HostAndPort(String string, int i) {
      this.host = string;
      this.port = i;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   @Override
   public int hashCode() {
      int i = 1;
      i = 37 * i + this.host.hashCode();
      return 37 * i + this.port;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (object.getClass() != HostAndPort.class) {
         return false;
      }

      HostAndPort hostAndPort = (HostAndPort)object;
      return hostAndPort.host.equals(this.host) && hostAndPort.port == this.port;
   }

   @Override
   public String toString() {
      return super.toString();
   }
}
