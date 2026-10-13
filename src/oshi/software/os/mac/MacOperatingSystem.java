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
   private static final int X = 4;
   private static final int R = 2;
   private static final int D = 3;
   private static final int S = 4;
   private static final int Y = 5;
   private static final int H = 6;
   private static final long BOOTTIME;
   private static final long d = 164746557421L;

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
      long var1 = d ^ 48550876888294L;
      String var4 = this.major == 10 && this.minor >= 12 ? "macOS" : System.getProperty("os.name");
      String var5 = this.parseCodeName();
      long var10000 = 8784670395099931783L ^ var1;
      String var6 = SysctlUtil.sysctl("kern.osversion", "");
      return new AbstractOperatingSystem.FamilyVersionInfo(var4, new OperatingSystem.OSVersionInfo(this.osXVersion, var5, var6));
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
   public int queryBitness(int var1) {
      return var1 != 64 && (this.major != 10 || this.minor <= 6) ? ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("getconf LONG_BIT"), 32) : 64;
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
   public OSProcess[] getProcesses(int var1, OperatingSystem.ProcessSort var2, boolean var3) {
      ArrayList var7 = new ArrayList();
      int[] var8 = new int[this.maxProc];
      int var9 = SystemB.INSTANCE.proc_listpids(1, 0, var8, var8.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;

      for (int var10 = 0; var10 < var9; var10++) {
         if (var8[var10] != 0) {
            OSProcess var11 = this.getProcess(var8[var10], var3);
            if (var11 != null) {
               var7.add(var11);
            }
         }
      }

      List var12 = this.processSort(var7, var1, var2);
      return (oshi.software.os.OSProcess[])(var12.toArray(new OSProcess[0]));
   }

   @Override
   public OSProcess getProcess(int var1, boolean var2) {
      ProcTaskAllInfo var6 = new ProcTaskAllInfo();
      if (0 > SystemB.INSTANCE.proc_pidinfo(var1, 2, 0L, var6, var6.size())) {
         return null;
      }

      String var7 = null;
      String var8 = "";
      Memory var9 = new Memory(4096L);
      if (0 < SystemB.INSTANCE.proc_pidpath(var1, var9, 4096)) {
         var8 = var9.getString(0L).trim();
         String[] var10 = var8.split("/");
         if (var10.length > 0) {
            var7 = var10[var10.length - 1];
         }
      }

      if (var6.ptinfo.pti_threadnum < 1) {
         return null;
      }

      if (var7 == null) {
         for (int var25 = 0; var25 < var6.pbsd.pbi_comm.length; var25++) {
            if (var6.pbsd.pbi_comm[var25] == 0) {
               var7 = new String(var6.pbsd.pbi_comm, 0, var25, StandardCharsets.UTF_8);
               break;
            }
         }
      }

      long var26 = 0L;
      long var12 = 0L;
      if (this.minor >= 9) {
         RUsageInfoV2 var14 = new RUsageInfoV2();
         if (0 == SystemB.INSTANCE.proc_pid_rusage(var1, 2, var14)) {
            var26 = var14.ri_diskio_bytesread;
            var12 = var14.ri_diskio_byteswritten;
         }
      }

      long var27 = System.currentTimeMillis();
      OSProcess var16 = new OSProcess(this);
      var16.setName(var7);
      var16.setPath(var8);
      switch (var6.pbsd.pbi_status) {
         case 1:
            var16.setState(OSProcess.State.SLEEPING);
            break;
         case 2:
            var16.setState(OSProcess.State.WAITING);
            break;
         case 3:
            var16.setState(OSProcess.State.RUNNING);
            break;
         case 4:
            var16.setState(OSProcess.State.NEW);
            break;
         case 5:
            var16.setState(OSProcess.State.ZOMBIE);
            break;
         case 6:
            var16.setState(OSProcess.State.STOPPED);
            break;
         default:
            var16.setState(OSProcess.State.OTHER);
      }

      var16.setProcessID(var1);
      var16.setParentProcessID(var6.pbsd.pbi_ppid);
      var16.setUserID(Integer.toString(var6.pbsd.pbi_uid));
      Passwd var17 = SystemB.INSTANCE.getpwuid(var6.pbsd.pbi_uid);
      var16.setUser(var17 == null ? var16.getUserID() : var17.pw_name);
      var16.setGroupID(Integer.toString(var6.pbsd.pbi_gid));
      Group var18 = SystemB.INSTANCE.getgrgid(var6.pbsd.pbi_gid);
      var16.setGroup(var18 == null ? var16.getGroupID() : var18.gr_name);
      var16.setThreadCount(var6.ptinfo.pti_threadnum);
      var16.setPriority(var6.ptinfo.pti_priority);
      var16.setVirtualSize(var6.ptinfo.pti_virtual_size);
      var16.setResidentSetSize(var6.ptinfo.pti_resident_size);
      var16.setKernelTime(var6.ptinfo.pti_total_system / 1000000L);
      var16.setUserTime(var6.ptinfo.pti_total_user / 1000000L);
      var16.setStartTime(var6.pbsd.pbi_start_tvsec * 1000L + var6.pbsd.pbi_start_tvusec / 1000L);
      var16.setUpTime(var27 - var16.getStartTime());
      var16.setBytesRead(var26);
      var16.setBytesWritten(var12);
      var16.setCommandLine(this.getCommandLine(var1));
      var16.setOpenFiles(var6.pbsd.pbi_nfiles);
      var16.setBitness((var6.pbsd.pbi_flags & 4) == 0 ? 32 : 64);
      VnodePathInfo var19 = new VnodePathInfo();
      if (0 < SystemB.INSTANCE.proc_pidinfo(var1, 9, 0L, var19, var19.size())) {
         int var20 = 0;

         for (byte var24 : var19.pvi_cdir.vip_path) {
            if (var24 == 0) {
               break;
            }

            var20++;
         }

         var16.setCurrentWorkingDirectory(new String(var19.pvi_cdir.vip_path, 0, var20, StandardCharsets.US_ASCII));
      }

      return var16;
   }

   @Override
   public OSProcess[] getChildProcesses(int var1, int var2, OperatingSystem.ProcessSort var3) {
      ArrayList var7 = new ArrayList();
      int[] var8 = new int[this.maxProc];
      int var9 = SystemB.INSTANCE.proc_listpids(1, 0, var8, var8.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;

      for (int var10 = 0; var10 < var9; var10++) {
         if (var8[var10] != 0 && var1 == this.getParentProcessPid(var8[var10])) {
            OSProcess var11 = this.getProcess(var8[var10], true);
            if (var11 != null) {
               var7.add(var11);
            }
         }
      }

      List var12 = this.processSort(var7, var2, var3);
      return (oshi.software.os.OSProcess[])(var12.toArray(new OSProcess[0]));
   }

   private int getParentProcessPid(int var1) {
      ProcTaskAllInfo var5 = new ProcTaskAllInfo();
      return 0 > SystemB.INSTANCE.proc_pidinfo(var1, 2, 0L, var5, var5.size()) ? 0 : var5.pbsd.pbi_ppid;
   }

   private String getCommandLine(int var1) {
      int[] var5 = new int[]{1, 49, var1};
      int var6 = SysctlUtil.sysctl("kern.argmax", 0);
      Memory var7 = new Memory(var6);
      IntByReference var8 = new IntByReference(var6);
      if (0 != SystemB.INSTANCE.sysctl(var5, var5.length, var7, var8, null, 0)) {
         return "";
      }

      int var9 = var7.getInt(0L);
      if (var9 >= 0 && var9 <= 1024) {
         ArrayList var10 = new ArrayList(var9);
         long var11 = SystemB.INT_SIZE;
         var11 += var7.getString(var11).length();

         while (var9-- > 0 && var11 < var8.getValue()) {
            while (var7.getByte(var11) == 0 && ++var11 < var8.getValue()) {
            }

            String var13 = var7.getString(var11);
            var10.add(var13);
            var11 += var13.length();
         }

         return String.join("\u0000", var10);
      } else {
         return "";
      }
   }

   @Override
   public long getProcessAffinityMask(int var1) {
      int var4 = SysctlUtil.sysctl("hw.logicalcpu", 1);
      return var4 < 64 ? (1L << var4) - 1L : -1L;
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
      int[] var4 = new int[this.getProcessCount() + 10];
      int var5 = SystemB.INSTANCE.proc_listpids(1, 0, var4, var4.length) / SystemB.INT_SIZE;
      int var6 = 0;
      ProcTaskInfo var7 = new ProcTaskInfo();

      for (int var8 = 0; var8 < var5; var8++) {
         SystemB.INSTANCE.proc_pidinfo(var4[var8], 4, 0L, var7, var7.size());
         var6 += var7.pti_threadnum;
      }

      return var6;
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
      ArrayList var5 = new ArrayList();
      HashSet var6 = new HashSet();

      for (OSProcess var10 : this.getChildProcesses(1, 0, OperatingSystem.ProcessSort.PID)) {
         OSService var11 = new OSService(var10.getName(), var10.getProcessID(), OSService.State.RUNNING);
         var5.add(var11);
         var6.add(var10.getName());
      }

      ArrayList var15 = new ArrayList();
      File var16 = new File("/System/Library/LaunchAgents");
      if (var16.exists() && var16.isDirectory()) {
         var15.addAll(Arrays.asList(var16.listFiles((var0, var1) -> var1.toLowerCase().endsWith(".plist"))));
      }

      var16 = new File("/System/Library/LaunchDaemons");
      if (var16.exists() && var16.isDirectory()) {
         var15.addAll(Arrays.asList(var16.listFiles((var0, var1) -> var1.toLowerCase().endsWith(".plist"))));
      }

      for (Object var19$raw : var15) {
         File var19 = (File)var19$raw;
         String var20 = var19.getName().substring(0, var19.getName().length() - 6);
         int var12 = var20.lastIndexOf(46);
         String var13 = var12 >= 0 && var12 <= var20.length() - 2 ? var20.substring(var12 + 1) : var20;
         if (!var6.contains(var20) && !var6.contains(var13)) {
            OSService var14 = new OSService(var20, 0, OSService.State.STOPPED);
            var5.add(var14);
         }
      }

      return (oshi.software.os.OSService[])(var5.toArray(new OSService[0]));
   }

   static {
      long var20 = d ^ 31703834107096L;
      long var10000 = var20 ^ 6105657261644988929L;
      var10000 = var20 ^ 1197614836592261684L;
      var10000 = var20 ^ 743142429263370285L;
      var10000 = var20 ^ 743142429263370285L;
      var10000 = var20 ^ 6498231792738444290L;
      var10000 = var20 ^ 6973601558899677628L;
      Timeval var22 = new Timeval();
      if (SysctlUtil.sysctl("kern.boottime", var22) && var22.tv_sec.longValue() != 0L) {
         BOOTTIME = var22.tv_sec.longValue();
      } else {
         BOOTTIME = ParseUtil.parseLongOrDefault(
            ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), System.currentTimeMillis() / 1000L
         );
      }
   }
}
