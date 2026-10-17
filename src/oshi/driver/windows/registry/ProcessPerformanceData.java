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
   private ProcessPerformanceData() {
   }

   public static Map<Integer, OSProcess> buildProcessMapFromRegistry(OperatingSystem os, Collection<Integer> pids) {
      Triplet triplet = HkeyPerformanceDataUtil.readPerfDataFromRegistry("Process", ProcessInformation.ProcessPerformanceProperty.class);
      if (triplet == null) {
         return null;
      }

      List list = (List)triplet.getA();
      long l = (Long)triplet.getB();
      long m = (Long)triplet.getC();
      HashMap hashMap = new HashMap();

      for (Object object : list) {
         Map map = (Map)object;
         int i = (Integer)map.get(ProcessInformation.ProcessPerformanceProperty.PROCESSID);
         String string = (String)map.get(ProcessInformation.ProcessPerformanceProperty.NAME);
         if ((pids == null || pids.contains(i)) && !"_Total".equals(string)) {
            OSProcess osProcess = new OSProcess(os);
            osProcess.setProcessID(i);
            osProcess.setName(string);
            long n = (l - (Long)map.get(ProcessInformation.ProcessPerformanceProperty.CREATIONDATE)) / 10000L;
            osProcess.setUpTime(n < 1L ? 1L : n);
            osProcess.setStartTime(m - n);
            osProcess.setBytesRead((Long)map.get(ProcessInformation.ProcessPerformanceProperty.READTRANSFERCOUNT));
            osProcess.setBytesWritten((Long)map.get(ProcessInformation.ProcessPerformanceProperty.WRITETRANSFERCOUNT));
            osProcess.setResidentSetSize((Long)map.get(ProcessInformation.ProcessPerformanceProperty.PRIVATEPAGECOUNT));
            osProcess.setParentProcessID((Integer)map.get(ProcessInformation.ProcessPerformanceProperty.PARENTPROCESSID));
            osProcess.setPriority((Integer)map.get(ProcessInformation.ProcessPerformanceProperty.PRIORITY));
            hashMap.put(i, osProcess);
         }
      }

      return hashMap;
   }
}
