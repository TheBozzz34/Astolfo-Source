package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32LogicalDisk {
   private static final long a = 32820938067771L;
   private static final long e = -2090634369948450782L;

   private Win32LogicalDisk() {
   }

   public static WmiResult<Win32LogicalDisk.LogicalDiskProperty> queryLogicalDisk(String var0, boolean var1) {
      StringBuilder var5 = new StringBuilder("Win32_LogicalDisk");
      boolean var6 = false;
      if (var1) {
         var5.append(" WHERE DriveType != 4");
         var6 = true;
      }

      if (var0 != null) {
         var5.append(var6 ? " AND" : " WHERE").append(" Name=\"").append(var0).append((char)((int)e));
      }

      WmiQuery var7 = new WmiQuery<>(var5.toString(), Win32LogicalDisk.LogicalDiskProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var7);
   }

   public enum LogicalDiskProperty {
      DESCRIPTION,
      DRIVETYPE,
      FILESYSTEM,
      FREESPACE,
      NAME,
      PROVIDERNAME,
      SIZE,
      VOLUMENAME;
   }
}
