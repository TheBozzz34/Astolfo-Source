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
   private static final long BOOTTIME = querySystemBootTime();
   private Supplier<Map<Integer, OSProcess>> processMapFromRegistry = Memoizer.memoize(this::queryProcessMapFromRegistry, Memoizer.defaultExpiration());
   private Supplier<Map<Integer, OSProcess>> processMapFromPerfCounters = Memoizer.memoize(this::queryProcessMapFromPerfCounters, Memoizer.defaultExpiration());

   public WindowsOperatingSystem() {
      this.version = new WindowsOSVersionInfoEx();
   }

   @Override
   public String queryManufacturer() {
      return "Microsoft";
   }

   @Override
   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      WmiResult wmiResult = Win32OperatingSystem.queryOsVersion();
      if (wmiResult.getResultCount() < 1) {
         return new AbstractOperatingSystem.FamilyVersionInfo("Windows", new OperatingSystem.OSVersionInfo(System.getProperty("os.version"), null, null));
      }

      int i = WmiUtil.getUint32(wmiResult, Win32OperatingSystem.OSVersionProperty.SUITEMASK, 0);
      String string = WmiUtil.getString(wmiResult, Win32OperatingSystem.OSVersionProperty.BUILDNUMBER, 0);
      String string2 = parseVersion(wmiResult, i, string);
      String string3 = parseCodeName(i);
      return new AbstractOperatingSystem.FamilyVersionInfo("Windows", new OperatingSystem.OSVersionInfo(string2, string3, string));
   }

   private static String parseVersion(WmiResult<Win32OperatingSystem.OSVersionProperty> versionInfo, int suiteMask, String buildNumber) {
      String string = System.getProperty("os.version");
      String[] strings = WmiUtil.getString(versionInfo, Win32OperatingSystem.OSVersionProperty.VERSION, 0).split("\\D");
      int i = strings.length > 0 ? ParseUtil.parseIntOrDefault(strings[0], 0) : 0;
      int j = strings.length > 1 ? ParseUtil.parseIntOrDefault(strings[1], 0) : 0;
      boolean bl = WmiUtil.getUint32(versionInfo, Win32OperatingSystem.OSVersionProperty.PRODUCTTYPE, 0) == 1;
      switch (i) {
         case 5:
            if (j == 2) {
               if ((suiteMask & 32768) != 0) {
                  string = "Home Server";
               } else if (bl) {
                  string = "XP";
               } else {
                  string = User32.INSTANCE.GetSystemMetrics(89) != 0 ? "Server 2003" : "Server 2003 R2";
               }
            } else if (j == 1) {
               string = "XP";
            } else if (j == 0) {
               string = "2000";
            }
            break;
         case 6:
            if (j == 3) {
               string = bl ? "8.1" : "Server 2012 R2";
            } else if (j == 2) {
               string = bl ? "8" : "Server 2012";
            } else if (j == 1) {
               string = bl ? "7" : "Server 2008 R2";
            } else if (j == 0) {
               string = bl ? "Vista" : "Server 2008";
            }
            break;
         case 10:
            if (j == 0) {
               if (bl) {
                  string = "10";
               } else {
                  string = ParseUtil.parseLongOrDefault(buildNumber, 0L) > 17762L ? "Server 2019" : "Server 2016";
               }
            }
      }

      String string2 = WmiUtil.getString(versionInfo, Win32OperatingSystem.OSVersionProperty.CSDVERSION, 0);
      if (!string2.isEmpty() && !"unknown".equals(string2)) {
         string = string + " " + string2.replace("Service Pack ", "SP");
      }

      return string;
   }

   private static String parseCodeName(int suiteMask) {
      ArrayList arrayList = new ArrayList();
      if ((suiteMask & 2) != 0) {
         arrayList.add("Enterprise");
      }

      if ((suiteMask & 4) != 0) {
         arrayList.add("BackOffice");
      }

      if ((suiteMask & 8) != 0) {
         arrayList.add("Communication Server");
      }

      if ((suiteMask & 128) != 0) {
         arrayList.add("Datacenter");
      }

      if ((suiteMask & 512) != 0) {
         arrayList.add("Home");
      }

      if ((suiteMask & 1024) != 0) {
         arrayList.add("Web Server");
      }

      if ((suiteMask & 8192) != 0) {
         arrayList.add("Storage Server");
      }

      if ((suiteMask & 16384) != 0) {
         arrayList.add("Compute Cluster");
      }

      return String.join(",", arrayList);
   }

   @Override
   public int queryBitness(int jvmBitness) {
      if (jvmBitness < 64 && System.getenv("ProgramFiles(x86)") != null && IS_VISTA_OR_GREATER) {
         WmiResult wmiResult = Win32Processor.queryBitness();
         if (wmiResult.getResultCount() > 0) {
            return WmiUtil.getUint16(wmiResult, Win32Processor.BitnessProperty.ADDRESSWIDTH, 0);
         }
      }

      return jvmBitness;
   }

   @Override
   public boolean queryElevated() {
      try {
         File file = new File(System.getenv("windir") + "\\system32\\config\\systemprofile");
         return file.isDirectory();
      } catch (SecurityException securityException) {
         return false;
      }
   }

   @Override
   public FileSystem getFileSystem() {
      return new WindowsFileSystem();
   }

   @Override
   public OSProcess[] getProcesses(int limit, OperatingSystem.ProcessSort sortIn, boolean slowFields) {
      List<OSProcess> procList = this.processMapToList(null, slowFields);
      List<OSProcess> sorted = this.processSort(procList, limit, sortIn);
      return sorted.toArray(new OSProcess[0]);
   }

   @Override
   public List<OSProcess> getProcesses(Collection<Integer> pids) {
      return this.processMapToList(pids, true);
   }

   @Override
   public OSProcess[] getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sortIn) {
      HashSet hashSet = new HashSet();
      ByReference byReference = new ByReference();
      HANDLE handle = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new DWORD(0L));

      try {
         while (Kernel32.INSTANCE.Process32Next(handle, byReference)) {
            if (byReference.th32ParentProcessID.intValue() == parentPid) {
               hashSet.add(byReference.th32ProcessID.intValue());
            }
         }
      } finally {
         Kernel32.INSTANCE.CloseHandle(handle);
      }

      List list = this.getProcesses(hashSet);
      List list2 = this.processSort(list, limit, sortIn);
      return (oshi.software.os.OSProcess[])(list2.toArray(new OSProcess[0]));
   }

   @Override
   public OSProcess getProcess(int pid, boolean slowFields) {
      List list = this.processMapToList(Arrays.asList(pid), slowFields);
      return list.isEmpty() ? null : (OSProcess)list.get(0);
   }

   private List<OSProcess> processMapToList(Collection<Integer> pids, boolean slowFields) {
      Map map = this.processMapFromRegistry.get();
      if (map == null) {
         map = pids == null ? this.processMapFromPerfCounters.get() : this.buildProcessMapFromPerfCounters(pids);
      }

      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      int i = this.getProcessId();
      Pointer pointer = null;
      WTS_PROCESS_INFO_EX[] wtS_PROCESS_INFO_EXs = null;
      IntByReference intByReference = new IntByReference(0);
      WmiResult wmiResult = null;
      if (IS_WINDOWS7_OR_GREATER) {
         PointerByReference pointerByReference = new PointerByReference();
         if (!Wtsapi32.INSTANCE.WTSEnumerateProcessesEx(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, new IntByReference(1), -2, pointerByReference, intByReference)) {
            return new ArrayList<>(0);
         }

         pointer = pointerByReference.getValue();
         WTS_PROCESS_INFO_EX wtS_PROCESS_INFO_EX = new WTS_PROCESS_INFO_EX(pointer);
         wtS_PROCESS_INFO_EXs = (WTS_PROCESS_INFO_EX[])wtS_PROCESS_INFO_EX.toArray(intByReference.getValue());
      } else {
         wmiResult = Win32Process.queryProcesses(pids);
      }

      ArrayList arrayList3 = new ArrayList();
      int j = IS_WINDOWS7_OR_GREATER ? wtS_PROCESS_INFO_EXs.length : wmiResult.getResultCount();

      for (int k = 0; k < j; k++) {
         int l = IS_WINDOWS7_OR_GREATER ? wtS_PROCESS_INFO_EXs[k].ProcessId : WmiUtil.getUint32(wmiResult, Win32Process.ProcessXPProperty.PROCESSID, k);
         OSProcess osProcess;
         if (map.isEmpty()) {
            if (pids != null && !pids.contains(l)) {
               continue;
            }

            osProcess = new OSProcess(this);
            osProcess.setProcessID(l);
            osProcess.setName(IS_WINDOWS7_OR_GREATER ? wtS_PROCESS_INFO_EXs[k].pProcessName : WmiUtil.getString(wmiResult, Win32Process.ProcessXPProperty.NAME, k));
         } else {
            osProcess = (OSProcess)map.get(l);
            if (osProcess == null || pids != null && !pids.contains(l)) {
               continue;
            }
         }

         if (l == i) {
            String string = new File(".").getAbsolutePath();
            osProcess.setCurrentWorkingDirectory(string.isEmpty() ? "" : string.substring(0, string.length() - 1));
         }

         if (IS_WINDOWS7_OR_GREATER) {
            WTS_PROCESS_INFO_EX wtS_PROCESS_INFO_EX2 = wtS_PROCESS_INFO_EXs[k];
            osProcess.setKernelTime(wtS_PROCESS_INFO_EX2.KernelTime.getValue() / 10000L);
            osProcess.setUserTime(wtS_PROCESS_INFO_EX2.UserTime.getValue() / 10000L);
            osProcess.setThreadCount(wtS_PROCESS_INFO_EX2.NumberOfThreads);
            osProcess.setVirtualSize(wtS_PROCESS_INFO_EX2.PagefileUsage & 4294967295L);
            osProcess.setOpenFiles(wtS_PROCESS_INFO_EX2.HandleCount);
         } else {
            osProcess.setKernelTime(WmiUtil.getUint64(wmiResult, Win32Process.ProcessXPProperty.KERNELMODETIME, k) / 10000L);
            osProcess.setUserTime(WmiUtil.getUint64(wmiResult, Win32Process.ProcessXPProperty.USERMODETIME, k) / 10000L);
            osProcess.setThreadCount(WmiUtil.getUint32(wmiResult, Win32Process.ProcessXPProperty.THREADCOUNT, k));
            osProcess.setVirtualSize(1024L * (WmiUtil.getUint32(wmiResult, Win32Process.ProcessXPProperty.PAGEFILEUSAGE, k) & 4294967295L));
            osProcess.setOpenFiles(WmiUtil.getUint32(wmiResult, Win32Process.ProcessXPProperty.HANDLECOUNT, k));
         }

         HANDLE handle = Kernel32.INSTANCE.OpenProcess(1024, false, osProcess.getProcessID());
         if (handle != null) {
            osProcess.setBitness(this.getBitness());
            if (IS_VISTA_OR_GREATER && this.getBitness() == 64) {
               IntByReference intByReference2 = new IntByReference(0);
               if (Kernel32.INSTANCE.IsWow64Process(handle, intByReference2)) {
                  osProcess.setBitness(intByReference2.getValue() > 0 ? 32 : 64);
               }
            }

            HANDLEByReference handleByReference = new HANDLEByReference();

            try {
               osProcess.setPath(
                  IS_WINDOWS7_OR_GREATER
                     ? Kernel32Util.QueryFullProcessImageName(handle, 0)
                     : WmiUtil.getString(wmiResult, Win32Process.ProcessXPProperty.EXECUTABLEPATH, k)
               );
               if (Advapi32.INSTANCE.OpenProcessToken(handle, 10, handleByReference)) {
                  Account account = Advapi32Util.getTokenAccount(handleByReference.getValue());
                  osProcess.setUser(account.name);
                  osProcess.setUserID(account.sidString);
                  if (slowFields) {
                     Account[] accounts = Advapi32Util.getTokenGroups(handleByReference.getValue());
                     arrayList.clear();
                     arrayList2.clear();

                     for (Account account2 : accounts) {
                        arrayList.add(account2.name);
                        arrayList2.add(account2.sidString);
                     }

                     osProcess.setGroup(String.join(",", arrayList));
                     osProcess.setGroupID(String.join(",", arrayList2));
                  }
               } else {
                  int m = Kernel32.INSTANCE.GetLastError();
                  if (m != 5) {
                  }
               }
            } catch (Win32Exception win32Exception) {
               this.handleWin32ExceptionOnGetProcessInfo(osProcess, win32Exception);
            } finally {
               HANDLE handle2 = handleByReference.getValue();
               if (handle2 != null) {
                  Kernel32.INSTANCE.CloseHandle(handle2);
               }
            }

            Kernel32.INSTANCE.CloseHandle(handle);
         }

         osProcess.setState(OSProcess.State.RUNNING);
         osProcess.setCommandLine("");
         arrayList3.add(osProcess);
      }

      if (pointer != null && !Wtsapi32.INSTANCE.WTSFreeMemoryEx(1, pointer, intByReference.getValue())) {
         return new ArrayList<>(0);
      }

      if (slowFields) {
         HashSet hashSet = new HashSet();
         if (pids != null) {
            for (Object object : arrayList3) {
               OSProcess osProcess2 = (OSProcess)object;
               hashSet.add(osProcess2.getProcessID());
            }
         }

         WmiResult wmiResult2 = Win32Process.queryCommandLines(hashSet);

         for (int n = 0; n < wmiResult2.getResultCount(); n++) {
            int i2 = WmiUtil.getUint32(wmiResult2, Win32Process.CommandLineProperty.PROCESSID, n);
            if (map.containsKey(i2)) {
               OSProcess osProcess3 = (OSProcess)map.get(i2);
               osProcess3.setCommandLine(WmiUtil.getString(wmiResult2, Win32Process.CommandLineProperty.COMMANDLINE, n));
            }
         }
      }

      return arrayList3;
   }

   public void handleWin32ExceptionOnGetProcessInfo(OSProcess proc, Win32Exception ex) {
   }

   private Map<Integer, OSProcess> queryProcessMapFromRegistry() {
      return ProcessPerformanceData.buildProcessMapFromRegistry(this, null);
   }

   private Map<Integer, OSProcess> queryProcessMapFromPerfCounters() {
      return this.buildProcessMapFromPerfCounters(null);
   }

   private Map<Integer, OSProcess> buildProcessMapFromPerfCounters(Collection<Integer> pids) {
      HashMap hashMap = new HashMap();
      Pair pair = ProcessInformation.queryProcessCounters();
      long l = System.currentTimeMillis();
      List list = (List)pair.getA();
      Map map = (Map)pair.getB();
      List list2 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.PROCESSID);
      List list3 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.PARENTPROCESSID);
      List list4 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.PRIORITY);
      List list5 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.READTRANSFERCOUNT);
      List list6 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.WRITETRANSFERCOUNT);
      List list7 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.PRIVATEPAGECOUNT);
      List list8 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.CREATIONDATE);

      for (int i = 0; i < list.size(); i++) {
         int j = ((Long)list2.get(i)).intValue();
         if (pids == null || pids.contains(j)) {
            OSProcess osProcess = new OSProcess(this);
            hashMap.put(j, osProcess);
            osProcess.setProcessID(j);
            osProcess.setName((String)list.get(i));
            osProcess.setParentProcessID(((Long)list3.get(i)).intValue());
            osProcess.setPriority(((Long)list4.get(i)).intValue());
            long m = (Long)list8.get(i);
            if (m > l) {
               m = FILETIME.filetimeToDate((int)(m >> 32), (int)(m & 4294967295L)).getTime();
            }

            osProcess.setUpTime(l - m);
            osProcess.setStartTime(m);
            osProcess.setBytesRead((Long)list5.get(i));
            osProcess.setBytesWritten((Long)list6.get(i));
            osProcess.setResidentSetSize((Long)list7.get(i));
         }
      }

      return hashMap;
   }

   @Override
   public long getProcessAffinityMask(int processId) {
      HANDLE handle = Kernel32.INSTANCE.OpenProcess(1024, false, processId);
      if (handle != null) {
         ULONG_PTRByReference ulonG_PTRByReference = new ULONG_PTRByReference();
         ULONG_PTRByReference ulonG_PTRByReference2 = new ULONG_PTRByReference();
         if (Kernel32.INSTANCE.GetProcessAffinityMask(handle, ulonG_PTRByReference, ulonG_PTRByReference2)) {
            return Pointer.nativeValue(ulonG_PTRByReference.getValue().toPointer());
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
      PERFORMANCE_INFORMATION performancE_INFORMATION = new PERFORMANCE_INFORMATION();
      return !Psapi.INSTANCE.GetPerformanceInfo(performancE_INFORMATION, performancE_INFORMATION.size()) ? 0 : performancE_INFORMATION.ProcessCount.intValue();
   }

   @Override
   public int getThreadCount() {
      PERFORMANCE_INFORMATION performancE_INFORMATION = new PERFORMANCE_INFORMATION();
      return !Psapi.INSTANCE.GetPerformanceInfo(performancE_INFORMATION, performancE_INFORMATION.size()) ? 0 : performancE_INFORMATION.ThreadCount.intValue();
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
      return BOOTTIME;
   }

   private static long querySystemBootTime() {
      String string = systemLog.get();
      if (string != null) {
         try {
            EventLogIterator eventLogIterator = new EventLogIterator(null, string, 8);
            long l = 0L;

            while (eventLogIterator.hasNext()) {
               EventLogRecord eventLogRecord = eventLogIterator.next();
               if (eventLogRecord.getStatusCode() == 12) {
                  return eventLogRecord.getRecord().TimeGenerated.longValue();
               }

               if (eventLogRecord.getStatusCode() == 6005) {
                  if (l > 0L) {
                     return l;
                  }

                  l = eventLogRecord.getRecord().TimeGenerated.longValue();
               }
            }

            if (l > 0L) {
               return l;
            }
         } catch (Win32Exception win32Exception) {
         }
      }

      return System.currentTimeMillis() / 1000L - querySystemUptime();
   }

   @Override
   public NetworkParams getNetworkParams() {
      return new WindowsNetworkParams();
   }

   private static void enableDebugPrivilege() {
      HANDLEByReference handleByReference = new HANDLEByReference();
      boolean bl = Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 40, handleByReference);
      if (bl) {
         LUID luid = new LUID();
         bl = Advapi32.INSTANCE.LookupPrivilegeValue(null, "SeDebugPrivilege", luid);
         if (!bl) {
            Kernel32.INSTANCE.CloseHandle(handleByReference.getValue());
         } else {
            TOKEN_PRIVILEGES tokeN_PRIVILEGES = new TOKEN_PRIVILEGES(1);
            tokeN_PRIVILEGES.Privileges[0] = new LUID_AND_ATTRIBUTES(luid, new DWORD(2L));
            bl = Advapi32.INSTANCE.AdjustTokenPrivileges(handleByReference.getValue(), false, tokeN_PRIVILEGES, 0, null, null);
            Kernel32.INSTANCE.CloseHandle(handleByReference.getValue());
         }
      }
   }

   @Override
   public OSService[] getServices() {
      try (W32ServiceManager w32ServiceManager = new W32ServiceManager()) {
         w32ServiceManager.open(4);
         ENUM_SERVICE_STATUS_PROCESS[] enuM_SERVICE_STATUS_PROCESSs = w32ServiceManager.enumServicesStatusExProcess(48, 3, null);
         OSService[] osServices = new OSService[enuM_SERVICE_STATUS_PROCESSs.length];

         for (int i = 0; i < enuM_SERVICE_STATUS_PROCESSs.length; i++) {
            OSService.State state;
            switch (enuM_SERVICE_STATUS_PROCESSs[i].ServiceStatusProcess.dwCurrentState) {
               case 1:
                  state = OSService.State.STOPPED;
                  break;
               case 4:
                  state = OSService.State.RUNNING;
                  break;
               default:
                  state = OSService.State.OTHER;
            }

            osServices[i] = new OSService(enuM_SERVICE_STATUS_PROCESSs[i].lpDisplayName, enuM_SERVICE_STATUS_PROCESSs[i].ServiceStatusProcess.dwProcessId, state);
         }

         return osServices;
      } catch (Win32Exception win32Exception) {
         return new OSService[0];
      }
   }

   private static String querySystemLog() {
      String string = GlobalConfig.get("", "System");
      if (string.isEmpty()) {
         return null;
      }

      HANDLE handle = Advapi32.INSTANCE.OpenEventLog(null, string);
      return handle == null ? null : string;
   }

   static {
      enableDebugPrivilege();
   }
}
