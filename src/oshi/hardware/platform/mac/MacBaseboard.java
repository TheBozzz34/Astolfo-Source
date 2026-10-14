package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.IOKit.IOService;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.tuples.Quartet;

@Immutable
public final class MacBaseboard extends AbstractBaseboard {
   private final Supplier<Quartet<String, String, String, String>> manufModelVersSerial = Memoizer.memoize(MacBaseboard::queryPlatform);

   @Override
   public String p() {
      return this.manufModelVersSerial.get().getA();
   }

   @Override
   public String g() {
      return this.manufModelVersSerial.get().getB();
   }

   @Override
   public String W() {
      return this.manufModelVersSerial.get().getC();
   }

   @Override
   public String A() {
      return this.manufModelVersSerial.get().getD();
   }

   private static Quartet<String, String, String, String> queryPlatform() {
      String string = null;
      String string2 = null;
      String string3 = null;
      String string4 = null;
      IOService ioService = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
      if (ioService != null) {
         byte[] bytes = ioService.getByteArrayProperty("manufacturer");
         if (bytes != null) {
            string = new String(bytes, StandardCharsets.UTF_8).trim();
         }

         bytes = ioService.getByteArrayProperty("board-id");
         if (bytes != null) {
            string2 = new String(bytes, StandardCharsets.UTF_8).trim();
         }

         bytes = ioService.getByteArrayProperty("version");
         if (bytes != null) {
            string3 = new String(bytes, StandardCharsets.UTF_8).trim();
         }

         string4 = ioService.getStringProperty("IOPlatformSerialNumber");
         ioService.release();
      }

      return new Quartet<>(
         Util.isBlank(string) ? "Apple Inc." : string,
         Util.isBlank(string2) ? "unknown" : string2,
         Util.isBlank(string3) ? "unknown" : string3,
         Util.isBlank(string4) ? "unknown" : string4
      );
   }
}
