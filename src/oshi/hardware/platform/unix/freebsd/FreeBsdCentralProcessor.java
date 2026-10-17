package oshi.hardware.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public final class FreeBsdCentralProcessor extends AbstractCentralProcessor {
   private static final Pattern CPUMASK = Pattern.compile(".*<cpu\\s.*mask=\"(?:0x)?(\\p{XDigit}+)\".*>.*</cpu>.*");

   @Override
   public CentralProcessor.ProcessorIdentifier queryProcessorId() {
      Pattern pattern = Pattern.compile("Origin=\"([^\"]*)\".*Id=(\\S+).*Family=(\\S+).*Model=(\\S+).*Stepping=(\\S+).*");
      Pattern pattern2 = Pattern.compile("Features=(\\S+)<.*");
      String string = "";
      String string2 = BsdSysctlUtil.sysctl("hw.model", "");
      String string3 = "";
      String string4 = "";
      String string5 = "";
      long l = 0L;

      for (String string6 : FileUtil.readFile("/var/run/dmesg.boot")) {
         string6 = string6.trim();
         if (string6.startsWith("CPU:") && string2.isEmpty()) {
            string2 = string6.replace("CPU:", "").trim();
         } else if (string6.startsWith("Origin=")) {
            Matcher matcher2 = pattern.matcher(string6);
            if (matcher2.matches()) {
               string = matcher2.group(1);
               l |= Long.decode(matcher2.group(2));
               string3 = Integer.decode(matcher2.group(3)).toString();
               string4 = Integer.decode(matcher2.group(4)).toString();
               string5 = Integer.decode(matcher2.group(5)).toString();
            }
         } else if (string6.startsWith("Features=")) {
            Matcher matcher3 = pattern2.matcher(string6);
            if (matcher3.matches()) {
               l |= Long.decode(matcher3.group(1)) << 32;
            }
            break;
         }
      }

      boolean bl = ExecutingCommand.getFirstAnswer("uname -m").trim().contains("64");
      String string7 = this.getProcessorIDfromDmiDecode(l);
      return new CentralProcessor.ProcessorIdentifier(string, string2, string3, string4, string5, string7, bl);
   }

   @Override
   public CentralProcessor.LogicalProcessor[] initProcessorCounts() {
      List list = parseTopology();
      if (list.isEmpty()) {
         list.add(new CentralProcessor.LogicalProcessor(0, 0, 0));
      }

      return (oshi.hardware.CentralProcessor.LogicalProcessor[])(list.toArray(new CentralProcessor.LogicalProcessor[0]));
   }

   private static List<CentralProcessor.LogicalProcessor> parseTopology() {
      String[] strings = BsdSysctlUtil.sysctl("kern.sched.topology_spec", "").split("\\n|\\r");
      long l = 1L;
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      int i = 0;

      for (String string : strings) {
         if (string.contains("<group level=")) {
            i++;
         } else if (string.contains("</group>")) {
            i--;
         } else if (string.contains("<cpu")) {
            Matcher matcher2 = CPUMASK.matcher(string);
            if (matcher2.matches()) {
               switch (i) {
                  case 1:
                     l = Long.parseLong(matcher2.group(1), 16);
                     break;
                  case 2:
                     arrayList.add(Long.parseLong(matcher2.group(1), 16));
                     break;
                  case 3:
                     arrayList2.add(Long.parseLong(matcher2.group(1), 16));
               }
            }
         }
      }

      return matchBitmasks(l, arrayList, arrayList2);
   }

   private static List<CentralProcessor.LogicalProcessor> matchBitmasks(long group1, List<Long> group2, List<Long> group3) {
      ArrayList arrayList = new ArrayList();
      int i = Long.numberOfTrailingZeros(group1);
      int j = 63 - Long.numberOfLeadingZeros(group1);

      for (int k = i; k <= j; k++) {
         if ((group1 & 1L << k) > 0L) {
            byte b = 0;
            CentralProcessor.LogicalProcessor logicalProcessor = new CentralProcessor.LogicalProcessor(
               k, getMatchingBitmask(group3, k), getMatchingBitmask(group2, k), b
            );
            arrayList.add(logicalProcessor);
         }
      }

      return arrayList;
   }

   private static int getMatchingBitmask(List<Long> bitmasks, int lp) {
      for (int i = 0; i < bitmasks.size(); i++) {
         if (((Long)bitmasks.get(i) & 1L << lp) > 0L) {
            return i;
         }
      }

      return 0;
   }

   @Override
   public long[] querySystemCpuLoadTicks() {
      long[] longs = new long[CentralProcessor.TickType.values().length];
      FreeBsdLibc.CpTime cpTime = new FreeBsdLibc.CpTime();
      BsdSysctlUtil.sysctl("kern.cp_time", cpTime);
      longs[CentralProcessor.TickType.USER.getIndex()] = cpTime.cpu_ticks[0];
      longs[CentralProcessor.TickType.NICE.getIndex()] = cpTime.cpu_ticks[1];
      longs[CentralProcessor.TickType.SYSTEM.getIndex()] = cpTime.cpu_ticks[2];
      longs[CentralProcessor.TickType.IRQ.getIndex()] = cpTime.cpu_ticks[3];
      longs[CentralProcessor.TickType.IDLE.getIndex()] = cpTime.cpu_ticks[4];
      return longs;
   }

   @Override
   public long[] queryCurrentFreq() {
      long l = BsdSysctlUtil.sysctl("dev.cpu.0.freq", -1L);
      if (l > 0L) {
         l *= 1000000L;
      } else {
         l = BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
      }

      long[] longs = new long[this.getLogicalProcessorCount()];
      Arrays.fill(longs, l);
      return longs;
   }

   @Override
   public long queryMaxFreq() {
      long l = -1L;
      String string = BsdSysctlUtil.sysctl("dev.cpu.0.freq_levels", "");

      for (String string2 : ParseUtil.whitespaces.split(string)) {
         long m = ParseUtil.parseLongOrDefault(string2.split("/")[0], -1L);
         if (l < m) {
            l = m;
         }
      }

      if (l > 0L) {
         l *= 1000000L;
      } else {
         l = BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
      }

      return l;
   }

   @Override
   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] doubles = new double[nelem];
         int i = FreeBsdLibc.INSTANCE.getloadavg(doubles, nelem);
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
      long l = new FreeBsdLibc.CpTime().size();
      long m = l * this.getLogicalProcessorCount();
      Memory memory = new Memory(m);
      String string = "kern.cp_times";
      if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(string, memory, new IntByReference((int)m), null, 0)) {
         return longss;
      }

      for (int i = 0; i < this.getLogicalProcessorCount(); i++) {
         longss[i][CentralProcessor.TickType.USER.getIndex()] = memory.getLong(l * i + 0 * FreeBsdLibc.UINT64_SIZE);
         longss[i][CentralProcessor.TickType.NICE.getIndex()] = memory.getLong(l * i + 1 * FreeBsdLibc.UINT64_SIZE);
         longss[i][CentralProcessor.TickType.SYSTEM.getIndex()] = memory.getLong(l * i + 2 * FreeBsdLibc.UINT64_SIZE);
         longss[i][CentralProcessor.TickType.IRQ.getIndex()] = memory.getLong(l * i + 3 * FreeBsdLibc.UINT64_SIZE);
         longss[i][CentralProcessor.TickType.IDLE.getIndex()] = memory.getLong(l * i + 4 * FreeBsdLibc.UINT64_SIZE);
      }

      return longss;
   }

   private String getProcessorIDfromDmiDecode(long processorID) {
      boolean bl = false;
      String string = "Processor Information";

      for (String string2 : ExecutingCommand.runNative("dmidecode -t system")) {
         if (!bl && string2.contains(string)) {
            string = "ID:";
            bl = true;
         } else if (bl && string2.contains(string)) {
            return string2.split(string)[1].trim();
         }
      }

      return String.format("%016X", processorID);
   }

   @Override
   public long queryContextSwitches() {
      String string = "vm.stats.sys.v_swtch";
      IntByReference intByReference = new IntByReference(FreeBsdLibc.INT_SIZE);
      Memory memory = new Memory(intByReference.getValue());
      return 0 != FreeBsdLibc.INSTANCE.sysctlbyname(string, memory, intByReference, null, 0) ? -1L : ParseUtil.unsignedIntToLong(memory.getInt(0L));
   }

   @Override
   public long queryInterrupts() {
      String string = "vm.stats.sys.v_intr";
      IntByReference intByReference = new IntByReference(FreeBsdLibc.INT_SIZE);
      Memory memory = new Memory(intByReference.getValue());
      return 0 != FreeBsdLibc.INSTANCE.sysctlbyname(string, memory, intByReference, null, 0) ? -1L : ParseUtil.unsignedIntToLong(memory.getInt(0L));
   }
}
