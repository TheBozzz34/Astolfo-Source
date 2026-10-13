package oshi.software.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.NetworkParams;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@ThreadSafe
public abstract class AbstractNetworkParams implements NetworkParams {
   private static final long a = 5612684153801L;

   @Override
   public String getDomainName() {
      try {
         return InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException var2) {
         return "";
      }
   }

   @Override
   public String getHostName() {
      try {
         String var3 = InetAddress.getLocalHost().getHostName();
         int var4 = var3.indexOf(46);
         return var4 == -1 ? var3 : var3.substring(0, var4);
      } catch (UnknownHostException var5) {
         return "";
      }
   }

   @Override
   public String[] getDnsServers() {
      List var4 = FileUtil.readFile("/etc/resolv.conf");
      String var5 = "nameserver";
      byte var6 = 3;
      ArrayList var7 = new ArrayList();

      for (int var8 = 0; var8 < var4.size() && var7.size() < var6; var8++) {
         String var9 = (String)var4.get(var8);
         if (var9.startsWith(var5)) {
            String var10 = var9.substring(var5.length()).replaceFirst("^[ \t]+", "");
            if (var10.length() != 0 && var10.charAt(0) != '#' && var10.charAt(0) != ';') {
               String var11 = var10.split("[ \t#;]", 2)[0];
               var7.add(var11);
            }
         }
      }

      return (java.lang.String[])(var7.toArray(new String[0]));
   }

   public static String searchGateway(List<String> var0) {
      for (String var5 : var0) {
         String var6 = var5.replaceFirst("^\\s+", "");
         if (var6.startsWith("gateway:")) {
            String[] var7 = ParseUtil.Q.split(var6);
            if (var7.length < 2) {
               return "";
            }

            return var7[1].split("%")[0];
         }
      }

      return "";
   }

   @Override
   public String toString() {
      return String.format(
         "Host name: %s, Domain name: %s, DNS servers: %s, IPv4 Gateway: %s, IPv6 Gateway: %s",
         this.getHostName(),
         this.getDomainName(),
         Arrays.toString(this.getDnsServers()),
         this.getIpv4DefaultGateway(),
         this.getIpv6DefaultGateway()
      );
   }
}
