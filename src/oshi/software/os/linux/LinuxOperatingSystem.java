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
   private static final String k = "ls -f " + ProcPath.PID_FD;
   private static final long s;
   private final int z;
   private static final int[] PROC_PID_STAT_ORDERS;
   private static final int P;
   private static final long d;
   private static final long v;
   private static final long f = 129376003711098L;

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
      Triplet var4 = queryFamilyVersionCodenameFromReleaseFiles();
      String var5 = null;
      List var6 = FileUtil.readFile(ProcPath.VERSION);
      if (!var6.isEmpty()) {
         String[] var7 = ParseUtil.Q.split((CharSequence)var6.get(0));

         for (String var11 : var7) {
            if (!"Linux".equals(var11) && !"version".equals(var11)) {
               var5 = var11;
               break;
            }
         }
      }

      OperatingSystem.OSVersionInfo var12 = new OperatingSystem.OSVersionInfo((String)var4.q(), (String)var4.U(), var5);
      return new AbstractOperatingSystem.FamilyVersionInfo((String)var4.v(), var12);
   }

   @Override
   public int queryBitness(int var1) {
      return var1 < 64 && ExecutingCommand.getFirstAnswer("uname -m").indexOf("64") == -1 ? var1 : 64;
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
   public OSProcess[] getProcesses(int var1, OperatingSystem.ProcessSort var2, boolean var3) {
      ArrayList var7 = new ArrayList();
      File[] var8 = ProcessStat.getPidFiles();
      LinuxUserGroupInfo var9 = new LinuxUserGroupInfo();

      for (File var13 : var8) {
         int var14 = ParseUtil.parseIntOrDefault(var13.getName(), 0);
         OSProcess var15 = this.getProcess(var14, var9, var3);
         if (var15 != null) {
            var7.add(var15);
         }
      }

      List var16 = this.processSort(var7, var1, var2);
      return (oshi.software.os.OSProcess[])(var16.toArray(new OSProcess[0]));
   }

   @Override
   public OSProcess getProcess(int var1, boolean var2) {
      return this.getProcess(var1, new LinuxUserGroupInfo(), var2);
   }

   private OSProcess getProcess(int var1, LinuxUserGroupInfo var2, boolean var3) {
      String var7 = "";
      String var8 = String.format(ProcPath.PID_EXE, var1);

      try {
         Path var9 = Paths.get(var8);
         var7 = Files.readSymbolicLink(var9).toString();
         int var10 = var7.indexOf(" (deleted)");
         if (var10 != -1) {
            var7 = var7.substring(0, var10);
         }
      } catch (InvalidPathException | IOException | UnsupportedOperationException | SecurityException var34) {
      }

      Map var37 = FileUtil.getKeyValueMapFromFile(String.format(ProcPath.PID_IO, var1), ":");
      long var38 = System.currentTimeMillis();
      String var12 = FileUtil.getStringFromFile(String.format(ProcPath.PID_STAT, var1));
      if (var12.isEmpty()) {
         return null;
      }

      long[] var13 = ParseUtil.parseStringToLongArray(var12, PROC_PID_STAT_ORDERS, P, ' ');
      OSProcess var14 = new OSProcess(this);
      var14.setProcessID(var1);
      var14.setCommandLine(FileUtil.getStringFromFile(String.format(ProcPath.PID_CMDLINE, var1)));
      long var15 = v + var13[LinuxOperatingSystem.ProcPidStat.START_TIME.ordinal()] * 1000L / d;
      if (var15 >= var38) {
         var15 = var38 - 1L;
      }

      var14.setStartTime(var15);
      var14.setParentProcessID((int)var13[LinuxOperatingSystem.ProcPidStat.PPID.ordinal()]);
      var14.setThreadCount((int)var13[LinuxOperatingSystem.ProcPidStat.THREAD_COUNT.ordinal()]);
      var14.setPriority((int)var13[LinuxOperatingSystem.ProcPidStat.PRIORITY.ordinal()]);
      var14.setVirtualSize(var13[LinuxOperatingSystem.ProcPidStat.VSZ.ordinal()]);
      var14.setResidentSetSize(var13[LinuxOperatingSystem.ProcPidStat.RSS.ordinal()] * this.z);
      var14.setKernelTime(var13[LinuxOperatingSystem.ProcPidStat.KERNEL_TIME.ordinal()] * 1000L / d);
      var14.setUserTime(var13[LinuxOperatingSystem.ProcPidStat.USER_TIME.ordinal()] * 1000L / d);
      var14.setUpTime(var38 - var14.getStartTime());
      var14.setBytesRead(ParseUtil.parseLongOrDefault((java.lang.String)(var37.getOrDefault("read_bytes", "")), 0L));
      var14.setBytesWritten(ParseUtil.parseLongOrDefault((java.lang.String)(var37.getOrDefault("write_bytes", "")), 0L));
      if (var3) {
         List var17 = ExecutingCommand.runNative(String.format(k, var1));
         var14.setOpenFiles(var17.size() - 1L);
         byte[] var18 = new byte[5];
         if (!var7.isEmpty()) {
            try (FileInputStream var19 = new FileInputStream(var7)) {
               if (var19.read(var18) == var18.length) {
                  var14.setBitness(var18[4] == 1 ? 32 : 64);
               }
            } catch (IOException var36) {
            }
         }
      }

      Map var39 = FileUtil.getKeyValueMapFromFile(String.format(ProcPath.PID_STATUS, var1), ":");
      var14.setName((java.lang.String)(var39.getOrDefault("Name", "")));
      var14.setPath(var7);
      switch (((java.lang.String)(var39.getOrDefault("State", "U"))).charAt(0)) {
         case 'D':
            var14.setState(OSProcess.State.WAITING);
            break;
         case 'R':
            var14.setState(OSProcess.State.RUNNING);
            break;
         case 'S':
            var14.setState(OSProcess.State.SLEEPING);
            break;
         case 'T':
            var14.setState(OSProcess.State.STOPPED);
            break;
         case 'Z':
            var14.setState(OSProcess.State.ZOMBIE);
            break;
         default:
            var14.setState(OSProcess.State.OTHER);
      }

      var14.setUserID(ParseUtil.Q.split((java.lang.CharSequence)(var39.getOrDefault("Uid", "")))[0]);
      var14.setGroupID(ParseUtil.Q.split((java.lang.CharSequence)(var39.getOrDefault("Gid", "")))[0]);
      Pair var40 = var2.getUser(var14.getUserID());
      var14.setUser((String)var40.getB());
      var14.setGroup(var2.getGroupName(var14.getGroupID()));

      try {
         String var41 = String.format(ProcPath.PID_CWD, var1);
         String var42 = new File(var41).getCanonicalPath();
         if (!var42.equals(var41)) {
            var14.setCurrentWorkingDirectory(var42);
         }
      } catch (IOException var31) {
      }

      return var14;
   }

   @Override
   public OSProcess[] getChildProcesses(int var1, int var2, OperatingSystem.ProcessSort var3) {
      ArrayList var7 = new ArrayList();
      File[] var8 = ProcessStat.getPidFiles();
      LinuxUserGroupInfo var9 = new LinuxUserGroupInfo();

      for (File var13 : var8) {
         int var14 = ParseUtil.parseIntOrDefault(var13.getName(), 0);
         if (var1 == getParentPidFromProcFile(var14)) {
            OSProcess var15 = this.getProcess(var14, var9, true);
            if (var15 != null) {
               var7.add(var15);
            }
         }
      }

      List var16 = this.processSort(var7, var2, var3);
      return (oshi.software.os.OSProcess[])(var16.toArray(new OSProcess[0]));
   }

   private static int getParentPidFromProcFile(int var0) {
      String var3 = FileUtil.getStringFromFile(String.format("/proc/%d/stat", var0));
      long[] var4 = ParseUtil.parseStringToLongArray(var3, PROC_PID_STAT_ORDERS, P, ' ');
      return (int)var4[LinuxOperatingSystem.ProcPidStat.PPID.ordinal()];
   }

   @Override
   public long getProcessAffinityMask(int var1) {
      String var4 = ExecutingCommand.getFirstAnswer("taskset -p " + var1);
      String[] var5 = ParseUtil.Q.split(var4);

      try {
         return new BigInteger(var5[var5.length - 1], 16).longValue();
      } catch (NumberFormatException var7) {
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
         Sysinfo var4 = new Sysinfo();
         return 0 != LibC.INSTANCE.sysinfo(var4) ? 0 : var4.procs;
      } catch (UnsatisfiedLinkError | NoClassDefFoundError var5) {
         return 0;
      }
   }

   @Override
   public long getSystemUptime() {
      return (long)UpTime.getSystemUptimeSeconds();
   }

   @Override
   public long getSystemBootTime() {
      return s;
   }

   @Override
   public NetworkParams getNetworkParams() {
      return new LinuxNetworkParams();
   }

   private static Triplet<String, String, String> queryFamilyVersionCodenameFromReleaseFiles() {
      Triplet var3;
      if ((var3 = readDistribRelease("/etc/system-release")) != null) {
         return var3;
      }

      if ((var3 = readOsRelease()) != null) {
         return var3;
      }

      if ((var3 = execLsbRelease()) != null) {
         return var3;
      }

      if ((var3 = readLsbRelease()) != null) {
         return var3;
      }

      String var4 = getReleaseFilename();
      if ((var3 = readDistribRelease(var4)) != null) {
         return var3;
      }

      String var5 = filenameToFamily(var4.replace("/etc/", "").replace("release", "").replace("version", "").replace("-", "").replace("_", ""));
      return new Triplet<>(var5, "unknown", "unknown");
   }

   private static Triplet<String, String, String> readOsRelease() {
      String var3 = null;
      String var4 = "unknown";
      String var5 = "unknown";

      for (String var8 : FileUtil.readFile("/etc/os-release")) {
         if (var8.startsWith("VERSION=")) {
            var8 = var8.replace("VERSION=", "").replaceAll("^\"|\"$", "").trim();
            String[] var9 = var8.split("[()]");
            if (var9.length <= 1) {
               var9 = var8.split(", ");
            }

            if (var9.length > 0) {
               var4 = var9[0].trim();
            }

            if (var9.length > 1) {
               var5 = var9[1].trim();
            }
         } else if (var8.startsWith("NAME=") && var3 == null) {
            var3 = var8.replace("NAME=", "").replaceAll("^\"|\"$", "").trim();
         } else if (var8.startsWith("VERSION_ID=") && var4.equals("unknown")) {
            var4 = var8.replace("VERSION_ID=", "").replaceAll("^\"|\"$", "").trim();
         }
      }

      return var3 == null ? null : new Triplet<>(var3, var4, var5);
   }

   private static Triplet<String, String, String> execLsbRelease() {
      String var3 = null;
      String var4 = "unknown";
      String var5 = "unknown";

      for (String var7 : ExecutingCommand.runNative("lsb_release -a")) {
         if (var7.startsWith("Description:")) {
            var7 = var7.replace("Description:", "").trim();
            if (var7.contains(" release ")) {
               Triplet var8 = parseRelease(var7, " release ");
               var3 = (String)var8.v();
               if (var4.equals("unknown")) {
                  var4 = (String)var8.q();
               }

               if (var5.equals("unknown")) {
                  var5 = (String)var8.U();
               }
            }
         } else if (var7.startsWith("Distributor ID:") && var3 == null) {
            var3 = var7.replace("Distributor ID:", "").trim();
         } else if (var7.startsWith("Release:") && var4.equals("unknown")) {
            var4 = var7.replace("Release:", "").trim();
         } else if (var7.startsWith("Codename:") && var5.equals("unknown")) {
            var5 = var7.replace("Codename:", "").trim();
         }
      }

      return var3 == null ? null : new Triplet<>(var3, var4, var5);
   }

   private static Triplet<String, String, String> readLsbRelease() {
      String var3 = null;
      String var4 = "unknown";
      String var5 = "unknown";

      for (String var8 : FileUtil.readFile("/etc/lsb-release")) {
         if (var8.startsWith("DISTRIB_DESCRIPTION=")) {
            var8 = var8.replace("DISTRIB_DESCRIPTION=", "").replaceAll("^\"|\"$", "").trim();
            if (var8.contains(" release ")) {
               Triplet var9 = parseRelease(var8, " release ");
               var3 = (String)var9.v();
               if (var4.equals("unknown")) {
                  var4 = (String)var9.q();
               }

               if (var5.equals("unknown")) {
                  var5 = (String)var9.U();
               }
            }
         } else if (var8.startsWith("DISTRIB_ID=") && var3 == null) {
            var3 = var8.replace("DISTRIB_ID=", "").replaceAll("^\"|\"$", "").trim();
         } else if (var8.startsWith("DISTRIB_RELEASE=") && var4.equals("unknown")) {
            var4 = var8.replace("DISTRIB_RELEASE=", "").replaceAll("^\"|\"$", "").trim();
         } else if (var8.startsWith("DISTRIB_CODENAME=") && var5.equals("unknown")) {
            var5 = var8.replace("DISTRIB_CODENAME=", "").replaceAll("^\"|\"$", "").trim();
         }
      }

      return var3 == null ? null : new Triplet<>(var3, var4, var5);
   }

   private static Triplet<String, String, String> readDistribRelease(String var0) {
      if (new File(var0).exists()) {
         for (String var6 : FileUtil.readFile(var0)) {
            if (var6.contains(" release ")) {
               return parseRelease(var6, " release ");
            }

            if (var6.contains(" VERSION ")) {
               return parseRelease(var6, " VERSION ");
            }
         }
      }

      return null;
   }

   private static Triplet<String, String, String> parseRelease(String var0, String var1) {
      String[] var5 = var0.split(var1);
      String var6 = var5[0].trim();
      String var7 = "unknown";
      String var8 = "unknown";
      if (var5.length > 1) {
         var5 = var5[1].split("[()]");
         if (var5.length > 0) {
            var7 = var5[0].trim();
         }

         if (var5.length > 1) {
            var8 = var5[1].trim();
         }
      }

      return new Triplet<>(var6, var7, var8);
   }

   public static String getReleaseFilename() {
      File var3 = new File("/etc");
      File[] var4 = var3.listFiles(
         var0 -> (
               var0.getName().endsWith("-release")
                  || var0.getName().endsWith("-version")
                  || var0.getName().endsWith("_release")
                  || var0.getName().endsWith("_version")
            )
            && !var0.getName().endsWith("os-release")
            && !var0.getName().endsWith("lsb-release")
            && !var0.getName().endsWith("system-release")
      );
      if (var4 != null && var4.length > 0) {
         return var4[0].getPath();
      } else {
         return new File("/etc/release").exists() ? "/etc/release" : "/etc/issue";
      }
   }

   private static String filenameToFamily(String var0) {
      switch (var0.toLowerCase()) {
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
            return var0.substring(0, 1).toUpperCase() + var0.substring(1);
      }
   }

   public static long H() {
      return d;
   }

   @Override
   public OSService[] getServices() {
      ArrayList var5 = new ArrayList();
      HashSet var6 = new HashSet();

      for (OSProcess var10 : this.getChildProcesses(1, 0, OperatingSystem.ProcessSort.PID)) {
         OSService var11 = new OSService(var10.getName(), var10.getProcessID(), OSService.State.RUNNING);
         var5.add(var11);
         var6.add(var10.getName());
      }

      boolean var18 = false;

      for (String var22 : ExecutingCommand.runNative("systemctl list-unit-files")) {
         String[] var24 = ParseUtil.Q.split(var22);
         if (var24.length == 2 && var24[0].endsWith(".service") && "enabled".equals(var24[1])) {
            String var12 = var24[0].substring(0, var24[0].length() - 8);
            int var13 = var12.lastIndexOf(46);
            String var14 = var13 >= 0 && var13 <= var12.length() - 2 ? var12.substring(var13 + 1) : var12;
            if (!var6.contains(var12) && !var6.contains(var14)) {
               OSService var15 = new OSService(var12, 0, OSService.State.STOPPED);
               var5.add(var15);
               var18 = true;
            }
         }
      }

      if (!var18) {
         File var21 = new File("/etc/init");
         if (var21.exists() && var21.isDirectory()) {
            for (File var27 : var21.listFiles((var0, var1) -> var1.toLowerCase().endsWith(".conf"))) {
               String var28 = var27.getName().substring(0, var27.getName().length() - 5);
               int var29 = var28.lastIndexOf(46);
               String var16 = var29 >= 0 && var29 <= var28.length() - 2 ? var28.substring(var29 + 1) : var28;
               if (!var6.contains(var28) && !var6.contains(var16)) {
                  OSService var17 = new OSService(var28, 0, OSService.State.STOPPED);
                  var5.add(var17);
               }
            }
         }
      }

      return (oshi.software.os.OSService[])(var5.toArray(new OSService[0]));
   }

   static {
      long var22 = CpuStat.getBootTime();
      if (var22 == 0L) {
         var22 = System.currentTimeMillis() / 1000L - (long)UpTime.getSystemUptimeSeconds();
      }

      s = var22;
      PROC_PID_STAT_ORDERS = new int[LinuxOperatingSystem.ProcPidStat.values().length];

      for (LinuxOperatingSystem.ProcPidStat var25 : LinuxOperatingSystem.ProcPidStat.values()) {
         PROC_PID_STAT_ORDERS[var25.ordinal()] = var25.getOrder() - 1;
      }

      String var27 = FileUtil.getStringFromFile(ProcPath.SELF_STAT);
      if (!var27.isEmpty() && var27.contains(")")) {
         P = ParseUtil.countStringToLongArray(var27, ' ') + 3;
      } else {
         P = 52;
      }

      d = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf CLK_TCK"), 100L);
      double var28 = UpTime.getSystemUptimeSeconds();
      long var30 = System.currentTimeMillis();
      double var29 = var28 + UpTime.getSystemUptimeSeconds();
      v = var30 - (long)(500.0 * var29 + 0.5);
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
      private static final LinuxOperatingSystem.ProcPidStat[] I = new LinuxOperatingSystem.ProcPidStat[]{
         PPID, USER_TIME, KERNEL_TIME, PRIORITY, THREAD_COUNT, START_TIME, VSZ, RSS
      };

      public int getOrder() {
         return this.order;
      }

      ProcPidStat(int var3) {
         this.order = var3;
      }
   }
}
