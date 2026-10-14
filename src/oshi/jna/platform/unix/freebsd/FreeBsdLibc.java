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

   int sysctl(int[] ints, int i, Pointer pointer, IntByReference intByReference, Pointer pointer2, int j);

   int sysctlbyname(String string, Pointer pointer, IntByReference intByReference, Pointer pointer2, int i);

   int sysctlnametomib(String string, Pointer pointer, IntByReference intByReference);

   @FieldOrder("cpu_ticks")
   class CpTime extends Structure {
      public long[] cpu_ticks = new long[5];
   }
}
