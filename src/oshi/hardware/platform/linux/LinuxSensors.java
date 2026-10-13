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
   private static final long c = 7792427612461L;
   private static final long k = 4542938151957561350L;

   public LinuxSensors() {
      if (!IS_PI) {
         this.populateSensorsMapFromHwmon();
         if (!this.sensorsMap.containsKey("temp")) {
            this.populateSensorsMapFromThermalZone();
         }
      }
   }

   private void populateSensorsMapFromHwmon() {
      for (String var7 : SENSORS) {
         String var8 = var7;
         this.getSensorFilesFromPath("/sys/class/hwmon/hwmon", var7, var1 -> {
            try {
               return var1.getName().startsWith(var8) && var1.getName().endsWith("_input") && FileUtil.getIntFromFile(var1.getCanonicalPath()) > 0;
            } catch (IOException var6) {
               return false;
            }
         });
      }
   }

   private void populateSensorsMapFromThermalZone() {
      this.getSensorFilesFromPath("/sys/class/thermal/thermal_zone", "temp", var0 -> var0.getName().equals("temp"));
   }

   private void getSensorFilesFromPath(String var1, String var2, FileFilter var3) {
      for (int var7 = 0; Paths.get(var1 + var7).toFile().isDirectory(); var7++) {
         String var8 = var1 + var7;
         File var9 = new File(var8);
         File[] var10 = var9.listFiles(var3);
         if (var10 != null && var10.length > 0) {
            this.sensorsMap.put(var2, String.format("%s/%s", var8, var2));
         }
      }
   }

   @Override
   public double queryCpuTemperature() {
      if (IS_PI) {
         return queryCpuTemperatureFromVcGenCmd();
      }

      String var4 = this.sensorsMap.get("temp");
      if (var4 != null) {
         long var5 = 0L;
         if (var4.contains("hwmon")) {
            var5 = FileUtil.getLongFromFile(String.format("%s1_input", var4));
            if (var5 > 0L) {
               return var5 / 1000.0;
            }

            long var7 = 0L;
            int var9 = 0;

            for (int var10 = 2; var10 <= (int)k; var10++) {
               var5 = FileUtil.getLongFromFile(String.format("%s%d_input", var4, var10));
               if (var5 > 0L) {
                  var7 += var5;
                  var9++;
               }
            }

            if (var9 > 0) {
               return var7 / (var9 * 1000.0);
            }
         } else if (var4.contains("thermal_zone")) {
            var5 = FileUtil.getLongFromFile(var4);
            if (var5 > 0L) {
               return var5 / 1000.0;
            }
         }
      }

      return 0.0;
   }

   private static double queryCpuTemperatureFromVcGenCmd() {
      String var2 = ExecutingCommand.getFirstAnswer("vcgencmd measure_temp");
      return var2.startsWith("temp=") && var2.endsWith("'C") ? ParseUtil.parseDoubleOrDefault(var2.replaceAll("\\^[0-9]+(\\.[0-9]{1,4})?$", ""), 0.0) : 0.0;
   }

   @Override
   public int[] queryFanSpeeds() {
      if (!IS_PI) {
         String var4 = this.sensorsMap.get("fan");
         if (var4 != null) {
            ArrayList var5 = new ArrayList();
            int var6 = 1;

            while (true) {
               String var7 = String.format("%s%d_input", var4, var6);
               if (!new File(var7).exists()) {
                  int[] var9 = new int[var5.size()];

                  for (int var8 = 0; var8 < var5.size(); var8++) {
                     var9[var8] = (Integer)var5.get(var8);
                  }

                  return var9;
               }

               var5.add(FileUtil.getIntFromFile(var7));
               var6++;
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

      String var4 = this.sensorsMap.get("in");
      return var4 != null ? FileUtil.getIntFromFile(String.format("%s1_input", var4)) / 1000.0 : 0.0;
   }

   private static double queryCpuVoltageFromVcGenCmd() {
      String var3 = ExecutingCommand.getFirstAnswer("vcgencmd measure_volts core");
      return var3.startsWith("volt=") && var3.endsWith("V") ? ParseUtil.parseDoubleOrDefault(var3.replaceAll("\\^[0-9]+(\\.[0-9]{1,4})?$", ""), 0.0) : 0.0;
   }
}
