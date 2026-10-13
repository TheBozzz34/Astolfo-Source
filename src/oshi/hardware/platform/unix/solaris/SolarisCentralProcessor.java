package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.unix.solaris.SolarisLibc;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public final class SolarisCentralProcessor extends AbstractCentralProcessor {
   private static final long c = 69333438023470L;
   private static final long j = 5181961841730912288L;

   @Override
   public CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String var4 = "";
      String var5 = "";
      String var6 = "";
      String var7 = "";
      String var8 = "";

      try (KstatUtil.KstatChain var9 = KstatUtil.openChain()) {
         Kstat var11 = var9.lookup("cpu_info", -1, null);
         if (var11 != null && var9.read(var11)) {
            var4 = KstatUtil.dataLookupString(var11, "vendor_id");
            var5 = KstatUtil.dataLookupString(var11, "brand");
            var6 = KstatUtil.dataLookupString(var11, "family");
            var7 = KstatUtil.dataLookupString(var11, "model");
            var8 = KstatUtil.dataLookupString(var11, "stepping");
         }
      }

      boolean var21 = "64".equals(ExecutingCommand.getFirstAnswer("isainfo -b").trim());
      String var22 = getProcessorID(var8, var7, var6);
      return new CentralProcessor.ProcessorIdentifier(var4, var5, var6, var7, var8, var22, var21);
   }

   @Override
   public CentralProcessor.LogicalProcessor[] initProcessorCounts() {
      Map var4 = mapNumaNodes();
      ArrayList var5 = new ArrayList();

      try (KstatUtil.KstatChain var6 = KstatUtil.openChain()) {
         for (Kstat var10 : var6.lookupAll("cpu_info", -1, null)) {
            if (var10 != null && var6.read(var10)) {
               int var11 = var5.size();
               String var12 = KstatUtil.dataLookupString(var10, "chip_id");
               String var13 = KstatUtil.dataLookupString(var10, "core_id");
               CentralProcessor.LogicalProcessor var14 = new CentralProcessor.LogicalProcessor(
                  var11, ParseUtil.parseIntOrDefault(var13, 0), ParseUtil.parseIntOrDefault(var12, 0), (int)(var4.getOrDefault(var11, 0))
               );
               var5.add(var14);
            }
         }
      }

      if (var5.isEmpty()) {
         var5.add(new CentralProcessor.LogicalProcessor(0, 0, 0));
      }

      return (oshi.hardware.CentralProcessor.LogicalProcessor[])(var5.toArray(new CentralProcessor.LogicalProcessor[0]));
   }

   private static Map<Integer, Integer> mapNumaNodes() {
      HashMap var3 = new HashMap();
      List var4 = ExecutingCommand.runNative("lgrpinfo -c leaves");
      int var5 = 0;

      for (Object var7$raw : var4) {
         String var7 = (String)var7$raw;
         if (var7.startsWith("lgroup")) {
            var5 = ParseUtil.getFirstIntValue(var7);
         } else if (var7.contains("CPUs:")) {
            String[] var8 = ParseUtil.Q.split(var7.split(":")[1]);

            for (String var12 : var8) {
               if (var12.contains("-")) {
                  int var13 = ParseUtil.getFirstIntValue(var12);
                  int var14 = ParseUtil.getNthIntValue(var7, 2);

                  for (int var15 = var13; var15 <= var14; var15++) {
                     var3.put(var15, var5);
                  }
               } else {
                  var3.put(ParseUtil.parseIntOrDefault(var12, 0), var5);
               }
            }
         }
      }

      return var3;
   }

   @Override
   public long[] querySystemCpuLoadTicks() {
      long[] var4 = new long[CentralProcessor.TickType.values().length];
      long[][] var5 = this.getProcessorCpuLoadTicks();

      for (int var6 = 0; var6 < var4.length; var6++) {
         for (long[] var10 : var5) {
            var4[var6] += var10[var6];
         }

         var4[var6] /= var5.length;
      }

      return var4;
   }

   @Override
   public long[] queryCurrentFreq() {
      long[] var4 = new long[this.getLogicalProcessorCount()];
      Arrays.fill(var4, -7922361626683138111L);

      try (KstatUtil.KstatChain var5 = KstatUtil.openChain()) {
         for (int var7 = 0; var7 < var4.length; var7++) {
            for (Kstat var9 : var5.lookupAll("cpu_info", var7, null)) {
               if (var5.read(var9)) {
                  var4[var7] = KstatUtil.dataLookupLong(var9, "current_clock_Hz");
               }
            }
         }
      }

      return var4;
   }

   @Override
   public long queryMaxFreq() {
      long var4 = -7922361626683138111L;

      try (KstatUtil.KstatChain var6 = KstatUtil.openChain()) {
         for (Kstat var9 : var6.lookupAll("cpu_info", 0, null)) {
            if (var6.read(var9)) {
               String var10 = KstatUtil.dataLookupString(var9, "supported_frequencies_Hz");
               if (!var10.isEmpty()) {
                  for (String var14 : var10.split(":")) {
                     long var15 = ParseUtil.parseLongOrDefault(var14, -7922361626683138111L);
                     if (var4 < var15) {
                        var4 = var15;
                     }
                  }
               }
            }
         }
      }

      return var4;
   }

   @Override
   public double[] getSystemLoadAverage(int var1) {
      if (var1 >= 1 && var1 <= 3) {
         double[] var5 = new double[var1];
         int var6 = SolarisLibc.INSTANCE.getloadavg(var5, var1);
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
      long[][] var4 = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
      int var5 = -1;

      try (KstatUtil.KstatChain var6 = KstatUtil.openChain()) {
         for (Kstat var9 : var6.lookupAll("cpu", -1, "sys")) {
            if (++var5 >= var4.length) {
               break;
            }

            if (var6.read(var9)) {
               var4[var5][CentralProcessor.TickType.IDLE.getIndex()] = KstatUtil.dataLookupLong(var9, "cpu_ticks_idle");
               var4[var5][CentralProcessor.TickType.SYSTEM.getIndex()] = KstatUtil.dataLookupLong(var9, "cpu_ticks_kernel");
               var4[var5][CentralProcessor.TickType.USER.getIndex()] = KstatUtil.dataLookupLong(var9, "cpu_ticks_user");
            }
         }
      }

      return var4;
   }

   private static String getProcessorID(String var0, String var1, String var2) {
      List var6 = ExecutingCommand.runNative("isainfo -v");
      StringBuilder var7 = new StringBuilder();

      for (Object var9$raw : var6) {
         String var9 = (String)var9$raw;
         if (var9.startsWith("32-bit")) {
            break;
         }

         if (!var9.startsWith("64-bit")) {
            var7.append((char)((int)j)).append(var9.trim());
         }
      }

      return createProcessorID(var0, var1, var2, ParseUtil.Q.split(var7.toString().toLowerCase()));
   }

   @Override
   public long queryContextSwitches() {
      long var4 = 0L;

      for (String var8 : ExecutingCommand.runNative("kstat -p cpu_stat:::/pswitch\\\\|inv_swtch/")) {
         var4 += ParseUtil.parseLastLong(var8, 0L);
      }

      return var4 > 0L ? var4 : -7922361626683138111L;
   }

   @Override
   public long queryInterrupts() {
      long var4 = 0L;

      for (String var8 : ExecutingCommand.runNative("kstat -p cpu_stat:::/intr/")) {
         var4 += ParseUtil.parseLastLong(var8, 0L);
      }

      return var4 > 0L ? var4 : -7922361626683138111L;
   }
}
