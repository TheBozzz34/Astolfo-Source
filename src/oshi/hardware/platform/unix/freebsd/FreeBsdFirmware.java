package oshi.hardware.platform.unix.freebsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.tuples.Triplet;

@Immutable
public final class FreeBsdFirmware extends AbstractFirmware {
   private final Supplier<Triplet<String, String, String>> manufVersRelease = Memoizer.memoize(FreeBsdFirmware::readDmiDecode);

   @Override
   public String getManufacturer() {
      return this.manufVersRelease.get().v();
   }

   @Override
   public String getVersion() {
      return this.manufVersRelease.get().q();
   }

   @Override
   public String getReleaseDate() {
      return this.manufVersRelease.get().getC();
   }

   private static Triplet<String, String, String> readDmiDecode() {
      String string = null;
      String string2 = null;
      String string3 = "";

      for (String string4 : ExecutingCommand.runNative("dmidecode -t bios")) {
         if (string4.contains("Vendor:")) {
            string = string4.split("Vendor:")[1].trim();
         } else if (string4.contains("Version:")) {
            string2 = string4.split("Version:")[1].trim();
         } else if (string4.contains("Release Date:")) {
            string3 = string4.split("Release Date:")[1].trim();
         }
      }

      string3 = ParseUtil.parseMmDdYyyyToYyyyMmDD(string3);
      return new Triplet<>(Util.isBlank(string) ? "unknown" : string, Util.isBlank(string2) ? "unknown" : string2, Util.isBlank(string3) ? "unknown" : string3);
   }
}
