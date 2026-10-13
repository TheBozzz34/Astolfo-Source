package oshi.jna.platform.unix.freebsd;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.ptr.IntByReference;
import oshi.jna.platform.unix.CLibrary;

public interface FreeBsdLibc extends CLibrary {
   FreeBsdLibc INSTANCE = Native.load("libc", FreeBsdLibc.class);
   int UINT64_SIZE = Native.getNativeSize(long.class);
   int INT_SIZE = Native.getNativeSize(int.class);
   int CPUSTATES = 5;
   int CP_USER = 0;
   int CP_NICE = 1;
   int CP_SYS = 2;
   int CP_INTR = 3;
   int CP_IDLE = 4;

   int sysctl(int[] var1, int var2, Pointer var3, IntByReference var4, Pointer var5, int var6);

   int sysctlbyname(String var1, Pointer var2, IntByReference var3, Pointer var4, int var5);

   int sysctlnametomib(String var1, Pointer var2, IntByReference var3);

   @FieldOrder("cpu_ticks")
   class CpTime extends Structure {
      public long[] cpu_ticks = new long[5];
      private static final long a = 38185479421981L;
   }
}
