package oshi.hardware.platform.unix.solaris;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;

@Immutable
public final class SolarisFirmware extends AbstractFirmware {
   private final String manufacturer;
   private final String version;
   private final String releaseDate;

   public SolarisFirmware(String var1, String var2, String var3) {
      this.manufacturer = var1;
      this.version = var2;
      this.releaseDate = var3;
   }

   @Override
   public String R() {
      return this.manufacturer;
   }

   @Override
   public String P() {
      return this.version;
   }

   @Override
   public String getReleaseDate() {
      return this.releaseDate;
   }
}
