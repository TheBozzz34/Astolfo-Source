package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32DiskDrive {
   private static final long a = 40314074960735L;

   private Win32DiskDrive() {
   }

   public static WmiResult<Win32DiskDrive.DiskDriveProperty> queryDiskDrive() {
      WmiQuery var2 = new WmiQuery<>("Win32_DiskDrive", Win32DiskDrive.DiskDriveProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var2);
   }

   public static WmiResult<Win32DiskDrive.DeviceIdProperty> queryDiskDriveId(String var0) {
      WmiQuery var3 = new WmiQuery<>("Win32_DiskDrive" + var0, Win32DiskDrive.DeviceIdProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var3);
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
