package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.wmi.MSAcpiThermalZoneTemperature;
import oshi.driver.windows.wmi.OhmHardware;
import oshi.driver.windows.wmi.OhmSensor;
import oshi.driver.windows.wmi.Win32Fan;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.hardware.common.AbstractSensors;
import oshi.util.platform.windows.WmiUtil;

@ThreadSafe
public final class WindowsSensors extends AbstractSensors {
   @Override
   public double queryCpuTemperature() {
      double d = getTempFromOHM();
      return d > 0.0 ? d : getTempFromWMI();
   }

   private static double getTempFromOHM() {
      WmiResult wmiResult = OhmHardware.queryHwIdentifier("Hardware", "CPU");
      if (wmiResult.getResultCount() > 0) {
         String string = WmiUtil.getString(wmiResult, OhmHardware.IdentifierProperty.IDENTIFIER, 0);
         if (string.length() > 0) {
            WmiResult wmiResult2 = OhmSensor.querySensorValue(string, "Temperature");
            if (wmiResult2.getResultCount() > 0) {
               double d = 0.0;

               for (int i = 0; i < wmiResult2.getResultCount(); i++) {
                  d += WmiUtil.getFloat(wmiResult2, OhmSensor.ValueProperty.VALUE, i);
               }

               return d / wmiResult2.getResultCount();
            }
         }
      }

      return 0.0;
   }

   private static double getTempFromWMI() {
      double d = 0.0;
      long l = 0L;
      WmiResult wmiResult = MSAcpiThermalZoneTemperature.queryCurrentTemperature();
      if (wmiResult.getResultCount() > 0) {
         l = WmiUtil.getUint32asLong(wmiResult, MSAcpiThermalZoneTemperature.TemperatureProperty.CURRENTTEMPERATURE, 0);
      }

      if (l > 2732L) {
         d = l / 10.0 - 273.15;
      } else if (l > 274L) {
         d = l - 273.0;
      }

      return d < 0.0 ? 0.0 : d;
   }

   @Override
   public int[] queryFanSpeeds() {
      int[] ints = getFansFromOHM();
      if (ints.length > 0) {
         return ints;
      }

      ints = getFansFromWMI();
      return ints.length > 0 ? ints : new int[0];
   }

   private static int[] getFansFromOHM() {
      WmiResult wmiResult = OhmHardware.queryHwIdentifier("Hardware", "CPU");
      if (wmiResult.getResultCount() > 0) {
         String string = WmiUtil.getString(wmiResult, OhmHardware.IdentifierProperty.IDENTIFIER, 0);
         if (string.length() > 0) {
            WmiResult wmiResult2 = OhmSensor.querySensorValue(string, "Fan");
            if (wmiResult2.getResultCount() > 0) {
               int[] ints = new int[wmiResult2.getResultCount()];

               for (int i = 0; i < wmiResult2.getResultCount(); i++) {
                  ints[i] = (int)WmiUtil.getFloat(wmiResult2, OhmSensor.ValueProperty.VALUE, i);
               }

               return ints;
            }
         }
      }

      return new int[0];
   }

   private static int[] getFansFromWMI() {
      WmiResult wmiResult = Win32Fan.querySpeed();
      if (wmiResult.getResultCount() <= 1) {
         return new int[0];
      }

      int[] ints = new int[wmiResult.getResultCount()];

      for (int i = 0; i < wmiResult.getResultCount(); i++) {
         ints[i] = (int)WmiUtil.getUint64(wmiResult, Win32Fan.SpeedProperty.DESIREDSPEED, i);
      }

      return ints;
   }

   @Override
   public double queryCpuVoltage() {
      double d = getVoltsFromOHM();
      return d > 0.0 ? d : getVoltsFromWMI();
   }

   private static double getVoltsFromOHM() {
      WmiResult wmiResult = OhmHardware.queryHwIdentifier("Sensor", "Voltage");
      if (wmiResult.getResultCount() > 0) {
         String string = null;

         for (int i = 0; i < wmiResult.getResultCount(); i++) {
            String string2 = WmiUtil.getString(wmiResult, OhmHardware.IdentifierProperty.IDENTIFIER, i);
            if (string2.toLowerCase().contains("cpu")) {
               string = string2;
               break;
            }
         }

         if (string == null) {
            string = WmiUtil.getString(wmiResult, OhmHardware.IdentifierProperty.IDENTIFIER, 0);
         }

         WmiResult wmiResult2 = OhmSensor.querySensorValue(string, "Voltage");
         if (wmiResult2.getResultCount() > 0) {
            return WmiUtil.getFloat(wmiResult2, OhmSensor.ValueProperty.VALUE, 0);
         }
      }

      return 0.0;
   }

   private static double getVoltsFromWMI() {
      WmiResult wmiResult = Win32Processor.queryVoltage();
      if (wmiResult.getResultCount() > 1) {
         int i = WmiUtil.getUint16(wmiResult, Win32Processor.VoltProperty.CURRENTVOLTAGE, 0);
         if (i > 0) {
            if ((i & 128) != 0) {
               return (i & 127) / 10.0;
            }

            i = WmiUtil.getUint32(wmiResult, Win32Processor.VoltProperty.VOLTAGECAPS, 0);
            if ((i & 1) > 0) {
               return 5.0;
            }

            if ((i & 2) > 0) {
               return 3.3;
            }

            if ((i & 4) > 0) {
               return 2.9;
            }
         }
      }

      return 0.0;
   }
}
