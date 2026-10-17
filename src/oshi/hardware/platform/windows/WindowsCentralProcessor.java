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

   @Override
   public CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String string = "";
      String string2 = "";
      String string3 = "";
      String string4 = "";
      String string5 = "";
      String string6 = "";
      boolean bl = false;
      String[] strings = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\");
      if (strings.length > 0) {
         String string7 = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\" + strings[0];
         string = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, string7, "VendorIdentifier");
         string2 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, string7, "ProcessorNameString");
         string3 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, string7, "Identifier");
      }

      if (!string3.isEmpty()) {
         string4 = this.parseIdentifier(string3, "Family");
         string5 = this.parseIdentifier(string3, "Model");
         string6 = this.parseIdentifier(string3, "Stepping");
      }

      SYSTEM_INFO systeM_INFO = new SYSTEM_INFO();
      Kernel32.INSTANCE.GetNativeSystemInfo(systeM_INFO);
      int i = systeM_INFO.processorArchitecture.pi.wProcessorArchitecture.intValue();
      if (i == 9 || i == 12 || i == 6) {
         bl = true;
      }

      WmiResult wmiResult = Win32Processor.queryProcessorId();
      String string8;
      if (wmiResult.getResultCount() > 0) {
         string8 = WmiUtil.getString(wmiResult, Win32Processor.ProcessorIdProperty.PROCESSORID, 0);
      } else {
         string8 = createProcessorID(string6, string5, string4, bl ? new String[]{"ia64"} : new String[0]);
      }

      return new CentralProcessor.ProcessorIdentifier(string, string2, string4, string5, string6, string8, bl);
   }

   private String parseIdentifier(String identifier, String key) {
      String[] strings = ParseUtil.whitespaces.split(identifier);
      boolean bl = false;

      for (String string : strings) {
         if (bl) {
            return string;
         }

         bl = string.equals(key);
      }

      return "";
   }

   @Override
   public CentralProcessor.LogicalProcessor[] initProcessorCounts() {
      return VersionHelpers.IsWindows7OrGreater() ? this.getLogicalProcessorInformationEx() : this.getLogicalProcessorInformation();
   }

   private CentralProcessor.LogicalProcessor[] getLogicalProcessorInformationEx() {
      try {
         SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[] systeM_LOGICAL_PROCESSOR_INFORMATION_EXs = Kernel32Util.getLogicalProcessorInformationEx(65535);
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();
         ArrayList arrayList3 = new ArrayList();

         for (int i = 0; i < systeM_LOGICAL_PROCESSOR_INFORMATION_EXs.length; i++) {
            switch (systeM_LOGICAL_PROCESSOR_INFORMATION_EXs[i].relationship) {
               case 0:
                  arrayList3.add(((PROCESSOR_RELATIONSHIP)systeM_LOGICAL_PROCESSOR_INFORMATION_EXs[i]).groupMask[0]);
                  break;
               case 1:
                  arrayList2.add((NUMA_NODE_RELATIONSHIP)systeM_LOGICAL_PROCESSOR_INFORMATION_EXs[i]);
               case 2:
               default:
                  break;
               case 3:
                  arrayList.add(((PROCESSOR_RELATIONSHIP)systeM_LOGICAL_PROCESSOR_INFORMATION_EXs[i]).groupMask);
            }
         }

         arrayList3.sort(Comparator.comparing(c -> ((com.sun.jna.platform.win32.WinNT.GROUP_AFFINITY)c).group * 64L + ((com.sun.jna.platform.win32.WinNT.GROUP_AFFINITY)c).mask.longValue()));
         arrayList.sort(Comparator.comparing(p -> ((com.sun.jna.platform.win32.WinNT.GROUP_AFFINITY[])p)[0].group * 64L + ((com.sun.jna.platform.win32.WinNT.GROUP_AFFINITY[])p)[0].mask.longValue()));
         ArrayList arrayList4 = new ArrayList();

         for (Object object : arrayList3) {
            GROUP_AFFINITY grouP_AFFINITY = (GROUP_AFFINITY)object;
            short s = grouP_AFFINITY.group;
            long l = grouP_AFFINITY.mask.longValue();
            int j = Long.numberOfTrailingZeros(l);
            int k = 63 - Long.numberOfLeadingZeros(l);

            for (int m = j; m <= k; m++) {
               if ((l & 1L << m) > 0L) {
                  CentralProcessor.LogicalProcessor logicalProcessor = new CentralProcessor.LogicalProcessor(
                     m, getMatchingCore(arrayList3, s, m), getMatchingPackage(arrayList, s, m), getMatchingNumaNode(arrayList2, s, m), s
                  );
                  arrayList4.add(logicalProcessor);
               }
            }
         }

         arrayList4.sort(Comparator.comparing(CentralProcessor.LogicalProcessor::getNumaNode).thenComparing(CentralProcessor.LogicalProcessor::getProcessorNumber));
         int n = 0;
         this.numaNodeProcToLogicalProcMap = new HashMap<>();

         for (Object object2 : arrayList4) {
            CentralProcessor.LogicalProcessor logicalProcessor2 = (CentralProcessor.LogicalProcessor)object2;
            this.numaNodeProcToLogicalProcMap.put(String.format("%d,%d", logicalProcessor2.getNumaNode(), logicalProcessor2.getProcessorNumber()), n++);
         }

         return (oshi.hardware.CentralProcessor.LogicalProcessor[])(arrayList4.toArray(new CentralProcessor.LogicalProcessor[0]));
      } catch (Exception exception) {
         return new CentralProcessor.LogicalProcessor[0];
      }
   }

   private static int getMatchingPackage(List<GROUP_AFFINITY[]> packages, int g, int lp) {
      for (int i = 0; i < packages.size(); i++) {
         for (int j = 0; j < ((GROUP_AFFINITY[])packages.get(i)).length; j++) {
            if ((((GROUP_AFFINITY[])packages.get(i))[j].mask.longValue() & 1L << lp) > 0L && ((GROUP_AFFINITY[])packages.get(i))[j].group == g) {
               return i;
            }
         }
      }

      return 0;
   }

   private static int getMatchingNumaNode(List<NUMA_NODE_RELATIONSHIP> numaNodes, int g, int lp) {
      for (int i = 0; i < numaNodes.size(); i++) {
         if ((((NUMA_NODE_RELATIONSHIP)numaNodes.get(i)).groupMask.mask.longValue() & 1L << lp) > 0L
            && ((NUMA_NODE_RELATIONSHIP)numaNodes.get(i)).groupMask.group == g) {
            return ((NUMA_NODE_RELATIONSHIP)numaNodes.get(i)).nodeNumber;
         }
      }

      return 0;
   }

   private static int getMatchingCore(List<GROUP_AFFINITY> cores, int g, int lp) {
      for (int i = 0; i < cores.size(); i++) {
         if ((((GROUP_AFFINITY)cores.get(i)).mask.longValue() & 1L << lp) > 0L && ((GROUP_AFFINITY)cores.get(i)).group == g) {
            return i;
         }
      }

      return 0;
   }

   private CentralProcessor.LogicalProcessor[] getLogicalProcessorInformation() {
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] systeM_LOGICAL_PROCESSOR_INFORMATIONs = Kernel32Util.getLogicalProcessorInformation();

      for (SYSTEM_LOGICAL_PROCESSOR_INFORMATION systeM_LOGICAL_PROCESSOR_INFORMATION : systeM_LOGICAL_PROCESSOR_INFORMATIONs) {
         if (systeM_LOGICAL_PROCESSOR_INFORMATION.relationship == 3) {
            arrayList.add(systeM_LOGICAL_PROCESSOR_INFORMATION.processorMask.longValue());
         } else if (systeM_LOGICAL_PROCESSOR_INFORMATION.relationship == 0) {
            arrayList2.add(systeM_LOGICAL_PROCESSOR_INFORMATION.processorMask.longValue());
         }
      }

      arrayList2.sort(null);
      arrayList.sort(null);
      ArrayList arrayList3 = new ArrayList();

      for (int i = 0; i < arrayList2.size(); i++) {
         long l = (Long)arrayList2.get(i);
         int j = Long.numberOfTrailingZeros(l);
         int k = 63 - Long.numberOfLeadingZeros(l);

         for (int m = j; m <= k; m++) {
            if ((l & 1L << m) > 0L) {
               CentralProcessor.LogicalProcessor logicalProcessor = new CentralProcessor.LogicalProcessor(m, i, this.getBitMatchingPackageNumber(arrayList, m));
               arrayList3.add(logicalProcessor);
            }
         }
      }

      return (oshi.hardware.CentralProcessor.LogicalProcessor[])(arrayList3.toArray(new CentralProcessor.LogicalProcessor[0]));
   }

   private int getBitMatchingPackageNumber(List<Long> packageMaskList, int logProc) {
      for (int i = 0; i < packageMaskList.size(); i++) {
         if (((Long)packageMaskList.get(i) & 1L << logProc) > 0L) {
            return i;
         }
      }

      return 0;
   }

   @Override
   public long[] querySystemCpuLoadTicks() {
      long[] longs = new long[CentralProcessor.TickType.values().length];
      FILETIME filetime = new FILETIME();
      FILETIME filetime2 = new FILETIME();
      FILETIME filetime3 = new FILETIME();
      if (!Kernel32.INSTANCE.GetSystemTimes(filetime, filetime2, filetime3)) {
         return longs;
      }

      Map map = ProcessorInformation.querySystemCounters();
      longs[CentralProcessor.TickType.IRQ.getIndex()] = ((java.lang.Long)(map.getOrDefault(ProcessorInformation.SystemTickCountProperty.PERCENTINTERRUPTTIME, 0L))) / 10000L;
      longs[CentralProcessor.TickType.SOFTIRQ.getIndex()] = ((java.lang.Long)(map.getOrDefault(ProcessorInformation.SystemTickCountProperty.PERCENTDPCTIME, 0L))) / 10000L;
      longs[CentralProcessor.TickType.IDLE.getIndex()] = filetime.toDWordLong().longValue() / 10000L;
      longs[CentralProcessor.TickType.SYSTEM.getIndex()] = filetime2.toDWordLong().longValue() / 10000L - longs[CentralProcessor.TickType.IDLE.getIndex()];
      longs[CentralProcessor.TickType.USER.getIndex()] = filetime3.toDWordLong().longValue() / 10000L;
      longs[CentralProcessor.TickType.SYSTEM.getIndex()] -= longs[CentralProcessor.TickType.IRQ.getIndex()] + longs[CentralProcessor.TickType.SOFTIRQ.getIndex()];
      return longs;
   }

   @Override
   public long[] queryCurrentFreq() {
      if (VersionHelpers.IsWindows7OrGreater()) {
         Pair pair = ProcessorInformation.queryFrequencyCounters();
         List list = (List)pair.getA();
         Map map = (Map)pair.getB();
         List list2 = (List)map.get(ProcessorInformation.ProcessorFrequencyProperty.PERCENTOFMAXIMUMFREQUENCY);
         if (!list.isEmpty()) {
            long l = this.getMaxFreq();
            long[] longs = new long[this.getLogicalProcessorCount()];

            for (int i = 0; i < list.size(); i++) {
               int j = ((String)list.get(i)).contains(",")
                  ? this.numaNodeProcToLogicalProcMap.getOrDefault(list.get(i), 0)
                  : ParseUtil.parseIntOrDefault((String)list.get(i), 0);
               if (j < this.getLogicalProcessorCount()) {
                  longs[j] = (Long)list2.get(j) * l / 100L;
               }
            }

            return longs;
         }
      }

      return this.queryNTPower(2);
   }

   @Override
   public long queryMaxFreq() {
      long[] freqs = this.queryNTPower(1);
      return Arrays.stream(freqs).max().getAsLong();
   }

   private long[] queryNTPower(int fieldIndex) {
      PowrProf.ProcessorPowerInformation processorPowerInformation = new PowrProf.ProcessorPowerInformation();
      long[] longs = new long[this.getLogicalProcessorCount()];
      int i = processorPowerInformation.size() * longs.length;
      Memory memory = new Memory(i);
      if (0 != PowrProf.INSTANCE.CallNtPowerInformation(11, null, 0, memory, i)) {
         Arrays.fill(longs, -1L);
         return longs;
      }

      for (int j = 0; j < longs.length; j++) {
         processorPowerInformation = new PowrProf.ProcessorPowerInformation(memory.share((long)j * processorPowerInformation.size()));
         if (fieldIndex == 1) {
            longs[j] = processorPowerInformation.maxMhz * 1000000L;
         } else if (fieldIndex == 2) {
            longs[j] = processorPowerInformation.currentMhz * 1000000L;
         } else {
            longs[j] = -1L;
         }
      }

      return longs;
   }

   @Override
   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] doubles = new double[nelem];

         for (int i = 0; i < doubles.length; i++) {
            doubles[i] = -1.0;
         }

         return doubles;
      } else {
         throw new IllegalArgumentException("Must include from one to three elements.");
      }
   }

   @Override
   public long[][] queryProcessorCpuLoadTicks() {
      Pair pair = ProcessorInformation.queryProcessorCounters();
      List list = (List)pair.getA();
      Map map = (Map)pair.getB();
      List list2 = (List)map.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPRIVILEGEDTIME);
      List list3 = (List)map.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTUSERTIME);
      List list4 = (List)map.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTINTERRUPTTIME);
      List list5 = (List)map.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTDPCTIME);
      List list6 = (List)map.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPROCESSORTIME);
      long[][] longss = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
      if (!list.isEmpty() && list2 != null && list3 != null && list4 != null && list5 != null && list6 != null) {
         for (int i = 0; i < list.size(); i++) {
            int j = ((String)list.get(i)).contains(",")
               ? this.numaNodeProcToLogicalProcMap.getOrDefault(list.get(i), 0)
               : ParseUtil.parseIntOrDefault((String)list.get(i), 0);
            if (j < this.getLogicalProcessorCount()) {
               longss[j][CentralProcessor.TickType.SYSTEM.getIndex()] = (Long)list2.get(j);
               longss[j][CentralProcessor.TickType.USER.getIndex()] = (Long)list3.get(j);
               longss[j][CentralProcessor.TickType.IRQ.getIndex()] = (Long)list4.get(j);
               longss[j][CentralProcessor.TickType.SOFTIRQ.getIndex()] = (Long)list5.get(j);
               longss[j][CentralProcessor.TickType.IDLE.getIndex()] = (Long)list6.get(j);
               longss[j][CentralProcessor.TickType.SYSTEM.getIndex()] -= longss[j][CentralProcessor.TickType.IRQ.getIndex()]
                  + longss[j][CentralProcessor.TickType.SOFTIRQ.getIndex()];
               longss[j][CentralProcessor.TickType.SYSTEM.getIndex()] /= 10000L;
               longss[j][CentralProcessor.TickType.USER.getIndex()] /= 10000L;
               longss[j][CentralProcessor.TickType.IRQ.getIndex()] /= 10000L;
               longss[j][CentralProcessor.TickType.SOFTIRQ.getIndex()] /= 10000L;
               longss[j][CentralProcessor.TickType.IDLE.getIndex()] /= 10000L;
            }
         }

         return longss;
      } else {
         return longss;
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
