package oshi.hardware.platform.unix.freebsd;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractBaseboard;

@Immutable
public final class FreeBsdBaseboard extends AbstractBaseboard {
   private final String manufacturer;
   private final String model;
   private final String serialNumber;
   private final String version;

   public FreeBsdBaseboard(String var1, String var2, String var3, String var4) {
      this.manufacturer = var1;
      this.model = var2;
      this.serialNumber = var3;
      this.version = var4;
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
