package oshi.software.os.linux;

import java.io.File;
import java.util.List;
import oshi.software.common.AbstractOSVersionInfoEx;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@Deprecated
public class LinuxOSVersionInfoEx extends AbstractOSVersionInfoEx {
   private static final long b = 29451416051243L;

   public LinuxOSVersionInfoEx() {
      this(null, null);
   }

   public LinuxOSVersionInfoEx(String var1, String var2) {
      this.n(var1);
      this.I(var2);
      if (this.getVersion() == null) {
         this.setVersionFromReleaseFiles();
      }

      if (this.getCodeName() == null) {
         this.I("");
      }

      List var6 = null;
      var6 = FileUtil.readFile(ProcPath.VERSION);
      if (!var6.isEmpty()) {
         String[] var7 = ParseUtil.Q.split((CharSequence)var6.get(0));

         for (String var11 : var7) {
            if (!"Linux".equals(var11) && !"version".equals(var11)) {
               this.C(var11);
               return;
            }
         }
      }

      this.C("");
   }

   private void setVersionFromReleaseFiles() {
      if (!this.readDistribRelease("/etc/system-release")) {
         if (!this.readOsRelease()) {
            if (!this.execLsbRelease()) {
               if (!this.readLsbRelease()) {
                  String var4 = LinuxOperatingSystem.getReleaseFilename();
                  if (!this.readDistribRelease(var4)) {
                     if (this.getVersion() == null) {
                        this.n(System.getProperty("os.version"));
                     }
                  }
               }
            }
         }
      }
   }

   private boolean readOsRelease() {
      if (new File("/etc/os-release").exists()) {
         for (String var6 : FileUtil.readFile("/etc/os-release")) {
            if (var6.startsWith("VERSION=")) {
               var6 = ParseUtil.getDoubleQuoteStringValue(var6).trim();
               String[] var7 = var6.split("[()]");
               if (var7.length <= 1) {
                  var7 = var6.split(", ");
               }

               if (var7.length > 0) {
                  this.version = var7[0].trim();
               }

               if (var7.length > 1) {
                  this.codeName = var7[1].trim();
               }
            } else if (var6.startsWith("VERSION_ID=") && this.version == null) {
               this.version = ParseUtil.getDoubleQuoteStringValue(var6).trim();
            }
         }
      }

      return this.version != null;
   }

   private boolean execLsbRelease() {
      for (String var5 : ExecutingCommand.runNative("lsb_release -a")) {
         if (var5.startsWith("Description:")) {
            var5 = var5.replace("Description:", "").trim();
            if (var5.contains(" release ")) {
               this.version = this.parseRelease(var5, " release ");
            }
         } else if (var5.startsWith("Release:") && this.version == null) {
            this.version = var5.replace("Release:", "").trim();
         } else if (var5.startsWith("Codename:") && this.codeName == null) {
            this.codeName = var5.replace("Codename:", "").trim();
         }
      }

      return this.version != null;
   }

   private boolean readLsbRelease() {
      if (new File("/etc/lsb-release").exists()) {
         for (String var6 : FileUtil.readFile("/etc/lsb-release")) {
            if (var6.startsWith("DISTRIB_DESCRIPTION=")) {
               var6 = ParseUtil.getDoubleQuoteStringValue(var6).trim();
               if (var6.contains(" release ")) {
                  this.version = this.parseRelease(var6, " release ");
               }
            } else if (var6.startsWith("DISTRIB_RELEASE=") && this.version == null) {
               this.version = ParseUtil.getDoubleQuoteStringValue(var6).trim();
            } else if (var6.startsWith("DISTRIB_CODENAME=") && this.codeName == null) {
               this.codeName = ParseUtil.getDoubleQuoteStringValue(var6).trim();
            }
         }
      }

      return this.version != null;
   }

   private boolean readDistribRelease(String var1) {
      if (new File(var1).exists()) {
         for (String var7 : FileUtil.readFile(var1)) {
            if (var7.contains(" release ")) {
               this.version = this.parseRelease(var7, " release ");
               break;
            }

            if (var7.contains(" VERSION ")) {
               this.version = this.parseRelease(var7, " VERSION ");
               break;
            }
         }
      }

      return this.version != null;
   }

   private String parseRelease(String var1, String var2) {
      String[] var6 = var1.split(var2);
      if (var6.length > 1) {
         var6 = var6[1].split("[()]");
         if (var6.length > 0) {
            this.version = var6[0].trim();
         }

         if (var6.length > 1) {
            this.codeName = var6[1].trim();
         }
      }

      return this.version;
   }
}
