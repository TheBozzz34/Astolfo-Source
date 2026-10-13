package bypass.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class ProxyListReader {
   private static final long a = 36586587140980L;

   public static Set<HostAndPort> r(Path var0, String var1) throws IOException {
      HashSet var5 = new HashSet();

      String var8;
      try (BufferedReader var6 = Files.newBufferedReader(var0)) {
         while ((var8 = var6.readLine()) != null) {
            String[] var9 = var8.split(var1);
            var5.add(new HostAndPort(var9[0], Integer.parseInt(var9[1])));
         }
      }

      return var5;
   }

   public static Set<HostAndPort> N(Path var0) throws IOException {
      return r(var0, ":");
   }
}
