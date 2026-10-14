package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32VideoController {
   private Win32VideoController() {
   }

   public static WmiResult<Win32VideoController.VideoControllerProperty> queryVideoController() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_VideoController", Win32VideoController.VideoControllerProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum VideoControllerProperty {
      ADAPTERCOMPATIBILITY,
      ADAPTERRAM,
      DRIVERVERSION,
      NAME,
      PNPDEVICEID;
   }
}
