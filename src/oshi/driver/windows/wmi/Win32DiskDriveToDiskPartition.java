package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32DiskDriveToDiskPartition {
   private static final long a = 65956840683914L;

   private Win32DiskDriveToDiskPartition() {
   }

   public static WmiResult<Win32DiskDriveToDiskPartition.DriveToPartitionProperty> queryDriveToPartition() {
      WmiQuery var2 = new WmiQuery<>("Win32_DiskDriveToDiskPartition", Win32DiskDriveToDiskPartition.DriveToPartitionProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var2);
   }

   public enum DriveToPartitionProperty {
      ANTECEDENT,
      DEPENDENT;
   }
}
