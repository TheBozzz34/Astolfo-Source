package oshi.hardware.platform.linux;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class LinuxGlobalMemory extends AbstractGlobalMemory {
   private final Supplier<Pair<Long, Long>> availTotal = Memoizer.memoize(LinuxGlobalMemory::readMemInfo, Memoizer.defaultExpiration());
   private final Supplier<Long> pageSize = Memoizer.memoize(LinuxGlobalMemory::queryPageSize);
   private final Supplier<VirtualMemory> vm = Memoizer.memoize(LinuxGlobalMemory::createVirtualMemory);

   @Override
   public long getAvailable() {
      return this.availTotal.get().getA();
   }

   @Override
   public long getTotal() {
      return this.availTotal.get().getB();
   }

   @Override
   public long getPageSize() {
      return this.pageSize.get();
   }

   @Override
   public VirtualMemory getVirtualMemory() {
      return this.vm.get();
   }

   private static long queryPageSize() {
      return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf PAGE_SIZE"), 4096L);
   }

   private static Pair<Long, Long> readMemInfo() {
      long l = 0L;
      long m = 0L;
      long n = 0L;
      long l2 = 0L;
      long l3 = 0L;

      for (String string : FileUtil.readFile(ProcPath.MEMINFO)) {
         String[] strings = ParseUtil.whitespaces.split(string);
         if (strings.length > 1) {
            switch (strings[0]) {
               case "MemTotal:":
                  l3 = parseMeminfo(strings);
                  break;
               case "MemAvailable:":
                  long l4 = parseMeminfo(strings);
                  return new Pair<>(l4, l3);
               case "MemFree:":
                  l = parseMeminfo(strings);
                  break;
               case "Active(file):":
                  m = parseMeminfo(strings);
                  break;
               case "Inactive(file):":
                  n = parseMeminfo(strings);
                  break;
               case "SReclaimable:":
                  l2 = parseMeminfo(strings);
            }
         }
      }

      return new Pair<>(l + m + n + l2, l3);
   }

   private static long parseMeminfo(String[] memorySplit) {
      if (memorySplit.length < 2) {
         return 0L;
      }

      long l = ParseUtil.parseLongOrDefault(memorySplit[1], 0L);
      if (memorySplit.length > 2 && "kB".equals(memorySplit[2])) {
         l *= 1024L;
      }

      return l;
   }

   private static VirtualMemory createVirtualMemory() {
      return new LinuxVirtualMemory();
   }
}
