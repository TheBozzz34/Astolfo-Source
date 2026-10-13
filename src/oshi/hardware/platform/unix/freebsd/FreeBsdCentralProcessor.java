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
   private static final long c = 19818144091181L;

   @Override
   public CentralProcessor.ProcessorIdentifier queryProcessorId() {
      Pattern var4 = Pattern.compile("Origin=\"([^\"]*)\".*Id=(\\S+).*Family=(\\S+).*Model=(\\S+).*Stepping=(\\S+).*");
      Pattern var5 = Pattern.compile("Features=(\\S+)<.*");
      String var6 = "";
      String var7 = BsdSysctlUtil.sysctl("hw.model", "");
      String var8 = "";
      String var9 = "";
      String var10 = "";
      long var13 = 0L;

      for (String var17 : FileUtil.readFile("/var/run/dmesg.boot")) {
         var17 = var17.trim();
         if (var17.startsWith("CPU:") && var7.isEmpty()) {
            var7 = var17.replace("CPU:", "").trim();
         } else if (var17.startsWith("Origin=")) {
            Matcher var20 = var4.matcher(var17);
            if (var20.matches()) {
               var6 = var20.group(1);
               var13 |= Long.decode(var20.group(2));
               var8 = Integer.decode(var20.group(3)).toString();
               var9 = Integer.decode(var20.group(4)).toString();
               var10 = Integer.decode(var20.group(5)).toString();
            }
         } else if (var17.startsWith("Features=")) {
            Matcher var18 = var5.matcher(var17);
            if (var18.matches()) {
               var13 |= Long.decode(var18.group(1)) << 32;
            }
            break;
         }
      }

      boolean var12 = ExecutingCommand.getFirstAnswer("uname -m").trim().contains("64");
      String var11 = this.getProcessorIDfromDmiDecode(var13);
      return new CentralProcessor.ProcessorIdentifier(var6, var7, var8, var9, var10, var11, var12);
   }

   @Override
   public CentralProcessor.LogicalProcessor[] initProcessorCounts() {
      List var4 = parseTopology();
      if (var4.isEmpty()) {
         var4.add(new CentralProcessor.LogicalProcessor(0, 0, 0));
      }

      return (oshi.hardware.CentralProcessor.LogicalProcessor[])(var4.toArray(new CentralProcessor.LogicalProcessor[0]));
   }

   private static List<CentralProcessor.LogicalProcessor> parseTopology() {
      String[] var3 = BsdSysctlUtil.sysctl("kern.sched.topology_spec", "").split("\\n|\\r");
      long var4 = 1L;
      ArrayList var6 = new ArrayList();
      ArrayList var7 = new ArrayList();
      int var8 = 0;

      for (String var12 : var3) {
         if (var12.contains("<group level=")) {
            var8++;
         } else if (var12.contains("</group>")) {
            var8--;
         } else if (var12.contains("<cpu")) {
            Matcher var13 = CPUMASK.matcher(var12);
            if (var13.matches()) {
               switch (var8) {
                  case 1:
                     var4 = Long.parseLong(var13.group(1), 16);
                     break;
                  case 2:
                     var6.add(Long.parseLong(var13.group(1), 16));
                     break;
                  case 3:
                     var7.add(Long.parseLong(var13.group(1), 16));
               }
            }
         }
      }

      return matchBitmasks(var4, var6, var7);
   }

   private static List<CentralProcessor.LogicalProcessor> matchBitmasks(long var0, List<Long> var2, List<Long> var3) {
      ArrayList var7 = new ArrayList();
      int var8 = Long.numberOfTrailingZeros(var0);
      int var9 = 63 - Long.numberOfLeadingZeros(var0);

      for (int var10 = var8; var10 <= var9; var10++) {
         if ((var0 & 1L << var10) > 0L) {
            byte var11 = 0;
            CentralProcessor.LogicalProcessor var12 = new CentralProcessor.LogicalProcessor(
               var10, getMatchingBitmask(var3, var10), getMatchingBitmask(var2, var10), var11
            );
            var7.add(var12);
         }
      }

      return var7;
   }

   private static int getMatchingBitmask(List<Long> var0, int var1) {
      for (int var5 = 0; var5 < var0.size(); var5++) {
         if (((Long)var0.get(var5) & 1L << var1) > 0L) {
            return var5;
         }
      }

      return 0;
   }

   @Override
   public long[] querySystemCpuLoadTicks() {
      long[] var3 = new long[CentralProcessor.TickType.values().length];
      FreeBsdLibc.CpTime var4 = new FreeBsdLibc.CpTime();
      BsdSysctlUtil.sysctl("kern.cp_time", var4);
      var3[CentralProcessor.TickType.USER.getIndex()] = var4.cpu_ticks[0];
      var3[CentralProcessor.TickType.NICE.getIndex()] = var4.cpu_ticks[1];
      var3[CentralProcessor.TickType.SYSTEM.getIndex()] = var4.cpu_ticks[2];
      var3[CentralProcessor.TickType.IRQ.getIndex()] = var4.cpu_ticks[3];
      var3[CentralProcessor.TickType.IDLE.getIndex()] = var4.cpu_ticks[4];
      return var3;
   }

   @Override
   public long[] queryCurrentFreq() {
      long var4 = BsdSysctlUtil.sysctl("dev.cpu.0.freq", -1L);
      if (var4 > 0L) {
         var4 *= 1000000L;
      } else {
         var4 = BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
      }

      long[] var6 = new long[this.getLogicalProcessorCount()];
      Arrays.fill(var6, var4);
      return var6;
   }

   @Override
   public long queryMaxFreq() {
      long var4 = -1L;
      String var6 = BsdSysctlUtil.sysctl("dev.cpu.0.freq_levels", "");

      for (String var10 : ParseUtil.Q.split(var6)) {
         long var11 = ParseUtil.parseLongOrDefault(var10.split("/")[0], -1L);
         if (var4 < var11) {
            var4 = var11;
         }
      }

      if (var4 > 0L) {
         var4 *= 1000000L;
      } else {
         var4 = BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
      }

      return var4;
   }

   @Override
   public double[] getSystemLoadAverage(int var1) {
      if (var1 >= 1 && var1 <= 3) {
         double[] var5 = new double[var1];
         int var6 = FreeBsdLibc.INSTANCE.getloadavg(var5, var1);
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
      long var5 = new FreeBsdLibc.CpTime().size();
      long var7 = var5 * this.getLogicalProcessorCount();
      Memory var9 = new Memory(var7);
      String var10 = "kern.cp_times";
      if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(var10, var9, new IntByReference((int)var7), null, 0)) {
         return var4;
      }

      for (int var11 = 0; var11 < this.getLogicalProcessorCount(); var11++) {
         var4[var11][CentralProcessor.TickType.USER.getIndex()] = var9.getLong(var5 * var11 + 0 * FreeBsdLibc.UINT64_SIZE);
         var4[var11][CentralProcessor.TickType.NICE.getIndex()] = var9.getLong(var5 * var11 + 1 * FreeBsdLibc.UINT64_SIZE);
         var4[var11][CentralProcessor.TickType.SYSTEM.getIndex()] = var9.getLong(var5 * var11 + 2 * FreeBsdLibc.UINT64_SIZE);
         var4[var11][CentralProcessor.TickType.IRQ.getIndex()] = var9.getLong(var5 * var11 + 3 * FreeBsdLibc.UINT64_SIZE);
         var4[var11][CentralProcessor.TickType.IDLE.getIndex()] = var9.getLong(var5 * var11 + 4 * FreeBsdLibc.UINT64_SIZE);
      }

      return var4;
   }

   private String getProcessorIDfromDmiDecode(long var1) {
      boolean var6 = false;
      String var7 = "Processor Information";

      for (String var9 : ExecutingCommand.runNative("dmidecode -t system")) {
         if (!var6 && var9.contains(var7)) {
            var7 = "ID:";
            var6 = true;
         } else if (var6 && var9.contains(var7)) {
            return var9.split(var7)[1].trim();
         }
      }

      return String.format("%016X", var1);
   }

   @Override
   public long queryContextSwitches() {
      String var4 = "vm.stats.sys.v_swtch";
      IntByReference var5 = new IntByReference(FreeBsdLibc.INT_SIZE);
      Memory var6 = new Memory(var5.getValue());
      return 0 != FreeBsdLibc.INSTANCE.sysctlbyname(var4, var6, var5, null, 0) ? -1L : ParseUtil.unsignedIntToLong(var6.getInt(0L));
   }

   @Override
   public long queryInterrupts() {
      String var4 = "vm.stats.sys.v_intr";
      IntByReference var5 = new IntByReference(FreeBsdLibc.INT_SIZE);
      Memory var6 = new Memory(var5.getValue());
      return 0 != FreeBsdLibc.INSTANCE.sysctlbyname(var4, var6, var5, null, 0) ? -1L : ParseUtil.unsignedIntToLong(var6.getInt(0L));
   }
}
