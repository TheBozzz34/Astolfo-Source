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
   private static final long b = 29147138231078L;

   @Override
   public String R() {
      return this.manufVersRelease.get().v();
   }

   @Override
   public String P() {
      return this.manufVersRelease.get().q();
   }

   @Override
   public String getReleaseDate() {
      return this.manufVersRelease.get().U();
   }

   private static Triplet<String, String, String> readDmiDecode() {
      String var3 = null;
      String var4 = null;
      String var5 = "";

      for (String var10 : ExecutingCommand.runNative("dmidecode -t bios")) {
         if (var10.contains("Vendor:")) {
            var3 = var10.split("Vendor:")[1].trim();
         } else if (var10.contains("Version:")) {
            var4 = var10.split("Version:")[1].trim();
         } else if (var10.contains("Release Date:")) {
            var5 = var10.split("Release Date:")[1].trim();
         }
      }

      var5 = ParseUtil.parseMmDdYyyyToYyyyMmDD(var5);
      return new Triplet<>(Util.isBlank(var3) ? "unknown" : var3, Util.isBlank(var4) ? "unknown" : var4, Util.isBlank(var5) ? "unknown" : var5);
   }
}
