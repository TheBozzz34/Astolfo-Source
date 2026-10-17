package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.sun.jna.platform.win32.Psapi.PERFORMANCE_INFORMATION;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.wmi.Win32PhysicalMemory;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.Memoizer;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class WindowsGlobalMemory extends AbstractGlobalMemory {
   private static final boolean IS_WINDOWS10_OR_GREATER = VersionHelpers.IsWindows10OrGreater();
   private final Supplier<Triplet<Long, Long, Long>> availTotalSize = Memoizer.memoize(WindowsGlobalMemory::readPerfInfo, Memoizer.defaultExpiration());
   private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);

   @Override
   public long getAvailable() {
      return this.availTotalSize.get().v();
   }

   @Override
   public long getTotal() {
      return this.availTotalSize.get().q();
   }

   @Override
   public long getPageSize() {
      return this.availTotalSize.get().getC();
   }

   @Override
   public VirtualMemory getVirtualMemory() {
      return this.vm.get();
   }

   private VirtualMemory createVirtualMemory() {
      return new WindowsVirtualMemory(this.getPageSize());
   }

   @Override
   public PhysicalMemory[] getPhysicalMemory() {
      PhysicalMemory[] physicalMemorys;
      if (IS_WINDOWS10_OR_GREATER) {
         WmiResult wmiResult = Win32PhysicalMemory.queryphysicalMemory();
         physicalMemorys = new PhysicalMemory[wmiResult.getResultCount()];

         for (int i = 0; i < wmiResult.getResultCount(); i++) {
            String string = WmiUtil.getString(wmiResult, Win32PhysicalMemory.PhysicalMemoryProperty.BANKLABEL, i);
            long l = WmiUtil.getUint64(wmiResult, Win32PhysicalMemory.PhysicalMemoryProperty.CAPACITY, i);
            long m = WmiUtil.getUint32(wmiResult, Win32PhysicalMemory.PhysicalMemoryProperty.SPEED, i) * 1000000L;
            String string2 = WmiUtil.getString(wmiResult, Win32PhysicalMemory.PhysicalMemoryProperty.MANUFACTURER, i);
            String string3 = smBiosMemoryType(WmiUtil.getUint32(wmiResult, Win32PhysicalMemory.PhysicalMemoryProperty.SMBIOSMEMORYTYPE, i));
            physicalMemorys[i] = new PhysicalMemory(string, l, m, string2, string3);
         }
      } else {
         WmiResult wmiResult2 = Win32PhysicalMemory.queryphysicalMemoryWin8();
         physicalMemorys = new PhysicalMemory[wmiResult2.getResultCount()];

         for (int j = 0; j < wmiResult2.getResultCount(); j++) {
            String string4 = WmiUtil.getString(wmiResult2, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.BANKLABEL, j);
            long n = WmiUtil.getUint64(wmiResult2, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.CAPACITY, j);
            long l2 = WmiUtil.getUint32(wmiResult2, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.SPEED, j) * 1000000L;
            String string5 = WmiUtil.getString(wmiResult2, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.MANUFACTURER, j);
            String string6 = memoryType(WmiUtil.getUint16(wmiResult2, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.MEMORYTYPE, j));
            physicalMemorys[j] = new PhysicalMemory(string4, n, l2, string5, string6);
         }
      }

      return physicalMemorys;
   }

   private static String memoryType(int typeIn) {
      switch (typeIn) {
         case 1:
            return "Other";
         case 2:
            return "DRAM";
         case 3:
            return "Synchronous DRAM";
         case 4:
            return "Cache DRAM";
         case 5:
            return "EDO";
         case 6:
            return "EDRAM";
         case 7:
            return "VRAM";
         case 8:
            return "SRAM";
         case 9:
            return "RAM";
         case 10:
            return "ROM";
         case 11:
            return "Flash";
         case 12:
            return "EEPROM";
         case 13:
            return "FEPROM";
         case 14:
            return "EPROM";
         case 15:
            return "CDRAM";
         case 16:
            return "3DRAM";
         case 17:
            return "SDRAM";
         case 18:
            return "SGRAM";
         case 19:
            return "RDRAM";
         case 20:
            return "DDR";
         case 21:
            return "DDR2";
         case 22:
            return "DDR2-FB-DIMM";
         case 23:
         default:
            return "Unknown";
         case 24:
            return "DDR3";
         case 25:
            return "FBD2";
      }
   }

   private static String smBiosMemoryType(int typeIn) {
      switch (typeIn) {
         case 1:
            return "Other";
         case 2:
         case 21:
         case 22:
         case 23:
         default:
            return "Unknown";
         case 3:
            return "DRAM";
         case 4:
            return "EDRAM";
         case 5:
            return "VRAM";
         case 6:
            return "SRAM";
         case 7:
            return "RAM";
         case 8:
            return "ROM";
         case 9:
            return "FLASH";
         case 10:
            return "EEPROM";
         case 11:
            return "FEPROM";
         case 12:
            return "EPROM";
         case 13:
            return "CDRAM";
         case 14:
            return "3DRAM";
         case 15:
            return "SDRAM";
         case 16:
            return "SGRAM";
         case 17:
            return "RDRAM";
         case 18:
            return "DDR";
         case 19:
            return "DDR2";
         case 20:
            return "DDR2 FB-DIMM";
         case 24:
            return "DDR3";
         case 25:
            return "FBD2";
         case 26:
            return "DDR4";
         case 27:
            return "LPDDR";
         case 28:
            return "LPDDR2";
         case 29:
            return "LPDDR3";
         case 30:
            return "LPDDR4";
         case 31:
            return "Logical non-volatile device";
      }
   }

   private static Triplet<Long, Long, Long> readPerfInfo() {
      PERFORMANCE_INFORMATION performancE_INFORMATION = new PERFORMANCE_INFORMATION();
      if (!Psapi.INSTANCE.GetPerformanceInfo(performancE_INFORMATION, performancE_INFORMATION.size())) {
         return new Triplet<>(0L, 0L, 4098L);
      }

      long l = performancE_INFORMATION.PageSize.longValue();
      long m = l * performancE_INFORMATION.PhysicalAvailable.longValue();
      long n = l * performancE_INFORMATION.PhysicalTotal.longValue();
      return new Triplet<>(m, n, l);
   }
}
