package oshi.hardware.platform.linux;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@ThreadSafe
public final class LinuxSensors extends AbstractSensors {
   private static final String[] SENSORS = new String[]{"temp", "fan", "in"};
   private static final boolean IS_PI = queryCpuTemperatureFromVcGenCmd() > 0.0;
   private final Map<String, String> sensorsMap = new HashMap<>();

   public LinuxSensors() {
      if (!IS_PI) {
         this.populateSensorsMapFromHwmon();
         if (!this.sensorsMap.containsKey("temp")) {
            this.populateSensorsMapFromThermalZone();
         }
      }
   }

   private void populateSensorsMapFromHwmon() {
      for (String string : SENSORS) {
         String string2 = string;
         this.getSensorFilesFromPath("/sys/class/hwmon/hwmon", string, f -> {
            try {
               return f.getName().startsWith(string2) && f.getName().endsWith("_input") && FileUtil.getIntFromFile(f.getCanonicalPath()) > 0;
            } catch (IOException ioException) {
               return false;
            }
         });
      }
   }

   private void populateSensorsMapFromThermalZone() {
      this.getSensorFilesFromPath("/sys/class/thermal/thermal_zone", "temp", f -> f.getName().equals("temp"));
   }

   private void getSensorFilesFromPath(String sensorPath, String sensor, FileFilter sensorFileFilter) {
      for (int i = 0; Paths.get(sensorPath + i).toFile().isDirectory(); i++) {
         String string = sensorPath + i;
         File file = new File(string);
         File[] files = file.listFiles(sensorFileFilter);
         if (files != null && files.length > 0) {
            this.sensorsMap.put(sensor, String.format("%s/%s", string, sensor));
         }
      }
   }

   @Override
   public double queryCpuTemperature() {
      if (IS_PI) {
         return queryCpuTemperatureFromVcGenCmd();
      }

      String string = this.sensorsMap.get("temp");
      if (string != null) {
         long l = 0L;
         if (string.contains("hwmon")) {
            l = FileUtil.getLongFromFile(String.format("%s1_input", string));
            if (l > 0L) {
               return l / 1000.0;
            }

            long m = 0L;
            int i = 0;

            for (int j = 2; j <= 6; j++) {
               l = FileUtil.getLongFromFile(String.format("%s%d_input", string, j));
               if (l > 0L) {
                  m += l;
                  i++;
               }
            }

            if (i > 0) {
               return m / (i * 1000.0);
            }
         } else if (string.contains("thermal_zone")) {
            l = FileUtil.getLongFromFile(string);
            if (l > 0L) {
               return l / 1000.0;
            }
         }
      }

      return 0.0;
   }

   private static double queryCpuTemperatureFromVcGenCmd() {
      String string = ExecutingCommand.getFirstAnswer("vcgencmd measure_temp");
      return string.startsWith("temp=") && string.endsWith("'C") ? ParseUtil.parseDoubleOrDefault(string.replaceAll("\\^[0-9]+(\\.[0-9]{1,4})?$", ""), 0.0) : 0.0;
   }

   @Override
   public int[] queryFanSpeeds() {
      if (!IS_PI) {
         String string = this.sensorsMap.get("fan");
         if (string != null) {
            ArrayList arrayList = new ArrayList();
            int i = 1;

            while (true) {
               String string2 = String.format("%s%d_input", string, i);
               if (!new File(string2).exists()) {
                  int[] ints = new int[arrayList.size()];

                  for (int j = 0; j < arrayList.size(); j++) {
                     ints[j] = (Integer)arrayList.get(j);
                  }

                  return ints;
               }

               arrayList.add(FileUtil.getIntFromFile(string2));
               i++;
            }
         }
      }

      return new int[0];
   }

   @Override
   public double queryCpuVoltage() {
      if (IS_PI) {
         return queryCpuVoltageFromVcGenCmd();
      }

      String string = this.sensorsMap.get("in");
      return string != null ? FileUtil.getIntFromFile(String.format("%s1_input", string)) / 1000.0 : 0.0;
   }

   private static double queryCpuVoltageFromVcGenCmd() {
      String string = ExecutingCommand.getFirstAnswer("vcgencmd measure_volts core");
      return string.startsWith("volt=") && string.endsWith("V") ? ParseUtil.parseDoubleOrDefault(string.replaceAll("\\^[0-9]+(\\.[0-9]{1,4})?$", ""), 0.0) : 0.0;
   }
}
