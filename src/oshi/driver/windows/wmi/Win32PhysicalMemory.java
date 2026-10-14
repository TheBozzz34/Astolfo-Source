package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32PhysicalMemory {
   private Win32PhysicalMemory() {
   }

   public static WmiResult<Win32PhysicalMemory.PhysicalMemoryProperty> queryphysicalMemory() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_PhysicalMemory", Win32PhysicalMemory.PhysicalMemoryProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public static WmiResult<Win32PhysicalMemory.PhysicalMemoryPropertyWin8> queryphysicalMemoryWin8() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_PhysicalMemory", Win32PhysicalMemory.PhysicalMemoryPropertyWin8.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum PhysicalMemoryProperty {
      BANKLABEL,
      CAPACITY,
      SPEED,
      MANUFACTURER,
      SMBIOSMEMORYTYPE;
   }

   public enum PhysicalMemoryPropertyWin8 {
      BANKLABEL,
      CAPACITY,
      SPEED,
      MANUFACTURER,
      MEMORYTYPE;
   }
}
