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

   public static WmiResult<OhmSensor.ValueProperty> querySensorValue(String identifier, String sensorType) {
      StringBuilder stringBuilder = new StringBuilder("Sensor");
      stringBuilder.append(" WHERE Parent = \"").append(identifier);
      stringBuilder.append("\" AND SensorType=\"").append(sensorType).append((char)((int)e));
      WmiQuery wmiQuery = new WmiQuery<>("ROOT\\OpenHardwareMonitor", stringBuilder.toString(), OhmSensor.ValueProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum ValueProperty {
      VALUE;
   }
}
