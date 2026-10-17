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
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("manufacturer=").append(this.getManufacturer()).append(", ");
      stringBuilder.append("model=").append(this.getModel()).append(", ");
      stringBuilder.append("serial number=").append(this.getSerialNumber());
      return stringBuilder.toString();
   }
}
