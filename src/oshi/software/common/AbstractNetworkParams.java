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
   @Override
   public String getDomainName() {
      try {
         return InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException unknownHostException) {
         return "";
      }
   }

   @Override
   public String getHostName() {
      try {
         String string = InetAddress.getLocalHost().getHostName();
         int i = string.indexOf(46);
         return i == -1 ? string : string.substring(0, i);
      } catch (UnknownHostException unknownHostException) {
         return "";
      }
   }

   @Override
   public String[] getDnsServers() {
      List list = FileUtil.readFile("/etc/resolv.conf");
      String string = "nameserver";
      byte b = 3;
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < list.size() && arrayList.size() < b; i++) {
         String string2 = (String)list.get(i);
         if (string2.startsWith(string)) {
            String string3 = string2.substring(string.length()).replaceFirst("^[ \t]+", "");
            if (string3.length() != 0 && string3.charAt(0) != '#' && string3.charAt(0) != ';') {
               String string4 = string3.split("[ \t#;]", 2)[0];
               arrayList.add(string4);
            }
         }
      }

      return (java.lang.String[])(arrayList.toArray(new String[0]));
   }

   public static String searchGateway(List<String> lines) {
      for (String string : lines) {
         String string2 = string.replaceFirst("^\\s+", "");
         if (string2.startsWith("gateway:")) {
            String[] strings = ParseUtil.whitespaces.split(string2);
            if (strings.length < 2) {
               return "";
            }

            return strings[1].split("%")[0];
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
