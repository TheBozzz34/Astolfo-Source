package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import java.util.Collection;
import java.util.Set;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Process {
   private Win32Process() {
   }

   public static WmiResult<Win32Process.CommandLineProperty> queryCommandLines(Set<Integer> pidsToQuery) {
      StringBuilder stringBuilder = new StringBuilder("Win32_Process");
      boolean bl = true;

      for (Integer integer : pidsToQuery) {
         if (bl) {
            stringBuilder.append(" WHERE ProcessID=");
            bl = false;
         } else {
            stringBuilder.append(" OR ProcessID=");
         }

         stringBuilder.append(integer);
      }

      WmiQuery wmiQuery = new WmiQuery<>(stringBuilder.toString(), Win32Process.CommandLineProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
   }

   public static WmiResult<Win32Process.ProcessXPProperty> queryProcesses(Collection<Integer> pids) {
      StringBuilder stringBuilder = new StringBuilder("Win32_Process");
      if (pids != null) {
         boolean bl = true;

         for (Integer integer : pids) {
            if (bl) {
               stringBuilder.append(" WHERE ProcessID=");
               bl = false;
            } else {
               stringBuilder.append(" OR ProcessID=");
            }

            stringBuilder.append(integer);
         }
      }

      WmiQuery wmiQuery = new WmiQuery<>(stringBuilder.toString(), Win32Process.ProcessXPProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(wmiQuery);
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
