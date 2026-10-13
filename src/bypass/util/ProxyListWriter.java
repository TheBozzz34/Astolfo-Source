package bypass.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

public final class ProxyListWriter {
   private static final long a = 113720108509415L;

   public static void k(Path var0, Collection<HostAndPort> var1, String var2) throws IOException {
      if (Files.notExists(var0)) {
         Files.createFile(var0);
      }

      try (BufferedWriter var6 = Files.newBufferedWriter(var0, StandardOpenOption.APPEND)) {
         for (HostAndPort var9 : var1) {
            var6.write(var9.getHost());
            var6.write(var2);
            var6.write(String.valueOf(var9.getPort()));
            var6.newLine();
         }
      }
   }

   public static void y(Path var0, Collection<HostAndPort> var1) throws IOException {
      k(var0, var1, ":");
   }
}
