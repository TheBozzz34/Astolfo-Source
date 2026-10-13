package oshi.software.os.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class WindowsOSSystemInfo {
   private SYSTEM_INFO z = null;
   private static final long a = 28030844735658L;

   public WindowsOSSystemInfo() {
      this.K();
   }

   public WindowsOSSystemInfo(SYSTEM_INFO var1) {
      this.z = var1;
   }

   private void K() {
      SYSTEM_INFO var4 = new SYSTEM_INFO();
      Kernel32.INSTANCE.GetSystemInfo(var4);

      try {
         IntByReference var5 = new IntByReference();
         HANDLE var6 = Kernel32.INSTANCE.GetCurrentProcess();
         if (Kernel32.INSTANCE.IsWow64Process(var6, var5) && var5.getValue() > 0) {
            Kernel32.INSTANCE.GetNativeSystemInfo(var4);
         }
      } catch (UnsatisfiedLinkError var7) {
      }

      this.z = var4;
   }

   public int N() {
      return this.z.dwNumberOfProcessors.intValue();
   }
}
