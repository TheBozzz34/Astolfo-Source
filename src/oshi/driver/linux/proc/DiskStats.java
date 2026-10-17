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
   private DiskStats() {
   }

   public static Map<String, Map<DiskStats.IoStat, Long>> getDiskStats() {
      HashMap hashMap = new HashMap();
      DiskStats.IoStat[] ioStats = DiskStats.IoStat.class.getEnumConstants();

      for (String string : FileUtil.readFile(ProcPath.DISKSTATS)) {
         String[] strings = ParseUtil.whitespaces.split(string.trim());
         EnumMap enumMap = new EnumMap<>(DiskStats.IoStat.class);
         String string2 = null;

         for (int i = 0; i < ioStats.length && i < strings.length; i++) {
            if (ioStats[i] == DiskStats.IoStat.NAME) {
               string2 = strings[i];
            } else {
               enumMap.put(ioStats[i], ParseUtil.parseLongOrDefault(strings[i], 0L));
            }
         }

         if (string2 != null) {
            hashMap.put(string2, enumMap);
         }
      }

      return hashMap;
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

      private static final DiskStats.IoStat[] VALUES = new DiskStats.IoStat[]{
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
