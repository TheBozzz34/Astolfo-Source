package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32USBController {
   private static final long a = 1895703077222L;

   private Win32USBController() {
   }

   public static WmiResult<Win32USBController.USBControllerProperty> queryUSBControllers() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_USBController", Win32USBController.USBControllerProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum USBControllerProperty {
      PNPDEVICEID;
   }
}
