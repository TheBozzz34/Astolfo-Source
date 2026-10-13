package oshi.hardware;

import oshi.SystemInfo;
import oshi.annotation.concurrent.NotThreadSafe;
import oshi.hardware.platform.linux.LinuxDisks;
import oshi.hardware.platform.mac.MacDisks;
import oshi.hardware.platform.unix.freebsd.FreeBsdDisks;
import oshi.hardware.platform.unix.solaris.SolarisDisks;
import oshi.hardware.platform.windows.WindowsDisks;
import oshi.util.FormatUtil;

@NotThreadSafe
public class HWDiskStore implements Comparable<HWDiskStore> {
   private String model = "";
   private String name = "";
   private String serial = "";
   private long size = 0L;
   private long reads = 0L;
   private long readBytes = 0L;
   private long writes = 0L;
   private long writeBytes = 0L;
   private long currentQueueLength = 0L;
   private long transferTime = 0L;
   private HWPartition[] partitions = new HWPartition[0];
   private long timeStamp = 0L;
   private static final long a = 120662254243529L;

   public HWDiskStore() {
   }

   public HWDiskStore(HWDiskStore var1) {
      HWPartition[] var5 = var1.getPartitions();
      HWPartition[] var6 = new HWPartition[var5.length];

      for (int var7 = 0; var7 < var5.length; var7++) {
         var6[var7] = new HWPartition(
            var5[var7].getIdentification(),
            var5[var7].getName(),
            var5[var7].getType(),
            var5[var7].getUuid(),
            var5[var7].getSize(),
            var5[var7].getMajor(),
            var5[var7].getMinor(),
            var5[var7].getMountPoint()
         );
      }

      this.name = var1.name;
      this.model = var1.model;
      this.serial = var1.serial;
      this.size = var1.size;
      this.reads = var1.reads;
      this.readBytes = var1.readBytes;
      this.writes = var1.writes;
      this.writeBytes = var1.writeBytes;
      this.currentQueueLength = var1.currentQueueLength;
      this.transferTime = var1.transferTime;
      this.partitions = var6;
      this.timeStamp = var1.timeStamp;
   }

   public boolean updateAtrributes() {
      switch (SystemInfo.getCurrentPlatformEnum()) {
         case WINDOWS:
            return WindowsDisks.updateDiskStats(this);
         case LINUX:
            return LinuxDisks.updateDiskStats(this);
         case MACOSX:
            return MacDisks.updateDiskStats(this);
         case SOLARIS:
            return SolarisDisks.updateDiskStats(this);
         case FREEBSD:
            return FreeBsdDisks.updateDiskStats(this);
         default:
            return false;
      }
   }

   public String getName() {
      return this.name;
   }

   public String getModel() {
      return this.model;
   }

   public String getSerial() {
      return this.serial;
   }

   public long getSize() {
      return this.size;
   }

   public long getReads() {
      return this.reads;
   }

   public long getReadBytes() {
      return this.readBytes;
   }

   public long getWrites() {
      return this.writes;
   }

   public long getWriteBytes() {
      return this.writeBytes;
   }

   public long q() {
      return this.currentQueueLength;
   }

   public long getTransferTime() {
      return this.transferTime;
   }

   public HWPartition[] getPartitions() {
      return this.partitions;
   }

   public long n() {
      return this.timeStamp;
   }

   public void setName(String var1) {
      this.name = var1 == null ? "" : var1;
   }

   public void setModel(String var1) {
      this.model = var1 == null ? "" : var1;
   }

   public void setSerial(String var1) {
      this.serial = var1 == null ? "" : var1;
   }

   public void setSize(long var1) {
      this.size = var1;
   }

   public void setReads(long var1) {
      this.reads = var1;
   }

   public void setReadBytes(long var1) {
      this.readBytes = var1;
   }

   public void setWrites(long var1) {
      this.writes = var1;
   }

   public void setWriteBytes(long var1) {
      this.writeBytes = var1;
   }

   public void setCurrentQueueLength(long var1) {
      this.currentQueueLength = var1;
   }

   public void setTransferTime(long var1) {
      this.transferTime = var1;
   }

   public void setPartitions(HWPartition[] var1) {
      this.partitions = var1;
   }

   public void setTimeStamp(long var1) {
      this.timeStamp = var1;
   }

   public int compareTo(HWDiskStore var1) {
      return this.getName().compareTo(var1.getName());
   }

   @Override
   public int hashCode() {
      int var5 = 1;
      var5 = 31 * var5 + (this.model == null ? 0 : this.model.hashCode());
      var5 = 31 * var5 + (this.name == null ? 0 : this.name.hashCode());
      var5 = 31 * var5 + (this.serial == null ? 0 : this.serial.hashCode());
      return 31 * var5 + (int)(this.size ^ this.size >>> 32);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 == null) {
         return false;
      }

      if (!var1.getClass().equals(this.getClass())) {
         return false;
      }

      HWDiskStore var5 = (HWDiskStore)var1;
      if (this.model == null) {
         if (var5.model != null) {
            return false;
         }
      } else if (!this.model.equals(var5.model)) {
         return false;
      }

      if (this.name == null) {
         if (var5.name != null) {
            return false;
         }
      } else if (!this.name.equals(var5.name)) {
         return false;
      }

      if (this.serial == null) {
         if (var5.serial != null) {
            return false;
         }
      } else if (!this.serial.equals(var5.serial)) {
         return false;
      }

      return this.size == var5.size;
   }

   @Override
   public String toString() {
      boolean var4 = this.getReads() > 0L || this.getWrites() > 0L;
      StringBuilder var5 = new StringBuilder();
      var5.append(this.getName()).append(": ");
      var5.append("(model: ").append(this.getModel());
      var5.append(" - S/N: ").append(this.getSerial()).append(") ");
      var5.append("size: ").append(this.getSize() > 0L ? FormatUtil.formatBytesDecimal(this.getSize()) : "?").append(", ");
      var5.append("reads: ").append(var4 ? this.getReads() : "?");
      var5.append(" (").append(var4 ? FormatUtil.formatBytes(this.getReadBytes()) : "?").append("), ");
      var5.append("writes: ").append(var4 ? this.getWrites() : "?");
      var5.append(" (").append(var4 ? FormatUtil.formatBytes(this.getWriteBytes()) : "?").append("), ");
      var5.append("xfer: ").append(var4 ? this.getTransferTime() : "?");
      return var5.toString();
   }
}
