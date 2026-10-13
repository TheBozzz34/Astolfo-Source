package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class MSAcpiThermalZoneTemperature {
   public static final String a = "ROOT\\WMI";
   private static final long b = 57846900369914L;

   private MSAcpiThermalZoneTemperature() {
   }

   public static WmiResult<MSAcpiThermalZoneTemperature.TemperatureProperty> queryCurrentTemperature() {
      WmiQuery var2 = new WmiQuery<>("ROOT\\WMI", "MSAcpi_ThermalZoneTemperature", MSAcpiThermalZoneTemperature.TemperatureProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var2);
   }

   public enum TemperatureProperty {
      CURRENTTEMPERATURE;
   }
}
