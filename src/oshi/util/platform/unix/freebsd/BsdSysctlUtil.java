package oshi.util.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;

@ThreadSafe
public final class BsdSysctlUtil {
   private static final long a = 137374768743723L;

   private BsdSysctlUtil() {
   }

   public static int sysctl(String var0, int var1) {
      IntByReference var5 = new IntByReference(FreeBsdLibc.INT_SIZE);
      Memory var6 = new Memory(var5.getValue());
      return 0 != FreeBsdLibc.INSTANCE.sysctlbyname(var0, var6, var5, null, 0) ? var1 : var6.getInt(0L);
   }

   public static long sysctl(String var0, long var1) {
      IntByReference var7 = new IntByReference(FreeBsdLibc.UINT64_SIZE);
      Memory var8 = new Memory(var7.getValue());
      return 0 != FreeBsdLibc.INSTANCE.sysctlbyname(var0, var8, var7, null, 0) ? var1 : var8.getLong(0L);
   }

   public static String sysctl(String var0, String var1) {
      IntByReference var4 = new IntByReference();
      if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(var0, null, var4, null, 0)) {
         return var1;
      }

      Memory var5 = new Memory(var4.getValue() + 1L);
      return 0 != FreeBsdLibc.INSTANCE.sysctlbyname(var0, var5, var4, null, 0) ? var1 : var5.getString(0L);
   }

   public static boolean sysctl(String var0, Structure var1) {
      if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(var0, var1.getPointer(), new IntByReference(var1.size()), null, 0)) {
         return false;
      }

      var1.read();
      return true;
   }
}
