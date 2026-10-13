package oshi.software.os;

import java.util.function.Supplier;
import oshi.annotation.concurrent.NotThreadSafe;
import oshi.util.Memoizer;

@NotThreadSafe
public class OSProcess {
   private final OperatingSystem operatingSystem;
   private final Supplier<Double> cumulativeCpuLoad = Memoizer.memoize(this::S);
   private String name = "";
   private String path = "";
   private String commandLine = "";
   private String currentWorkingDirectory = "";
   private String user = "";
   private String userID = "";
   private String group = "";
   private String groupID = "";
   private OSProcess.State state = OSProcess.State.OTHER;
   private int processID;
   private int parentProcessID;
   private int threadCount;
   private int priority;
   private long virtualSize;
   private long residentSetSize;
   private long kernelTime;
   private long userTime;
   private long startTime;
   private long upTime;
   private long bytesRead;
   private long bytesWritten;
   private long openFiles;
   private int bitness;
   private double cpuPercent = -1.0;
   private static final long b = 44551403490183L;
   private static final long n = -6923444705141194659L;

   public OSProcess(OperatingSystem var1) {
      this.operatingSystem = var1;
   }

   public OSProcess(OperatingSystem var1, int var2) throws InstantiationException {
      this.processID = var2;
      this.operatingSystem = var1;
      if (!this.updateAttributes(false)) {
         throw new InstantiationException("A process with ID " + var2 + " does not exist.");
      }
   }

   public boolean updateAttributes() {
      return this.updateAttributes(true);
   }

   public boolean updateAttributes(boolean var1) {
      OSProcess var5 = this.operatingSystem.getProcess(this.processID, var1);
      if (var5 == null) {
         return false;
      }

      this.copyValuesToThisProcess(var5);
      return true;
   }

   public String getName() {
      return this.name;
   }

   public String getPath() {
      return this.path;
   }

   public String L() {
      return this.commandLine;
   }

   public String j_String() {
      return this.currentWorkingDirectory;
   }

   public String d() {
      return this.user;
   }

   public String getUserID() {
      return this.userID;
   }

   public String k() {
      return this.group;
   }

   public String getGroupID() {
      return this.groupID;
   }

   public OSProcess.State getState() {
      return this.state;
   }

   public int getProcessID() {
      return this.processID;
   }

   public int getParentProcessID() {
      return this.parentProcessID;
   }

   public int a() {
      return this.threadCount;
   }

   public int m() {
      return this.priority;
   }

   public long K() {
      return this.virtualSize;
   }

   public long h() {
      return this.residentSetSize;
   }

   public long getKernelTime() {
      return this.kernelTime;
   }

   public long W() {
      return this.userTime;
   }

   public long getUpTime() {
      return this.upTime;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public long j() {
      return this.bytesRead;
   }

   public long C() {
      return this.bytesWritten;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setPath(String var1) {
      this.path = var1;
   }

   public void setCommandLine(String var1) {
      this.commandLine = var1;
   }

   public void setCurrentWorkingDirectory(String var1) {
      this.currentWorkingDirectory = var1;
   }

   public void setUser(String var1) {
      this.user = var1;
   }

   public void setUserID(String var1) {
      this.userID = var1;
   }

   public void setGroup(String var1) {
      this.group = var1;
   }

   public void setGroupID(String var1) {
      this.groupID = var1;
   }

   public void setState(OSProcess.State var1) {
      this.state = var1;
   }

   public void setProcessID(int var1) {
      this.processID = var1;
   }

   public void setParentProcessID(int var1) {
      this.parentProcessID = var1;
   }

   public void setThreadCount(int var1) {
      this.threadCount = var1;
   }

   public void setPriority(int var1) {
      this.priority = var1;
   }

   public void setVirtualSize(long var1) {
      this.virtualSize = var1;
   }

   public void setResidentSetSize(long var1) {
      this.residentSetSize = var1;
   }

   public void setKernelTime(long var1) {
      this.kernelTime = var1;
   }

   public void setUserTime(long var1) {
      this.userTime = var1;
   }

   public void setStartTime(long var1) {
      this.startTime = var1;
   }

   public void setUpTime(long var1) {
      this.upTime = var1;
   }

   public void setBytesRead(long var1) {
      this.bytesRead = var1;
   }

   public void setBytesWritten(long var1) {
      this.bytesWritten = var1;
   }

   public void setOpenFiles(long var1) {
      this.openFiles = var1;
   }

   public long O() {
      return this.openFiles;
   }

   @Deprecated
   public double Q() {
      return this.getProcessCpuLoadCumulative();
   }

   public double getProcessCpuLoadCumulative() {
      return this.cumulativeCpuLoad.get();
   }

   private double S() {
      return (double)(this.kernelTime + this.userTime) / this.upTime;
   }

   public double getProcessCpuLoadBetweenTicks(OSProcess var1) {
      return var1 != null && this.processID == var1.processID && this.upTime > var1.upTime
         ? (double)(this.userTime - var1.userTime + this.kernelTime - var1.kernelTime) / (this.upTime - var1.upTime)
         : this.getProcessCpuLoadCumulative();
   }

   public int N() {
      return this.bitness;
   }

   public void setBitness(int var1) {
      this.bitness = var1;
   }

   @Override
   public String toString() {
      StringBuilder var3 = new StringBuilder("OSProcess@");
      var3.append(Integer.toHexString(this.hashCode()));
      var3.append("[processID=").append(this.processID);
      var3.append(", name=").append(this.name).append((char)((int)n));
      return var3.toString();
   }

   private void copyValuesToThisProcess(OSProcess var1) {
      this.name = var1.name;
      this.path = var1.path;
      if (!var1.commandLine.isEmpty()) {
         this.commandLine = var1.commandLine;
      }

      this.currentWorkingDirectory = var1.currentWorkingDirectory;
      this.user = var1.user;
      this.userID = var1.userID;
      if (!var1.group.isEmpty()) {
         this.group = var1.group;
      }

      if (!var1.groupID.isEmpty()) {
         this.groupID = var1.groupID;
      }

      this.state = var1.state;
      this.processID = var1.processID;
      this.parentProcessID = var1.parentProcessID;
      this.threadCount = var1.threadCount;
      this.priority = var1.priority;
      this.virtualSize = var1.virtualSize;
      this.residentSetSize = var1.residentSetSize;
      this.kernelTime = var1.kernelTime;
      this.userTime = var1.userTime;
      this.startTime = var1.startTime;
      this.upTime = var1.upTime;
      this.bytesRead = var1.bytesRead;
      this.bytesWritten = var1.bytesWritten;
      if (var1.openFiles > 0L) {
         this.openFiles = var1.openFiles;
      }

      this.bitness = var1.bitness;
      this.cpuPercent = var1.cpuPercent;
   }

   public enum State {
      NEW,
      RUNNING,
      SLEEPING,
      WAITING,
      ZOMBIE,
      STOPPED,
      OTHER;
   }
}
