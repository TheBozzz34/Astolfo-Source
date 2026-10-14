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

   public static WmiResult<OhmHardware.IdentifierProperty> queryHwIdentifier(String typeToQuery, String typeName) {
      StringBuilder stringBuilder = new StringBuilder("Hardware");
      stringBuilder.append(" WHERE ").append(typeToQuery).append("Type=\"").append(typeName).append((char)((int)e));
      WmiQuery wmiQuery = new WmiQuery<>("ROOT\\OpenHardwareMonitor", stringBuilder.toString(), OhmHardware.IdentifierProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum IdentifierProperty {
      IDENTIFIER;
   }
}
