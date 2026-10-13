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
   private static final long b = 39155936217617L;

   @Override
   public String V() {
      return this.manufModelSerialVers.get().getA();
   }

   @Override
   public String z() {
      return this.manufModelSerialVers.get().getB();
   }

   @Override
   public String k() {
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
      String var3 = null;
      String var4 = null;
      String var5 = null;
      String var6 = null;

      for (String var12 : ExecutingCommand.runNative("dmidecode -t system")) {
         if (var12.contains("Manufacturer:")) {
            var3 = var12.split("Manufacturer:")[1].trim();
         } else if (var12.contains("Product Name:")) {
            var4 = var12.split("Product Name:")[1].trim();
         } else if (var12.contains("Serial Number:")) {
            var5 = var12.split("Serial Number:")[1].trim();
         } else if (var12.contains("Version:")) {
            var6 = var12.split("Version:")[1].trim();
         }
      }

      if (Util.isBlank(var5)) {
         var5 = querySystemSerialNumber();
      }

      return new Quartet<>(
         Util.isBlank(var3) ? "unknown" : var3,
         Util.isBlank(var4) ? "unknown" : var4,
         Util.isBlank(var5) ? "unknown" : var5,
         Util.isBlank(var6) ? "unknown" : var6
      );
   }

   private static String querySystemSerialNumber() {
      String var3 = "system.hardware.serial =";

      for (String var5 : ExecutingCommand.runNative("lshal")) {
         if (var5.contains(var3)) {
            return ParseUtil.getSingleQuoteStringValue(var5);
         }
      }

      return "unknown";
   }
}
