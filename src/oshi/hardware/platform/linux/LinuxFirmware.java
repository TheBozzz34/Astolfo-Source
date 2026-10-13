package oshi.hardware.platform.linux;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;

@Immutable
public final class LinuxFirmware extends AbstractFirmware {
   private static final DateTimeFormatter VCGEN_FORMATTER = DateTimeFormatter.ofPattern("MMM d uuuu HH:mm:ss", Locale.ENGLISH);
   private final Supplier<String> manufacturer = Memoizer.memoize(this::r);
   private final Supplier<String> description = Memoizer.memoize(this::j);
   private final Supplier<String> version = Memoizer.memoize(this::g);
   private final Supplier<String> releaseDate = Memoizer.memoize(this::v);
   private final Supplier<String> name = Memoizer.memoize(this::queryName);
   private final Supplier<LinuxFirmware.VcGenCmdStrings> vcGenCmd = Memoizer.memoize(this::queryVcGenCmd);
   private final Supplier<LinuxFirmware.BiosStrings> bios = Memoizer.memoize(this::queryBios);
   private static final long b = 13145740860257L;

   @Override
   public String R() {
      return this.manufacturer.get();
   }

   @Override
   public String getDescription() {
      return this.description.get();
   }

   @Override
   public String P() {
      return this.version.get();
   }

   @Override
   public String getReleaseDate() {
      return this.releaseDate.get();
   }

   @Override
   public String getName() {
      return this.name.get();
   }

   private String r() {
      String var4 = null;
      return (var4 = queryManufacturerFromSysfs()) == null && (var4 = LinuxFirmware.VcGenCmdStrings.Z(this.vcGenCmd.get())) == null ? "unknown" : var4;
   }

   private String j() {
      String var4 = null;
      return (var4 = queryDescriptionFromSysfs()) == null && (var4 = LinuxFirmware.VcGenCmdStrings.Q(this.vcGenCmd.get())) == null ? "unknown" : var4;
   }

   private String g() {
      String var4 = null;
      return (var4 = this.queryVersionFromSysfs()) == null && (var4 = LinuxFirmware.VcGenCmdStrings.y(this.vcGenCmd.get())) == null ? "unknown" : var4;
   }

   private String v() {
      String var4 = null;
      return (var4 = queryReleaseDateFromSysfs()) == null && (var4 = LinuxFirmware.VcGenCmdStrings.w(this.vcGenCmd.get())) == null ? "unknown" : var4;
   }

   private String queryName() {
      String var4 = null;
      return (var4 = LinuxFirmware.BiosStrings.g(this.bios.get())) == null && (var4 = LinuxFirmware.VcGenCmdStrings.k(this.vcGenCmd.get())) == null
         ? "unknown"
         : var4;
   }

   private static String queryManufacturerFromSysfs() {
      String var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_vendor").trim();
      return var3.isEmpty() ? var3 : null;
   }

   private static String queryDescriptionFromSysfs() {
      String var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/modalias").trim();
      return !var3.isEmpty() ? var3 : null;
   }

   private String queryVersionFromSysfs() {
      String var4 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_version").trim();
      if (!var4.isEmpty()) {
         String var5 = LinuxFirmware.BiosStrings.J(this.bios.get());
         return var4 + (Util.isBlank(var5) ? "" : " (revision " + var5 + ")");
      } else {
         return null;
      }
   }

   private static String queryReleaseDateFromSysfs() {
      String var3 = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/bios_date").trim();
      return !var3.isEmpty() ? ParseUtil.parseMmDdYyyyToYyyyMmDD(var3) : null;
   }

   private LinuxFirmware.BiosStrings queryBios() {
      String var4 = null;
      String var5 = null;

      for (String var9 : ExecutingCommand.runNative("dmidecode -t bios")) {
         if (var9.contains("SMBIOS")) {
            String[] var10 = ParseUtil.Q.split(var9);
            if (var10.length >= 2) {
               var4 = var10[0] + " " + var10[1];
            }
         }

         if (var9.contains("Bios Revision:")) {
            var5 = var9.split("Bios Revision:")[1].trim();
            break;
         }
      }

      return new LinuxFirmware.BiosStrings(var4, var5);
   }

   private LinuxFirmware.VcGenCmdStrings queryVcGenCmd() {
      String var3 = null;
      String var4 = null;
      String var5 = null;
      List var6 = ExecutingCommand.runNative("vcgencmd version");
      if (var6.size() >= 3) {
         try {
            var3 = DateTimeFormatter.ISO_LOCAL_DATE.format(VCGEN_FORMATTER.parse((CharSequence)var6.get(0)));
         } catch (DateTimeParseException var8) {
            var3 = "unknown";
         }

         String[] var7 = ParseUtil.Q.split((CharSequence)var6.get(1));
         var4 = var7[var7.length - 1];
         var5 = ((String)var6.get(2)).replace("version ", "");
         return new LinuxFirmware.VcGenCmdStrings(var3, var4, var5, "RPi", "Bootloader");
      } else {
         return new LinuxFirmware.VcGenCmdStrings(null, null, null, null, null);
      }
   }

   private static final class BiosStrings {
      private final String biosName;
      private final String biosRevision;

      public BiosStrings(String var1, String var2) {
         this.biosName = var1;
         this.biosRevision = var2;
      }

      public static String g(LinuxFirmware.BiosStrings var0) {
         return var0.biosName;
      }

      public static String J(LinuxFirmware.BiosStrings var0) {
         return var0.biosRevision;
      }
   }

   private static final class VcGenCmdStrings {
      private final String releaseDate;
      private final String manufacturer;
      private final String version;
      private final String name;
      private final String description;

      public VcGenCmdStrings(String var1, String var2, String var3, String var4, String var5) {
         this.releaseDate = var1;
         this.manufacturer = var2;
         this.version = var3;
         this.name = var4;
         this.description = var5;
      }

      public static String Z(LinuxFirmware.VcGenCmdStrings var0) {
         return var0.manufacturer;
      }

      public static String Q(LinuxFirmware.VcGenCmdStrings var0) {
         return var0.description;
      }

      public static String y(LinuxFirmware.VcGenCmdStrings var0) {
         return var0.version;
      }

      public static String w(LinuxFirmware.VcGenCmdStrings var0) {
         return var0.releaseDate;
      }

      public static String k(LinuxFirmware.VcGenCmdStrings var0) {
         return var0.name;
      }
   }
}
