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
      WmiResult wmiResult = Win32OperatingSystem.queryOsVersion();
      if (wmiResult.getResultCount() < 1) {
         this.handleNoVersionInfo();
      } else {
         int i = WmiUtil.getUint32(wmiResult, Win32OperatingSystem.OSVersionProperty.SUITEMASK, 0);
         this.C(WmiUtil.getString(wmiResult, Win32OperatingSystem.OSVersionProperty.BUILDNUMBER, 0));
         this.n(this.parseVersion(wmiResult, i));
         this.I(this.parseCodeName(i));
      }
   }

   public void handleNoVersionInfo() {
      this.n(System.getProperty("os.version"));
      this.I("");
      this.C("");
   }

   private String parseVersion(WmiResult<Win32OperatingSystem.OSVersionProperty> versionInfo, int suiteMask) {
      String string = System.getProperty("os.version");
      String[] strings = WmiUtil.getString(versionInfo, Win32OperatingSystem.OSVersionProperty.VERSION, 0).split("\\D");
      int i = strings.length > 0 ? ParseUtil.parseIntOrDefault(strings[0], 0) : 0;
      int j = strings.length > 1 ? ParseUtil.parseIntOrDefault(strings[1], 0) : 0;
      boolean bl = WmiUtil.getUint32(versionInfo, Win32OperatingSystem.OSVersionProperty.PRODUCTTYPE, 0) == 1;
      switch (i) {
         case 5:
            if (j == 2) {
               if ((suiteMask & 32768) != 0) {
                  string = "Home Server";
               } else if (bl) {
                  string = "XP";
               } else {
                  string = User32.INSTANCE.GetSystemMetrics(89) != 0 ? "Server 2003" : "Server 2003 R2";
               }
            } else if (j == 1) {
               string = "XP";
            } else if (j == 0) {
               string = "2000";
            }
            break;
         case 6:
            if (j == 3) {
               string = bl ? "8.1" : "Server 2012 R2";
            } else if (j == 2) {
               string = bl ? "8" : "Server 2012";
            } else if (j == 1) {
               string = bl ? "7" : "Server 2008 R2";
            } else if (j == 0) {
               string = bl ? "Vista" : "Server 2008";
            }
            break;
         case 10:
            if (j == 0) {
               if (bl) {
                  string = "10";
               } else {
                  string = ParseUtil.parseLongOrDefault(this.getBuildNumber(), 0L) > 17762L ? "Server 2019" : "Server 2016";
               }
            }
      }

      String string2 = WmiUtil.getString(versionInfo, Win32OperatingSystem.OSVersionProperty.CSDVERSION, 0);
      if (!string2.isEmpty() && !"unknown".equals(string2)) {
         string = string + " " + string2.replace("Service Pack ", "SP");
      }

      return string;
   }

   private String parseCodeName(int suiteMask) {
      ArrayList arrayList = new ArrayList();
      if ((suiteMask & 2) != 0) {
         arrayList.add("Enterprise");
      }

      if ((suiteMask & 4) != 0) {
         arrayList.add("BackOffice");
      }

      if ((suiteMask & 8) != 0) {
         arrayList.add("Communication Server");
      }

      if ((suiteMask & 128) != 0) {
         arrayList.add("Datacenter");
      }

      if ((suiteMask & 512) != 0) {
         arrayList.add("Home");
      }

      if ((suiteMask & 1024) != 0) {
         arrayList.add("Web Server");
      }

      if ((suiteMask & 8192) != 0) {
         arrayList.add("Storage Server");
      }

      if ((suiteMask & 16384) != 0) {
         arrayList.add("Compute Cluster");
      }

      return String.join(",", arrayList);
   }
}
