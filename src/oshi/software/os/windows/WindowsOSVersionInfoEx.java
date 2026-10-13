package oshi.software.os.windows;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import java.util.ArrayList;
import oshi.driver.windows.wmi.Win32OperatingSystem;
import oshi.software.common.AbstractOSVersionInfoEx;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;

@Deprecated
public class WindowsOSVersionInfoEx extends AbstractOSVersionInfoEx {
   private static final long b = 34204388604567L;

   public WindowsOSVersionInfoEx() {
      WmiResult var4 = Win32OperatingSystem.queryOsVersion();
      if (var4.getResultCount() < 1) {
         this.handleNoVersionInfo();
      } else {
         int var5 = WmiUtil.getUint32(var4, Win32OperatingSystem.OSVersionProperty.SUITEMASK, 0);
         this.C(WmiUtil.getString(var4, Win32OperatingSystem.OSVersionProperty.BUILDNUMBER, 0));
         this.n(this.parseVersion(var4, var5));
         this.I(this.parseCodeName(var5));
      }
   }

   public void handleNoVersionInfo() {
      this.n(System.getProperty("os.version"));
      this.I("");
      this.C("");
   }

   private String parseVersion(WmiResult<Win32OperatingSystem.OSVersionProperty> var1, int var2) {
      String var6 = System.getProperty("os.version");
      String[] var7 = WmiUtil.getString(var1, Win32OperatingSystem.OSVersionProperty.VERSION, 0).split("\\D");
      int var8 = var7.length > 0 ? ParseUtil.parseIntOrDefault(var7[0], 0) : 0;
      int var9 = var7.length > 1 ? ParseUtil.parseIntOrDefault(var7[1], 0) : 0;
      boolean var10 = WmiUtil.getUint32(var1, Win32OperatingSystem.OSVersionProperty.PRODUCTTYPE, 0) == 1;
      switch (var8) {
         case 5:
            if (var9 == 2) {
               if ((var2 & 32768) != 0) {
                  var6 = "Home Server";
               } else if (var10) {
                  var6 = "XP";
               } else {
                  var6 = User32.INSTANCE.GetSystemMetrics(89) != 0 ? "Server 2003" : "Server 2003 R2";
               }
            } else if (var9 == 1) {
               var6 = "XP";
            } else if (var9 == 0) {
               var6 = "2000";
            }
            break;
         case 6:
            if (var9 == 3) {
               var6 = var10 ? "8.1" : "Server 2012 R2";
            } else if (var9 == 2) {
               var6 = var10 ? "8" : "Server 2012";
            } else if (var9 == 1) {
               var6 = var10 ? "7" : "Server 2008 R2";
            } else if (var9 == 0) {
               var6 = var10 ? "Vista" : "Server 2008";
            }
            break;
         case 10:
            if (var9 == 0) {
               if (var10) {
                  var6 = "10";
               } else {
                  var6 = ParseUtil.parseLongOrDefault(this.getBuildNumber(), 0L) > 17762L ? "Server 2019" : "Server 2016";
               }
            }
      }

      String var11 = WmiUtil.getString(var1, Win32OperatingSystem.OSVersionProperty.CSDVERSION, 0);
      if (!var11.isEmpty() && !"unknown".equals(var11)) {
         var6 = var6 + " " + var11.replace("Service Pack ", "SP");
      }

      return var6;
   }

   private String parseCodeName(int var1) {
      ArrayList var5 = new ArrayList();
      if ((var1 & 2) != 0) {
         var5.add("Enterprise");
      }

      if ((var1 & 4) != 0) {
         var5.add("BackOffice");
      }

      if ((var1 & 8) != 0) {
         var5.add("Communication Server");
      }

      if ((var1 & 128) != 0) {
         var5.add("Datacenter");
      }

      if ((var1 & 512) != 0) {
         var5.add("Home");
      }

      if ((var1 & 1024) != 0) {
         var5.add("Web Server");
      }

      if ((var1 & 8192) != 0) {
         var5.add("Storage Server");
      }

      if ((var1 & 16384) != 0) {
         var5.add("Compute Cluster");
      }

      return String.join(",", var5);
   }
}
