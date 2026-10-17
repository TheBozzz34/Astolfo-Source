package oshi.software.os.mac;

import com.sun.jna.Memory;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.mac.SystemB.Group;
import com.sun.jna.platform.mac.SystemB.Passwd;
import com.sun.jna.platform.mac.SystemB.ProcTaskAllInfo;
import com.sun.jna.platform.mac.SystemB.ProcTaskInfo;
import com.sun.jna.platform.mac.SystemB.RUsageInfoV2;
import com.sun.jna.platform.mac.SystemB.Timeval;
import com.sun.jna.platform.mac.SystemB.VnodePathInfo;
import com.sun.jna.ptr.IntByReference;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
public class MacOperatingSystem extends AbstractOperatingSystem {
   private int maxProc = 1024;
   private final String osXVersion;
   private final int major;
   private final int minor;
   private static final int P_LP64 = 4;
   private static final int SWAIT = 2;
   private static final int SRUN = 3;
   private static final int SIDL = 4;
   private static final int SZOMB = 5;
   private static final int SSTOP = 6;
   private static final long BOOTTIME;

   public MacOperatingSystem() {
      this.version = new MacOSVersionInfoEx();
      this.osXVersion = System.getProperty("os.version");
      this.major = ParseUtil.getFirstIntValue(this.osXVersion);
      this.minor = ParseUtil.getNthIntValue(this.osXVersion, 2);
      this.maxProc = SysctlUtil.sysctl("kern.maxproc", 4096);
   }

   @Override
   public String queryManufacturer() {
      return "Apple";
   }

   @Override
   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      String string = this.major == 10 && this.minor >= 12 ? "macOS" : System.getProperty("os.name");
      String string2 = this.parseCodeName();
      String string3 = SysctlUtil.sysctl("kern.osversion", "");
      return new AbstractOperatingSystem.FamilyVersionInfo(string, new OperatingSystem.OSVersionInfo(this.osXVersion, string2, string3));
   }

   private String parseCodeName() {
      if (this.major == 10) {
         switch (this.minor) {
            case 0:
               return "Cheetah";
            case 1:
               return "Puma";
            case 2:
               return "Jaguar";
            case 3:
               return "Panther";
            case 4:
               return "Tiger";
            case 5:
               return "Leopard";
            case 6:
               return "Snow Leopard";
            case 7:
               return "Lion";
            case 8:
               return "Mountain Lion";
            case 9:
               return "Mavericks";
            case 10:
               return "Yosemite";
            case 11:
               return "El Capitan";
            case 12:
               return "Sierra";
            case 13:
               return "High Sierra";
            case 14:
               return "Mojave";
            case 15:
               return "Catalina";
         }
      }

      return "";
   }

   @Override
   public int queryBitness(int jvmBitness) {
      return jvmBitness != 64 && (this.major != 10 || this.minor <= 6)
         ? ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("getconf LONG_BIT"), 32)
         : 64;
   }

   @Override
   public boolean queryElevated() {
      return System.getenv("SUDO_COMMAND") != null;
   }

   @Override
   public FileSystem getFileSystem() {
      return new MacFileSystem();
   }

   @Override
   public OSProcess[] getProcesses(int limit, OperatingSystem.ProcessSort sortIn, boolean slowFields) {
      ArrayList arrayList = new ArrayList();
      int[] ints = new int[this.maxProc];
      int i = SystemB.INSTANCE.proc_listpids(1, 0, ints, ints.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;

      for (int j = 0; j < i; j++) {
         if (ints[j] != 0) {
            OSProcess osProcess = this.getProcess(ints[j], slowFields);
            if (osProcess != null) {
               arrayList.add(osProcess);
            }
         }
      }

      List list = this.processSort(arrayList, limit, sortIn);
      return (oshi.software.os.OSProcess[])(list.toArray(new OSProcess[0]));
   }

   @Override
   public OSProcess getProcess(int pid, boolean slowFields) {
      ProcTaskAllInfo procTaskAllInfo = new ProcTaskAllInfo();
      if (0 > SystemB.INSTANCE.proc_pidinfo(pid, 2, 0L, procTaskAllInfo, procTaskAllInfo.size())) {
         return null;
      }

      String string = null;
      String string2 = "";
      Memory memory = new Memory(4096L);
      if (0 < SystemB.INSTANCE.proc_pidpath(pid, memory, 4096)) {
         string2 = memory.getString(0L).trim();
         String[] strings = string2.split("/");
         if (strings.length > 0) {
            string = strings[strings.length - 1];
         }
      }

      if (procTaskAllInfo.ptinfo.pti_threadnum < 1) {
         return null;
      }

      if (string == null) {
         for (int i = 0; i < procTaskAllInfo.pbsd.pbi_comm.length; i++) {
            if (procTaskAllInfo.pbsd.pbi_comm[i] == 0) {
               string = new String(procTaskAllInfo.pbsd.pbi_comm, 0, i, StandardCharsets.UTF_8);
               break;
            }
         }
      }

      long l = 0L;
      long m = 0L;
      if (this.minor >= 9) {
         RUsageInfoV2 rUsageInfoV2 = new RUsageInfoV2();
         if (0 == SystemB.INSTANCE.proc_pid_rusage(pid, 2, rUsageInfoV2)) {
            l = rUsageInfoV2.ri_diskio_bytesread;
            m = rUsageInfoV2.ri_diskio_byteswritten;
         }
      }

      long n = System.currentTimeMillis();
      OSProcess osProcess = new OSProcess(this);
      osProcess.setName(string);
      osProcess.setPath(string2);
      switch (procTaskAllInfo.pbsd.pbi_status) {
         case 1:
            osProcess.setState(OSProcess.State.SLEEPING);
            break;
         case 2:
            osProcess.setState(OSProcess.State.WAITING);
            break;
         case 3:
            osProcess.setState(OSProcess.State.RUNNING);
            break;
         case 4:
            osProcess.setState(OSProcess.State.NEW);
            break;
         case 5:
            osProcess.setState(OSProcess.State.ZOMBIE);
            break;
         case 6:
            osProcess.setState(OSProcess.State.STOPPED);
            break;
         default:
            osProcess.setState(OSProcess.State.OTHER);
      }

      osProcess.setProcessID(pid);
      osProcess.setParentProcessID(procTaskAllInfo.pbsd.pbi_ppid);
      osProcess.setUserID(Integer.toString(procTaskAllInfo.pbsd.pbi_uid));
      Passwd passwd = SystemB.INSTANCE.getpwuid(procTaskAllInfo.pbsd.pbi_uid);
      osProcess.setUser(passwd == null ? osProcess.getUserID() : passwd.pw_name);
      osProcess.setGroupID(Integer.toString(procTaskAllInfo.pbsd.pbi_gid));
      Group group = SystemB.INSTANCE.getgrgid(procTaskAllInfo.pbsd.pbi_gid);
      osProcess.setGroup(group == null ? osProcess.getGroupID() : group.gr_name);
      osProcess.setThreadCount(procTaskAllInfo.ptinfo.pti_threadnum);
      osProcess.setPriority(procTaskAllInfo.ptinfo.pti_priority);
      osProcess.setVirtualSize(procTaskAllInfo.ptinfo.pti_virtual_size);
      osProcess.setResidentSetSize(procTaskAllInfo.ptinfo.pti_resident_size);
      osProcess.setKernelTime(procTaskAllInfo.ptinfo.pti_total_system / 1000000L);
      osProcess.setUserTime(procTaskAllInfo.ptinfo.pti_total_user / 1000000L);
      osProcess.setStartTime(procTaskAllInfo.pbsd.pbi_start_tvsec * 1000L + procTaskAllInfo.pbsd.pbi_start_tvusec / 1000L);
      osProcess.setUpTime(n - osProcess.getStartTime());
      osProcess.setBytesRead(l);
      osProcess.setBytesWritten(m);
      osProcess.setCommandLine(this.getCommandLine(pid));
      osProcess.setOpenFiles(procTaskAllInfo.pbsd.pbi_nfiles);
      osProcess.setBitness((procTaskAllInfo.pbsd.pbi_flags & 4) == 0 ? 32 : 64);
      VnodePathInfo vnodePathInfo = new VnodePathInfo();
      if (0 < SystemB.INSTANCE.proc_pidinfo(pid, 9, 0L, vnodePathInfo, vnodePathInfo.size())) {
         int j = 0;

         for (byte b : vnodePathInfo.pvi_cdir.vip_path) {
            if (b == 0) {
               break;
            }

            j++;
         }

         osProcess.setCurrentWorkingDirectory(new String(vnodePathInfo.pvi_cdir.vip_path, 0, j, StandardCharsets.US_ASCII));
      }

      return osProcess;
   }

   @Override
   public OSProcess[] getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sortIn) {
      ArrayList arrayList = new ArrayList();
      int[] ints = new int[this.maxProc];
      int i = SystemB.INSTANCE.proc_listpids(1, 0, ints, ints.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;

      for (int j = 0; j < i; j++) {
         if (ints[j] != 0 && parentPid == this.getParentProcessPid(ints[j])) {
            OSProcess osProcess = this.getProcess(ints[j], true);
            if (osProcess != null) {
               arrayList.add(osProcess);
            }
         }
      }

      List list = this.processSort(arrayList, limit, sortIn);
      return (oshi.software.os.OSProcess[])(list.toArray(new OSProcess[0]));
   }

   private int getParentProcessPid(int pid) {
      ProcTaskAllInfo procTaskAllInfo = new ProcTaskAllInfo();
      return 0 > SystemB.INSTANCE.proc_pidinfo(pid, 2, 0L, procTaskAllInfo, procTaskAllInfo.size()) ? 0 : procTaskAllInfo.pbsd.pbi_ppid;
   }

   private String getCommandLine(int pid) {
      int[] ints = new int[]{1, 49, pid};
      int i = SysctlUtil.sysctl("kern.argmax", 0);
      Memory memory = new Memory(i);
      IntByReference intByReference = new IntByReference(i);
      if (0 != SystemB.INSTANCE.sysctl(ints, ints.length, memory, intByReference, null, 0)) {
         return "";
      }

      int j = memory.getInt(0L);
      if (j >= 0 && j <= 1024) {
         ArrayList arrayList = new ArrayList(j);
         long l = SystemB.INT_SIZE;
         l += memory.getString(l).length();

         while (j-- > 0 && l < intByReference.getValue()) {
            while (memory.getByte(l) == 0 && ++l < intByReference.getValue()) {
            }

            String string = memory.getString(l);
            arrayList.add(string);
            l += string.length();
         }

         return String.join("\u0000", arrayList);
      } else {
         return "";
      }
   }

   @Override
   public long getProcessAffinityMask(int processId) {
      int i = SysctlUtil.sysctl("hw.logicalcpu", 1);
      return i < 64 ? (1L << i) - 1L : -1L;
   }

   @Override
   public int getProcessId() {
      return SystemB.INSTANCE.getpid();
   }

   @Override
   public int getProcessCount() {
      return SystemB.INSTANCE.proc_listpids(1, 0, null, 0) / SystemB.INT_SIZE;
   }

   @Override
   public int getThreadCount() {
      int[] ints = new int[this.getProcessCount() + 10];
      int i = SystemB.INSTANCE.proc_listpids(1, 0, ints, ints.length) / SystemB.INT_SIZE;
      int j = 0;
      ProcTaskInfo procTaskInfo = new ProcTaskInfo();

      for (int k = 0; k < i; k++) {
         SystemB.INSTANCE.proc_pidinfo(ints[k], 4, 0L, procTaskInfo, procTaskInfo.size());
         j += procTaskInfo.pti_threadnum;
      }

      return j;
   }

   @Override
   public long getSystemUptime() {
      return System.currentTimeMillis() / 1000L - BOOTTIME;
   }

   @Override
   public long getSystemBootTime() {
      return BOOTTIME;
   }

   @Override
   public NetworkParams getNetworkParams() {
      return new MacNetworkParams();
   }

   @Override
   public OSService[] getServices() {
      ArrayList arrayList = new ArrayList();
      HashSet hashSet = new HashSet();

      for (OSProcess osProcess : this.getChildProcesses(1, 0, OperatingSystem.ProcessSort.PID)) {
         OSService osService = new OSService(osProcess.getName(), osProcess.getProcessID(), OSService.State.RUNNING);
         arrayList.add(osService);
         hashSet.add(osProcess.getName());
      }

      ArrayList arrayList2 = new ArrayList();
      File file = new File("/System/Library/LaunchAgents");
      if (file.exists() && file.isDirectory()) {
         arrayList2.addAll(Arrays.asList(file.listFiles((f, name) -> name.toLowerCase().endsWith(".plist"))));
      }

      file = new File("/System/Library/LaunchDaemons");
      if (file.exists() && file.isDirectory()) {
         arrayList2.addAll(Arrays.asList(file.listFiles((f, name) -> name.toLowerCase().endsWith(".plist"))));
      }

      for (Object object : arrayList2) {
         File file2 = (File)object;
         String string = file2.getName().substring(0, file2.getName().length() - 6);
         int i = string.lastIndexOf(46);
         String string2 = i >= 0 && i <= string.length() - 2 ? string.substring(i + 1) : string;
         if (!hashSet.contains(string) && !hashSet.contains(string2)) {
            OSService osService2 = new OSService(string, 0, OSService.State.STOPPED);
            arrayList.add(osService2);
         }
      }

      return (oshi.software.os.OSService[])(arrayList.toArray(new OSService[0]));
   }

   static {
      Timeval timeval = new Timeval();
      if (SysctlUtil.sysctl("kern.boottime", timeval) && timeval.tv_sec.longValue() != 0L) {
         BOOTTIME = timeval.tv_sec.longValue();
      } else {
         BOOTTIME = ParseUtil.parseLongOrDefault(
            ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), System.currentTimeMillis() / 1000L
         );
      }
   }
}
