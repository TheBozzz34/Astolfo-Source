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

   public WindowsDisplay(byte[] edid) {
      super(edid);
   }

   public static Display[] getDisplays() {
      ArrayList arrayList = new ArrayList();
      HANDLE handle = SU.SetupDiGetClassDevs(GUID_DEVINTERFACE_MONITOR, null, null, 18);
      if (!handle.equals(WinBase.INVALID_HANDLE_VALUE)) {
         SP_DEVICE_INTERFACE_DATA sP_DEVICE_INTERFACE_DATA = new SP_DEVICE_INTERFACE_DATA();
         sP_DEVICE_INTERFACE_DATA.cbSize = sP_DEVICE_INTERFACE_DATA.size();
         SP_DEVINFO_DATA sP_DEVINFO_DATA = new SP_DEVINFO_DATA();

         for (int i = 0; SU.SetupDiEnumDeviceInfo(handle, i, sP_DEVINFO_DATA); i++) {
            HKEY hkey = SU.SetupDiOpenDevRegKey(handle, sP_DEVINFO_DATA, 1, 0, 1, 1);
            byte[] bytes = new byte[1];
            IntByReference intByReference = new IntByReference();
            IntByReference intByReference2 = new IntByReference();
            if (ADV.RegQueryValueEx(hkey, "EDID", 0, intByReference, bytes, intByReference2) == 234) {
               bytes = new byte[intByReference2.getValue()];
               if (ADV.RegQueryValueEx(hkey, "EDID", 0, intByReference, bytes, intByReference2) == 0) {
                  WindowsDisplay windowsDisplay = new WindowsDisplay(bytes);
                  arrayList.add(windowsDisplay);
               }
            }

            Advapi32.INSTANCE.RegCloseKey(hkey);
         }

         SU.SetupDiDestroyDeviceInfoList(handle);
      }

      return (oshi.hardware.Display[])(arrayList.toArray(new Display[0]));
   }
}
