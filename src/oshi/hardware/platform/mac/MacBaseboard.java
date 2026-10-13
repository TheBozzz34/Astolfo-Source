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
   private static final long b = 22582351758025L;

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
      String var3 = null;
      String var4 = null;
      String var5 = null;
      String var6 = null;
      IOService var7 = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
      if (var7 != null) {
         byte[] var8 = var7.getByteArrayProperty("manufacturer");
         if (var8 != null) {
            var3 = new String(var8, StandardCharsets.UTF_8).trim();
         }

         var8 = var7.getByteArrayProperty("board-id");
         if (var8 != null) {
            var4 = new String(var8, StandardCharsets.UTF_8).trim();
         }

         var8 = var7.getByteArrayProperty("version");
         if (var8 != null) {
            var5 = new String(var8, StandardCharsets.UTF_8).trim();
         }

         var6 = var7.getStringProperty("IOPlatformSerialNumber");
         var7.release();
      }

      return new Quartet<>(
         Util.isBlank(var3) ? "Apple Inc." : var3,
         Util.isBlank(var4) ? "unknown" : var4,
         Util.isBlank(var5) ? "unknown" : var5,
         Util.isBlank(var6) ? "unknown" : var6
      );
   }
}
