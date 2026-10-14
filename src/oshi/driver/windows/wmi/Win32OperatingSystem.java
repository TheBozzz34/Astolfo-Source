package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32OperatingSystem {
   private static final long a = 84973023934958L;

   private Win32OperatingSystem() {
   }

   public static WmiResult<Win32OperatingSystem.OSVersionProperty> queryOsVersion() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_OperatingSystem", Win32OperatingSystem.OSVersionProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum OSVersionProperty {
      VERSION,
      PRODUCTTYPE,
      BUILDNUMBER,
      CSDVERSION,
      SUITEMASK;
   }
}
