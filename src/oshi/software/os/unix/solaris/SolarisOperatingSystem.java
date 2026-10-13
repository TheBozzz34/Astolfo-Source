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
   private static final long A = querySystemBootTime();
   private static final long d = 68904284690820L;

   public SolarisOperatingSystem() {
      this.version = new SolarisOSVersionInfoEx();
   }

   @Override
   public String queryManufacturer() {
      return "Oracle";
   }

   @Override
   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      String[] var4 = ParseUtil.Q.split(ExecutingCommand.getFirstAnswer("uname -rv"));
      String var5 = var4[0];
      String var6 = null;
      if (var4.length > 1) {
         var6 = var4[1];
      }

      return new AbstractOperatingSystem.FamilyVersionInfo("SunOS", new OperatingSystem.OSVersionInfo(var5, "Solaris", var6));
   }

   @Override
   public int queryBitness(int var1) {
      return var1 == 64 ? 64 : ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("isainfo -b"), 32);
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
   public OSProcess[] getProcesses(int var1, OperatingSystem.ProcessSort var2, boolean var3) {
      List var6 = this.getProcessListFromPS("ps -eo s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args", -1, var3);
      List var7 = this.processSort(var6, var1, var2);
      return (oshi.software.os.OSProcess[])(var7.toArray(new OSProcess[0]));
   }

   @Override
   public OSProcess getProcess(int var1, boolean var2) {
      List var6 = this.getProcessListFromPS("ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p ", var1, var2);
      return var6.isEmpty() ? null : (OSProcess)var6.get(0);
   }

   @Override
   public OSProcess[] getChildProcesses(int var1, int var2, OperatingSystem.ProcessSort var3) {
      List var6 = this.getProcessListFromPS("ps -eo s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args --ppid", var1, true);
      List var7 = this.processSort(var6, var2, var3);
      return (oshi.software.os.OSProcess[])(var7.toArray(new OSProcess[0]));
   }

   private List<OSProcess> getProcessListFromPS(String var1, int var2, boolean var3) {
      Map var7 = LsofUtil.getCwdMap(var2);
      ArrayList var8 = new ArrayList();
      List var9 = ExecutingCommand.runNative(var1 + (var2 < 0 ? "" : var2));
      if (!var9.isEmpty() && var9.size() >= 2) {
         var9.remove(0);

         for (Object var11$raw : var9) {
            String var11 = (String)var11$raw;
            String[] var12 = ParseUtil.Q.split(var11.trim(), 15);
            if (var12.length >= 15) {
               long var13 = System.currentTimeMillis();
               OSProcess var15 = new OSProcess(this);
               switch (var12[0].charAt(0)) {
                  case 'O':
                     var15.setState(OSProcess.State.RUNNING);
                     break;
                  case 'P':
                  case 'Q':
                  case 'U':
                  case 'V':
                  case 'X':
                  case 'Y':
                  default:
                     var15.setState(OSProcess.State.OTHER);
                     break;
                  case 'R':
                  case 'W':
                     var15.setState(OSProcess.State.WAITING);
                     break;
                  case 'S':
                     var15.setState(OSProcess.State.SLEEPING);
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
               var15.setUserTime(ParseUtil.parseDHMSOrDefault(var12[12], 0L));
               var15.setPath(var12[13]);
               var15.setName(var15.getPath().substring(var15.getPath().lastIndexOf(47) + 1));
               var15.setCommandLine(var12[14]);
               var15.setCurrentWorkingDirectory((java.lang.String)(var7.getOrDefault(var15.getProcessID(), "")));
               if (var3) {
                  List var18 = ExecutingCommand.runNative(String.format("lsof -p %d", var2));
                  var15.setOpenFiles(var18.size() - 1L);

                  for (String var21 : ExecutingCommand.runNative("pflags " + var2)) {
                     if (var21.contains("data model")) {
                        if (var21.contains("LP32")) {
                           var15.setBitness(32);
                        } else if (var21.contains("LP64")) {
                           var15.setBitness(64);
                        }
                        break;
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
      String var7 = ExecutingCommand.getFirstAnswer("pbind -q " + var1);
      if (var7.isEmpty()) {
         for (String var15 : ExecutingCommand.runNative("psrinfo")) {
            String[] var16 = ParseUtil.Q.split(var15);
            int var12 = ParseUtil.parseIntOrDefault(var16[0], -1);
            if (var12 >= 0) {
               var5 |= 1L << var12;
            }
         }

         return var5;
      } else {
         if (var7.endsWith(".") && var7.contains("strongly bound to processor(s)")) {
            String var8 = var7.substring(0, var7.length() - 1);
            String[] var9 = ParseUtil.Q.split(var8);

            for (int var10 = var9.length - 1; var10 >= 0; var10--) {
               int var11 = ParseUtil.parseIntOrDefault(var9[var10], -1);
               if (var11 < 0) {
                  break;
               }

               var5 |= 1L << var11;
            }
         }

         return var5;
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
      List var4 = ExecutingCommand.runNative("ps -eLo pid");
      return !var4.isEmpty() ? var4.size() - 1 : this.getProcessCount();
   }

   @Override
   public long getSystemUptime() {
      return querySystemUptime();
   }

   private static long querySystemUptime() {
      try (KstatUtil.KstatChain var2 = KstatUtil.openChain()) {
         Kstat var4 = var2.lookup("unix", 0, "system_misc");
         if (var4 != null) {
            return var4.ks_snaptime / 1000000000L;
         }
      }

      return 0L;
   }

   @Override
   public long getSystemBootTime() {
      return A;
   }

   private static long querySystemBootTime() {
      try (KstatUtil.KstatChain var2 = KstatUtil.openChain()) {
         Kstat var4 = var2.lookup("unix", 0, "system_misc");
         if (var4 != null && var2.read(var4)) {
            return KstatUtil.dataLookupLong(var4, "boot_time");
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
      ArrayList var5 = new ArrayList();
      ArrayList var6 = new ArrayList();
      File var7 = new File("/etc/init.d");
      File[] var8;
      if (var7.exists() && var7.isDirectory() && (var8 = var7.listFiles()) != null) {
         for (File var12 : var8) {
            var6.add(var12.getName());
         }
      }

      for (String var16 : ExecutingCommand.runNative("svcs -p")) {
         if (var16.startsWith("online")) {
            int var19 = var16.lastIndexOf(":/");
            if (var19 > 0) {
               String var20 = var16.substring(var19 + 1);
               if (var20.endsWith(":default")) {
                  var20 = var20.substring(0, var20.length() - 8);
               }

               var5.add(new OSService(var20, 0, OSService.State.STOPPED));
            }
         } else if (var16.startsWith(" ")) {
            String[] var18 = ParseUtil.Q.split(var16.trim());
            if (var18.length == 3) {
               var5.add(new OSService(var18[2], ParseUtil.parseIntOrDefault(var18[1], 0), OSService.State.RUNNING));
            }
         } else if (var16.startsWith("legacy_run")) {
            for (Object var13$raw : var6) {
               String var13 = (String)var13$raw;
               if (var16.endsWith(var13)) {
                  var5.add(new OSService(var13, 0, OSService.State.STOPPED));
                  break;
               }
            }
         }
      }

      return (oshi.software.os.OSService[])(var5.toArray(new OSService[0]));
   }
}
