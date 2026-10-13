package oshi.hardware.platform.unix.solaris;

import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Baseboard;
import oshi.hardware.Firmware;
import oshi.hardware.common.AbstractComputerSystem;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;

@Immutable
public final class SolarisComputerSystem extends AbstractComputerSystem {
   private final Supplier<SolarisComputerSystem.SmbiosStrings> smbiosStrings = Memoizer.memoize(SolarisComputerSystem::readSmbios);
   private static final long b = 94490058673044L;

   @Override
   public String V() {
      return SolarisComputerSystem.SmbiosStrings.L(this.smbiosStrings.get());
   }

   @Override
   public String z() {
      return SolarisComputerSystem.SmbiosStrings.v(this.smbiosStrings.get());
   }

   @Override
   public String k() {
      return SolarisComputerSystem.SmbiosStrings.X(this.smbiosStrings.get());
   }

   @Override
   public Firmware createFirmware() {
      return new SolarisFirmware(
         SolarisComputerSystem.SmbiosStrings.V(this.smbiosStrings.get()),
         SolarisComputerSystem.SmbiosStrings.C(this.smbiosStrings.get()),
         SolarisComputerSystem.SmbiosStrings.P(this.smbiosStrings.get())
      );
   }

   @Override
   public Baseboard createBaseboard() {
      return new SolarisBaseboard(
         SolarisComputerSystem.SmbiosStrings.Y(this.smbiosStrings.get()),
         SolarisComputerSystem.SmbiosStrings.a(this.smbiosStrings.get()),
         SolarisComputerSystem.SmbiosStrings.r(this.smbiosStrings.get()),
         SolarisComputerSystem.SmbiosStrings.O(this.smbiosStrings.get())
      );
   }

   private static SolarisComputerSystem.SmbiosStrings readSmbios() {
      String var3 = null;
      String var4 = null;
      String var5 = null;
      String var6 = null;
      String var7 = null;
      String var8 = null;
      String var9 = null;
      String var10 = null;
      String var11 = null;
      String var12 = null;
      int var20 = -1;

      for (String var22 : ExecutingCommand.runNative("smbios")) {
         if (var22.contains("SMB_TYPE_") && (var20 = getSmbType(var22)) == Integer.MAX_VALUE) {
            break;
         }

         switch (var20) {
            case 0:
               if (var22.contains("Vendor:")) {
                  var3 = var22.split("Vendor:")[1].trim();
               } else if (var22.contains("VersionString:")) {
                  var4 = var22.split("VersionString:")[1].trim();
               } else if (var22.contains("Release Date:")) {
                  var5 = var22.split("Release Date:")[1].trim();
               }
               break;
            case 1:
               if (var22.contains("Manufacturer:")) {
                  var6 = var22.split("Manufacturer:")[1].trim();
               } else if (var22.contains("Product:")) {
                  var7 = var22.split("Product:")[1].trim();
               } else if (var22.contains("Serial Number:")) {
                  var8 = var22.split("Serial Number:")[1].trim();
               }
               break;
            case 2:
               if (var22.contains("Manufacturer:")) {
                  var9 = var22.split("Manufacturer:")[1].trim();
               } else if (var22.contains("Product:")) {
                  var10 = var22.split("Product:")[1].trim();
               } else if (var22.contains("Version:")) {
                  var11 = var22.split("Version:")[1].trim();
               } else if (var22.contains("Serial Number:")) {
                  var12 = var22.split("Serial Number:")[1].trim();
               }
         }
      }

      if (Util.isBlank(var8)) {
         var8 = readSerialNumber();
      }

      return new SolarisComputerSystem.SmbiosStrings(var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   private static int getSmbType(String var0) {
      if (var0.contains("SMB_TYPE_BIOS")) {
         return 0;
      } else if (var0.contains("SMB_TYPE_SYSTEM")) {
         return 1;
      } else {
         return var0.contains("SMB_TYPE_BASEBOARD") ? 2 : Integer.MAX_VALUE;
      }
   }

   private static String readSerialNumber() {
      String var3 = ExecutingCommand.getFirstAnswer("sneep");
      if (var3.isEmpty()) {
         String var4 = "chassis-sn:";

         for (String var6 : ExecutingCommand.runNative("prtconf -pv")) {
            if (var6.contains(var4)) {
               var3 = ParseUtil.getSingleQuoteStringValue(var6);
               break;
            }
         }
      }

      return var3;
   }

   private static final class SmbiosStrings {
      private final String biosVendor;
      private final String biosVersion;
      private final String biosDate;
      private final String manufacturer;
      private final String model;
      private final String serialNumber;
      private final String boardManufacturer;
      private final String boardModel;
      private final String boardVersion;
      private final String boardSerialNumber;
      private static final long b = 44592109150143L;

      public SmbiosStrings(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10) {
         this.biosVendor = Util.isBlank(var1) ? "unknown" : var1;
         this.biosVersion = Util.isBlank(var2) ? "unknown" : var2;
         this.biosDate = Util.isBlank(var3) ? "unknown" : var3;
         this.manufacturer = Util.isBlank(var4) ? "unknown" : var4;
         this.model = Util.isBlank(var5) ? "unknown" : var5;
         this.serialNumber = Util.isBlank(var6) ? "unknown" : var6;
         this.boardManufacturer = Util.isBlank(var7) ? "unknown" : var7;
         this.boardModel = Util.isBlank(var8) ? "unknown" : var8;
         this.boardVersion = Util.isBlank(var9) ? "unknown" : var9;
         this.boardSerialNumber = Util.isBlank(var10) ? "unknown" : var10;
      }

      public static String L(SolarisComputerSystem.SmbiosStrings var0) {
         return var0.manufacturer;
      }

      public static String v(SolarisComputerSystem.SmbiosStrings var0) {
         return var0.model;
      }

      public static String X(SolarisComputerSystem.SmbiosStrings var0) {
         return var0.serialNumber;
      }

      public static String V(SolarisComputerSystem.SmbiosStrings var0) {
         return var0.biosVendor;
      }

      public static String C(SolarisComputerSystem.SmbiosStrings var0) {
         return var0.biosVersion;
      }

      public static String P(SolarisComputerSystem.SmbiosStrings var0) {
         return var0.biosDate;
      }

      public static String Y(SolarisComputerSystem.SmbiosStrings var0) {
         return var0.boardManufacturer;
      }

      public static String a(SolarisComputerSystem.SmbiosStrings var0) {
         return var0.boardModel;
      }

      public static String r(SolarisComputerSystem.SmbiosStrings var0) {
         return var0.boardSerialNumber;
      }

      public static String O(SolarisComputerSystem.SmbiosStrings var0) {
         return var0.boardVersion;
      }
   }
}
