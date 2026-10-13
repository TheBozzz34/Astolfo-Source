package oshi.hardware.platform.windows;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinNT.GROUP_AFFINITY;
import com.sun.jna.platform.win32.WinNT.NUMA_NODE_RELATIONSHIP;
import com.sun.jna.platform.win32.WinNT.PROCESSOR_RELATIONSHIP;
import com.sun.jna.platform.win32.WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION;
import com.sun.jna.platform.win32.WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.ProcessorInformation;
import oshi.driver.windows.perfmon.SystemInformation;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.windows.PowrProf;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class WindowsCentralProcessor extends AbstractCentralProcessor {
   private Map<String, Integer> numaNodeProcToLogicalProcMap;
   private static final long c = 61874349964357L;

   @Override
   public CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String var5 = "";
      String var6 = "";
      String var7 = "";
      String var8 = "";
      String var9 = "";
      String var10 = "";
      boolean var12 = false;
      String[] var14 = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\");
      if (var14.length > 0) {
         String var15 = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\" + var14[0];
         var5 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, var15, "VendorIdentifier");
         var6 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, var15, "ProcessorNameString");
         var7 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, var15, "Identifier");
      }

      if (!var7.isEmpty()) {
         var8 = this.parseIdentifier(var7, "Family");
         var9 = this.parseIdentifier(var7, "Model");
         var10 = this.parseIdentifier(var7, "Stepping");
      }

      SYSTEM_INFO var18 = new SYSTEM_INFO();
      Kernel32.INSTANCE.GetNativeSystemInfo(var18);
      int var16 = var18.processorArchitecture.pi.wProcessorArchitecture.intValue();
      if (var16 == 9 || var16 == 12 || var16 == 6) {
         var12 = true;
      }

      WmiResult var17 = Win32Processor.queryProcessorId();
      String var11;
      if (var17.getResultCount() > 0) {
         var11 = WmiUtil.getString(var17, Win32Processor.ProcessorIdProperty.PROCESSORID, 0);
      } else {
         var11 = createProcessorID(var10, var9, var8, var12 ? new String[]{"ia64"} : new String[0]);
      }

      return new CentralProcessor.ProcessorIdentifier(var5, var6, var8, var9, var10, var11, var12);
   }

   private String parseIdentifier(String var1, String var2) {
      String[] var6 = ParseUtil.Q.split(var1);
      boolean var7 = false;

      for (String var11 : var6) {
         if (var7) {
            return var11;
         }

         var7 = var11.equals(var2);
      }

      return "";
   }

   @Override
   public CentralProcessor.LogicalProcessor[] initProcessorCounts() {
      return VersionHelpers.IsWindows7OrGreater() ? this.getLogicalProcessorInformationEx() : this.getLogicalProcessorInformation();
   }

   private CentralProcessor.LogicalProcessor[] getLogicalProcessorInformationEx() {
      long var1 = c ^ 67023847532962L;

      try {
         SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[] var4 = Kernel32Util.getLogicalProcessorInformationEx(65535);
         ArrayList var5 = new ArrayList();
         ArrayList var6 = new ArrayList();
         ArrayList var7 = new ArrayList();

         for (int var8 = 0; var8 < var4.length; var8++) {
            switch (var4[var8].relationship) {
               case 0:
                  var7.add(((PROCESSOR_RELATIONSHIP)var4[var8]).groupMask[0]);
                  break;
               case 1:
                  var6.add((NUMA_NODE_RELATIONSHIP)var4[var8]);
               case 2:
               default:
                  break;
               case 3:
                  var5.add(((PROCESSOR_RELATIONSHIP)var4[var8]).groupMask);
            }
         }

         var7.sort(Comparator.comparing(var0 -> ((com.sun.jna.platform.win32.WinNT.GROUP_AFFINITY)var0).group * 64L + ((com.sun.jna.platform.win32.WinNT.GROUP_AFFINITY)var0).mask.longValue()));
         var5.sort(Comparator.comparing(var0 -> ((com.sun.jna.platform.win32.WinNT.GROUP_AFFINITY[])var0)[0].group * 64L + ((com.sun.jna.platform.win32.WinNT.GROUP_AFFINITY[])var0)[0].mask.longValue()));
         ArrayList var19 = new ArrayList();

         for (Object var10$raw : var7) {
            GROUP_AFFINITY var10 = (GROUP_AFFINITY)var10$raw;
            short var11 = var10.group;
            long var10000 = 8309171419103477737L ^ var1;
            long var12 = var10.mask.longValue();
            int var14 = Long.numberOfTrailingZeros(var12);
            int var15 = 63 - Long.numberOfLeadingZeros(var12);

            for (int var16 = var14; var16 <= var15; var16++) {
               if ((var12 & 1L << var16) > 0L) {
                  CentralProcessor.LogicalProcessor var17 = new CentralProcessor.LogicalProcessor(
                     var16, getMatchingCore(var7, var11, var16), getMatchingPackage(var5, var11, var16), getMatchingNumaNode(var6, var11, var16), var11
                  );
                  var19.add(var17);
               }
            }
         }

         var19.sort(Comparator.comparing(CentralProcessor.LogicalProcessor::getNumaNode).thenComparing(CentralProcessor.LogicalProcessor::getProcessorNumber));
         int var20 = 0;
         this.numaNodeProcToLogicalProcMap = new HashMap<>();

         for (Object var22$raw : var19) {
            CentralProcessor.LogicalProcessor var22 = (CentralProcessor.LogicalProcessor)var22$raw;
            this.numaNodeProcToLogicalProcMap.put(String.format("%d,%d", var22.getNumaNode(), var22.getProcessorNumber()), var20++);
         }

         return (oshi.hardware.CentralProcessor.LogicalProcessor[])(var19.toArray(new CentralProcessor.LogicalProcessor[0]));
      } catch (Exception var18) {
         return new CentralProcessor.LogicalProcessor[0];
      }
   }

   private static int getMatchingPackage(List<GROUP_AFFINITY[]> var0, int var1, int var2) {
      for (int var6 = 0; var6 < var0.size(); var6++) {
         for (int var7 = 0; var7 < ((GROUP_AFFINITY[])var0.get(var6)).length; var7++) {
            if ((((GROUP_AFFINITY[])var0.get(var6))[var7].mask.longValue() & 1L << var2) > 0L && ((GROUP_AFFINITY[])var0.get(var6))[var7].group == var1) {
               return var6;
            }
         }
      }

      return 0;
   }

   private static int getMatchingNumaNode(List<NUMA_NODE_RELATIONSHIP> var0, int var1, int var2) {
      for (int var6 = 0; var6 < var0.size(); var6++) {
         if ((((NUMA_NODE_RELATIONSHIP)var0.get(var6)).groupMask.mask.longValue() & 1L << var2) > 0L
            && ((NUMA_NODE_RELATIONSHIP)var0.get(var6)).groupMask.group == var1) {
            return ((NUMA_NODE_RELATIONSHIP)var0.get(var6)).nodeNumber;
         }
      }

      return 0;
   }

   private static int getMatchingCore(List<GROUP_AFFINITY> var0, int var1, int var2) {
      for (int var6 = 0; var6 < var0.size(); var6++) {
         if ((((GROUP_AFFINITY)var0.get(var6)).mask.longValue() & 1L << var2) > 0L && ((GROUP_AFFINITY)var0.get(var6)).group == var1) {
            return var6;
         }
      }

      return 0;
   }

   private CentralProcessor.LogicalProcessor[] getLogicalProcessorInformation() {
      long var1 = c ^ 18766654459388L;
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] var6 = Kernel32Util.getLogicalProcessorInformation();

      for (SYSTEM_LOGICAL_PROCESSOR_INFORMATION var10 : var6) {
         if (var10.relationship == 3) {
            var4.add(var10.processorMask.longValue());
         } else if (var10.relationship == 0) {
            var5.add(var10.processorMask.longValue());
         }
      }

      var5.sort(null);
      var4.sort(null);
      ArrayList var15 = new ArrayList();

      for (int var16 = 0; var16 < var5.size(); var16++) {
         long var17 = (Long)var5.get(var16);
         int var11 = Long.numberOfTrailingZeros(var17);
         long var10000 = 6218496673654721905L ^ var1;
         int var12 = 63 - Long.numberOfLeadingZeros(var17);

         for (int var13 = var11; var13 <= var12; var13++) {
            if ((var17 & 1L << var13) > 0L) {
               CentralProcessor.LogicalProcessor var14 = new CentralProcessor.LogicalProcessor(var13, var16, this.getBitMatchingPackageNumber(var4, var13));
               var15.add(var14);
            }
         }
      }

      return (oshi.hardware.CentralProcessor.LogicalProcessor[])(var15.toArray(new CentralProcessor.LogicalProcessor[0]));
   }

   private int getBitMatchingPackageNumber(List<Long> var1, int var2) {
      for (int var6 = 0; var6 < var1.size(); var6++) {
         if (((Long)var1.get(var6) & 1L << var2) > 0L) {
            return var6;
         }
      }

      return 0;
   }

   @Override
   public long[] querySystemCpuLoadTicks() {
      long[] var3 = new long[CentralProcessor.TickType.values().length];
      FILETIME var4 = new FILETIME();
      FILETIME var5 = new FILETIME();
      FILETIME var6 = new FILETIME();
      if (!Kernel32.INSTANCE.GetSystemTimes(var4, var5, var6)) {
         return var3;
      }

      Map var7 = ProcessorInformation.querySystemCounters();
      var3[CentralProcessor.TickType.IRQ.getIndex()] = ((java.lang.Long)(var7.getOrDefault(ProcessorInformation.SystemTickCountProperty.PERCENTINTERRUPTTIME, 0L))) / 10000L;
      var3[CentralProcessor.TickType.SOFTIRQ.getIndex()] = ((java.lang.Long)(var7.getOrDefault(ProcessorInformation.SystemTickCountProperty.PERCENTDPCTIME, 0L))) / 10000L;
      var3[CentralProcessor.TickType.IDLE.getIndex()] = var4.toDWordLong().longValue() / 10000L;
      var3[CentralProcessor.TickType.SYSTEM.getIndex()] = var5.toDWordLong().longValue() / 10000L - var3[CentralProcessor.TickType.IDLE.getIndex()];
      var3[CentralProcessor.TickType.USER.getIndex()] = var6.toDWordLong().longValue() / 10000L;
      var3[CentralProcessor.TickType.SYSTEM.getIndex()] -= var3[CentralProcessor.TickType.IRQ.getIndex()] + var3[CentralProcessor.TickType.SOFTIRQ.getIndex()];
      return var3;
   }

   @Override
   public long[] queryCurrentFreq() {
      if (VersionHelpers.IsWindows7OrGreater()) {
         Pair var4 = ProcessorInformation.queryFrequencyCounters();
         List var5 = (List)var4.getA();
         Map var6 = (Map)var4.getB();
         List var7 = (List)var6.get(ProcessorInformation.ProcessorFrequencyProperty.PERCENTOFMAXIMUMFREQUENCY);
         if (!var5.isEmpty()) {
            long var8 = this.K();
            long[] var10 = new long[this.getLogicalProcessorCount()];

            for (int var11 = 0; var11 < var5.size(); var11++) {
               int var12 = ((String)var5.get(var11)).contains(",")
                  ? this.numaNodeProcToLogicalProcMap.getOrDefault(var5.get(var11), 0)
                  : ParseUtil.parseIntOrDefault((String)var5.get(var11), 0);
               if (var12 < this.getLogicalProcessorCount()) {
                  var10[var12] = (Long)var7.get(var12) * var8 / 100L;
               }
            }

            return var10;
         }
      }

      return this.queryNTPower(2);
   }

   @Override
   public long queryMaxFreq() {
      long[] var1 = this.queryNTPower(1);
      return Arrays.stream(var1).max().getAsLong();
   }

   private long[] queryNTPower(int var1) {
      PowrProf.ProcessorPowerInformation var5 = new PowrProf.ProcessorPowerInformation();
      long[] var6 = new long[this.getLogicalProcessorCount()];
      int var7 = var5.size() * var6.length;
      Memory var8 = new Memory(var7);
      if (0 != PowrProf.INSTANCE.CallNtPowerInformation(11, null, 0, var8, var7)) {
         Arrays.fill(var6, -1L);
         return var6;
      }

      for (int var9 = 0; var9 < var6.length; var9++) {
         var5 = new PowrProf.ProcessorPowerInformation(var8.share((long)var9 * var5.size()));
         if (var1 == 1) {
            var6[var9] = var5.maxMhz * 1000000L;
         } else if (var1 == 2) {
            var6[var9] = var5.currentMhz * 1000000L;
         } else {
            var6[var9] = -1L;
         }
      }

      return var6;
   }

   @Override
   public double[] getSystemLoadAverage(int var1) {
      if (var1 >= 1 && var1 <= 3) {
         double[] var5 = new double[var1];

         for (int var6 = 0; var6 < var5.length; var6++) {
            var5[var6] = -1.0;
         }

         return var5;
      } else {
         throw new IllegalArgumentException("Must include from one to three elements.");
      }
   }

   @Override
   public long[][] queryProcessorCpuLoadTicks() {
      Pair var4 = ProcessorInformation.queryProcessorCounters();
      List var5 = (List)var4.getA();
      Map var6 = (Map)var4.getB();
      List var7 = (List)var6.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPRIVILEGEDTIME);
      List var8 = (List)var6.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTUSERTIME);
      List var9 = (List)var6.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTINTERRUPTTIME);
      List var10 = (List)var6.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTDPCTIME);
      List var11 = (List)var6.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPROCESSORTIME);
      long[][] var12 = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
      if (!var5.isEmpty() && var7 != null && var8 != null && var9 != null && var10 != null && var11 != null) {
         for (int var13 = 0; var13 < var5.size(); var13++) {
            int var14 = ((String)var5.get(var13)).contains(",")
               ? this.numaNodeProcToLogicalProcMap.getOrDefault(var5.get(var13), 0)
               : ParseUtil.parseIntOrDefault((String)var5.get(var13), 0);
            if (var14 < this.getLogicalProcessorCount()) {
               var12[var14][CentralProcessor.TickType.SYSTEM.getIndex()] = (Long)var7.get(var14);
               var12[var14][CentralProcessor.TickType.USER.getIndex()] = (Long)var8.get(var14);
               var12[var14][CentralProcessor.TickType.IRQ.getIndex()] = (Long)var9.get(var14);
               var12[var14][CentralProcessor.TickType.SOFTIRQ.getIndex()] = (Long)var10.get(var14);
               var12[var14][CentralProcessor.TickType.IDLE.getIndex()] = (Long)var11.get(var14);
               var12[var14][CentralProcessor.TickType.SYSTEM.getIndex()] -= var12[var14][CentralProcessor.TickType.IRQ.getIndex()]
                  + var12[var14][CentralProcessor.TickType.SOFTIRQ.getIndex()];
               var12[var14][CentralProcessor.TickType.SYSTEM.getIndex()] /= 10000L;
               var12[var14][CentralProcessor.TickType.USER.getIndex()] /= 10000L;
               var12[var14][CentralProcessor.TickType.IRQ.getIndex()] /= 10000L;
               var12[var14][CentralProcessor.TickType.SOFTIRQ.getIndex()] /= 10000L;
               var12[var14][CentralProcessor.TickType.IDLE.getIndex()] /= 10000L;
            }
         }

         return var12;
      } else {
         return var12;
      }
   }

   @Override
   public long queryContextSwitches() {
      return SystemInformation.queryContextSwitchCounters().getOrDefault(SystemInformation.ContextSwitchProperty.CONTEXTSWITCHESPERSEC, 0L);
   }

   @Override
   public long queryInterrupts() {
      return ProcessorInformation.queryInterruptCounters().getOrDefault(ProcessorInformation.InterruptsProperty.INTERRUPTSPERSEC, 0L);
   }
}
