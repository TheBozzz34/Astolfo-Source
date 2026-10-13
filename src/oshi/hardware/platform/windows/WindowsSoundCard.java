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

   public WindowsSoundCard(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public static List<WindowsSoundCard> getSoundCards() {
      ArrayList var3 = new ArrayList();
      String[] var4 = Advapi32Util.registryGetKeys(
         WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\"
      );

      for (String var8 : var4) {
         String var9 = "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\" + var8;

         try {
            if (Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, var9, "Driver")) {
               var3.add(
                  new WindowsSoundCard(
                     Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, var9, "Driver")
                        + " "
                        + Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, var9, "DriverVersion"),
                     Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, var9, "ProviderName")
                        + " "
                        + Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, var9, "DriverDesc"),
                     Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, var9, "DriverDesc")
                  )
               );
            }
         } catch (Win32Exception var11) {
            if (var11.getErrorCode() != 5) {
               throw var11;
            }
         }
      }

      return var3;
   }
}
