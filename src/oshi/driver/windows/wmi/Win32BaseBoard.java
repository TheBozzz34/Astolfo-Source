package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32BaseBoard {
   private static final long a = 27573544625235L;

   private Win32BaseBoard() {
   }

   public static WmiResult<Win32BaseBoard.BaseBoardProperty> queryBaseboardInfo() {
      WmiQuery var2 = new WmiQuery<>("Win32_BaseBoard", Win32BaseBoard.BaseBoardProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var2);
   }

   public enum BaseBoardProperty {
      MANUFACTURER,
      MODEL,
      VERSION,
      SERIALNUMBER;
   }
}
