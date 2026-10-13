package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class SolarisSensors extends AbstractSensors {
   private static final long b = 139468581974510L;

   @Override
   public double queryCpuTemperature() {
      double var4 = 0.0;

      for (String var7 : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c temperature-sensor")) {
         if (var7.trim().startsWith("Temperature:")) {
            int var8 = ParseUtil.parseLastInt(var7, 0);
            if (var8 > var4) {
               var4 = var8;
            }
         }
      }

      if (var4 > 1000.0) {
         var4 /= 1000.0;
      }

      return var4;
   }

   @Override
   public int[] queryFanSpeeds() {
      ArrayList var4 = new ArrayList();

      for (String var6 : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c fan")) {
         if (var6.trim().startsWith("Speed:")) {
            var4.add(ParseUtil.parseLastInt(var6, 0));
         }
      }

      int[] var7 = new int[var4.size()];

      for (int var8 = 0; var8 < var4.size(); var8++) {
         var7[var8] = (Integer)var4.get(var8);
      }

      return var7;
   }

   @Override
   public double queryCpuVoltage() {
      double var4 = 0.0;

      for (String var7 : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c voltage-sensor")) {
         if (var7.trim().startsWith("Voltage:")) {
            var4 = ParseUtil.parseDoubleOrDefault(var7.replace("Voltage:", "").trim(), 0.0);
            break;
         }
      }

      return var4;
   }
}
