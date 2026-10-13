package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.mac.SystemB.VMStatistics;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
public final class MacGlobalMemory extends AbstractGlobalMemory {
   private final Supplier<Long> available = Memoizer.memoize(this::queryVmStats, Memoizer.defaultExpiration());
   private final Supplier<Long> total = Memoizer.memoize(MacGlobalMemory::queryPhysMem);
   private final Supplier<Long> pageSize = Memoizer.memoize(MacGlobalMemory::queryPageSize);
   private final Supplier<VirtualMemory> vm = Memoizer.memoize(MacGlobalMemory::createVirtualMemory);
   private static final long a = 31056967371686L;
   private static final long j = 303024176254419002L;

   @Override
   public long d() {
      return this.available.get();
   }

   @Override
   public long f() {
      return this.total.get();
   }

   @Override
   public long getPageSize() {
      return this.pageSize.get();
   }

   @Override
   public VirtualMemory getVirtualMemory() {
      return this.vm.get();
   }

   @Override
   public PhysicalMemory[] getPhysicalMemory() {
      ArrayList var4 = new ArrayList();
      List var5 = ExecutingCommand.runNative("system_profiler SPMemoryDataType");
      int var6 = 0;
      String var7 = "unknown";
      long var8 = 0L;
      long var10 = 0L;
      String var12 = "unknown";
      String var13 = "unknown";

      for (Object var15$raw : var5) {
         String var15 = (String)var15$raw;
         if (var15.trim().startsWith("BANK")) {
            if (var6++ > 0) {
               var4.add(new PhysicalMemory(var7, var8, var10, var12, var13));
            }

            var7 = var15.trim();
            int var16 = var7.lastIndexOf((int)j);
            if (var16 > 0) {
               var7 = var7.substring(0, var16 - 1);
            }
         } else if (var6 > 0) {
            String[] var19 = var15.trim().split(":");
            if (var19.length == 2) {
               switch (var19[0]) {
                  case "Size":
                     var8 = ParseUtil.parseDecimalMemorySizeToBinary(var19[1].trim());
                     break;
                  case "Type":
                     var13 = var19[1].trim();
                     break;
                  case "Speed":
                     var10 = ParseUtil.parseHertz(var19[1]);
                     break;
                  case "Manufacturer":
                     var12 = var19[1].trim();
               }
            }
         }
      }

      var4.add(new PhysicalMemory(var7, var8, var10, var12, var13));
      return (oshi.hardware.PhysicalMemory[])(var4.toArray(new PhysicalMemory[0]));
   }

   private long queryVmStats() {
      VMStatistics var4 = new VMStatistics();
      return 0 != SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(), 2, var4, new IntByReference(var4.size() / SystemB.INT_SIZE))
         ? 0L
         : (var4.free_count + var4.inactive_count) * this.getPageSize();
   }

   private static long queryPhysMem() {
      return SysctlUtil.sysctl("hw.memsize", 0L);
   }

   private static long queryPageSize() {
      LongByReference var2 = new LongByReference();
      return 0 == SystemB.INSTANCE.host_page_size(SystemB.INSTANCE.mach_host_self(), var2) ? var2.getValue() : 4098L;
   }

   private static VirtualMemory createVirtualMemory() {
      return new MacVirtualMemory();
   }
}
