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
   private static final long a = 9001266690894L;

   @Override
   public long d() {
      return this.availTotal.get().getA();
   }

   @Override
   public long f() {
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
      long var3 = 0L;
      long var5 = 0L;
      long var7 = 0L;
      long var9 = 0L;
      long var11 = 0L;

      for (String var17 : FileUtil.readFile(ProcPath.MEMINFO)) {
         String[] var18 = ParseUtil.Q.split(var17);
         if (var18.length > 1) {
            switch (var18[0]) {
               case "MemTotal:":
                  var11 = parseMeminfo(var18);
                  break;
               case "MemAvailable:":
                  long var13 = parseMeminfo(var18);
                  return new Pair<>(var13, var11);
               case "MemFree:":
                  var3 = parseMeminfo(var18);
                  break;
               case "Active(file):":
                  var5 = parseMeminfo(var18);
                  break;
               case "Inactive(file):":
                  var7 = parseMeminfo(var18);
                  break;
               case "SReclaimable:":
                  var9 = parseMeminfo(var18);
            }
         }
      }

      return new Pair<>(var3 + var5 + var7 + var9, var11);
   }

   private static long parseMeminfo(String[] var0) {
      if (var0.length < 2) {
         return 0L;
      }

      long var4 = ParseUtil.parseLongOrDefault(var0[1], 0L);
      if (var0.length > 2 && "kB".equals(var0[2])) {
         var4 *= 1024L;
      }

      return var4;
   }

   private static VirtualMemory createVirtualMemory() {
      return new LinuxVirtualMemory();
   }
}
