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
   private static final Comparator<OSProcess> RSS_DESC_SORT = Comparator.comparingLong(OSProcess::h).reversed();
   private static final Comparator<OSProcess> UPTIME_ASC_SORT = Comparator.comparingLong(OSProcess::getUpTime);
   private static final Comparator<OSProcess> UPTIME_DESC_SORT = UPTIME_ASC_SORT.reversed();
   private static final Comparator<OSProcess> PID_ASC_SORT = Comparator.comparingInt(OSProcess::getProcessID);
   private static final Comparator<OSProcess> PARENTPID_ASC_SORT = Comparator.comparingInt(OSProcess::getParentProcessID);
   private static final Comparator<OSProcess> NAME_ASC_SORT = Comparator.comparing(OSProcess::getName, String.CASE_INSENSITIVE_ORDER);
   private static final long b = 114778679966102L;

   @Override
   public String getManufacturer() {
      return this.manufacturer.get();
   }

   public abstract String queryManufacturer();

   @Override
   public String getFamily() {
      return AbstractOperatingSystem.FamilyVersionInfo.X(this.familyVersionInfo.get());
   }

   @Override
   public OperatingSystem.OSVersionInfo getVersionInfo() {
      return AbstractOperatingSystem.FamilyVersionInfo.T(this.familyVersionInfo.get());
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

      int var4 = System.getProperty("os.arch").indexOf("64") != -1 ? 64 : 32;
      return this.queryBitness(var4);
   }

   public abstract int queryBitness(int var1);

   @Override
   public boolean isElevated() {
      return this.elevated.get();
   }

   @Override
   public OSService[] getServices() {
      return new OSService[0];
   }

   public abstract boolean queryElevated();

   public List<OSProcess> processSort(List<OSProcess> var1, int var2, OperatingSystem.ProcessSort var3) {
      if (var3 != null) {
         switch (var3) {
            case CPU:
               var1.sort(CPU_DESC_SORT);
               break;
            case MEMORY:
               var1.sort(RSS_DESC_SORT);
               break;
            case OLDEST:
               var1.sort(UPTIME_DESC_SORT);
               break;
            case NEWEST:
               var1.sort(UPTIME_ASC_SORT);
               break;
            case PID:
               var1.sort(PID_ASC_SORT);
               break;
            case PARENTPID:
               var1.sort(PARENTPID_ASC_SORT);
               break;
            case NAME:
               var1.sort(NAME_ASC_SORT);
               break;
            default:
               throw new IllegalArgumentException("Unimplemented enum type: " + var3.toString());
         }
      }

      int var7 = var1.size();
      if (var2 > 0 && var7 > var2) {
         var7 = var2;
         ArrayList var8 = new ArrayList();

         for (int var9 = 0; var9 < var7; var9++) {
            var8.add(var1.get(var9));
         }

         return var8;
      } else {
         return var1;
      }
   }

   @Override
   public OSProcess[] getProcesses() {
      return this.getProcesses(0, null, false);
   }

   @Override
   public OSProcess[] getProcesses(int var1, OperatingSystem.ProcessSort var2) {
      return this.getProcesses(var1, var2, false);
   }

   @Override
   public List<OSProcess> getProcesses(Collection<Integer> var1) {
      return this.getProcesses(var1, true);
   }

   @Override
   public List<OSProcess> getProcesses(Collection<Integer> var1, boolean var2) {
      ArrayList var6 = new ArrayList(var1.size());

      for (Integer var8 : var1) {
         OSProcess var9 = this.getProcess(var8, var2);
         if (var9 != null) {
            var6.add(var9);
         }
      }

      return var6;
   }

   @Override
   public OSProcess getProcess(int var1) {
      return this.getProcess(var1, true);
   }

   @Override
   public String toString() {
      StringBuilder var3 = new StringBuilder();
      var3.append(this.getManufacturer()).append(' ').append(this.getFamily()).append(' ').append(this.getVersion().toString());
      return var3.toString();
   }

   public static final class FamilyVersionInfo {
      private final String family;
      private final OperatingSystem.OSVersionInfo versionInfo;

      public FamilyVersionInfo(String var1, OperatingSystem.OSVersionInfo var2) {
         this.family = var1;
         this.versionInfo = var2;
      }

      public static String X(AbstractOperatingSystem.FamilyVersionInfo var0) {
         return var0.family;
      }

      public static OperatingSystem.OSVersionInfo T(AbstractOperatingSystem.FamilyVersionInfo var0) {
         return var0.versionInfo;
      }
   }
}
