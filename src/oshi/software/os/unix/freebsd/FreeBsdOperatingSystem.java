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
   private static final long d = 32426221552950L;

   public FreeBsdOperatingSystem() {
      this.version = new FreeBsdOSVersionInfoEx();
   }

   @Override
   public String queryManufacturer() {
      return "Unix/BSD";
   }

   @Override
   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      String var3 = BsdSysctlUtil.sysctl("kern.ostype", "FreeBSD");
      String var4 = BsdSysctlUtil.sysctl("kern.osrelease", "");
      String var5 = BsdSysctlUtil.sysctl("kern.version", "");
      String var6 = var5.split(":")[0].replace(var3, "").replace(var4, "").trim();
      return new AbstractOperatingSystem.FamilyVersionInfo(var3, new OperatingSystem.OSVersionInfo(var4, null, var6));
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
      return new FreeBsdFileSystem();
   }

   @Override
   public OSProcess[] getProcesses(int var1, OperatingSystem.ProcessSort var2, boolean var3) {
      List var6 = this.getProcessListFromPS("ps -awwxo state,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etimes,systime,time,comm,args", -1, var3);
      List var7 = this.processSort(var6, var1, var2);
      return (oshi.software.os.OSProcess[])(var7.toArray(new OSProcess[0]));
   }

   @Override
   public OSProcess getProcess(int var1, boolean var2) {
      List var6 = this.getProcessListFromPS("ps -awwxo state,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etimes,systime,time,comm,args -p ", var1, var2);
      return var6.isEmpty() ? null : (OSProcess)var6.get(0);
   }

   @Override
   public OSProcess[] getChildProcesses(int var1, int var2, OperatingSystem.ProcessSort var3) {
      ArrayList var7 = new ArrayList();

      for (OSProcess var11 : this.getProcesses(0, null)) {
         if (var11.getParentProcessID() == var1) {
            var7.add(var11);
         }
      }

      List var12 = this.processSort(var7, var2, var3);
      return (oshi.software.os.OSProcess[])(var12.toArray(new OSProcess[0]));
   }

   private List<OSProcess> getProcessListFromPS(String var1, int var2, boolean var3) {
      Map var7 = LsofUtil.getCwdMap(var2);
      ArrayList var8 = new ArrayList();
      List var9 = ExecutingCommand.runNative(var1 + (var2 < 0 ? "" : var2));
      if (!var9.isEmpty() && var9.size() >= 2) {
         var9.remove(0);

         for (Object var11$raw : var9) {
            String var11 = (String)var11$raw;
            String[] var12 = ParseUtil.Q.split(var11.trim(), 16);
            if (var12.length >= 16) {
               long var13 = System.currentTimeMillis();
               OSProcess var15 = new OSProcess(this);
               switch (var12[0].charAt(0)) {
                  case 'D':
                  case 'L':
                  case 'U':
                     var15.setState(OSProcess.State.WAITING);
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
                     var15.setState(OSProcess.State.OTHER);
                     break;
                  case 'I':
                  case 'S':
                     var15.setState(OSProcess.State.SLEEPING);
                     break;
                  case 'R':
                     var15.setState(OSProcess.State.RUNNING);
                     break;
                  case 'T':
                     var15.setState(OSProcess.State.STOPPED);
                     break;
                  case 'Z':
                     var15.setState(OSProcess.State.ZOMBIE);
               }

               var15.setProcessID(ParseUtil.parseIntOrDefault(var12[1], 0));
               var15.setParentProcessID(ParseUtil.parseIntOrDefault(var12[2], 0));
               var15.setUser(var12[3]);
               var15.setUserID(var12[4]);
               var15.setGroup(var12[5]);
               var15.setGroupID(var12[6]);
               var15.setThreadCount(ParseUtil.parseIntOrDefault(var12[7], 0));
               var15.setPriority(ParseUtil.parseIntOrDefault(var12[8], 0));
               var15.setVirtualSize(ParseUtil.parseLongOrDefault(var12[9], 0L) * 1024L);
               var15.setResidentSetSize(ParseUtil.parseLongOrDefault(var12[10], 0L) * 1024L);
               long var16 = ParseUtil.parseDHMSOrDefault(var12[11], 0L);
               var15.setUpTime(var16 < 1L ? 1L : var16);
               var15.setStartTime(var13 - var15.getUpTime());
               var15.setKernelTime(ParseUtil.parseDHMSOrDefault(var12[12], 0L));
               var15.setUserTime(ParseUtil.parseDHMSOrDefault(var12[13], 0L) - var15.getKernelTime());
               var15.setPath(var12[14]);
               var15.setName(var15.getPath().substring(var15.getPath().lastIndexOf(47) + 1));
               var15.setCommandLine(var12[15]);
               var15.setCurrentWorkingDirectory((java.lang.String)(var7.getOrDefault(var15.getProcessID(), "")));
               if (var3) {
                  List var18 = ExecutingCommand.runNative(String.format("lsof -p %d", var2));
                  var15.setOpenFiles(var18.size() - 1L);
                  int[] var19 = new int[]{1, 14, 9, var2};
                  Memory var20 = new Memory(32L);
                  IntByReference var21 = new IntByReference(32);
                  if (0 == FreeBsdLibc.INSTANCE.sysctl(var19, var19.length, var20, var21, null, 0)) {
                     String var22 = var20.getString(0L);
                     if (var22.contains("ELF32")) {
                        var15.setBitness(32);
                     } else if (var22.contains("ELF64")) {
                        var15.setBitness(64);
                     }
                  }
               }

               var8.add(var15);
            }
         }

         return var8;
      } else {
         return var8;
      }
   }

   @Override
   public long getProcessAffinityMask(int var1) {
      long var5 = 0L;
      String var7 = ExecutingCommand.getFirstAnswer("cpuset -gp " + var1);
      String[] var8 = var7.split(":");
      if (var8.length > 1) {
         String[] var9 = var8[1].split(",");

         for (String var13 : var9) {
            int var14 = ParseUtil.parseIntOrDefault(var13.trim(), -1);
            if (var14 >= 0) {
               var5 |= 1L << var14;
            }
         }
      }

      return var5;
   }

   @Override
   public int getProcessId() {
      return FreeBsdLibc.INSTANCE.getpid();
   }

   @Override
   public int getProcessCount() {
      List var4 = ExecutingCommand.runNative("ps -axo pid");
      return !var4.isEmpty() ? var4.size() - 1 : 0;
   }

   @Override
   public int getThreadCount() {
      int var4 = 0;

      for (String var6 : ExecutingCommand.runNative("ps -axo nlwp")) {
         var4 += ParseUtil.parseIntOrDefault(var6.trim(), 0);
      }

      return var4;
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
      CLibrary.Timeval var2 = new CLibrary.Timeval();
      return BsdSysctlUtil.sysctl("kern.boottime", var2) && var2.tv_sec != 0L
         ? var2.tv_sec
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
      ArrayList var4 = new ArrayList();
      HashSet var5 = new HashSet();

      for (OSProcess var9 : this.getChildProcesses(1, 0, OperatingSystem.ProcessSort.PID)) {
         OSService var10 = new OSService(var9.getName(), var9.getProcessID(), OSService.State.RUNNING);
         var4.add(var10);
         var5.add(var9.getName());
      }

      File var14 = new File("/etc/rc.d");
      File[] var15;
      if (var14.exists() && var14.isDirectory() && (var15 = var14.listFiles()) != null) {
         for (File var11 : var15) {
            String var12 = var11.getName();
            if (!var5.contains(var12)) {
               OSService var13 = new OSService(var12, 0, OSService.State.STOPPED);
               var4.add(var13);
            }
         }
      }

      return (oshi.software.os.OSService[])(var4.toArray(new OSService[0]));
   }
}
