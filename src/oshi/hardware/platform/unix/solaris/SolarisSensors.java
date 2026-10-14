package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class SolarisSensors extends AbstractSensors {
   @Override
   public double queryCpuTemperature() {
      double d = 0.0;

      for (String string : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c temperature-sensor")) {
         if (string.trim().startsWith("Temperature:")) {
            int i = ParseUtil.parseLastInt(string, 0);
            if (i > d) {
               d = i;
            }
         }
      }

      if (d > 1000.0) {
         d /= 1000.0;
      }

      return d;
   }

   @Override
   public int[] queryFanSpeeds() {
      ArrayList arrayList = new ArrayList();

      for (String string : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c fan")) {
         if (string.trim().startsWith("Speed:")) {
            arrayList.add(ParseUtil.parseLastInt(string, 0));
         }
      }

      int[] ints = new int[arrayList.size()];

      for (int i = 0; i < arrayList.size(); i++) {
         ints[i] = (Integer)arrayList.get(i);
      }

      return ints;
   }

   @Override
   public double queryCpuVoltage() {
      double d = 0.0;

      for (String string : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c voltage-sensor")) {
         if (string.trim().startsWith("Voltage:")) {
            d = ParseUtil.parseDoubleOrDefault(string.replace("Voltage:", "").trim(), 0.0);
            break;
         }
      }

      return d;
   }
}
