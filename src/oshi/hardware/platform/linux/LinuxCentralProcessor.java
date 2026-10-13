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
   private static final long c = 21074499392943L;

   @Override
   public CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String var4 = "";
      String var5 = "";
      String var6 = "";
      String var7 = "";
      String var8 = "";
      boolean var10 = false;
      StringBuilder var11 = new StringBuilder();
      String[] var12 = new String[0];

      for (String var15 : FileUtil.readFile(ProcPath.CPUINFO)) {
         String[] var16 = ParseUtil.H.split(var15);
         if (var16.length >= 2) {
            switch (var16[0]) {
               case "vendor_id":
               case "CPU implementer":
                  var4 = var16[1];
                  break;
               case "model name":
                  var5 = var16[1];
                  break;
               case "flags":
                  var12 = var16[1].toLowerCase().split(" ");

                  for (String var22 : var12) {
                     if ("lm".equals(var22)) {
                        var10 = true;
                        break;
                     }
                  }
                  break;
               case "stepping":
                  var8 = var16[1];
                  break;
               case "CPU variant":
                  var11.insert(0, "r" + var16[1]);
                  break;
               case "CPU revision":
                  var11.append('p').append(var16[1]);
                  break;
               case "model":
               case "CPU part":
                  var7 = var16[1];
                  break;
               case "cpu family":
               case "CPU architecture":
                  var6 = var16[1];
            }
         }
      }

      if (var8.isEmpty()) {
         var8 = var11.toString();
      }

      String var9 = getProcessorID(var4, var8, var7, var6, var12);
      if (var4.startsWith("0x")) {
         for (String var25 : ExecutingCommand.runNative("lscpu")) {
            if (var25.startsWith("Architecture:")) {
               var4 = var25.replace("Architecture:", "").trim();
            }
         }
      }

      return new CentralProcessor.ProcessorIdentifier(var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public CentralProcessor.LogicalProcessor[] initProcessorCounts() {
      Map var4 = mapNumaNodes();
      List var5 = FileUtil.readFile(ProcPath.CPUINFO);
      ArrayList var6 = new ArrayList();
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      boolean var10 = true;

      for (Object var12$raw : var5) {
         String var12 = (String)var12$raw;
         if (var12.startsWith("processor")) {
            if (!var10) {
               var6.add(new CentralProcessor.LogicalProcessor(var7, var8, var9, (int)(var4.getOrDefault(var7, 0))));
            } else {
               var10 = false;
            }

            var7 = ParseUtil.parseLastInt(var12, 0);
         } else if (var12.startsWith("core id") || var12.startsWith("cpu number")) {
            var8 = ParseUtil.parseLastInt(var12, 0);
         } else if (var12.startsWith("physical id")) {
            var9 = ParseUtil.parseLastInt(var12, 0);
         }
      }

      var6.add(new CentralProcessor.LogicalProcessor(var7, var8, var9, (int)(var4.getOrDefault(var7, 0))));
      return (oshi.hardware.CentralProcessor.LogicalProcessor[])(var6.toArray(new CentralProcessor.LogicalProcessor[0]));
   }

   private static Map<Integer, Integer> mapNumaNodes() {
      HashMap var3 = new HashMap();

      for (String var6 : ExecutingCommand.runNative("lscpu -p=cpu,node")) {
         if (!var6.startsWith("#")) {
            String[] var7 = var6.split(",");
            if (var7.length == 2) {
               var3.put(ParseUtil.parseIntOrDefault(var7[0], 0), ParseUtil.parseIntOrDefault(var7[1], 0));
            }
         }
      }

      return var3;
   }

   @Override
   public long[] querySystemCpuLoadTicks() {
      long[] var4 = CpuStat.getSystemCpuLoadTicks();
      long var5 = LinuxOperatingSystem.H();

      for (int var7 = 0; var7 < var4.length; var7++) {
         var4[var7] = var4[var7] * 1000L / var5;
      }

      return var4;
   }

   @Override
   public long[] queryCurrentFreq() {
      String var4 = GlobalConfig.get("oshi.cpu.freq.path", "");
      long[] var5 = new long[this.getLogicalProcessorCount()];
      long var6 = 0L;

      for (int var8 = 0; var8 < var5.length; var8++) {
         var5[var8] = FileUtil.getLongFromFile(var4 + var8 + "/cpufreq/scaling_cur_freq");
         if (var5[var8] == 0L) {
            var5[var8] = FileUtil.getLongFromFile(var4 + var8 + "/cpufreq/cpuinfo_cur_freq");
         }

         if (var6 < var5[var8]) {
            var6 = var5[var8];
         }
      }

      if (var6 > 0L) {
         for (int var13 = 0; var13 < var5.length; var13++) {
            var5[var13] *= 1000L;
         }

         return var5;
      } else {
         Arrays.fill(var5, -1L);
         List var12 = FileUtil.readFile(ProcPath.CPUINFO);
         int var9 = 0;

         for (Object var11$raw : var12) {
            String var11 = (String)var11$raw;
            if (var11.toLowerCase().contains("cpu mhz")) {
               var5[var9] = (long)(ParseUtil.parseLastDouble(var11, 0.0) * 1000000.0);
               if (++var9 >= var5.length) {
                  break;
               }
            }
         }

         return var5;
      }
   }

   @Override
   public long queryMaxFreq() {
      String var4 = GlobalConfig.get("oshi.cpu.freq.path", "");
      long var5 = 0L;

      for (int var7 = 0; var7 < this.getLogicalProcessorCount(); var7++) {
         long var8 = FileUtil.getLongFromFile(var4 + var7 + "/cpufreq/scaling_max_freq");
         if (var8 == 0L) {
            var8 = FileUtil.getLongFromFile(var4 + var7 + "/cpufreq/cpuinfo_max_freq");
         }

         if (var5 < var8) {
            var5 = var8;
         }
      }

      return var5 > 0L ? var5 * 1000L : -1L;
   }

   @Override
   public double[] getSystemLoadAverage(int var1) {
      if (var1 >= 1 && var1 <= 3) {
         double[] var5 = new double[var1];
         int var6 = LinuxLibc.INSTANCE.getloadavg(var5, var1);
         if (var6 < var1) {
            for (int var7 = Math.max(var6, 0); var7 < var5.length; var7++) {
               var5[var7] = -1.0;
            }
         }

         return var5;
      } else {
         throw new IllegalArgumentException("Must include from one to three elements.");
      }
   }

   @Override
   public long[][] queryProcessorCpuLoadTicks() {
      long[][] var4 = CpuStat.getProcessorCpuLoadTicks(this.getLogicalProcessorCount());
      long var5 = LinuxOperatingSystem.H();

      for (int var7 = 0; var7 < var4.length; var7++) {
         for (int var8 = 0; var8 < var4[var7].length; var8++) {
            var4[var7][var8] = var4[var7][var8] * 1000L / var5;
         }
      }

      return var4;
   }

   private static String getProcessorID(String var0, String var1, String var2, String var3, String[] var4) {
      boolean var8 = false;
      String var9 = "Processor Information";

      for (String var11 : ExecutingCommand.runNative("dmidecode -t 4")) {
         if (!var8 && var11.contains(var9)) {
            var9 = "ID:";
            var8 = true;
         } else if (var8 && var11.contains(var9)) {
            return var11.split(var9)[1].trim();
         }
      }

      var9 = "eax=";

      for (String var20 : ExecutingCommand.runNative("cpuid -1r")) {
         if (var20.contains(var9) && var20.trim().startsWith("0x00000001")) {
            String var12 = "";
            String var13 = "";

            for (String var17 : ParseUtil.Q.split(var20)) {
               if (var17.startsWith("eax=")) {
                  var12 = ParseUtil.removeMatchingString(var17, "eax=0x");
               } else if (var17.startsWith("edx=")) {
                  var13 = ParseUtil.removeMatchingString(var17, "edx=0x");
               }
            }

            return var13 + var12;
         }
      }

      return var0.startsWith("0x") ? createMIDR(var0, var1, var2, var3) + "00000000" : createProcessorID(var1, var2, var3, var4);
   }

   private static String createMIDR(String var0, String var1, String var2, String var3) {
      int var7 = 0;
      if (var1.startsWith("r") && var1.contains("p")) {
         String[] var8 = var1.substring(1).split("p");
         var7 |= ParseUtil.parseLastInt(var8[1], 0);
         var7 |= ParseUtil.parseLastInt(var8[0], 0) << 20;
      }

      var7 |= ParseUtil.parseLastInt(var2, 0) << 4;
      var7 |= ParseUtil.parseLastInt(var3, 0) << 16;
      var7 |= ParseUtil.parseLastInt(var0, 0) << 24;
      return String.format("%08X", var7);
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
