package oshi.hardware.platform.linux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.CpuStat;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.GlobalConfig;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class LinuxCentralProcessor extends AbstractCentralProcessor {
   @Override
   public CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String string = "";
      String string2 = "";
      String string3 = "";
      String string4 = "";
      String string5 = "";
      boolean bl = false;
      StringBuilder stringBuilder = new StringBuilder();
      String[] strings = new String[0];

      for (String string6 : FileUtil.readFile(ProcPath.CPUINFO)) {
         String[] strings2 = ParseUtil.whitespacesColonWhitespace.split(string6);
         if (strings2.length >= 2) {
            switch (strings2[0]) {
               case "vendor_id":
               case "CPU implementer":
                  string = strings2[1];
                  break;
               case "model name":
                  string2 = strings2[1];
                  break;
               case "flags":
                  strings = strings2[1].toLowerCase().split(" ");

                  for (String string7 : strings) {
                     if ("lm".equals(string7)) {
                        bl = true;
                        break;
                     }
                  }
                  break;
               case "stepping":
                  string5 = strings2[1];
                  break;
               case "CPU variant":
                  stringBuilder.insert(0, "r" + strings2[1]);
                  break;
               case "CPU revision":
                  stringBuilder.append('p').append(strings2[1]);
                  break;
               case "model":
               case "CPU part":
                  string4 = strings2[1];
                  break;
               case "cpu family":
               case "CPU architecture":
                  string3 = strings2[1];
            }
         }
      }

      if (string5.isEmpty()) {
         string5 = stringBuilder.toString();
      }

      String string8 = getProcessorID(string, string5, string4, string3, strings);
      if (string.startsWith("0x")) {
         for (String string9 : ExecutingCommand.runNative("lscpu")) {
            if (string9.startsWith("Architecture:")) {
               string = string9.replace("Architecture:", "").trim();
            }
         }
      }

      return new CentralProcessor.ProcessorIdentifier(string, string2, string3, string4, string5, string8, bl);
   }

   @Override
   public CentralProcessor.LogicalProcessor[] initProcessorCounts() {
      Map map = mapNumaNodes();
      List list = FileUtil.readFile(ProcPath.CPUINFO);
      ArrayList arrayList = new ArrayList();
      int i = 0;
      int j = 0;
      int k = 0;
      boolean bl = true;

      for (Object object : list) {
         String string = (String)object;
         if (string.startsWith("processor")) {
            if (!bl) {
               arrayList.add(new CentralProcessor.LogicalProcessor(i, j, k, (int)(map.getOrDefault(i, 0))));
            } else {
               bl = false;
            }

            i = ParseUtil.parseLastInt(string, 0);
         } else if (string.startsWith("core id") || string.startsWith("cpu number")) {
            j = ParseUtil.parseLastInt(string, 0);
         } else if (string.startsWith("physical id")) {
            k = ParseUtil.parseLastInt(string, 0);
         }
      }

      arrayList.add(new CentralProcessor.LogicalProcessor(i, j, k, (int)(map.getOrDefault(i, 0))));
      return (oshi.hardware.CentralProcessor.LogicalProcessor[])(arrayList.toArray(new CentralProcessor.LogicalProcessor[0]));
   }

   private static Map<Integer, Integer> mapNumaNodes() {
      HashMap hashMap = new HashMap();

      for (String string : ExecutingCommand.runNative("lscpu -p=cpu,node")) {
         if (!string.startsWith("#")) {
            String[] strings = string.split(",");
            if (strings.length == 2) {
               hashMap.put(ParseUtil.parseIntOrDefault(strings[0], 0), ParseUtil.parseIntOrDefault(strings[1], 0));
            }
         }
      }

      return hashMap;
   }

   @Override
   public long[] querySystemCpuLoadTicks() {
      long[] longs = CpuStat.getSystemCpuLoadTicks();
      long l = LinuxOperatingSystem.getHz();

      for (int i = 0; i < longs.length; i++) {
         longs[i] = longs[i] * 1000L / l;
      }

      return longs;
   }

   @Override
   public long[] queryCurrentFreq() {
      String string = GlobalConfig.get("oshi.cpu.freq.path", "");
      long[] longs = new long[this.getLogicalProcessorCount()];
      long l = 0L;

      for (int i = 0; i < longs.length; i++) {
         longs[i] = FileUtil.getLongFromFile(string + i + "/cpufreq/scaling_cur_freq");
         if (longs[i] == 0L) {
            longs[i] = FileUtil.getLongFromFile(string + i + "/cpufreq/cpuinfo_cur_freq");
         }

         if (l < longs[i]) {
            l = longs[i];
         }
      }

      if (l > 0L) {
         for (int j = 0; j < longs.length; j++) {
            longs[j] *= 1000L;
         }

         return longs;
      } else {
         Arrays.fill(longs, -1L);
         List list = FileUtil.readFile(ProcPath.CPUINFO);
         int k = 0;

         for (Object object : list) {
            String string2 = (String)object;
            if (string2.toLowerCase().contains("cpu mhz")) {
               longs[k] = (long)(ParseUtil.parseLastDouble(string2, 0.0) * 1000000.0);
               if (++k >= longs.length) {
                  break;
               }
            }
         }

         return longs;
      }
   }

   @Override
   public long queryMaxFreq() {
      String string = GlobalConfig.get("oshi.cpu.freq.path", "");
      long l = 0L;

      for (int i = 0; i < this.getLogicalProcessorCount(); i++) {
         long m = FileUtil.getLongFromFile(string + i + "/cpufreq/scaling_max_freq");
         if (m == 0L) {
            m = FileUtil.getLongFromFile(string + i + "/cpufreq/cpuinfo_max_freq");
         }

         if (l < m) {
            l = m;
         }
      }

      return l > 0L ? l * 1000L : -1L;
   }

   @Override
   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] doubles = new double[nelem];
         int i = LinuxLibc.INSTANCE.getloadavg(doubles, nelem);
         if (i < nelem) {
            for (int j = Math.max(i, 0); j < doubles.length; j++) {
               doubles[j] = -1.0;
            }
         }

         return doubles;
      } else {
         throw new IllegalArgumentException("Must include from one to three elements.");
      }
   }

   @Override
   public long[][] queryProcessorCpuLoadTicks() {
      long[][] longss = CpuStat.getProcessorCpuLoadTicks(this.getLogicalProcessorCount());
      long l = LinuxOperatingSystem.getHz();

      for (int i = 0; i < longss.length; i++) {
         for (int j = 0; j < longss[i].length; j++) {
            longss[i][j] = longss[i][j] * 1000L / l;
         }
      }

      return longss;
   }

   private static String getProcessorID(String vendor, String stepping, String model, String family, String[] flags) {
      boolean bl = false;
      String string = "Processor Information";

      for (String string2 : ExecutingCommand.runNative("dmidecode -t 4")) {
         if (!bl && string2.contains(string)) {
            string = "ID:";
            bl = true;
         } else if (bl && string2.contains(string)) {
            return string2.split(string)[1].trim();
         }
      }

      string = "eax=";

      for (String string3 : ExecutingCommand.runNative("cpuid -1r")) {
         if (string3.contains(string) && string3.trim().startsWith("0x00000001")) {
            String string4 = "";
            String string5 = "";

            for (String string6 : ParseUtil.whitespaces.split(string3)) {
               if (string6.startsWith("eax=")) {
                  string4 = ParseUtil.removeMatchingString(string6, "eax=0x");
               } else if (string6.startsWith("edx=")) {
                  string5 = ParseUtil.removeMatchingString(string6, "edx=0x");
               }
            }

            return string5 + string4;
         }
      }

      return vendor.startsWith("0x") ? createMIDR(vendor, stepping, model, family) + "00000000" : createProcessorID(stepping, model, family, flags);
   }

   private static String createMIDR(String vendor, String stepping, String model, String family) {
      int i = 0;
      if (stepping.startsWith("r") && stepping.contains("p")) {
         String[] strings = stepping.substring(1).split("p");
         i |= ParseUtil.parseLastInt(strings[1], 0);
         i |= ParseUtil.parseLastInt(strings[0], 0) << 20;
      }

      i |= ParseUtil.parseLastInt(model, 0) << 4;
      i |= ParseUtil.parseLastInt(family, 0) << 16;
      i |= ParseUtil.parseLastInt(vendor, 0) << 24;
      return String.format("%08X", i);
   }

   @Override
   public long queryContextSwitches() {
      return CpuStat.getContextSwitches();
   }

   @Override
   public long queryInterrupts() {
      return CpuStat.getInterrupts();
   }
}
