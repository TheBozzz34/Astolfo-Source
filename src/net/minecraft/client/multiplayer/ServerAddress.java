package net.minecraft.client.multiplayer;

import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class ServerAddress {
   private final String ipAddress;
   private final int serverPort;
   private static final long a = 42452013606608L;

   private ServerAddress(String address, int port) {
      this.ipAddress = address;
      this.serverPort = port;
   }

   public String getIP() {
      return IDN.toASCII(this.ipAddress);
   }

   public int getPort() {
      return this.serverPort;
   }

   public static ServerAddress fromString(String string) {
      if (string == null) {
         return null;
      }

      String[] strings = string.split(":");
      if (string.startsWith("[")) {
         int i = string.indexOf("]");
         if (i > 0) {
            String string2 = string.substring(1, i);
            String string3 = string.substring(i + 1).trim();
            if (string3.startsWith(":") && string3.length() > 0) {
               string3 = string3.substring(1);
               strings = new String[]{string2, string3};
            } else {
               strings = new String[]{string2};
            }
         }
      }

      if (strings.length > 2) {
         strings = new String[]{string};
      }

      String string4 = strings[0];
      int j = strings.length > 1 ? parseIntWithDefault(strings[1], 25565) : 25565;
      if (j == 25565) {
         String[] strings2 = getServerAddress(string4);
         string4 = strings2[0];
         j = parseIntWithDefault(strings2[1], 25565);
      }

      return new ServerAddress(string4, j);
   }

   private static String[] getServerAddress(String string) {
      try {
         Class.forName("com.sun.jndi.dns.DnsContextFactory");
         Hashtable hashtable = new Hashtable();
         hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         hashtable.put("java.naming.provider.url", "dns:");
         hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
         InitialDirContext initialDirContext = new InitialDirContext(hashtable);
         Attributes attributes = initialDirContext.getAttributes("_minecraft._tcp." + string, new String[]{"SRV"});
         String[] strings = attributes.get("srv").get().toString().split(" ", 4);
         return new String[]{strings[3], strings[2]};
      } catch (Throwable throwable) {
         return new String[]{string, Integer.toString(25565)};
      }
   }

   private static int parseIntWithDefault(String string, int i) {
      try {
         return Integer.parseInt(string.trim());
      } catch (Exception exception) {
         return i;
      }
   }
}
