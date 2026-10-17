package oshi.software.os.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.CLibrary;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.LsofUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public class FreeBsdOperatingSystem extends AbstractOperatingSystem {
   private static final long BOOTTIME = querySystemBootTime();

   public FreeBsdOperatingSystem() {
      this.version = new FreeBsdOSVersionInfoEx();
   }

   @Override
   public String queryManufacturer() {
      return "Unix/BSD";
   }

   @Override
   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      String string = BsdSysctlUtil.sysctl("kern.ostype", "FreeBSD");
      String string2 = BsdSysctlUtil.sysctl("kern.osrelease", "");
      String string3 = BsdSysctlUtil.sysctl("kern.version", "");
      String string4 = string3.split(":")[0].replace(string, "").replace(string2, "").trim();
      return new AbstractOperatingSystem.FamilyVersionInfo(string, new OperatingSystem.OSVersionInfo(string2, null, string4));
   }

   @Override
   public int queryBitness(int jvmBitness) {
      return jvmBitness < 64 && ExecutingCommand.getFirstAnswer("uname -m").indexOf("64") == -1 ? jvmBitness : 64;
   }

   @Override
   public boolean queryElevated() {
      return System.getenv("SUDO_COMMAND") != null;
   }

   @Override
   public FileSystem getFileSystem() {
      return new FreeBsdFileSystem();
   }

   @Override
   public OSProcess[] getProcesses(int limit, OperatingSystem.ProcessSort sortIn, boolean slowFields) {
      List list = this.getProcessListFromPS("ps -awwxo state,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etimes,systime,time,comm,args", -1, slowFields);
      List list2 = this.processSort(list, limit, sortIn);
      return (oshi.software.os.OSProcess[])(list2.toArray(new OSProcess[0]));
   }

   @Override
   public OSProcess getProcess(int pid, boolean slowFields) {
      List list = this.getProcessListFromPS("ps -awwxo state,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etimes,systime,time,comm,args -p ", pid, slowFields);
      return list.isEmpty() ? null : (OSProcess)list.get(0);
   }

   @Override
   public OSProcess[] getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sortIn) {
      ArrayList arrayList = new ArrayList();

      for (OSProcess osProcess : this.getProcesses(0, null)) {
         if (osProcess.getParentProcessID() == parentPid) {
            arrayList.add(osProcess);
         }
      }

      List list = this.processSort(arrayList, limit, sortIn);
      return (oshi.software.os.OSProcess[])(list.toArray(new OSProcess[0]));
   }

   private List<OSProcess> getProcessListFromPS(String psCommand, int pid, boolean slowFields) {
      Map map = LsofUtil.getCwdMap(pid);
      ArrayList arrayList = new ArrayList();
      List list = ExecutingCommand.runNative(psCommand + (pid < 0 ? "" : pid));
      if (!list.isEmpty() && list.size() >= 2) {
         list.remove(0);

         for (Object object : list) {
            String string = (String)object;
            String[] strings = ParseUtil.whitespaces.split(string.trim(), 16);
            if (strings.length >= 16) {
               long l = System.currentTimeMillis();
               OSProcess osProcess = new OSProcess(this);
               switch (strings[0].charAt(0)) {
                  case 'D':
                  case 'L':
                  case 'U':
                     osProcess.setState(OSProcess.State.WAITING);
                     break;
                  case 'E':
                  case 'F':
                  case 'G':
                  case 'H':
                  case 'J':
                  case 'K':
                  case 'M':
                  case 'N':
                  case 'O':
                  case 'P':
                  case 'Q':
                  case 'V':
                  case 'W':
                  case 'X':
                  case 'Y':
                  default:
                     osProcess.setState(OSProcess.State.OTHER);
                     break;
                  case 'I':
                  case 'S':
                     osProcess.setState(OSProcess.State.SLEEPING);
                     break;
                  case 'R':
                     osProcess.setState(OSProcess.State.RUNNING);
                     break;
                  case 'T':
                     osProcess.setState(OSProcess.State.STOPPED);
                     break;
                  case 'Z':
                     osProcess.setState(OSProcess.State.ZOMBIE);
               }

               osProcess.setProcessID(ParseUtil.parseIntOrDefault(strings[1], 0));
               osProcess.setParentProcessID(ParseUtil.parseIntOrDefault(strings[2], 0));
               osProcess.setUser(strings[3]);
               osProcess.setUserID(strings[4]);
               osProcess.setGroup(strings[5]);
               osProcess.setGroupID(strings[6]);
               osProcess.setThreadCount(ParseUtil.parseIntOrDefault(strings[7], 0));
               osProcess.setPriority(ParseUtil.parseIntOrDefault(strings[8], 0));
               osProcess.setVirtualSize(ParseUtil.parseLongOrDefault(strings[9], 0L) * 1024L);
               osProcess.setResidentSetSize(ParseUtil.parseLongOrDefault(strings[10], 0L) * 1024L);
               long m = ParseUtil.parseDHMSOrDefault(strings[11], 0L);
               osProcess.setUpTime(m < 1L ? 1L : m);
               osProcess.setStartTime(l - osProcess.getUpTime());
               osProcess.setKernelTime(ParseUtil.parseDHMSOrDefault(strings[12], 0L));
               osProcess.setUserTime(ParseUtil.parseDHMSOrDefault(strings[13], 0L) - osProcess.getKernelTime());
               osProcess.setPath(strings[14]);
               osProcess.setName(osProcess.getPath().substring(osProcess.getPath().lastIndexOf(47) + 1));
               osProcess.setCommandLine(strings[15]);
               osProcess.setCurrentWorkingDirectory((java.lang.String)(map.getOrDefault(osProcess.getProcessID(), "")));
               if (slowFields) {
                  List list2 = ExecutingCommand.runNative(String.format("lsof -p %d", pid));
                  osProcess.setOpenFiles(list2.size() - 1L);
                  int[] ints = new int[]{1, 14, 9, pid};
                  Memory memory = new Memory(32L);
                  IntByReference intByReference = new IntByReference(32);
                  if (0 == FreeBsdLibc.INSTANCE.sysctl(ints, ints.length, memory, intByReference, null, 0)) {
                     String string2 = memory.getString(0L);
                     if (string2.contains("ELF32")) {
                        osProcess.setBitness(32);
                     } else if (string2.contains("ELF64")) {
                        osProcess.setBitness(64);
                     }
                  }
               }

               arrayList.add(osProcess);
            }
         }

         return arrayList;
      } else {
         return arrayList;
      }
   }

   @Override
   public long getProcessAffinityMask(int processId) {
      long l = 0L;
      String string = ExecutingCommand.getFirstAnswer("cpuset -gp " + processId);
      String[] strings = string.split(":");
      if (strings.length > 1) {
         String[] strings2 = strings[1].split(",");

         for (String string2 : strings2) {
            int i = ParseUtil.parseIntOrDefault(string2.trim(), -1);
            if (i >= 0) {
               l |= 1L << i;
            }
         }
      }

      return l;
   }

   @Override
   public int getProcessId() {
      return FreeBsdLibc.INSTANCE.getpid();
   }

   @Override
   public int getProcessCount() {
      List list = ExecutingCommand.runNative("ps -axo pid");
      return !list.isEmpty() ? list.size() - 1 : 0;
   }

   @Override
   public int getThreadCount() {
      int i = 0;

      for (String string : ExecutingCommand.runNative("ps -axo nlwp")) {
         i += ParseUtil.parseIntOrDefault(string.trim(), 0);
      }

      return i;
   }

   @Override
   public long getSystemUptime() {
      return System.currentTimeMillis() / 1000L - BOOTTIME;
   }

   @Override
   public long getSystemBootTime() {
      return BOOTTIME;
   }

   private static long querySystemBootTime() {
      CLibrary.Timeval timeval = new CLibrary.Timeval();
      return BsdSysctlUtil.sysctl("kern.boottime", timeval) && timeval.tv_sec != 0L
         ? timeval.tv_sec
         : ParseUtil.parseLongOrDefault(
            ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), System.currentTimeMillis() / 1000L
         );
   }

   @Override
   public NetworkParams getNetworkParams() {
      return new FreeBsdNetworkParams();
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

      File file = new File("/etc/rc.d");
      File[] files;
      if (file.exists() && file.isDirectory() && (files = file.listFiles()) != null) {
         for (File file2 : files) {
            String string = file2.getName();
            if (!hashSet.contains(string)) {
               OSService osService2 = new OSService(string, 0, OSService.State.STOPPED);
               arrayList.add(osService2);
            }
         }
      }

      return (oshi.software.os.OSService[])(arrayList.toArray(new OSService[0]));
   }
}
