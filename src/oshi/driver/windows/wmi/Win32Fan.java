package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Fan {
   private Win32Fan() {
   }

   public static WmiResult<Win32Fan.SpeedProperty> querySpeed() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_Fan", Win32Fan.SpeedProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum SpeedProperty {
      DESIREDSPEED;
   }
}
