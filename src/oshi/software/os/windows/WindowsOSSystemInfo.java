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

   public WindowsOSSystemInfo(SYSTEM_INFO si) {
      this.z = si;
   }

   private void K() {
      SYSTEM_INFO systeM_INFO = new SYSTEM_INFO();
      Kernel32.INSTANCE.GetSystemInfo(systeM_INFO);

      try {
         IntByReference intByReference = new IntByReference();
         HANDLE handle = Kernel32.INSTANCE.GetCurrentProcess();
         if (Kernel32.INSTANCE.IsWow64Process(handle, intByReference) && intByReference.getValue() > 0) {
            Kernel32.INSTANCE.GetNativeSystemInfo(systeM_INFO);
         }
      } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      }

      this.z = systeM_INFO;
   }

   public int N() {
      return this.z.dwNumberOfProcessors.intValue();
   }
}
