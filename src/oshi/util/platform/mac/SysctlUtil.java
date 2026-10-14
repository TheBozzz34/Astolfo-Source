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

   public static int sysctl(String name, int def) {
      IntByReference intByReference = new IntByReference(SystemB.INT_SIZE);
      Memory memory = new Memory(intByReference.getValue());
      return 0 != SystemB.INSTANCE.sysctlbyname(name, memory, intByReference, null, 0) ? def : memory.getInt(0L);
   }

   public static long sysctl(String name, long def) {
      IntByReference intByReference = new IntByReference(SystemB.UINT64_SIZE);
      Memory memory = new Memory(intByReference.getValue());
      return 0 != SystemB.INSTANCE.sysctlbyname(name, memory, intByReference, null, 0) ? def : memory.getLong(0L);
   }

   public static String sysctl(String name, String def) {
      IntByReference intByReference = new IntByReference();
      if (0 != SystemB.INSTANCE.sysctlbyname(name, null, intByReference, null, 0)) {
         return def;
      }

      Memory memory = new Memory(intByReference.getValue() + 1L);
      return 0 != SystemB.INSTANCE.sysctlbyname(name, memory, intByReference, null, 0) ? def : memory.getString(0L);
   }

   public static boolean sysctl(String name, Structure struct) {
      if (0 != SystemB.INSTANCE.sysctlbyname(name, struct.getPointer(), new IntByReference(struct.size()), null, 0)) {
         return false;
      }

      struct.read();
      return true;
   }
}
