package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.IOKit.IOIterator;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.platform.mac.IOKit.IOService;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.tuples.Quintet;

@Immutable
public final class MacFirmware extends AbstractFirmware {
   private final Supplier<Quintet<String, String, String, String, String>> manufNameDescVersRelease = Memoizer.memoize(MacFirmware::queryEfi);
   private static final long b = 72274638882170L;

   @Override
   public String R() {
      return this.manufNameDescVersRelease.get().y();
   }

   @Override
   public String getName() {
      return this.manufNameDescVersRelease.get().x();
   }

   @Override
   public String getDescription() {
      return this.manufNameDescVersRelease.get().q();
   }

   @Override
   public String P() {
      return this.manufNameDescVersRelease.get().J();
   }

   @Override
   public String getReleaseDate() {
      return this.manufNameDescVersRelease.get().s();
   }

   private static Quintet<String, String, String, String, String> queryEfi() {
      String var3 = null;
      String var4 = null;
      String var5 = null;
      String var6 = null;
      String var7 = null;
      IOService var8 = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
      if (var8 != null) {
         IOIterator var9 = var8.getChildIterator("IODeviceTree");
         if (var9 != null) {
            for (IORegistryEntry var10 = var9.next(); var10 != null; var10 = var9.next()) {
               switch (var10.getName()) {
                  case "rom":
                     byte[] var15 = var10.getByteArrayProperty("vendor");
                     if (var15 != null) {
                        var3 = new String(var15, StandardCharsets.UTF_8).trim();
                     }

                     var15 = var10.getByteArrayProperty("version");
                     if (var15 != null) {
                        var6 = new String(var15, StandardCharsets.UTF_8).trim();
                     }

                     var15 = var10.getByteArrayProperty("release-date");
                     if (var15 != null) {
                        var7 = new String(var15, StandardCharsets.UTF_8).trim();
                     }
                     break;
                  case "chosen":
                     byte[] var14 = var10.getByteArrayProperty("booter-name");
                     if (var14 != null) {
                        var4 = new String(var14, StandardCharsets.UTF_8).trim();
                     }
                     break;
                  case "efi":
                     byte[] var13 = var10.getByteArrayProperty("firmware-abi");
                     if (var13 != null) {
                        var5 = new String(var13, StandardCharsets.UTF_8).trim();
                     }
               }

               var10.release();
            }

            var9.release();
         }

         var8.release();
      }

      return new Quintet<>(
         Util.isBlank(var3) ? "unknown" : var3,
         Util.isBlank(var4) ? "unknown" : var4,
         Util.isBlank(var5) ? "unknown" : var5,
         Util.isBlank(var6) ? "unknown" : var6,
         Util.isBlank(var7) ? "unknown" : var7
      );
   }
}
