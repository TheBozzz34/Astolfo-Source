package bypass.via.util;

import bypass.via.providers.NativeVersionProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Arrays;
import java.util.stream.Stream;

public class ProtocolUtils {
   public static boolean isSupported(int i) {
      return isSupported(i, Via.getManager().getProviders().get(NativeVersionProvider.class).getServerProtocolVersion());
   }

   public static boolean isSupported(int i, int j) {
      return i == j || Via.getManager().getProtocolManager().getProtocolPath(j, i) != null;
   }

   public static String getProtocolName(int i) {
      return !ProtocolVersion.isRegistered(i) ? Integer.toString(i) : ProtocolVersion.getProtocol(i).getName();
   }

   public static boolean isStartOfProtocolText(String string) {
      try {
         Integer.parseInt(string);
         return true;
      } catch (NumberFormatException numberFormatException) {
         try {
            Integer.parseInt(string + '0');
            return true;
         } catch (NumberFormatException numberFormatException2) {
            return ProtocolVersion.getClosest(string) != null
               ? true
               : ProtocolVersion.getProtocols()
                  .stream()
                  .map(ProtocolVersion::getName)
                  .flatMap(string2 -> Stream.concat(Arrays.stream(string2.split("-")), Arrays.stream(new String[]{string2})))
                  .anyMatch(string3 -> string3.startsWith(string));
         }
      }
   }

   public static Integer parseProtocolId(String string) {
      try {
         return Integer.parseInt(string);
      } catch (NumberFormatException numberFormatException) {
         ProtocolVersion protocolVersion = ProtocolVersion.getClosest(string);
         return protocolVersion == null ? null : protocolVersion.getVersion();
      }
   }

   public static String[] getProtocolSuggestions(String string) {
      return ProtocolVersion.getProtocols()
         .stream()
         .map(ProtocolVersion::getName)
         .flatMap(string2 -> Stream.concat(Arrays.stream(string2.split("-")), Arrays.stream(new String[]{string2})))
         .distinct()
         .filter(string3 -> string3.startsWith(string))
         .toArray(String[]::new);
   }
}
