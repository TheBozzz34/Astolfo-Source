package oshi.software.os.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.ProcessStat;
import oshi.jna.platform.unix.solaris.SolarisLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.LsofUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public class SolarisOperatingSystem extends AbstractOperatingSystem {
   private static final long BOOTTIME = querySystemBootTime();

   public SolarisOperatingSystem() {
      this.version = new SolarisOSVersionInfoEx();
   }

   @Override
   public String queryManufacturer() {
      return "Oracle";
   }

   @Override
   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      String[] strings = ParseUtil.whitespaces.split(ExecutingCommand.getFirstAnswer("uname -rv"));
      String string = strings[0];
      String string2 = null;
      if (strings.length > 1) {
         string2 = strings[1];
      }

      return new AbstractOperatingSystem.FamilyVersionInfo("SunOS", new OperatingSystem.OSVersionInfo(string, "Solaris", string2));
   }

   @Override
   public int queryBitness(int jvmBitness) {
      return jvmBitness == 64 ? 64 : ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("isainfo -b"), 32);
   }

   @Override
   public boolean queryElevated() {
      return System.getenv("SUDO_COMMAND") != null;
   }

   @Override
   public FileSystem getFileSystem() {
      return new SolarisFileSystem();
   }

   @Override
   public OSProcess[] getProcesses(int limit, OperatingSystem.ProcessSort sortIn, boolean slowFields) {
      List list = this.getProcessListFromPS("ps -eo s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args", -1, slowFields);
      List list2 = this.processSort(list, limit, sortIn);
      return (oshi.software.os.OSProcess[])(list2.toArray(new OSProcess[0]));
   }

   @Override
   public OSProcess getProcess(int pid, boolean slowFields) {
      List list = this.getProcessListFromPS("ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p ", pid, slowFields);
      return list.isEmpty() ? null : (OSProcess)list.get(0);
   }

   @Override
   public OSProcess[] getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sortIn) {
      List list = this.getProcessListFromPS("ps -eo s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args --ppid", parentPid, true);
      List list2 = this.processSort(list, limit, sortIn);
      return (oshi.software.os.OSProcess[])(list2.toArray(new OSProcess[0]));
   }

   private List<OSProcess> getProcessListFromPS(String psCommand, int pid, boolean slowFields) {
      Map map = LsofUtil.getCwdMap(pid);
      ArrayList arrayList = new ArrayList();
      List list = ExecutingCommand.runNative(psCommand + (pid < 0 ? "" : pid));
      if (!list.isEmpty() && list.size() >= 2) {
         list.remove(0);

         for (Object object : list) {
            String string = (String)object;
            String[] strings = ParseUtil.whitespaces.split(string.trim(), 15);
            if (strings.length >= 15) {
               long l = System.currentTimeMillis();
               OSProcess osProcess = new OSProcess(this);
               switch (strings[0].charAt(0)) {
                  case 'O':
                     osProcess.setState(OSProcess.State.RUNNING);
                     break;
                  case 'P':
                  case 'Q':
                  case 'U':
                  case 'V':
                  case 'X':
                  case 'Y':
                  default:
                     osProcess.setState(OSProcess.State.OTHER);
                     break;
                  case 'R':
                  case 'W':
                     osProcess.setState(OSProcess.State.WAITING);
                     break;
                  case 'S':
                     osProcess.setState(OSProcess.State.SLEEPING);
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
               osProcess.setUserTime(ParseUtil.parseDHMSOrDefault(strings[12], 0L));
               osProcess.setPath(strings[13]);
               osProcess.setName(osProcess.getPath().substring(osProcess.getPath().lastIndexOf(47) + 1));
               osProcess.setCommandLine(strings[14]);
               osProcess.setCurrentWorkingDirectory((java.lang.String)(map.getOrDefault(osProcess.getProcessID(), "")));
               if (slowFields) {
                  List list2 = ExecutingCommand.runNative(String.format("lsof -p %d", pid));
                  osProcess.setOpenFiles(list2.size() - 1L);

                  for (String string2 : ExecutingCommand.runNative("pflags " + pid)) {
                     if (string2.contains("data model")) {
                        if (string2.contains("LP32")) {
                           osProcess.setBitness(32);
                        } else if (string2.contains("LP64")) {
                           osProcess.setBitness(64);
                        }
                        break;
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
      String string = ExecutingCommand.getFirstAnswer("pbind -q " + processId);
      if (string.isEmpty()) {
         for (String string2 : ExecutingCommand.runNative("psrinfo")) {
            String[] strings = ParseUtil.whitespaces.split(string2);
            int i = ParseUtil.parseIntOrDefault(strings[0], -1);
            if (i >= 0) {
               l |= 1L << i;
            }
         }

         return l;
      } else {
         if (string.endsWith(".") && string.contains("strongly bound to processor(s)")) {
            String string3 = string.substring(0, string.length() - 1);
            String[] strings2 = ParseUtil.whitespaces.split(string3);

            for (int j = strings2.length - 1; j >= 0; j--) {
               int k = ParseUtil.parseIntOrDefault(strings2[j], -1);
               if (k < 0) {
                  break;
               }

               l |= 1L << k;
            }
         }

         return l;
      }
   }

   @Override
   public int getProcessId() {
      return SolarisLibc.INSTANCE.getpid();
   }

   @Override
   public int getProcessCount() {
      return ProcessStat.getPidFiles().length;
   }

   @Override
   public int getThreadCount() {
      List list = ExecutingCommand.runNative("ps -eLo pid");
      return !list.isEmpty() ? list.size() - 1 : this.getProcessCount();
   }

   @Override
   public long getSystemUptime() {
      return querySystemUptime();
   }

   private static long querySystemUptime() {
      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         Kstat kstat = kstatChain.lookup("unix", 0, "system_misc");
         if (kstat != null) {
            return kstat.ks_snaptime / 1000000000L;
         }
      }

      return 0L;
   }

   @Override
   public long getSystemBootTime() {
      return BOOTTIME;
   }

   private static long querySystemBootTime() {
      try (KstatUtil.KstatChain kstatChain = KstatUtil.openChain()) {
         Kstat kstat = kstatChain.lookup("unix", 0, "system_misc");
         if (kstat != null && kstatChain.read(kstat)) {
            return KstatUtil.dataLookupLong(kstat, "boot_time");
         }
      }

      return System.currentTimeMillis() / 1000L - querySystemUptime();
   }

   @Override
   public NetworkParams getNetworkParams() {
      return new SolarisNetworkParams();
   }

   @Override
   public OSService[] getServices() {
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      File file = new File("/etc/init.d");
      File[] files;
      if (file.exists() && file.isDirectory() && (files = file.listFiles()) != null) {
         for (File file2 : files) {
            arrayList2.add(file2.getName());
         }
      }

      for (String string : ExecutingCommand.runNative("svcs -p")) {
         if (string.startsWith("online")) {
            int i = string.lastIndexOf(":/");
            if (i > 0) {
               String string2 = string.substring(i + 1);
               if (string2.endsWith(":default")) {
                  string2 = string2.substring(0, string2.length() - 8);
               }

               arrayList.add(new OSService(string2, 0, OSService.State.STOPPED));
            }
         } else if (string.startsWith(" ")) {
            String[] strings = ParseUtil.whitespaces.split(string.trim());
            if (strings.length == 3) {
               arrayList.add(new OSService(strings[2], ParseUtil.parseIntOrDefault(strings[1], 0), OSService.State.RUNNING));
            }
         } else if (string.startsWith("legacy_run")) {
            for (Object object : arrayList2) {
               String string3 = (String)object;
               if (string.endsWith(string3)) {
                  arrayList.add(new OSService(string3, 0, OSService.State.STOPPED));
                  break;
               }
            }
         }
      }

      return (oshi.software.os.OSService[])(arrayList.toArray(new OSService[0]));
   }
}
