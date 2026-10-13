package oshi.driver.linux.proc;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class DiskStats {
   private static final long a = 135567122227644L;

   private DiskStats() {
   }

   public static Map<String, Map<DiskStats.IoStat, Long>> getDiskStats() {
      HashMap var4 = new HashMap();
      DiskStats.IoStat[] var5 = DiskStats.IoStat.class.getEnumConstants();

      for (String var8 : FileUtil.readFile(ProcPath.DISKSTATS)) {
         String[] var9 = ParseUtil.Q.split(var8.trim());
         EnumMap var10 = new EnumMap<>(DiskStats.IoStat.class);
         String var11 = null;

         for (int var12 = 0; var12 < var5.length && var12 < var9.length; var12++) {
            if (var5[var12] == DiskStats.IoStat.NAME) {
               var11 = var9[var12];
            } else {
               var10.put(var5[var12], ParseUtil.parseLongOrDefault(var9[var12], 0L));
            }
         }

         if (var11 != null) {
            var4.put(var11, var10);
         }
      }

      return var4;
   }

   public enum IoStat {
      MAJOR,
      MINOR,
      NAME,
      READS,
      READS_MERGED,
      READS_SECTOR,
      READS_MS,
      WRITES,
      WRITES_MERGED,
      WRITES_SECTOR,
      WRITES_MS,
      IO_QUEUE_LENGTH,
      IO_MS,
      IO_MS_WEIGHTED,
      DISCARDS,
      DISCARDS_MERGED,
      DISCARDS_SECTOR,
      DISCARDS_MS,
      FLUSHES,
      FLUSHES_MS;

      private static final DiskStats.IoStat[] h = new DiskStats.IoStat[]{
         MAJOR,
         MINOR,
         NAME,
         READS,
         READS_MERGED,
         READS_SECTOR,
         READS_MS,
         WRITES,
         WRITES_MERGED,
         WRITES_SECTOR,
         WRITES_MS,
         IO_QUEUE_LENGTH,
         IO_MS,
         IO_MS_WEIGHTED,
         DISCARDS,
         DISCARDS_MERGED,
         DISCARDS_SECTOR,
         DISCARDS_MS,
         FLUSHES,
         FLUSHES_MS
      };
   }
}
