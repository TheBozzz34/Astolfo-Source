package bypass.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class ProxyListReader {
   private static final long a = 36586587140980L;

   public static Set<HostAndPort> r(Path path, String string) throws IOException {
      HashSet hashSet = new HashSet();

      String string2;
      try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
         while ((string2 = bufferedReader.readLine()) != null) {
            String[] strings = string2.split(string);
            hashSet.add(new HostAndPort(strings[0], Integer.parseInt(strings[1])));
         }
      }

      return hashSet;
   }

   public static Set<HostAndPort> N(Path path) throws IOException {
      return r(path, ":");
   }
}
