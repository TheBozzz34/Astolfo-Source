package oshi.hardware.platform.linux;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractBaseboard;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.linux.ProcPath;

@Immutable
public final class LinuxBaseboard extends AbstractBaseboard {
   private final Supplier<String> manufacturer = Memoizer.memoize(LinuxBaseboard::E);
   private final Supplier<String> model = Memoizer.memoize(LinuxBaseboard::H);
   private final Supplier<String> version = Memoizer.memoize(LinuxBaseboard::L);
   private final Supplier<String> serialNumber = Memoizer.memoize(LinuxBaseboard::T);
   private static final long b = 48583762541910L;

   @Override
   public String p() {
      return this.manufacturer.get();
   }

   @Override
   public String g() {
      return this.model.get();
   }

   @Override
   public String W() {
      return this.version.get();
   }

   @Override
   public String A() {
      return this.serialNumber.get();
   }

   private static String E() {
      String var3 = null;
      return (var3 = queryManufacturerFromSysfs()) == null && (var3 = LinuxBaseboard.ProcCpuStrings.J(queryProcCpu())) == null ? "unknown" : var3;
   }

   private static String H() {
      String var3 = null;
      return (var3 = queryModelFromSysfs()) == null && (var3 = LinuxBaseboard.ProcCpuStrings.F(queryProcCpu())) == null ? "unknown" : var3;
   }

   private static String L() {
      String var3 = null;
      return (var3 = queryVersionFromSysfs()) == null && (var3 = LinuxBaseboard.ProcCpuStrings.q(queryProcCpu())) == null ? "unknown" : var3;
   }

   private static String T() {
      String var3 = null;
      return (var3 = querySerialFromSysfs()) == null && (var3 = LinuxBaseboard.ProcCpuStrings.j(queryProcCpu())) == null ? "unknown" : var3;
   }

   private static String queryManufacturerFromSysfs() {
      String var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_vendor").trim();
      return !var3.isEmpty() ? var3 : null;
   }

   private static String queryModelFromSysfs() {
      String var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_name").trim();
      return !var3.isEmpty() ? var3 : null;
   }

   private static String queryVersionFromSysfs() {
      String var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_version").trim();
      return !var3.isEmpty() ? var3 : null;
   }

   private static String querySerialFromSysfs() {
      String var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_serial").trim();
      return !var3.isEmpty() ? var3 : null;
   }

   private static LinuxBaseboard.ProcCpuStrings queryProcCpu() {
      String var3 = null;
      String var4 = null;
      String var5 = null;
      String var6 = null;

      for (String var9 : FileUtil.readFile(ProcPath.CPUINFO)) {
         String[] var10 = ParseUtil.H.split(var9);
         if (var10.length >= 2) {
            switch (var10[0]) {
               case "Hardware":
                  var4 = var10[1];
                  break;
               case "Revision":
                  var5 = var10[1];
                  if (var5.length() > 1) {
                     var3 = queryBoardManufacturer(var5.charAt(1));
                  }
                  break;
               case "Serial":
                  var6 = var10[1];
            }
         }
      }

      return new LinuxBaseboard.ProcCpuStrings(var3, var4, var5, var6);
   }

   private static String queryBoardManufacturer(char var0) {
      switch (var0) {
         case '0':
            return "Sony UK";
         case '1':
            return "Egoman";
         case '2':
            return "Embest";
         case '3':
            return "Sony Japan";
         case '4':
            return "Embest";
         case '5':
            return "Stadium";
         default:
            return "unknown";
      }
   }

   private static final class ProcCpuStrings {
      private final String manufacturer;
      private final String model;
      private final String version;
      private final String serialNumber;
      private static final long a = 9123625676264L;

      public ProcCpuStrings(String var1, String var2, String var3, String var4) {
         this.manufacturer = Util.isBlank(var1) ? "unknown" : var1;
         this.model = Util.isBlank(var2) ? "unknown" : var2;
         this.version = Util.isBlank(var3) ? "unknown" : var3;
         this.serialNumber = Util.isBlank(var4) ? "unknown" : var4;
      }

      public static String J(LinuxBaseboard.ProcCpuStrings var0) {
         return var0.manufacturer;
      }

      public static String F(LinuxBaseboard.ProcCpuStrings var0) {
         return var0.model;
      }

      public static String q(LinuxBaseboard.ProcCpuStrings var0) {
         return var0.version;
      }

      public static String j(LinuxBaseboard.ProcCpuStrings var0) {
         return var0.serialNumber;
      }
   }
}
