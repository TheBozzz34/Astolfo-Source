package oshi.hardware.platform.linux;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@Immutable
public final class LinuxComputerSystem extends AbstractComputerSystem {
   private final Supplier<String> manufacturer = Memoizer.memoize(LinuxComputerSystem::y);
   private final Supplier<String> model = Memoizer.memoize(LinuxComputerSystem::h);
   private final Supplier<String> serialNumber = Memoizer.memoize(LinuxComputerSystem::N);
   private static final long b = 28769594498341L;

   @Override
   public String V() {
      return this.manufacturer.get();
   }

   @Override
   public String z() {
      return this.model.get();
   }

   @Override
   public String k() {
      return this.serialNumber.get();
   }

   @Override
   public Firmware createFirmware() {
      return new LinuxFirmware();
   }

   @Override
   public Baseboard createBaseboard() {
      return new LinuxBaseboard();
   }

   private static String y() {
      String var3 = null;
      return (var3 = queryManufacturerFromSysfs()) == null && (var3 = queryManufacturerFromProcCpu()) == null ? "unknown" : var3;
   }

   private static String h() {
      String var3 = null;
      return (var3 = queryModelFromSysfs()) == null && (var3 = queryModelFromDeviceTree()) == null && (var3 = queryModelFromLshw()) == null ? "unknown" : var3;
   }

   private static String N() {
      String var3 = null;
      return (var3 = querySerialFromSysfs()) == null
            && (var3 = querySerialFromDmiDecode()) == null
            && (var3 = querySerialFromLshal()) == null
            && (var3 = querySerialFromLshw()) == null
         ? "unknown"
         : var3;
   }

   private static String queryManufacturerFromSysfs() {
      String var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/sys_vendor").trim();
      return !var3.isEmpty() ? var3 : null;
   }

   private static String queryManufacturerFromProcCpu() {
      for (String var5 : FileUtil.readFile(ProcPath.CPUINFO)) {
         if (var5.startsWith("CPU implementer")) {
            int var6 = ParseUtil.parseLastInt(var5, 0);
            switch (var6) {
               case 65:
                  return "ARM";
               case 66:
                  return "Broadcom";
               case 67:
                  return "Cavium";
               case 68:
                  return "DEC";
               case 69:
               case 70:
               case 71:
               case 72:
               case 73:
               case 74:
               case 75:
               case 76:
               case 77:
               case 79:
               case 82:
               case 84:
               case 85:
               case 87:
               case 88:
               case 89:
               case 90:
               case 91:
               case 92:
               case 93:
               case 94:
               case 95:
               case 96:
               case 97:
               case 98:
               case 99:
               case 100:
               case 101:
               case 103:
               case 104:
               default:
                  return null;
               case 78:
                  return "Nvidia";
               case 80:
                  return "APM";
               case 81:
                  return "Qualcomm";
               case 83:
                  return "Samsung";
               case 86:
                  return "Marvell";
               case 102:
                  return "Faraday";
               case 105:
                  return "Intel";
            }
         }
      }

      return null;
   }

   private static String queryModelFromSysfs() {
      String var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_name").trim();
      String var4 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_version").trim();
      if (var3.isEmpty()) {
         return !var4.isEmpty() ? var4 : null;
      } else {
         return !var4.isEmpty() && !"None".equals(var4) ? var3 + " (version: " + var4 + ")" : var3;
      }
   }

   private static String queryModelFromDeviceTree() {
      String var3 = FileUtil.getStringFromFile("/sys/firmware/devicetree/base/model");
      return !var3.isEmpty() ? var3.replace("Machine: ", "") : null;
   }

   private static String queryModelFromLshw() {
      String var3 = "product:";

      for (String var5 : ExecutingCommand.runNative("lshw -C system")) {
         if (var5.contains(var3)) {
            return var5.split(var3)[1].trim();
         }
      }

      return null;
   }

   private static String querySerialFromSysfs() {
      String var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_serial");
      if (!var3.isEmpty() && !"None".equals(var3)) {
         return null;
      }

      var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/board_serial");
      return !var3.isEmpty() && !"None".equals(var3) ? var3 : null;
   }

   private static String querySerialFromDmiDecode() {
      String var3 = "Serial Number:";

      for (String var5 : ExecutingCommand.runNative("dmidecode -t system")) {
         if (var5.contains(var3)) {
            return var5.split(var3)[1].trim();
         }
      }

      return null;
   }

   private static String querySerialFromLshal() {
      String var3 = "system.hardware.serial =";

      for (String var5 : ExecutingCommand.runNative("lshal")) {
         if (var5.contains(var3)) {
            return ParseUtil.getSingleQuoteStringValue(var5);
         }
      }

      return null;
   }

   private static String querySerialFromLshw() {
      String var3 = "serial:";

      for (String var5 : ExecutingCommand.runNative("lshw -C system")) {
         if (var5.contains(var3)) {
            return var5.split(var3)[1].trim();
         }
      }

      return null;
   }
}
