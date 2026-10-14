package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32DiskPartition {
   private Win32DiskPartition() {
   }

   public static WmiResult<Win32DiskPartition.DiskPartitionProperty> queryPartition() {
      WmiQuery wmiQuery = new WmiQuery<>("Win32_DiskPartition", Win32DiskPartition.DiskPartitionProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public enum DiskPartitionProperty {
      INDEX,
      DESCRIPTION,
      DEVICEID,
      DISKINDEX,
      NAME,
      SIZE,
      TYPE;
   }
}
