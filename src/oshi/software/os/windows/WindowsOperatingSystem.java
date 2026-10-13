package oshi.software.os.windows;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.W32ServiceManager;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.Wtsapi32;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jna.platform.win32.Advapi32Util.EventLogIterator;
import com.sun.jna.platform.win32.Advapi32Util.EventLogRecord;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTRByReference;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.sun.jna.platform.win32.Psapi.PERFORMANCE_INFORMATION;
import com.sun.jna.platform.win32.Tlhelp32.PROCESSENTRY32.ByReference;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.LUID;
import com.sun.jna.platform.win32.WinNT.LUID_AND_ATTRIBUTES;
import com.sun.jna.platform.win32.WinNT.TOKEN_PRIVILEGES;
import com.sun.jna.platform.win32.Winsvc.ENUM_SERVICE_STATUS_PROCESS;
import com.sun.jna.platform.win32.Wtsapi32.WTS_PROCESS_INFO_EX;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.ProcessInformation;
import oshi.driver.windows.registry.ProcessPerformanceData;
import oshi.driver.windows.wmi.Win32OperatingSystem;
import oshi.driver.windows.wmi.Win32Process;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.jna.platform.windows.Kernel32;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OperatingSystem;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class WindowsOperatingSystem extends AbstractOperatingSystem {
   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
   private static final boolean IS_WINDOWS7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();
   private static Supplier<String> systemLog = Memoizer.memoize(WindowsOperatingSystem::querySystemLog, TimeUnit.HOURS.toNanos(1L));
   private static final long x = querySystemBootTime();
   private Supplier<Map<Integer, OSProcess>> processMapFromRegistry = Memoizer.memoize(this::E, Memoizer.defaultExpiration());
   private Supplier<Map<Integer, OSProcess>> processMapFromPerfCounters = Memoizer.memoize(this::N, Memoizer.defaultExpiration());
   private static final long d = 40454780084428L;

   public WindowsOperatingSystem() {
      this.version = new WindowsOSVersionInfoEx();
   }

   @Override
   public String queryManufacturer() {
      return "Microsoft";
   }

   @Override
   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      WmiResult var4 = Win32OperatingSystem.queryOsVersion();
      if (var4.getResultCount() < 1) {
         return new AbstractOperatingSystem.FamilyVersionInfo("Windows", new OperatingSystem.OSVersionInfo(System.getProperty("os.version"), null, null));
      }

      int var5 = WmiUtil.getUint32(var4, Win32OperatingSystem.OSVersionProperty.SUITEMASK, 0);
      String var6 = WmiUtil.getString(var4, Win32OperatingSystem.OSVersionProperty.BUILDNUMBER, 0);
      String var7 = parseVersion(var4, var5, var6);
      String var8 = parseCodeName(var5);
      return new AbstractOperatingSystem.FamilyVersionInfo("Windows", new OperatingSystem.OSVersionInfo(var7, var8, var6));
   }

   private static String parseVersion(WmiResult<Win32OperatingSystem.OSVersionProperty> var0, int var1, String var2) {
      String var6 = System.getProperty("os.version");
      String[] var7 = WmiUtil.getString(var0, Win32OperatingSystem.OSVersionProperty.VERSION, 0).split("\\D");
      int var8 = var7.length > 0 ? ParseUtil.parseIntOrDefault(var7[0], 0) : 0;
      int var9 = var7.length > 1 ? ParseUtil.parseIntOrDefault(var7[1], 0) : 0;
      boolean var10 = WmiUtil.getUint32(var0, Win32OperatingSystem.OSVersionProperty.PRODUCTTYPE, 0) == 1;
      switch (var8) {
         case 5:
            if (var9 == 2) {
               if ((var1 & 32768) != 0) {
                  var6 = "Home Server";
               } else if (var10) {
                  var6 = "XP";
               } else {
                  var6 = User32.INSTANCE.GetSystemMetrics(89) != 0 ? "Server 2003" : "Server 2003 R2";
               }
            } else if (var9 == 1) {
               var6 = "XP";
            } else if (var9 == 0) {
               var6 = "2000";
            }
            break;
         case 6:
            if (var9 == 3) {
               var6 = var10 ? "8.1" : "Server 2012 R2";
            } else if (var9 == 2) {
               var6 = var10 ? "8" : "Server 2012";
            } else if (var9 == 1) {
               var6 = var10 ? "7" : "Server 2008 R2";
            } else if (var9 == 0) {
               var6 = var10 ? "Vista" : "Server 2008";
            }
            break;
         case 10:
            if (var9 == 0) {
               if (var10) {
                  var6 = "10";
               } else {
                  var6 = ParseUtil.parseLongOrDefault(var2, 0L) > 17762L ? "Server 2019" : "Server 2016";
               }
            }
      }

      String var11 = WmiUtil.getString(var0, Win32OperatingSystem.OSVersionProperty.CSDVERSION, 0);
      if (!var11.isEmpty() && !"unknown".equals(var11)) {
         var6 = var6 + " " + var11.replace("Service Pack ", "SP");
      }

      return var6;
   }

   private static String parseCodeName(int var0) {
      ArrayList var4 = new ArrayList();
      if ((var0 & 2) != 0) {
         var4.add("Enterprise");
      }

      if ((var0 & 4) != 0) {
         var4.add("BackOffice");
      }

      if ((var0 & 8) != 0) {
         var4.add("Communication Server");
      }

      if ((var0 & 128) != 0) {
         var4.add("Datacenter");
      }

      if ((var0 & 512) != 0) {
         var4.add("Home");
      }

      if ((var0 & 1024) != 0) {
         var4.add("Web Server");
      }

      if ((var0 & 8192) != 0) {
         var4.add("Storage Server");
      }

      if ((var0 & 16384) != 0) {
         var4.add("Compute Cluster");
      }

      return String.join(",", var4);
   }

   @Override
   public int queryBitness(int var1) {
      if (var1 < 64 && System.getenv("ProgramFiles(x86)") != null && IS_VISTA_OR_GREATER) {
         WmiResult var5 = Win32Processor.queryBitness();
         if (var5.getResultCount() > 0) {
            return WmiUtil.getUint16(var5, Win32Processor.BitnessProperty.ADDRESSWIDTH, 0);
         }
      }

      return var1;
   }

   @Override
   public boolean queryElevated() {
      try {
         File var3 = new File(System.getenv("windir") + "\\system32\\config\\systemprofile");
         return var3.isDirectory();
      } catch (SecurityException var4) {
         return false;
      }
   }

   @Override
   public FileSystem getFileSystem() {
      return new WindowsFileSystem();
   }

   @Override
   public OSProcess[] getProcesses(int var1, OperatingSystem.ProcessSort var2, boolean var3) {
      List var4 = this.processMapToList(null, var3);
      List var5 = this.processSort(var4, var1, var2);
      return (oshi.software.os.OSProcess[])(var5.toArray(new OSProcess[0]));
   }

   @Override
   public List<OSProcess> getProcesses(Collection<Integer> var1) {
      return this.processMapToList(var1, true);
   }

   @Override
   public OSProcess[] getChildProcesses(int var1, int var2, OperatingSystem.ProcessSort var3) {
      HashSet var8 = new HashSet();
      ByReference var9 = new ByReference();
      HANDLE var10 = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new DWORD(0L));

      try {
         while (Kernel32.INSTANCE.Process32Next(var10, var9)) {
            if (var9.th32ParentProcessID.intValue() == var1) {
               var8.add(var9.th32ProcessID.intValue());
            }
         }
      } finally {
         Kernel32.INSTANCE.CloseHandle(var10);
      }

      List var11 = this.getProcesses(var8);
      List var12 = this.processSort(var11, var2, var3);
      return (oshi.software.os.OSProcess[])(var12.toArray(new OSProcess[0]));
   }

   @Override
   public OSProcess getProcess(int var1, boolean var2) {
      List var6 = this.processMapToList(Arrays.asList(var1), var2);
      return var6.isEmpty() ? null : (OSProcess)var6.get(0);
   }

   private List<OSProcess> processMapToList(Collection<Integer> var1, boolean var2) {
      Map var6 = this.processMapFromRegistry.get();
      if (var6 == null) {
         var6 = var1 == null ? this.processMapFromPerfCounters.get() : this.buildProcessMapFromPerfCounters(var1);
      }

      ArrayList var7 = new ArrayList();
      ArrayList var8 = new ArrayList();
      int var9 = this.getProcessId();
      Pointer var10 = null;
      WTS_PROCESS_INFO_EX[] var11 = null;
      IntByReference var12 = new IntByReference(0);
      WmiResult var13 = null;
      if (IS_WINDOWS7_OR_GREATER) {
         PointerByReference var14 = new PointerByReference();
         if (!Wtsapi32.INSTANCE.WTSEnumerateProcessesEx(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, new IntByReference(1), -2, var14, var12)) {
            return new ArrayList<>(0);
         }

         var10 = var14.getValue();
         WTS_PROCESS_INFO_EX var15 = new WTS_PROCESS_INFO_EX(var10);
         var11 = (WTS_PROCESS_INFO_EX[])var15.toArray(var12.getValue());
      } else {
         var13 = Win32Process.queryProcesses(var1);
      }

      ArrayList var33 = new ArrayList();
      int var34 = IS_WINDOWS7_OR_GREATER ? var11.length : var13.getResultCount();

      for (int var16 = 0; var16 < var34; var16++) {
         int var17 = IS_WINDOWS7_OR_GREATER ? var11[var16].ProcessId : WmiUtil.getUint32(var13, Win32Process.ProcessXPProperty.PROCESSID, var16);
         OSProcess var18;
         if (var6.isEmpty()) {
            if (var1 != null && !var1.contains(var17)) {
               continue;
            }

            var18 = new OSProcess(this);
            var18.setProcessID(var17);
            var18.setName(IS_WINDOWS7_OR_GREATER ? var11[var16].pProcessName : WmiUtil.getString(var13, Win32Process.ProcessXPProperty.NAME, var16));
         } else {
            var18 = (OSProcess)var6.get(var17);
            if (var18 == null || var1 != null && !var1.contains(var17)) {
               continue;
            }
         }

         if (var17 == var9) {
            String var19 = new File(".").getAbsolutePath();
            var18.setCurrentWorkingDirectory(var19.isEmpty() ? "" : var19.substring(0, var19.length() - 1));
         }

         if (IS_WINDOWS7_OR_GREATER) {
            WTS_PROCESS_INFO_EX var40 = var11[var16];
            var18.setKernelTime(var40.KernelTime.getValue() / 10000L);
            var18.setUserTime(var40.UserTime.getValue() / 10000L);
            var18.setThreadCount(var40.NumberOfThreads);
            var18.setVirtualSize(var40.PagefileUsage & 4294967295L);
            var18.setOpenFiles(var40.HandleCount);
         } else {
            var18.setKernelTime(WmiUtil.getUint64(var13, Win32Process.ProcessXPProperty.KERNELMODETIME, var16) / 10000L);
            var18.setUserTime(WmiUtil.getUint64(var13, Win32Process.ProcessXPProperty.USERMODETIME, var16) / 10000L);
            var18.setThreadCount(WmiUtil.getUint32(var13, Win32Process.ProcessXPProperty.THREADCOUNT, var16));
            var18.setVirtualSize(1024L * (WmiUtil.getUint32(var13, Win32Process.ProcessXPProperty.PAGEFILEUSAGE, var16) & 4294967295L));
            var18.setOpenFiles(WmiUtil.getUint32(var13, Win32Process.ProcessXPProperty.HANDLECOUNT, var16));
         }

         HANDLE var41 = Kernel32.INSTANCE.OpenProcess(1024, false, var18.getProcessID());
         if (var41 != null) {
            var18.setBitness(this.getBitness());
            if (IS_VISTA_OR_GREATER && this.getBitness() == 64) {
               IntByReference var20 = new IntByReference(0);
               if (Kernel32.INSTANCE.IsWow64Process(var41, var20)) {
                  var18.setBitness(var20.getValue() > 0 ? 32 : 64);
               }
            }

            HANDLEByReference var43 = new HANDLEByReference();

            try {
               var18.setPath(
                  IS_WINDOWS7_OR_GREATER
                     ? Kernel32Util.QueryFullProcessImageName(var41, 0)
                     : WmiUtil.getString(var13, Win32Process.ProcessXPProperty.EXECUTABLEPATH, var16)
               );
               if (Advapi32.INSTANCE.OpenProcessToken(var41, 10, var43)) {
                  Account var21 = Advapi32Util.getTokenAccount(var43.getValue());
                  var18.setUser(var21.name);
                  var18.setUserID(var21.sidString);
                  if (var2) {
                     Account[] var22 = Advapi32Util.getTokenGroups(var43.getValue());
                     var7.clear();
                     var8.clear();

                     for (Account var26 : var22) {
                        var7.add(var26.name);
                        var8.add(var26.sidString);
                     }

                     var18.setGroup(String.join(",", var7));
                     var18.setGroupID(String.join(",", var8));
                  }
               } else {
                  int var45 = Kernel32.INSTANCE.GetLastError();
                  if (var45 != 5) {
                  }
               }
            } catch (Win32Exception var31) {
               this.handleWin32ExceptionOnGetProcessInfo(var18, var31);
            } finally {
               HANDLE var28 = var43.getValue();
               if (var28 != null) {
                  Kernel32.INSTANCE.CloseHandle(var28);
               }
            }

            Kernel32.INSTANCE.CloseHandle(var41);
         }

         var18.setState(OSProcess.State.RUNNING);
         var18.setCommandLine("");
         var33.add(var18);
      }

      if (var10 != null && !Wtsapi32.INSTANCE.WTSFreeMemoryEx(1, var10, var12.getValue())) {
         return new ArrayList<>(0);
      }

      if (var2) {
         HashSet var35 = new HashSet();
         if (var1 != null) {
            for (Object var38$raw : var33) {
               OSProcess var38 = (OSProcess)var38$raw;
               var35.add(var38.getProcessID());
            }
         }

         WmiResult var37 = Win32Process.queryCommandLines(var35);

         for (int var39 = 0; var39 < var37.getResultCount(); var39++) {
            int var42 = WmiUtil.getUint32(var37, Win32Process.CommandLineProperty.PROCESSID, var39);
            if (var6.containsKey(var42)) {
               OSProcess var44 = (OSProcess)var6.get(var42);
               var44.setCommandLine(WmiUtil.getString(var37, Win32Process.CommandLineProperty.COMMANDLINE, var39));
            }
         }
      }

      return var33;
   }

   public void handleWin32ExceptionOnGetProcessInfo(OSProcess var1, Win32Exception var2) {
   }

   private Map<Integer, OSProcess> E() {
      return ProcessPerformanceData.buildProcessMapFromRegistry(this, null);
   }

   private Map<Integer, OSProcess> N() {
      return this.buildProcessMapFromPerfCounters(null);
   }

   private Map<Integer, OSProcess> buildProcessMapFromPerfCounters(Collection<Integer> var1) {
      HashMap var5 = new HashMap();
      Pair var6 = ProcessInformation.queryProcessCounters();
      long var7 = System.currentTimeMillis();
      List var9 = (List)var6.getA();
      Map var10 = (Map)var6.getB();
      List var11 = (List)var10.get(ProcessInformation.ProcessPerformanceProperty.PROCESSID);
      List var12 = (List)var10.get(ProcessInformation.ProcessPerformanceProperty.PARENTPROCESSID);
      List var13 = (List)var10.get(ProcessInformation.ProcessPerformanceProperty.PRIORITY);
      List var14 = (List)var10.get(ProcessInformation.ProcessPerformanceProperty.READTRANSFERCOUNT);
      List var15 = (List)var10.get(ProcessInformation.ProcessPerformanceProperty.WRITETRANSFERCOUNT);
      List var16 = (List)var10.get(ProcessInformation.ProcessPerformanceProperty.PRIVATEPAGECOUNT);
      List var17 = (List)var10.get(ProcessInformation.ProcessPerformanceProperty.CREATIONDATE);

      for (int var18 = 0; var18 < var9.size(); var18++) {
         int var19 = ((Long)var11.get(var18)).intValue();
         if (var1 == null || var1.contains(var19)) {
            OSProcess var20 = new OSProcess(this);
            var5.put(var19, var20);
            var20.setProcessID(var19);
            var20.setName((String)var9.get(var18));
            var20.setParentProcessID(((Long)var12.get(var18)).intValue());
            var20.setPriority(((Long)var13.get(var18)).intValue());
            long var21 = (Long)var17.get(var18);
            if (var21 > var7) {
               var21 = FILETIME.filetimeToDate((int)(var21 >> 32), (int)(var21 & 4294967295L)).getTime();
            }

            var20.setUpTime(var7 - var21);
            var20.setStartTime(var21);
            var20.setBytesRead((Long)var14.get(var18));
            var20.setBytesWritten((Long)var15.get(var18));
            var20.setResidentSetSize((Long)var16.get(var18));
         }
      }

      return var5;
   }

   @Override
   public long getProcessAffinityMask(int var1) {
      HANDLE var4 = Kernel32.INSTANCE.OpenProcess(1024, false, var1);
      if (var4 != null) {
         ULONG_PTRByReference var5 = new ULONG_PTRByReference();
         ULONG_PTRByReference var6 = new ULONG_PTRByReference();
         if (Kernel32.INSTANCE.GetProcessAffinityMask(var4, var5, var6)) {
            return Pointer.nativeValue(var5.getValue().toPointer());
         }
      }

      return 0L;
   }

   @Override
   public int getProcessId() {
      return Kernel32.INSTANCE.GetCurrentProcessId();
   }

   @Override
   public int getProcessCount() {
      PERFORMANCE_INFORMATION var4 = new PERFORMANCE_INFORMATION();
      return !Psapi.INSTANCE.GetPerformanceInfo(var4, var4.size()) ? 0 : var4.ProcessCount.intValue();
   }

   @Override
   public int getThreadCount() {
      PERFORMANCE_INFORMATION var4 = new PERFORMANCE_INFORMATION();
      return !Psapi.INSTANCE.GetPerformanceInfo(var4, var4.size()) ? 0 : var4.ThreadCount.intValue();
   }

   @Override
   public long getSystemUptime() {
      return querySystemUptime();
   }

   private static long querySystemUptime() {
      return IS_VISTA_OR_GREATER ? Kernel32.INSTANCE.GetTickCount64() / 1000L : Kernel32.INSTANCE.GetTickCount() / 1000L;
   }

   @Override
   public long getSystemBootTime() {
      return x;
   }

   private static long querySystemBootTime() {
      String var2 = systemLog.get();
      if (var2 != null) {
         try {
            EventLogIterator var3 = new EventLogIterator(null, var2, 8);
            long var4 = 0L;

            while (var3.hasNext()) {
               EventLogRecord var6 = var3.next();
               if (var6.getStatusCode() == 12) {
                  return var6.getRecord().TimeGenerated.longValue();
               }

               if (var6.getStatusCode() == 6005) {
                  if (var4 > 0L) {
                     return var4;
                  }

                  var4 = var6.getRecord().TimeGenerated.longValue();
               }
            }

            if (var4 > 0L) {
               return var4;
            }
         } catch (Win32Exception var7) {
         }
      }

      return System.currentTimeMillis() / 1000L - querySystemUptime();
   }

   @Override
   public NetworkParams getNetworkParams() {
      return new WindowsNetworkParams();
   }

   private static void enableDebugPrivilege() {
      HANDLEByReference var2 = new HANDLEByReference();
      boolean var3 = Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 40, var2);
      if (var3) {
         LUID var4 = new LUID();
         var3 = Advapi32.INSTANCE.LookupPrivilegeValue(null, "SeDebugPrivilege", var4);
         if (!var3) {
            Kernel32.INSTANCE.CloseHandle(var2.getValue());
         } else {
            TOKEN_PRIVILEGES var5 = new TOKEN_PRIVILEGES(1);
            var5.Privileges[0] = new LUID_AND_ATTRIBUTES(var4, new DWORD(2L));
            var3 = Advapi32.INSTANCE.AdjustTokenPrivileges(var2.getValue(), false, var5, 0, null, null);
            Kernel32.INSTANCE.CloseHandle(var2.getValue());
         }
      }
   }

   @Override
   public OSService[] getServices() {
      try (W32ServiceManager var4 = new W32ServiceManager()) {
         var4.open(4);
         ENUM_SERVICE_STATUS_PROCESS[] var6 = var4.enumServicesStatusExProcess(48, 3, null);
         OSService[] var7 = new OSService[var6.length];

         for (int var8 = 0; var8 < var6.length; var8++) {
            OSService.State var9;
            switch (var6[var8].ServiceStatusProcess.dwCurrentState) {
               case 1:
                  var9 = OSService.State.STOPPED;
                  break;
               case 4:
                  var9 = OSService.State.RUNNING;
                  break;
               default:
                  var9 = OSService.State.OTHER;
            }

            var7[var8] = new OSService(var6[var8].lpDisplayName, var6[var8].ServiceStatusProcess.dwProcessId, var9);
         }

         return var7;
      } catch (Win32Exception var20) {
         return new OSService[0];
      }
   }

   private static String querySystemLog() {
      String var2 = GlobalConfig.get("", "System");
      if (var2.isEmpty()) {
         return null;
      }

      HANDLE var3 = Advapi32.INSTANCE.OpenEventLog(null, var2);
      return var3 == null ? null : var2;
   }

   static {
      enableDebugPrivilege();
   }
}
