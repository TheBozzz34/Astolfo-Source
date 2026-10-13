package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class OhmHardware {
   private static final long a = 11453541632776L;
   private static final long e = 2903468514243248162L;

   private OhmHardware() {
   }

   public static WmiResult<OhmHardware.IdentifierProperty> queryHwIdentifier(String var0, String var1) {
      StringBuilder var5 = new StringBuilder("Hardware");
      var5.append(" WHERE ").append(var0).append("Type=\"").append(var1).append((char)((int)e));
      WmiQuery var6 = new WmiQuery<>("ROOT\\OpenHardwareMonitor", var5.toString(), OhmHardware.IdentifierProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var6);
   }

   public enum IdentifierProperty {
      IDENTIFIER;
   }
}
