package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.IOKit.IOService;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.Memoizer;
import oshi.util.Util;
import oshi.util.tuples.Triplet;

@Immutable
public final class MacComputerSystem extends AbstractComputerSystem {
   private final Supplier<Triplet<String, String, String>> manufacturerModelSerial = Memoizer.memoize(MacComputerSystem::platformExpert);
   private static final long b = 36838287982049L;

   @Override
   public String V() {
      return this.manufacturerModelSerial.get().v();
   }

   @Override
   public String z() {
      return this.manufacturerModelSerial.get().q();
   }

   @Override
   public String k() {
      return this.manufacturerModelSerial.get().U();
   }

   @Override
   public Firmware createFirmware() {
      return new MacFirmware();
   }

   @Override
   public Baseboard createBaseboard() {
      return new MacBaseboard();
   }

   private static Triplet<String, String, String> platformExpert() {
      String var3 = null;
      String var4 = null;
      String var5 = null;
      IOService var6 = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
      if (var6 != null) {
         byte[] var7 = var6.getByteArrayProperty("manufacturer");
         if (var7 != null) {
            var3 = new String(var7, StandardCharsets.UTF_8).trim();
         }

         var7 = var6.getByteArrayProperty("model");
         if (var7 != null) {
            var4 = new String(var7, StandardCharsets.UTF_8).trim();
         }

         var5 = var6.getStringProperty("IOPlatformSerialNumber");
         var6.release();
      }

      return new Triplet<>(Util.isBlank(var3) ? "Apple Inc." : var3, Util.isBlank(var4) ? "unknown" : var4, Util.isBlank(var5) ? "unknown" : var5);
   }
}
