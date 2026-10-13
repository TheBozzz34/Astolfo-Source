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
   private static final long b = 33165506972273L;

   @Override
   public double queryCpuTemperature() {
      double var4 = getTempFromOHM();
      return var4 > 0.0 ? var4 : getTempFromWMI();
   }

   private static double getTempFromOHM() {
      WmiResult var3 = OhmHardware.queryHwIdentifier("Hardware", "CPU");
      if (var3.getResultCount() > 0) {
         String var4 = WmiUtil.getString(var3, OhmHardware.IdentifierProperty.IDENTIFIER, 0);
         if (var4.length() > 0) {
            WmiResult var5 = OhmSensor.querySensorValue(var4, "Temperature");
            if (var5.getResultCount() > 0) {
               double var6 = 0.0;

               for (int var8 = 0; var8 < var5.getResultCount(); var8++) {
                  var6 += WmiUtil.getFloat(var5, OhmSensor.ValueProperty.VALUE, var8);
               }

               return var6 / var5.getResultCount();
            }
         }
      }

      return 0.0;
   }

   private static double getTempFromWMI() {
      double var3 = 0.0;
      long var5 = 0L;
      WmiResult var7 = MSAcpiThermalZoneTemperature.queryCurrentTemperature();
      if (var7.getResultCount() > 0) {
         var5 = WmiUtil.getUint32asLong(var7, MSAcpiThermalZoneTemperature.TemperatureProperty.CURRENTTEMPERATURE, 0);
      }

      if (var5 > 2732L) {
         var3 = var5 / 10.0 - 273.15;
      } else if (var5 > 274L) {
         var3 = var5 - 273.0;
      }

      return var3 < 0.0 ? 0.0 : var3;
   }

   @Override
   public int[] queryFanSpeeds() {
      int[] var4 = getFansFromOHM();
      if (var4.length > 0) {
         return var4;
      }

      var4 = getFansFromWMI();
      return var4.length > 0 ? var4 : new int[0];
   }

   private static int[] getFansFromOHM() {
      WmiResult var3 = OhmHardware.queryHwIdentifier("Hardware", "CPU");
      if (var3.getResultCount() > 0) {
         String var4 = WmiUtil.getString(var3, OhmHardware.IdentifierProperty.IDENTIFIER, 0);
         if (var4.length() > 0) {
            WmiResult var5 = OhmSensor.querySensorValue(var4, "Fan");
            if (var5.getResultCount() > 0) {
               int[] var6 = new int[var5.getResultCount()];

               for (int var7 = 0; var7 < var5.getResultCount(); var7++) {
                  var6[var7] = (int)WmiUtil.getFloat(var5, OhmSensor.ValueProperty.VALUE, var7);
               }

               return var6;
            }
         }
      }

      return new int[0];
   }

   private static int[] getFansFromWMI() {
      WmiResult var3 = Win32Fan.querySpeed();
      if (var3.getResultCount() <= 1) {
         return new int[0];
      }

      int[] var4 = new int[var3.getResultCount()];

      for (int var5 = 0; var5 < var3.getResultCount(); var5++) {
         var4[var5] = (int)WmiUtil.getUint64(var3, Win32Fan.SpeedProperty.DESIREDSPEED, var5);
      }

      return var4;
   }

   @Override
   public double queryCpuVoltage() {
      double var4 = getVoltsFromOHM();
      return var4 > 0.0 ? var4 : getVoltsFromWMI();
   }

   private static double getVoltsFromOHM() {
      WmiResult var3 = OhmHardware.queryHwIdentifier("Sensor", "Voltage");
      if (var3.getResultCount() > 0) {
         String var4 = null;

         for (int var5 = 0; var5 < var3.getResultCount(); var5++) {
            String var6 = WmiUtil.getString(var3, OhmHardware.IdentifierProperty.IDENTIFIER, var5);
            if (var6.toLowerCase().contains("cpu")) {
               var4 = var6;
               break;
            }
         }

         if (var4 == null) {
            var4 = WmiUtil.getString(var3, OhmHardware.IdentifierProperty.IDENTIFIER, 0);
         }

         WmiResult var7 = OhmSensor.querySensorValue(var4, "Voltage");
         if (var7.getResultCount() > 0) {
            return WmiUtil.getFloat(var7, OhmSensor.ValueProperty.VALUE, 0);
         }
      }

      return 0.0;
   }

   private static double getVoltsFromWMI() {
      WmiResult var3 = Win32Processor.queryVoltage();
      if (var3.getResultCount() > 1) {
         int var4 = WmiUtil.getUint16(var3, Win32Processor.VoltProperty.CURRENTVOLTAGE, 0);
         if (var4 > 0) {
            if ((var4 & 128) != 0) {
               return (var4 & 127) / 10.0;
            }

            var4 = WmiUtil.getUint32(var3, Win32Processor.VoltProperty.VOLTAGECAPS, 0);
            if ((var4 & 1) > 0) {
               return 5.0;
            }

            if ((var4 & 2) > 0) {
               return 3.3;
            }

            if ((var4 & 4) > 0) {
               return 2.9;
            }
         }
      }

      return 0.0;
   }
}
