package oshi.hardware.common;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Firmware;
import oshi.util.Memoizer;

@Immutable
public abstract class AbstractComputerSystem implements ComputerSystem {
   private final Supplier<Firmware> firmware = Memoizer.memoize(this::createFirmware);
   private final Supplier<Baseboard> baseboard = Memoizer.memoize(this::createBaseboard);
   private static final long a = 121551975839970L;

   @Override
   public Firmware getFirmware() {
      return this.firmware.get();
   }

   public abstract Firmware createFirmware();

   @Override
   public Baseboard getBaseboard() {
      return this.baseboard.get();
   }

   public abstract Baseboard createBaseboard();

   @Override
   public String toString() {
      StringBuilder var3 = new StringBuilder();
      var3.append("manufacturer=").append(this.V()).append(", ");
      var3.append("model=").append(this.z()).append(", ");
      var3.append("serial number=").append(this.k());
      return var3.toString();
   }
}
