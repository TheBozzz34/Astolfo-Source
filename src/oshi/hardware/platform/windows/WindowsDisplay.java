package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.SetupApi.SP_DEVICE_INTERFACE_DATA;
import com.sun.jna.platform.win32.SetupApi.SP_DEVINFO_DATA;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;

@Immutable
public final class WindowsDisplay extends AbstractDisplay {
   private static final SetupApi SU = SetupApi.INSTANCE;
   private static final Advapi32 ADV = Advapi32.INSTANCE;
   private static final GUID GUID_DEVINTERFACE_MONITOR = new GUID("E6F07B5F-EE97-4a90-B076-33F57BF4EAA7");
   private static final long a = 81778597713438L;

   public WindowsDisplay(byte[] var1) {
      super(var1);
   }

   public static Display[] getDisplays() {
      ArrayList var3 = new ArrayList();
      HANDLE var4 = SU.SetupDiGetClassDevs(GUID_DEVINTERFACE_MONITOR, null, null, 18);
      if (!var4.equals(WinBase.INVALID_HANDLE_VALUE)) {
         SP_DEVICE_INTERFACE_DATA var5 = new SP_DEVICE_INTERFACE_DATA();
         var5.cbSize = var5.size();
         SP_DEVINFO_DATA var6 = new SP_DEVINFO_DATA();

         for (int var7 = 0; SU.SetupDiEnumDeviceInfo(var4, var7, var6); var7++) {
            HKEY var8 = SU.SetupDiOpenDevRegKey(var4, var6, 1, 0, 1, 1);
            byte[] var9 = new byte[1];
            IntByReference var10 = new IntByReference();
            IntByReference var11 = new IntByReference();
            if (ADV.RegQueryValueEx(var8, "EDID", 0, var10, var9, var11) == 234) {
               var9 = new byte[var11.getValue()];
               if (ADV.RegQueryValueEx(var8, "EDID", 0, var10, var9, var11) == 0) {
                  WindowsDisplay var12 = new WindowsDisplay(var9);
                  var3.add(var12);
               }
            }

            Advapi32.INSTANCE.RegCloseKey(var8);
         }

         SU.SetupDiDestroyDeviceInfoList(var4);
      }

      return (oshi.hardware.Display[])(var3.toArray(new Display[0]));
   }
}
