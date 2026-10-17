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

   @Override
   public String getManufacturer() {
      return this.manufacturerModelSerial.get().v();
   }

   @Override
   public String getModel() {
      return this.manufacturerModelSerial.get().q();
   }

   @Override
   public String getSerialNumber() {
      return this.manufacturerModelSerial.get().getC();
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
      String string = null;
      String string2 = null;
      String string3 = null;
      IOService ioService = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
      if (ioService != null) {
         byte[] bytes = ioService.getByteArrayProperty("manufacturer");
         if (bytes != null) {
            string = new String(bytes, StandardCharsets.UTF_8).trim();
         }

         bytes = ioService.getByteArrayProperty("model");
         if (bytes != null) {
            string2 = new String(bytes, StandardCharsets.UTF_8).trim();
         }

         string3 = ioService.getStringProperty("IOPlatformSerialNumber");
         ioService.release();
      }

      return new Triplet<>(Util.isBlank(string) ? "Apple Inc." : string, Util.isBlank(string2) ? "unknown" : string2, Util.isBlank(string3) ? "unknown" : string3);
   }
}
