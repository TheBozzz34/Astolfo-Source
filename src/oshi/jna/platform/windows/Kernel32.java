package oshi.jna.platform.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTRByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APIOptions;

public interface Kernel32 extends com.sun.jna.platform.win32.Kernel32 {
   Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);

   boolean GetProcessAffinityMask(HANDLE handle, ULONG_PTRByReference ulonG_PTRByReference, ULONG_PTRByReference ulonG_PTRByReference2);
}
