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
   private static final long a = 39210484513134L;

   @Override
   public long d() {
      return this.availTotalSize.get().v();
   }

   @Override
   public long f() {
      return this.availTotalSize.get().q();
   }

   @Override
   public long getPageSize() {
      return this.availTotalSize.get().U();
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
      PhysicalMemory[] var4;
      if (IS_WINDOWS10_OR_GREATER) {
         WmiResult var5 = Win32PhysicalMemory.queryphysicalMemory();
         var4 = new PhysicalMemory[var5.getResultCount()];

         for (int var6 = 0; var6 < var5.getResultCount(); var6++) {
            String var7 = WmiUtil.getString(var5, Win32PhysicalMemory.PhysicalMemoryProperty.BANKLABEL, var6);
            long var8 = WmiUtil.getUint64(var5, Win32PhysicalMemory.PhysicalMemoryProperty.CAPACITY, var6);
            long var10 = WmiUtil.getUint32(var5, Win32PhysicalMemory.PhysicalMemoryProperty.SPEED, var6) * 1000000L;
            String var12 = WmiUtil.getString(var5, Win32PhysicalMemory.PhysicalMemoryProperty.MANUFACTURER, var6);
            String var13 = smBiosMemoryType(WmiUtil.getUint32(var5, Win32PhysicalMemory.PhysicalMemoryProperty.SMBIOSMEMORYTYPE, var6));
            var4[var6] = new PhysicalMemory(var7, var8, var10, var12, var13);
         }
      } else {
         WmiResult var14 = Win32PhysicalMemory.queryphysicalMemoryWin8();
         var4 = new PhysicalMemory[var14.getResultCount()];

         for (int var15 = 0; var15 < var14.getResultCount(); var15++) {
            String var16 = WmiUtil.getString(var14, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.BANKLABEL, var15);
            long var17 = WmiUtil.getUint64(var14, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.CAPACITY, var15);
            long var18 = WmiUtil.getUint32(var14, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.SPEED, var15) * 1000000L;
            String var19 = WmiUtil.getString(var14, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.MANUFACTURER, var15);
            String var20 = memoryType(WmiUtil.getUint16(var14, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.MEMORYTYPE, var15));
            var4[var15] = new PhysicalMemory(var16, var17, var18, var19, var20);
         }
      }

      return var4;
   }

   private static String memoryType(int var0) {
      switch (var0) {
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

   private static String smBiosMemoryType(int var0) {
      switch (var0) {
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
      PERFORMANCE_INFORMATION var2 = new PERFORMANCE_INFORMATION();
      if (!Psapi.INSTANCE.GetPerformanceInfo(var2, var2.size())) {
         return new Triplet<>(0L, 0L, 4098L);
      }

      long var3 = var2.PageSize.longValue();
      long var5 = var3 * var2.PhysicalAvailable.longValue();
      long var7 = var3 * var2.PhysicalTotal.longValue();
      return new Triplet<>(var5, var7, var3);
   }
}
