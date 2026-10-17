package oshi.software.common;

import com.sun.jna.Platform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystemVersion;
import oshi.util.Memoizer;

public abstract class AbstractOperatingSystem implements OperatingSystem {
   private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
   private final Supplier<AbstractOperatingSystem.FamilyVersionInfo> familyVersionInfo = Memoizer.memoize(this::queryFamilyVersionInfo);
   private final Supplier<Integer> bitness = Memoizer.memoize(this::queryPlatformBitness);
   private final Supplier<Boolean> elevated = Memoizer.memoize(this::queryElevated);
   public OperatingSystemVersion version;
   private static final Comparator<OSProcess> CPU_DESC_SORT = Comparator.comparingDouble(OSProcess::getProcessCpuLoadCumulative).reversed();
   private static final Comparator<OSProcess> RSS_DESC_SORT = Comparator.comparingLong(OSProcess::getResidentSetSize).reversed();
   private static final Comparator<OSProcess> UPTIME_ASC_SORT = Comparator.comparingLong(OSProcess::getUpTime);
   private static final Comparator<OSProcess> UPTIME_DESC_SORT = UPTIME_ASC_SORT.reversed();
   private static final Comparator<OSProcess> PID_ASC_SORT = Comparator.comparingInt(OSProcess::getProcessID);
   private static final Comparator<OSProcess> PARENTPID_ASC_SORT = Comparator.comparingInt(OSProcess::getParentProcessID);
   private static final Comparator<OSProcess> NAME_ASC_SORT = Comparator.comparing(OSProcess::getName, String.CASE_INSENSITIVE_ORDER);

   @Override
   public String getManufacturer() {
      return this.manufacturer.get();
   }

   public abstract String queryManufacturer();

   @Override
   public String getFamily() {
      return AbstractOperatingSystem.FamilyVersionInfo.familyOf(this.familyVersionInfo.get());
   }

   @Override
   public OperatingSystem.OSVersionInfo getVersionInfo() {
      return AbstractOperatingSystem.FamilyVersionInfo.versionInfoOf(this.familyVersionInfo.get());
   }

   public abstract AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo();

   @Override
   public OperatingSystemVersion getVersion() {
      return this.version;
   }

   @Override
   public int getBitness() {
      return this.bitness.get();
   }

   private int queryPlatformBitness() {
      if (Platform.is64Bit()) {
         return 64;
      }

      int i = System.getProperty("os.arch").indexOf("64") != -1 ? 64 : 32;
      return this.queryBitness(i);
   }

   public abstract int queryBitness(int i);

   @Override
   public boolean isElevated() {
      return this.elevated.get();
   }

   @Override
   public OSService[] getServices() {
      return new OSService[0];
   }

   public abstract boolean queryElevated();

   public List<OSProcess> processSort(List<OSProcess> processes, int limit, OperatingSystem.ProcessSort sortIn) {
      if (sortIn != null) {
         switch (sortIn) {
            case CPU:
               processes.sort(CPU_DESC_SORT);
               break;
            case MEMORY:
               processes.sort(RSS_DESC_SORT);
               break;
            case OLDEST:
               processes.sort(UPTIME_DESC_SORT);
               break;
            case NEWEST:
               processes.sort(UPTIME_ASC_SORT);
               break;
            case PID:
               processes.sort(PID_ASC_SORT);
               break;
            case PARENTPID:
               processes.sort(PARENTPID_ASC_SORT);
               break;
            case NAME:
               processes.sort(NAME_ASC_SORT);
               break;
            default:
               throw new IllegalArgumentException("Unimplemented enum type: " + sortIn.toString());
         }
      }

      int i = processes.size();
      if (limit > 0 && i > limit) {
         i = limit;
         ArrayList arrayList = new ArrayList();

         for (int j = 0; j < i; j++) {
            arrayList.add(processes.get(j));
         }

         return arrayList;
      } else {
         return processes;
      }
   }

   @Override
   public OSProcess[] getProcesses() {
      return this.getProcesses(0, null, false);
   }

   @Override
   public OSProcess[] getProcesses(int limit, OperatingSystem.ProcessSort sortIn) {
      return this.getProcesses(limit, sortIn, false);
   }

   @Override
   public List<OSProcess> getProcesses(Collection<Integer> pids) {
      return this.getProcesses(pids, true);
   }

   @Override
   public List<OSProcess> getProcesses(Collection<Integer> pids, boolean slowFields) {
      ArrayList arrayList = new ArrayList(pids.size());

      for (Integer integer : pids) {
         OSProcess osProcess = this.getProcess(integer, slowFields);
         if (osProcess != null) {
            arrayList.add(osProcess);
         }
      }

      return arrayList;
   }

   @Override
   public OSProcess getProcess(int pid) {
      return this.getProcess(pid, true);
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(this.getManufacturer()).append(' ').append(this.getFamily()).append(' ').append(this.getVersion().toString());
      return stringBuilder.toString();
   }

   public static final class FamilyVersionInfo {
      private final String family;
      private final OperatingSystem.OSVersionInfo versionInfo;

      public FamilyVersionInfo(String family, OperatingSystem.OSVersionInfo versionInfo) {
         this.family = family;
         this.versionInfo = versionInfo;
      }

      public static String familyOf(AbstractOperatingSystem.FamilyVersionInfo familyVersionInfo2) {
         return familyVersionInfo2.family;
      }

      public static OperatingSystem.OSVersionInfo versionInfoOf(AbstractOperatingSystem.FamilyVersionInfo familyVersionInfo2) {
         return familyVersionInfo2.versionInfo;
      }
   }
}
