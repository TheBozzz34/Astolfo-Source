package oshi.hardware.platform.unix.freebsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.tuples.Quartet;

@Immutable
public final class FreeBsdComputerSystem extends AbstractComputerSystem {
   private final Supplier<Quartet<String, String, String, String>> manufModelSerialVers = Memoizer.memoize(FreeBsdComputerSystem::readDmiDecode);

   @Override
   public String getManufacturer() {
      return this.manufModelSerialVers.get().getA();
   }

   @Override
   public String getModel() {
      return this.manufModelSerialVers.get().getB();
   }

   @Override
   public String getSerialNumber() {
      return this.manufModelSerialVers.get().getC();
   }

   @Override
   public Firmware createFirmware() {
      return new FreeBsdFirmware();
   }

   @Override
   public Baseboard createBaseboard() {
      return new FreeBsdBaseboard(
         this.manufModelSerialVers.get().getA(),
         this.manufModelSerialVers.get().getB(),
         this.manufModelSerialVers.get().getC(),
         this.manufModelSerialVers.get().getD()
      );
   }

   private static Quartet<String, String, String, String> readDmiDecode() {
      String string = null;
      String string2 = null;
      String string3 = null;
      String string4 = null;

      for (String string5 : ExecutingCommand.runNative("dmidecode -t system")) {
         if (string5.contains("Manufacturer:")) {
            string = string5.split("Manufacturer:")[1].trim();
         } else if (string5.contains("Product Name:")) {
            string2 = string5.split("Product Name:")[1].trim();
         } else if (string5.contains("Serial Number:")) {
            string3 = string5.split("Serial Number:")[1].trim();
         } else if (string5.contains("Version:")) {
            string4 = string5.split("Version:")[1].trim();
         }
      }

      if (Util.isBlank(string3)) {
         string3 = querySystemSerialNumber();
      }

      return new Quartet<>(
         Util.isBlank(string) ? "unknown" : string,
         Util.isBlank(string2) ? "unknown" : string2,
         Util.isBlank(string3) ? "unknown" : string3,
         Util.isBlank(string4) ? "unknown" : string4
      );
   }

   private static String querySystemSerialNumber() {
      String string = "system.hardware.serial =";

      for (String string2 : ExecutingCommand.runNative("lshal")) {
         if (string2.contains(string)) {
            return ParseUtil.getSingleQuoteStringValue(string2);
         }
      }

      return "unknown";
   }
}
