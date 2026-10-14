package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractSoundCard;

@Immutable
public final class WindowsSoundCard extends AbstractSoundCard {
   private static final long c = 2514283469435L;

   public WindowsSoundCard(String kernelVersion, String name, String codec) {
      super(kernelVersion, name, codec);
   }

   public static List<WindowsSoundCard> getSoundCards() {
      ArrayList arrayList = new ArrayList();
      String[] strings = Advapi32Util.registryGetKeys(
         WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\"
      );

      for (String string : strings) {
         String string2 = "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\" + string;

         try {
            if (Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, string2, "Driver")) {
               arrayList.add(
                  new WindowsSoundCard(
                     Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, string2, "Driver")
                        + " "
                        + Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, string2, "DriverVersion"),
                     Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, string2, "ProviderName")
                        + " "
                        + Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, string2, "DriverDesc"),
                     Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, string2, "DriverDesc")
                  )
               );
            }
         } catch (Win32Exception win32Exception) {
            if (win32Exception.getErrorCode() != 5) {
               throw win32Exception;
            }
         }
      }

      return arrayList;
   }
}
