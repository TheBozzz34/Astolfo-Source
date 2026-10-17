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

   @Override
   public long getAvailable() {
      return this.available.get();
   }

   @Override
   public long getTotal() {
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
      ArrayList arrayList = new ArrayList();
      List list = ExecutingCommand.runNative("system_profiler SPMemoryDataType");
      int i = 0;
      String string = "unknown";
      long l = 0L;
      long m = 0L;
      String string2 = "unknown";
      String string3 = "unknown";

      for (Object object : list) {
         String string4 = (String)object;
         if (string4.trim().startsWith("BANK")) {
            if (i++ > 0) {
               arrayList.add(new PhysicalMemory(string, l, m, string2, string3));
            }

            string = string4.trim();
            int j = string.lastIndexOf(58);
            if (j > 0) {
               string = string.substring(0, j - 1);
            }
         } else if (i > 0) {
            String[] strings = string4.trim().split(":");
            if (strings.length == 2) {
               switch (strings[0]) {
                  case "Size":
                     l = ParseUtil.parseDecimalMemorySizeToBinary(strings[1].trim());
                     break;
                  case "Type":
                     string3 = strings[1].trim();
                     break;
                  case "Speed":
                     m = ParseUtil.parseHertz(strings[1]);
                     break;
                  case "Manufacturer":
                     string2 = strings[1].trim();
               }
            }
         }
      }

      arrayList.add(new PhysicalMemory(string, l, m, string2, string3));
      return (oshi.hardware.PhysicalMemory[])(arrayList.toArray(new PhysicalMemory[0]));
   }

   private long queryVmStats() {
      VMStatistics vmStatistics = new VMStatistics();
      return 0 != SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(), 2, vmStatistics, new IntByReference(vmStatistics.size() / SystemB.INT_SIZE))
         ? 0L
         : (vmStatistics.free_count + vmStatistics.inactive_count) * this.getPageSize();
   }

   private static long queryPhysMem() {
      return SysctlUtil.sysctl("hw.memsize", 0L);
   }

   private static long queryPageSize() {
      LongByReference longByReference = new LongByReference();
      return 0 == SystemB.INSTANCE.host_page_size(SystemB.INSTANCE.mach_host_self(), longByReference) ? longByReference.getValue() : 4098L;
   }

   private static VirtualMemory createVirtualMemory() {
      return new MacVirtualMemory();
   }
}
