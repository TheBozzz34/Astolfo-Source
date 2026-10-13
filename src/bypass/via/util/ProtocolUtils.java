package bypass.via.util;

import bypass.via.providers.NativeVersionProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Arrays;
import java.util.stream.Stream;

public class ProtocolUtils {
   private static final long a = 138868623342436L;
   private static final long b = -3112017149586046928L;

   public static boolean isSupported(int var0) {
      return isSupported(var0, Via.getManager().getProviders().get(NativeVersionProvider.class).getServerProtocolVersion());
   }

   public static boolean isSupported(int var0, int var1) {
      return var0 == var1 || Via.getManager().getProtocolManager().getProtocolPath(var1, var0) != null;
   }

   public static String getProtocolName(int var0) {
      return !ProtocolVersion.isRegistered(var0) ? Integer.toString(var0) : ProtocolVersion.getProtocol(var0).getName();
   }

   public static boolean isStartOfProtocolText(String var0) {
      try {
         Integer.parseInt(var0);
         return true;
      } catch (NumberFormatException var6) {
         try {
            Integer.parseInt(var0 + (int)b);
            return true;
         } catch (NumberFormatException var5) {
            return ProtocolVersion.getClosest(var0) != null
               ? true
               : ProtocolVersion.getProtocols()
                  .stream()
                  .map(ProtocolVersion::getName)
                  .flatMap(var0x -> Stream.concat(Arrays.stream(var0x.split("-")), Arrays.stream(new String[]{var0x})))
                  .anyMatch(var1 -> var1.startsWith(var0));
         }
      }
   }

   public static Integer parseProtocolId(String var0) {
      try {
         return Integer.parseInt(var0);
      } catch (NumberFormatException var6) {
         ProtocolVersion var5 = ProtocolVersion.getClosest(var0);
         return var5 == null ? null : var5.getVersion();
      }
   }

   public static String[] getProtocolSuggestions(String var0) {
      return ProtocolVersion.getProtocols()
         .stream()
         .map(ProtocolVersion::getName)
         .flatMap(var0x -> Stream.concat(Arrays.stream(var0x.split("-")), Arrays.stream(new String[]{var0x})))
         .distinct()
         .filter(var1 -> var1.startsWith(var0))
         .toArray(String[]::new);
   }
}
