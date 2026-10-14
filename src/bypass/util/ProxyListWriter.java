package bypass.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

public final class ProxyListWriter {
   public static void k(Path path, Collection<HostAndPort> collection, String string) throws IOException {
      if (Files.notExists(path)) {
         Files.createFile(path);
      }

      try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
         for (HostAndPort hostAndPort : collection) {
            bufferedWriter.write(hostAndPort.getHost());
            bufferedWriter.write(string);
            bufferedWriter.write(String.valueOf(hostAndPort.getPort()));
            bufferedWriter.newLine();
         }
      }
   }

   public static void y(Path path, Collection<HostAndPort> collection) throws IOException {
      k(path, collection, ":");
   }
}
