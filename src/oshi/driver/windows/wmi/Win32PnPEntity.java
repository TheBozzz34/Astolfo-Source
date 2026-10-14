package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32PnPEntity {
   private Win32PnPEntity() {
   }

   public static WmiResult<Win32PnPEntity.PnPEntityProperty> queryDeviceId(String whereClause) {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_PnPEntity" + whereClause, Win32PnPEntity.PnPEntityProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum PnPEntityProperty {
      NAME,
      MANUFACTURER,
      PNPDEVICEID;
   }
}
