package oshi.software.os;

import java.util.function.Supplier;
import oshi.annotation.concurrent.NotThreadSafe;
import oshi.util.Memoizer;

@NotThreadSafe
public class OSProcess {
   private final OperatingSystem operatingSystem;
   private final Supplier<Double> cumulativeCpuLoad = Memoizer.memoize(this::queryCumulativeCpuLoad);
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

   public OSProcess(OperatingSystem operatingSystem) {
      this.operatingSystem = operatingSystem;
   }

   public OSProcess(OperatingSystem operatingSystem, int processID) throws InstantiationException {
      this.processID = processID;
      this.operatingSystem = operatingSystem;
      if (!this.updateAttributes(false)) {
         throw new InstantiationException("A process with ID " + processID + " does not exist.");
      }
   }

   public boolean updateAttributes() {
      return this.updateAttributes(true);
   }

   public boolean updateAttributes(boolean slowFields) {
      OSProcess osProcess = this.operatingSystem.getProcess(this.processID, slowFields);
      if (osProcess == null) {
         return false;
      }

      this.copyValuesToThisProcess(osProcess);
      return true;
   }

   public String getName() {
      return this.name;
   }

   public String getPath() {
      return this.path;
   }

   public String getCommandLine() {
      return this.commandLine;
   }

   public String j_String() {
      return this.currentWorkingDirectory;
   }

   public String getUser() {
      return this.user;
   }

   public String getUserID() {
      return this.userID;
   }

   public String getGroup() {
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

   public int getThreadCount() {
      return this.threadCount;
   }

   public int getPriority() {
      return this.priority;
   }

   public long getVirtualSize() {
      return this.virtualSize;
   }

   public long getResidentSetSize() {
      return this.residentSetSize;
   }

   public long getKernelTime() {
      return this.kernelTime;
   }

   public long getUserTime() {
      return this.userTime;
   }

   public long getUpTime() {
      return this.upTime;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public long getBytesRead() {
      return this.bytesRead;
   }

   public long getBytesWritten() {
      return this.bytesWritten;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public void setCommandLine(String commandLine) {
      this.commandLine = commandLine;
   }

   public void setCurrentWorkingDirectory(String currentWorkingDirectory) {
      this.currentWorkingDirectory = currentWorkingDirectory;
   }

   public void setUser(String user) {
      this.user = user;
   }

   public void setUserID(String userID) {
      this.userID = userID;
   }

   public void setGroup(String group) {
      this.group = group;
   }

   public void setGroupID(String groupID) {
      this.groupID = groupID;
   }

   public void setState(OSProcess.State state) {
      this.state = state;
   }

   public void setProcessID(int processID) {
      this.processID = processID;
   }

   public void setParentProcessID(int parentProcessID) {
      this.parentProcessID = parentProcessID;
   }

   public void setThreadCount(int threadCount) {
      this.threadCount = threadCount;
   }

   public void setPriority(int priority) {
      this.priority = priority;
   }

   public void setVirtualSize(long virtualSize) {
      this.virtualSize = virtualSize;
   }

   public void setResidentSetSize(long residentSetSize) {
      this.residentSetSize = residentSetSize;
   }

   public void setKernelTime(long kernelTime) {
      this.kernelTime = kernelTime;
   }

   public void setUserTime(long userTime) {
      this.userTime = userTime;
   }

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   public void setUpTime(long upTime) {
      this.upTime = upTime;
   }

   public void setBytesRead(long bytesRead) {
      this.bytesRead = bytesRead;
   }

   public void setBytesWritten(long bytesWritten) {
      this.bytesWritten = bytesWritten;
   }

   public void setOpenFiles(long count) {
      this.openFiles = count;
   }

   public long getOpenFiles() {
      return this.openFiles;
   }

   @Deprecated
   public double calculateCpuPercent() {
      return this.getProcessCpuLoadCumulative();
   }

   public double getProcessCpuLoadCumulative() {
      return this.cumulativeCpuLoad.get();
   }

   private double queryCumulativeCpuLoad() {
      return (double)(this.kernelTime + this.userTime) / this.upTime;
   }

   public double getProcessCpuLoadBetweenTicks(OSProcess priorSnapshot) {
      return priorSnapshot != null && this.processID == priorSnapshot.processID && this.upTime > priorSnapshot.upTime
         ? (double)(this.userTime - priorSnapshot.userTime + this.kernelTime - priorSnapshot.kernelTime) / (this.upTime - priorSnapshot.upTime)
         : this.getProcessCpuLoadCumulative();
   }

   public int getBitness() {
      return this.bitness;
   }

   public void setBitness(int bitness) {
      this.bitness = bitness;
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder("OSProcess@");
      stringBuilder.append(Integer.toHexString(this.hashCode()));
      stringBuilder.append("[processID=").append(this.processID);
      stringBuilder.append(", name=").append(this.name).append(']');
      return stringBuilder.toString();
   }

   private void copyValuesToThisProcess(OSProcess sourceProcess) {
      this.name = sourceProcess.name;
      this.path = sourceProcess.path;
      if (!sourceProcess.commandLine.isEmpty()) {
         this.commandLine = sourceProcess.commandLine;
      }

      this.currentWorkingDirectory = sourceProcess.currentWorkingDirectory;
      this.user = sourceProcess.user;
      this.userID = sourceProcess.userID;
      if (!sourceProcess.group.isEmpty()) {
         this.group = sourceProcess.group;
      }

      if (!sourceProcess.groupID.isEmpty()) {
         this.groupID = sourceProcess.groupID;
      }

      this.state = sourceProcess.state;
      this.processID = sourceProcess.processID;
      this.parentProcessID = sourceProcess.parentProcessID;
      this.threadCount = sourceProcess.threadCount;
      this.priority = sourceProcess.priority;
      this.virtualSize = sourceProcess.virtualSize;
      this.residentSetSize = sourceProcess.residentSetSize;
      this.kernelTime = sourceProcess.kernelTime;
      this.userTime = sourceProcess.userTime;
      this.startTime = sourceProcess.startTime;
      this.upTime = sourceProcess.upTime;
      this.bytesRead = sourceProcess.bytesRead;
      this.bytesWritten = sourceProcess.bytesWritten;
      if (sourceProcess.openFiles > 0L) {
         this.openFiles = sourceProcess.openFiles;
      }

      this.bitness = sourceProcess.bitness;
      this.cpuPercent = sourceProcess.cpuPercent;
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
