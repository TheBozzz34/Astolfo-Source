package oshi.driver.windows.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.ProcessInformation;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class ProcessPerformanceData {
   private static final long a = 24556450108999L;

   private ProcessPerformanceData() {
   }

   public static Map<Integer, OSProcess> buildProcessMapFromRegistry(OperatingSystem var0, Collection<Integer> var1) {
      Triplet var6 = HkeyPerformanceDataUtil.readPerfDataFromRegistry("Process", ProcessInformation.ProcessPerformanceProperty.class);
      if (var6 == null) {
         return null;
      }

      List var7 = (List)var6.v();
      long var8 = (Long)var6.q();
      long var10 = (Long)var6.U();
      HashMap var12 = new HashMap();

      for (Object var14$raw : var7) {
         Map var14 = (Map)var14$raw;
         int var15 = (Integer)var14.get(ProcessInformation.ProcessPerformanceProperty.PROCESSID);
         String var16 = (String)var14.get(ProcessInformation.ProcessPerformanceProperty.NAME);
         if ((var1 == null || var1.contains(var15)) && !"_Total".equals(var16)) {
            OSProcess var17 = new OSProcess(var0);
            var17.setProcessID(var15);
            var17.setName(var16);
            long var18 = (var8 - (Long)var14.get(ProcessInformation.ProcessPerformanceProperty.CREATIONDATE)) / 10000L;
            var17.setUpTime(var18 < 1L ? 1L : var18);
            var17.setStartTime(var10 - var18);
            var17.setBytesRead((Long)var14.get(ProcessInformation.ProcessPerformanceProperty.READTRANSFERCOUNT));
            var17.setBytesWritten((Long)var14.get(ProcessInformation.ProcessPerformanceProperty.WRITETRANSFERCOUNT));
            var17.setResidentSetSize((Long)var14.get(ProcessInformation.ProcessPerformanceProperty.PRIVATEPAGECOUNT));
            var17.setParentProcessID((Integer)var14.get(ProcessInformation.ProcessPerformanceProperty.PARENTPROCESSID));
            var17.setPriority((Integer)var14.get(ProcessInformation.ProcessPerformanceProperty.PRIORITY));
            var12.put(var15, var17);
         }
      }

      return var12;
   }
}
