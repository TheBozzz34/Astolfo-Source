package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import java.util.Collection;
import java.util.Set;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Process {
   private static final long a = 80804208670179L;

   private Win32Process() {
   }

   public static WmiResult<Win32Process.CommandLineProperty> queryCommandLines(Set<Integer> var0) {
      StringBuilder var4 = new StringBuilder("Win32_Process");
      boolean var5 = true;

      for (Integer var7 : var0) {
         if (var5) {
            var4.append(" WHERE ProcessID=");
            var5 = false;
         } else {
            var4.append(" OR ProcessID=");
         }

         var4.append(var7);
      }

      WmiQuery var8 = new WmiQuery<>(var4.toString(), Win32Process.CommandLineProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var8);
   }

   public static WmiResult<Win32Process.ProcessXPProperty> queryProcesses(Collection<Integer> var0) {
      StringBuilder var4 = new StringBuilder("Win32_Process");
      if (var0 != null) {
         boolean var5 = true;

         for (Integer var7 : var0) {
            if (var5) {
               var4.append(" WHERE ProcessID=");
               var5 = false;
            } else {
               var4.append(" OR ProcessID=");
            }

            var4.append(var7);
         }
      }

      WmiQuery var8 = new WmiQuery<>(var4.toString(), Win32Process.ProcessXPProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(var8);
   }

   public enum CommandLineProperty {
      PROCESSID,
      COMMANDLINE;
   }

   public enum ProcessXPProperty {
      PROCESSID,
      NAME,
      KERNELMODETIME,
      USERMODETIME,
      THREADCOUNT,
      PAGEFILEUSAGE,
      HANDLECOUNT,
      EXECUTABLEPATH;
   }
}
