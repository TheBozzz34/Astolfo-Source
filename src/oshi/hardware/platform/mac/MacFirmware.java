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
      String string = null;
      String string2 = null;
      String string3 = null;
      String string4 = null;
      String string5 = null;
      IOService ioService = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
      if (ioService != null) {
         IOIterator ioIterator = ioService.getChildIterator("IODeviceTree");
         if (ioIterator != null) {
            for (IORegistryEntry ioRegistryEntry = ioIterator.next(); ioRegistryEntry != null; ioRegistryEntry = ioIterator.next()) {
               switch (ioRegistryEntry.getName()) {
                  case "rom":
                     byte[] bytes = ioRegistryEntry.getByteArrayProperty("vendor");
                     if (bytes != null) {
                        string = new String(bytes, StandardCharsets.UTF_8).trim();
                     }

                     bytes = ioRegistryEntry.getByteArrayProperty("version");
                     if (bytes != null) {
                        string4 = new String(bytes, StandardCharsets.UTF_8).trim();
                     }

                     bytes = ioRegistryEntry.getByteArrayProperty("release-date");
                     if (bytes != null) {
                        string5 = new String(bytes, StandardCharsets.UTF_8).trim();
                     }
                     break;
                  case "chosen":
                     byte[] bytes2 = ioRegistryEntry.getByteArrayProperty("booter-name");
                     if (bytes2 != null) {
                        string2 = new String(bytes2, StandardCharsets.UTF_8).trim();
                     }
                     break;
                  case "efi":
                     byte[] bytes3 = ioRegistryEntry.getByteArrayProperty("firmware-abi");
                     if (bytes3 != null) {
                        string3 = new String(bytes3, StandardCharsets.UTF_8).trim();
                     }
               }

               ioRegistryEntry.release();
            }

            ioIterator.release();
         }

         ioService.release();
      }

      return new Quintet<>(
         Util.isBlank(string) ? "unknown" : string,
         Util.isBlank(string2) ? "unknown" : string2,
         Util.isBlank(string3) ? "unknown" : string3,
         Util.isBlank(string4) ? "unknown" : string4,
         Util.isBlank(string5) ? "unknown" : string5
      );
   }
}
