package oshi.software.os.linux;

import com.sun.jna.platform.linux.LibC;
import com.sun.jna.platform.linux.LibC.Sysinfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.CpuStat;
import oshi.driver.linux.proc.ProcessStat;
import oshi.driver.linux.proc.UpTime;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public class LinuxOperatingSystem extends AbstractOperatingSystem {
   private static final String LS_F_PROC_PID_FD = "ls -f " + ProcPath.PID_FD;
   private static final long BOOTTIME;
   private final int z;
   private static final int[] PROC_PID_STAT_ORDERS;
   private static final int PROC_PID_STAT_LENGTH;
   private static final long USER_HZ;
   private static final long BOOT_TIME;

   public LinuxOperatingSystem() {
      super.getVersionInfo();
      this.version = new LinuxOSVersionInfoEx("unknown", "unknown");
      this.z = ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("getconf PAGESIZE"), 4096);
   }

   @Override
   public String queryManufacturer() {
      return "GNU/Linux";
   }

   @Override
   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      Triplet triplet = queryFamilyVersionCodenameFromReleaseFiles();
      String string = null;
      List list = FileUtil.readFile(ProcPath.VERSION);
      if (!list.isEmpty()) {
         String[] strings = ParseUtil.whitespaces.split((CharSequence)list.get(0));

         for (String string2 : strings) {
            if (!"Linux".equals(string2) && !"version".equals(string2)) {
               string = string2;
               break;
            }
         }
      }

      OperatingSystem.OSVersionInfo osVersionInfo = new OperatingSystem.OSVersionInfo((String)triplet.getB(), (String)triplet.getC(), string);
      return new AbstractOperatingSystem.FamilyVersionInfo((String)triplet.getA(), osVersionInfo);
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
      return new LinuxFileSystem();
   }

   @Override
   public OSProcess[] getProcesses(int limit, OperatingSystem.ProcessSort sortIn, boolean slowFields) {
      ArrayList arrayList = new ArrayList();
      File[] files = ProcessStat.getPidFiles();
      LinuxUserGroupInfo linuxUserGroupInfo = new LinuxUserGroupInfo();

      for (File file : files) {
         int i = ParseUtil.parseIntOrDefault(file.getName(), 0);
         OSProcess osProcess = this.getProcess(i, linuxUserGroupInfo, slowFields);
         if (osProcess != null) {
            arrayList.add(osProcess);
         }
      }

      List list = this.processSort(arrayList, limit, sortIn);
      return (oshi.software.os.OSProcess[])(list.toArray(new OSProcess[0]));
   }

   @Override
   public OSProcess getProcess(int pid, boolean slowFields) {
      return this.getProcess(pid, new LinuxUserGroupInfo(), slowFields);
   }

   private OSProcess getProcess(int pid, LinuxUserGroupInfo userGroupInfo, boolean slowFields) {
      String string = "";
      String string2 = String.format(ProcPath.PID_EXE, pid);

      try {
         Path path = Paths.get(string2);
         string = Files.readSymbolicLink(path).toString();
         int i = string.indexOf(" (deleted)");
         if (i != -1) {
            string = string.substring(0, i);
         }
      } catch (InvalidPathException | IOException | UnsupportedOperationException | SecurityException value) {
      }

      Map map = FileUtil.getKeyValueMapFromFile(String.format(ProcPath.PID_IO, pid), ":");
      long l = System.currentTimeMillis();
      String string3 = FileUtil.getStringFromFile(String.format(ProcPath.PID_STAT, pid));
      if (string3.isEmpty()) {
         return null;
      }

      long[] longs = ParseUtil.parseStringToLongArray(string3, PROC_PID_STAT_ORDERS, PROC_PID_STAT_LENGTH, ' ');
      OSProcess osProcess = new OSProcess(this);
      osProcess.setProcessID(pid);
      osProcess.setCommandLine(FileUtil.getStringFromFile(String.format(ProcPath.PID_CMDLINE, pid)));
      long m = BOOT_TIME + longs[LinuxOperatingSystem.ProcPidStat.START_TIME.ordinal()] * 1000L / USER_HZ;
      if (m >= l) {
         m = l - 1L;
      }

      osProcess.setStartTime(m);
      osProcess.setParentProcessID((int)longs[LinuxOperatingSystem.ProcPidStat.PPID.ordinal()]);
      osProcess.setThreadCount((int)longs[LinuxOperatingSystem.ProcPidStat.THREAD_COUNT.ordinal()]);
      osProcess.setPriority((int)longs[LinuxOperatingSystem.ProcPidStat.PRIORITY.ordinal()]);
      osProcess.setVirtualSize(longs[LinuxOperatingSystem.ProcPidStat.VSZ.ordinal()]);
      osProcess.setResidentSetSize(longs[LinuxOperatingSystem.ProcPidStat.RSS.ordinal()] * this.z);
      osProcess.setKernelTime(longs[LinuxOperatingSystem.ProcPidStat.KERNEL_TIME.ordinal()] * 1000L / USER_HZ);
      osProcess.setUserTime(longs[LinuxOperatingSystem.ProcPidStat.USER_TIME.ordinal()] * 1000L / USER_HZ);
      osProcess.setUpTime(l - osProcess.getStartTime());
      osProcess.setBytesRead(ParseUtil.parseLongOrDefault((java.lang.String)(map.getOrDefault("read_bytes", "")), 0L));
      osProcess.setBytesWritten(ParseUtil.parseLongOrDefault((java.lang.String)(map.getOrDefault("write_bytes", "")), 0L));
      if (slowFields) {
         List list = ExecutingCommand.runNative(String.format(LS_F_PROC_PID_FD, pid));
         osProcess.setOpenFiles(list.size() - 1L);
         byte[] bytes = new byte[5];
         if (!string.isEmpty()) {
            try (FileInputStream fileInputStream = new FileInputStream(string)) {
               if (fileInputStream.read(bytes) == bytes.length) {
                  osProcess.setBitness(bytes[4] == 1 ? 32 : 64);
               }
            } catch (IOException ioException) {
            }
         }
      }

      Map map2 = FileUtil.getKeyValueMapFromFile(String.format(ProcPath.PID_STATUS, pid), ":");
      osProcess.setName((java.lang.String)(map2.getOrDefault("Name", "")));
      osProcess.setPath(string);
      switch (((java.lang.String)(map2.getOrDefault("State", "U"))).charAt(0)) {
         case 'D':
            osProcess.setState(OSProcess.State.WAITING);
            break;
         case 'R':
            osProcess.setState(OSProcess.State.RUNNING);
            break;
         case 'S':
            osProcess.setState(OSProcess.State.SLEEPING);
            break;
         case 'T':
            osProcess.setState(OSProcess.State.STOPPED);
            break;
         case 'Z':
            osProcess.setState(OSProcess.State.ZOMBIE);
            break;
         default:
            osProcess.setState(OSProcess.State.OTHER);
      }

      osProcess.setUserID(ParseUtil.whitespaces.split((java.lang.CharSequence)(map2.getOrDefault("Uid", "")))[0]);
      osProcess.setGroupID(ParseUtil.whitespaces.split((java.lang.CharSequence)(map2.getOrDefault("Gid", "")))[0]);
      Pair pair = userGroupInfo.getUser(osProcess.getUserID());
      osProcess.setUser((String)pair.getB());
      osProcess.setGroup(userGroupInfo.getGroupName(osProcess.getGroupID()));

      try {
         String string4 = String.format(ProcPath.PID_CWD, pid);
         String string5 = new File(string4).getCanonicalPath();
         if (!string5.equals(string4)) {
            osProcess.setCurrentWorkingDirectory(string5);
         }
      } catch (IOException ioException2) {
      }

      return osProcess;
   }

   @Override
   public OSProcess[] getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sortIn) {
      ArrayList arrayList = new ArrayList();
      File[] files = ProcessStat.getPidFiles();
      LinuxUserGroupInfo linuxUserGroupInfo = new LinuxUserGroupInfo();

      for (File file : files) {
         int i = ParseUtil.parseIntOrDefault(file.getName(), 0);
         if (parentPid == getParentPidFromProcFile(i)) {
            OSProcess osProcess = this.getProcess(i, linuxUserGroupInfo, true);
            if (osProcess != null) {
               arrayList.add(osProcess);
            }
         }
      }

      List list = this.processSort(arrayList, limit, sortIn);
      return (oshi.software.os.OSProcess[])(list.toArray(new OSProcess[0]));
   }

   private static int getParentPidFromProcFile(int pid) {
      String string = FileUtil.getStringFromFile(String.format("/proc/%d/stat", pid));
      long[] longs = ParseUtil.parseStringToLongArray(string, PROC_PID_STAT_ORDERS, PROC_PID_STAT_LENGTH, ' ');
      return (int)longs[LinuxOperatingSystem.ProcPidStat.PPID.ordinal()];
   }

   @Override
   public long getProcessAffinityMask(int processId) {
      String string = ExecutingCommand.getFirstAnswer("taskset -p " + processId);
      String[] strings = ParseUtil.whitespaces.split(string);

      try {
         return new BigInteger(strings[strings.length - 1], 16).longValue();
      } catch (NumberFormatException numberFormatException) {
         return 0L;
      }
   }

   @Override
   public int getProcessId() {
      return LinuxLibc.INSTANCE.getpid();
   }

   @Override
   public int getProcessCount() {
      return ProcessStat.getPidFiles().length;
   }

   @Override
   public int getThreadCount() {
      try {
         Sysinfo sysinfo2 = new Sysinfo();
         return 0 != LibC.INSTANCE.sysinfo(sysinfo2) ? 0 : sysinfo2.procs;
      } catch (UnsatisfiedLinkError | NoClassDefFoundError value) {
         return 0;
      }
   }

   @Override
   public long getSystemUptime() {
      return (long)UpTime.getSystemUptimeSeconds();
   }

   @Override
   public long getSystemBootTime() {
      return BOOTTIME;
   }

   @Override
   public NetworkParams getNetworkParams() {
      return new LinuxNetworkParams();
   }

   private static Triplet<String, String, String> queryFamilyVersionCodenameFromReleaseFiles() {
      Triplet triplet;
      if ((triplet = readDistribRelease("/etc/system-release")) != null) {
         return triplet;
      }

      if ((triplet = readOsRelease()) != null) {
         return triplet;
      }

      if ((triplet = execLsbRelease()) != null) {
         return triplet;
      }

      if ((triplet = readLsbRelease()) != null) {
         return triplet;
      }

      String string = getReleaseFilename();
      if ((triplet = readDistribRelease(string)) != null) {
         return triplet;
      }

      String string2 = filenameToFamily(string.replace("/etc/", "").replace("release", "").replace("version", "").replace("-", "").replace("_", ""));
      return new Triplet<>(string2, "unknown", "unknown");
   }

   private static Triplet<String, String, String> readOsRelease() {
      String string = null;
      String string2 = "unknown";
      String string3 = "unknown";

      for (String string4 : FileUtil.readFile("/etc/os-release")) {
         if (string4.startsWith("VERSION=")) {
            string4 = string4.replace("VERSION=", "").replaceAll("^\"|\"$", "").trim();
            String[] strings = string4.split("[()]");
            if (strings.length <= 1) {
               strings = string4.split(", ");
            }

            if (strings.length > 0) {
               string2 = strings[0].trim();
            }

            if (strings.length > 1) {
               string3 = strings[1].trim();
            }
         } else if (string4.startsWith("NAME=") && string == null) {
            string = string4.replace("NAME=", "").replaceAll("^\"|\"$", "").trim();
         } else if (string4.startsWith("VERSION_ID=") && string2.equals("unknown")) {
            string2 = string4.replace("VERSION_ID=", "").replaceAll("^\"|\"$", "").trim();
         }
      }

      return string == null ? null : new Triplet<>(string, string2, string3);
   }

   private static Triplet<String, String, String> execLsbRelease() {
      String string = null;
      String string2 = "unknown";
      String string3 = "unknown";

      for (String string4 : ExecutingCommand.runNative("lsb_release -a")) {
         if (string4.startsWith("Description:")) {
            string4 = string4.replace("Description:", "").trim();
            if (string4.contains(" release ")) {
               Triplet triplet = parseRelease(string4, " release ");
               string = (String)triplet.getA();
               if (string2.equals("unknown")) {
                  string2 = (String)triplet.getB();
               }

               if (string3.equals("unknown")) {
                  string3 = (String)triplet.getC();
               }
            }
         } else if (string4.startsWith("Distributor ID:") && string == null) {
            string = string4.replace("Distributor ID:", "").trim();
         } else if (string4.startsWith("Release:") && string2.equals("unknown")) {
            string2 = string4.replace("Release:", "").trim();
         } else if (string4.startsWith("Codename:") && string3.equals("unknown")) {
            string3 = string4.replace("Codename:", "").trim();
         }
      }

      return string == null ? null : new Triplet<>(string, string2, string3);
   }

   private static Triplet<String, String, String> readLsbRelease() {
      String string = null;
      String string2 = "unknown";
      String string3 = "unknown";

      for (String string4 : FileUtil.readFile("/etc/lsb-release")) {
         if (string4.startsWith("DISTRIB_DESCRIPTION=")) {
            string4 = string4.replace("DISTRIB_DESCRIPTION=", "").replaceAll("^\"|\"$", "").trim();
            if (string4.contains(" release ")) {
               Triplet triplet = parseRelease(string4, " release ");
               string = (String)triplet.getA();
               if (string2.equals("unknown")) {
                  string2 = (String)triplet.getB();
               }

               if (string3.equals("unknown")) {
                  string3 = (String)triplet.getC();
               }
            }
         } else if (string4.startsWith("DISTRIB_ID=") && string == null) {
            string = string4.replace("DISTRIB_ID=", "").replaceAll("^\"|\"$", "").trim();
         } else if (string4.startsWith("DISTRIB_RELEASE=") && string2.equals("unknown")) {
            string2 = string4.replace("DISTRIB_RELEASE=", "").replaceAll("^\"|\"$", "").trim();
         } else if (string4.startsWith("DISTRIB_CODENAME=") && string3.equals("unknown")) {
            string3 = string4.replace("DISTRIB_CODENAME=", "").replaceAll("^\"|\"$", "").trim();
         }
      }

      return string == null ? null : new Triplet<>(string, string2, string3);
   }

   private static Triplet<String, String, String> readDistribRelease(String filename) {
      if (new File(filename).exists()) {
         for (String string : FileUtil.readFile(filename)) {
            if (string.contains(" release ")) {
               return parseRelease(string, " release ");
            }

            if (string.contains(" VERSION ")) {
               return parseRelease(string, " VERSION ");
            }
         }
      }

      return null;
   }

   private static Triplet<String, String, String> parseRelease(String line, String splitLine) {
      String[] strings = line.split(splitLine);
      String string = strings[0].trim();
      String string2 = "unknown";
      String string3 = "unknown";
      if (strings.length > 1) {
         strings = strings[1].split("[()]");
         if (strings.length > 0) {
            string2 = strings[0].trim();
         }

         if (strings.length > 1) {
            string3 = strings[1].trim();
         }
      }

      return new Triplet<>(string, string2, string3);
   }

   public static String getReleaseFilename() {
      File file = new File("/etc");
      File[] files = file.listFiles(
         f -> (f.getName().endsWith("-release") || f.getName().endsWith("-version") || f.getName().endsWith("_release") || f.getName().endsWith("_version"))
            && !f.getName().endsWith("os-release")
            && !f.getName().endsWith("lsb-release")
            && !f.getName().endsWith("system-release")
      );
      if (files != null && files.length > 0) {
         return files[0].getPath();
      } else {
         return new File("/etc/release").exists() ? "/etc/release" : "/etc/issue";
      }
   }

   private static String filenameToFamily(String name) {
      switch (name.toLowerCase()) {
         case "":
            return "Solaris";
         case "blackcat":
            return "Black Cat";
         case "bluewhite64":
            return "BlueWhite64";
         case "e-smith":
            return "SME Server";
         case "eos":
            return "FreeEOS";
         case "hlfs":
            return "HLFS";
         case "lfs":
            return "Linux-From-Scratch";
         case "linuxppc":
            return "Linux-PPC";
         case "meego":
            return "MeeGo";
         case "mandakelinux":
            return "Mandrake";
         case "mklinux":
            return "MkLinux";
         case "nld":
            return "Novell Linux Desktop";
         case "novell":
         case "SuSE":
            return "SUSE Linux";
         case "pld":
            return "PLD";
         case "redhat":
            return "Red Hat Linux";
         case "sles":
            return "SUSE Linux ES9";
         case "sun":
            return "Sun JDS";
         case "synoinfo":
            return "Synology";
         case "tinysofa":
            return "Tiny Sofa";
         case "turbolinux":
            return "TurboLinux";
         case "ultrapenguin":
            return "UltraPenguin";
         case "va":
            return "VA-Linux";
         case "vmware":
            return "VMWareESX";
         case "yellowdog":
            return "Yellow Dog";
         case "issue":
            return "Unknown";
         default:
            return name.substring(0, 1).toUpperCase() + name.substring(1);
      }
   }

   public static long getHz() {
      return USER_HZ;
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

      boolean bl = false;

      for (String string : ExecutingCommand.runNative("systemctl list-unit-files")) {
         String[] strings = ParseUtil.whitespaces.split(string);
         if (strings.length == 2 && strings[0].endsWith(".service") && "enabled".equals(strings[1])) {
            String string2 = strings[0].substring(0, strings[0].length() - 8);
            int i = string2.lastIndexOf(46);
            String string3 = i >= 0 && i <= string2.length() - 2 ? string2.substring(i + 1) : string2;
            if (!hashSet.contains(string2) && !hashSet.contains(string3)) {
               OSService osService2 = new OSService(string2, 0, OSService.State.STOPPED);
               arrayList.add(osService2);
               bl = true;
            }
         }
      }

      if (!bl) {
         File file = new File("/etc/init");
         if (file.exists() && file.isDirectory()) {
            for (File file2 : file.listFiles((f, name) -> name.toLowerCase().endsWith(".conf"))) {
               String string4 = file2.getName().substring(0, file2.getName().length() - 5);
               int j = string4.lastIndexOf(46);
               String string5 = j >= 0 && j <= string4.length() - 2 ? string4.substring(j + 1) : string4;
               if (!hashSet.contains(string4) && !hashSet.contains(string5)) {
                  OSService osService3 = new OSService(string4, 0, OSService.State.STOPPED);
                  arrayList.add(osService3);
               }
            }
         }
      }

      return (oshi.software.os.OSService[])(arrayList.toArray(new OSService[0]));
   }

   static {
      long l = CpuStat.getBootTime();
      if (l == 0L) {
         l = System.currentTimeMillis() / 1000L - (long)UpTime.getSystemUptimeSeconds();
      }

      BOOTTIME = l;
      PROC_PID_STAT_ORDERS = new int[LinuxOperatingSystem.ProcPidStat.values().length];

      for (LinuxOperatingSystem.ProcPidStat procPidStat : LinuxOperatingSystem.ProcPidStat.values()) {
         PROC_PID_STAT_ORDERS[procPidStat.ordinal()] = procPidStat.getOrder() - 1;
      }

      String string = FileUtil.getStringFromFile(ProcPath.SELF_STAT);
      if (!string.isEmpty() && string.contains(")")) {
         PROC_PID_STAT_LENGTH = ParseUtil.countStringToLongArray(string, ' ') + 3;
      } else {
         PROC_PID_STAT_LENGTH = 52;
      }

      USER_HZ = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf CLK_TCK"), 100L);
      double d = UpTime.getSystemUptimeSeconds();
      long m = System.currentTimeMillis();
      double e = d + UpTime.getSystemUptimeSeconds();
      BOOT_TIME = m - (long)(500.0 * e + 0.5);
   }

   public enum ProcPidStat {
      PPID(4),
      USER_TIME(14),
      KERNEL_TIME(15),
      PRIORITY(18),
      THREAD_COUNT(20),
      START_TIME(22),
      VSZ(23),
      RSS(24);

      private int order;
      private static final LinuxOperatingSystem.ProcPidStat[] VALUES = new LinuxOperatingSystem.ProcPidStat[]{
         PPID, USER_TIME, KERNEL_TIME, PRIORITY, THREAD_COUNT, START_TIME, VSZ, RSS
      };

      public int getOrder() {
         return this.order;
      }

      ProcPidStat(int order) {
         this.order = order;
      }
   }
}
