package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32DiskDrive {
   private Win32DiskDrive() {
   }

   public static WmiResult<Win32DiskDrive.DiskDriveProperty> queryDiskDrive() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_DiskDrive", Win32DiskDrive.DiskDriveProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public static WmiResult<Win32DiskDrive.DeviceIdProperty> queryDiskDriveId(String whereClause) {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_DiskDrive" + whereClause, Win32DiskDrive.DeviceIdProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum DeviceIdProperty {
      PNPDEVICEID,
      SERIALNUMBER;
   }

   public enum DiskDriveProperty {
      INDEX,
      MANUFACTURER,
      MODEL,
      NAME,
      SERIALNUMBER,
      SIZE;
   }
}
