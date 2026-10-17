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
   @Override
   public CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String string = "";
      String string2 = "";
      String string3 = "";
      String string4 = "";
      String string5 = "";

      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         Kstat kstat = kstatChain.lookup("cpu_info", -1, null);
         if (kstat != null && kstatChain.read(kstat)) {
            string = KstatUtil.dataLookupString(kstat, "vendor_id");
            string2 = KstatUtil.dataLookupString(kstat, "brand");
            string3 = KstatUtil.dataLookupString(kstat, "family");
            string4 = KstatUtil.dataLookupString(kstat, "model");
            string5 = KstatUtil.dataLookupString(kstat, "stepping");
         }
      }

      boolean bl = "64".equals(ExecutingCommand.getFirstAnswer("isainfo -b").trim());
      String string6 = getProcessorID(string5, string4, string3);
      return new CentralProcessor.ProcessorIdentifier(string, string2, string3, string4, string5, string6, bl);
   }

   @Override
   public CentralProcessor.LogicalProcessor[] initProcessorCounts() {
      Map map = mapNumaNodes();
      ArrayList arrayList = new ArrayList();

      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         for (Kstat kstat : kstatChain.lookupAll("cpu_info", -1, null)) {
            if (kstat != null && kstatChain.read(kstat)) {
               int i = arrayList.size();
               String string = KstatUtil.dataLookupString(kstat, "chip_id");
               String string2 = KstatUtil.dataLookupString(kstat, "core_id");
               CentralProcessor.LogicalProcessor logicalProcessor = new CentralProcessor.LogicalProcessor(
                  i, ParseUtil.parseIntOrDefault(string2, 0), ParseUtil.parseIntOrDefault(string, 0), (int)(map.getOrDefault(i, 0))
               );
               arrayList.add(logicalProcessor);
            }
         }
      }

      if (arrayList.isEmpty()) {
         arrayList.add(new CentralProcessor.LogicalProcessor(0, 0, 0));
      }

      return (oshi.hardware.CentralProcessor.LogicalProcessor[])(arrayList.toArray(new CentralProcessor.LogicalProcessor[0]));
   }

   private static Map<Integer, Integer> mapNumaNodes() {
      HashMap hashMap = new HashMap();
      List list = ExecutingCommand.runNative("lgrpinfo -c leaves");
      int i = 0;

      for (Object object : list) {
         String string = (String)object;
         if (string.startsWith("lgroup")) {
            i = ParseUtil.getFirstIntValue(string);
         } else if (string.contains("CPUs:")) {
            String[] strings = ParseUtil.whitespaces.split(string.split(":")[1]);

            for (String string2 : strings) {
               if (string2.contains("-")) {
                  int j = ParseUtil.getFirstIntValue(string2);
                  int k = ParseUtil.getNthIntValue(string, 2);

                  for (int l = j; l <= k; l++) {
                     hashMap.put(l, i);
                  }
               } else {
                  hashMap.put(ParseUtil.parseIntOrDefault(string2, 0), i);
               }
            }
         }
      }

      return hashMap;
   }

   @Override
   public long[] querySystemCpuLoadTicks() {
      long[] longs = new long[CentralProcessor.TickType.values().length];
      long[][] longss = this.getProcessorCpuLoadTicks();

      for (int i = 0; i < longs.length; i++) {
         for (long[] longs2 : longss) {
            longs[i] += longs2[i];
         }

         longs[i] /= longss.length;
      }

      return longs;
   }

   @Override
   public long[] queryCurrentFreq() {
      long[] longs = new long[this.getLogicalProcessorCount()];
      Arrays.fill(longs, -7922361626683138111L);

      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         for (int i = 0; i < longs.length; i++) {
            for (Kstat kstat : kstatChain.lookupAll("cpu_info", i, null)) {
               if (kstatChain.read(kstat)) {
                  longs[i] = KstatUtil.dataLookupLong(kstat, "current_clock_Hz");
               }
            }
         }
      }

      return longs;
   }

   @Override
   public long queryMaxFreq() {
      long l = -7922361626683138111L;

      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         for (Kstat kstat : kstatChain.lookupAll("cpu_info", 0, null)) {
            if (kstatChain.read(kstat)) {
               String string = KstatUtil.dataLookupString(kstat, "supported_frequencies_Hz");
               if (!string.isEmpty()) {
                  for (String string2 : string.split(":")) {
                     long m = ParseUtil.parseLongOrDefault(string2, -7922361626683138111L);
                     if (l < m) {
                        l = m;
                     }
                  }
               }
            }
         }
      }

      return l;
   }

   @Override
   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] doubles = new double[nelem];
         int i = SolarisLibc.INSTANCE.getloadavg(doubles, nelem);
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
      long[][] longss = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
      int i = -1;

      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         for (Kstat kstat : kstatChain.lookupAll("cpu", -1, "sys")) {
            if (++i >= longss.length) {
               break;
            }

            if (kstatChain.read(kstat)) {
               longss[i][CentralProcessor.TickType.IDLE.getIndex()] = KstatUtil.dataLookupLong(kstat, "cpu_ticks_idle");
               longss[i][CentralProcessor.TickType.SYSTEM.getIndex()] = KstatUtil.dataLookupLong(kstat, "cpu_ticks_kernel");
               longss[i][CentralProcessor.TickType.USER.getIndex()] = KstatUtil.dataLookupLong(kstat, "cpu_ticks_user");
            }
         }
      }

      return longss;
   }

   private static String getProcessorID(String stepping, String model, String family) {
      List list = ExecutingCommand.runNative("isainfo -v");
      StringBuilder stringBuilder = new StringBuilder();

      for (Object object : list) {
         String string = (String)object;
         if (string.startsWith("32-bit")) {
            break;
         }

         if (!string.startsWith("64-bit")) {
            stringBuilder.append(' ').append(string.trim());
         }
      }

      return createProcessorID(stepping, model, family, ParseUtil.whitespaces.split(stringBuilder.toString().toLowerCase()));
   }

   @Override
   public long queryContextSwitches() {
      long l = 0L;

      for (String string : ExecutingCommand.runNative("kstat -p cpu_stat:::/pswitch\\\\|inv_swtch/")) {
         l += ParseUtil.parseLastLong(string, 0L);
      }

      return l > 0L ? l : -7922361626683138111L;
   }

   @Override
   public long queryInterrupts() {
      long l = 0L;

      for (String string : ExecutingCommand.runNative("kstat -p cpu_stat:::/intr/")) {
         l += ParseUtil.parseLastLong(string, 0L);
      }

      return l > 0L ? l : -7922361626683138111L;
   }
}
