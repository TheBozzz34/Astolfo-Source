package oshi.hardware.platform.unix.freebsd;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractBaseboard;

@Immutable
public final class FreeBsdBaseboard extends AbstractBaseboard {
   private final String manufacturer;
   private final String model;
   private final String serialNumber;
   private final String version;

   public FreeBsdBaseboard(String manufacturer, String model, String serialNumber, String version) {
      this.manufacturer = manufacturer;
      this.model = model;
      this.serialNumber = serialNumber;
      this.version = version;
   }

   @Override
   public String getManufacturer() {
      return this.manufacturer;
   }

   @Override
   public String getModel() {
      return this.model;
   }

   @Override
   public String getSerialNumber() {
      return this.serialNumber;
   }

   @Override
   public String getVersion() {
      return this.version;
   }
}
