package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32ComputerSystemProduct {
   private static final long a = 27110013703398L;

   private Win32ComputerSystemProduct() {
   }

   public static WmiResult<Win32ComputerSystemProduct.ComputerSystemProductProperty> queryIdentifyingNumber() {
      WmiQuery var2 = new WmiQuery<>("Win32_ComputerSystemProduct", Win32ComputerSystemProduct.ComputerSystemProductProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var2);
   }

   public enum ComputerSystemProductProperty {
      IDENTIFYINGNUMBER;
   }
}
