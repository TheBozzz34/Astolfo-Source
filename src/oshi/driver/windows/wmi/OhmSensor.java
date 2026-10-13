package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class OhmSensor {
   private static final long a = 18012064965644L;
   private static final long e = -6665790347198595038L;

   private OhmSensor() {
   }

   public static WmiResult<OhmSensor.ValueProperty> querySensorValue(String var0, String var1) {
      StringBuilder var6 = new StringBuilder("Sensor");
      var6.append(" WHERE Parent = \"").append(var0);
      var6.append("\" AND SensorType=\"").append(var1).append((char)((int)e));
      WmiQuery var7 = new WmiQuery<>("ROOT\\OpenHardwareMonitor", var6.toString(), OhmSensor.ValueProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var7);
   }

   public enum ValueProperty {
      VALUE;
   }
}
