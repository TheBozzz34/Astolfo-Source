package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32LogicalDisk {
   private Win32LogicalDisk() {
   }

   public static WmiResult<Win32LogicalDisk.LogicalDiskProperty> queryLogicalDisk(String nameToMatch, boolean localOnly) {
      StringBuilder stringBuilder = new StringBuilder("Win32_LogicalDisk");
      boolean bl = false;
      if (localOnly) {
         stringBuilder.append(" WHERE DriveType != 4");
         bl = true;
      }

      if (nameToMatch != null) {
         stringBuilder.append(bl ? " AND" : " WHERE").append(" Name=\"").append(nameToMatch).append('"');
      }

      WmiQuery wmiQuery = new WmiQuery<>(stringBuilder.toString(), Win32LogicalDisk.LogicalDiskProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
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
