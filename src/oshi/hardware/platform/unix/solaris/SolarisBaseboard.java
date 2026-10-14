package oshi.hardware.platform.unix.solaris;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractBaseboard;

@Immutable
public final class SolarisBaseboard extends AbstractBaseboard {
   private final String manufacturer;
   private final String model;
   private final String serialNumber;
   private final String version;

   public SolarisBaseboard(String manufacturer, String model, String serialNumber, String version) {
      this.manufacturer = manufacturer;
      this.model = model;
      this.serialNumber = serialNumber;
      this.version = version;
   }

   @Override
   public String p() {
      return this.manufacturer;
   }

   @Override
   public String g() {
      return this.model;
   }

   @Override
   public String A() {
      return this.serialNumber;
   }

   @Override
   public String W() {
      return this.version;
   }
}
