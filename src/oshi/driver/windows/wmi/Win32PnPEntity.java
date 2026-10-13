package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32PnPEntity {
   private static final long a = 79489787850691L;

   private Win32PnPEntity() {
   }

   public static WmiResult<Win32PnPEntity.PnPEntityProperty> queryDeviceId(String var0) {
      WmiQuery var3 = new WmiQuery<>("Win32_PnPEntity" + var0, Win32PnPEntity.PnPEntityProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var3);
   }

   public enum PnPEntityProperty {
      NAME,
      MANUFACTURER,
      PNPDEVICEID;
   }
}
