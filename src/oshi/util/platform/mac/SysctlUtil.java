package oshi.util.platform.mac;

import com.sun.jna.Memory;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class SysctlUtil {
   private static final long a = 92821533603094L;

   private SysctlUtil() {
   }

   public static int sysctl(String var0, int var1) {
      IntByReference var5 = new IntByReference(SystemB.INT_SIZE);
      Memory var6 = new Memory(var5.getValue());
      return 0 != SystemB.INSTANCE.sysctlbyname(var0, var6, var5, null, 0) ? var1 : var6.getInt(0L);
   }

   public static long sysctl(String var0, long var1) {
      IntByReference var6 = new IntByReference(SystemB.UINT64_SIZE);
      Memory var7 = new Memory(var6.getValue());
      return 0 != SystemB.INSTANCE.sysctlbyname(var0, var7, var6, null, 0) ? var1 : var7.getLong(0L);
   }

   public static String sysctl(String var0, String var1) {
      IntByReference var4 = new IntByReference();
      if (0 != SystemB.INSTANCE.sysctlbyname(var0, null, var4, null, 0)) {
         return var1;
      }

      Memory var5 = new Memory(var4.getValue() + 1L);
      return 0 != SystemB.INSTANCE.sysctlbyname(var0, var5, var4, null, 0) ? var1 : var5.getString(0L);
   }

   public static boolean sysctl(String var0, Structure var1) {
      if (0 != SystemB.INSTANCE.sysctlbyname(var0, var1.getPointer(), new IntByReference(var1.size()), null, 0)) {
         return false;
      }

      var1.read();
      return true;
   }
}
