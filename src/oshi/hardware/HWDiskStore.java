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

   public HWDiskStore() {
   }

   public HWDiskStore(HWDiskStore diskStore) {
      HWPartition[] hwPartitions = diskStore.getPartitions();
      HWPartition[] hwPartitions2 = new HWPartition[hwPartitions.length];

      for (int i = 0; i < hwPartitions.length; i++) {
         hwPartitions2[i] = new HWPartition(
            hwPartitions[i].getIdentification(),
            hwPartitions[i].getName(),
            hwPartitions[i].getType(),
            hwPartitions[i].getUuid(),
            hwPartitions[i].getSize(),
            hwPartitions[i].getMajor(),
            hwPartitions[i].getMinor(),
            hwPartitions[i].getMountPoint()
         );
      }

      this.name = diskStore.name;
      this.model = diskStore.model;
      this.serial = diskStore.serial;
      this.size = diskStore.size;
      this.reads = diskStore.reads;
      this.readBytes = diskStore.readBytes;
      this.writes = diskStore.writes;
      this.writeBytes = diskStore.writeBytes;
      this.currentQueueLength = diskStore.currentQueueLength;
      this.transferTime = diskStore.transferTime;
      this.partitions = hwPartitions2;
      this.timeStamp = diskStore.timeStamp;
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

   public long getCurrentQueueLength() {
      return this.currentQueueLength;
   }

   public long getTransferTime() {
      return this.transferTime;
   }

   public HWPartition[] getPartitions() {
      return this.partitions;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public void setName(String name) {
      this.name = name == null ? "" : name;
   }

   public void setModel(String model) {
      this.model = model == null ? "" : model;
   }

   public void setSerial(String serial) {
      this.serial = serial == null ? "" : serial;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public void setReads(long reads) {
      this.reads = reads;
   }

   public void setReadBytes(long readBytes) {
      this.readBytes = readBytes;
   }

   public void setWrites(long writes) {
      this.writes = writes;
   }

   public void setWriteBytes(long writeBytes) {
      this.writeBytes = writeBytes;
   }

   public void setCurrentQueueLength(long currentQueueLength) {
      this.currentQueueLength = currentQueueLength;
   }

   public void setTransferTime(long transferTime) {
      this.transferTime = transferTime;
   }

   public void setPartitions(HWPartition[] partitions) {
      this.partitions = partitions;
   }

   public void setTimeStamp(long timeStamp) {
      this.timeStamp = timeStamp;
   }

   public int compareTo(HWDiskStore store) {
      return this.getName().compareTo(store.getName());
   }

   @Override
   public int hashCode() {
      int i = 1;
      i = 31 * i + (this.model == null ? 0 : this.model.hashCode());
      i = 31 * i + (this.name == null ? 0 : this.name.hashCode());
      i = 31 * i + (this.serial == null ? 0 : this.serial.hashCode());
      return 31 * i + (int)(this.size ^ this.size >>> 32);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (!obj.getClass().equals(this.getClass())) {
         return false;
      }

      HWDiskStore hwDiskStore = (HWDiskStore)obj;
      if (this.model == null) {
         if (hwDiskStore.model != null) {
            return false;
         }
      } else if (!this.model.equals(hwDiskStore.model)) {
         return false;
      }

      if (this.name == null) {
         if (hwDiskStore.name != null) {
            return false;
         }
      } else if (!this.name.equals(hwDiskStore.name)) {
         return false;
      }

      if (this.serial == null) {
         if (hwDiskStore.serial != null) {
            return false;
         }
      } else if (!this.serial.equals(hwDiskStore.serial)) {
         return false;
      }

      return this.size == hwDiskStore.size;
   }

   @Override
   public String toString() {
      boolean bl = this.getReads() > 0L || this.getWrites() > 0L;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(this.getName()).append(": ");
      stringBuilder.append("(model: ").append(this.getModel());
      stringBuilder.append(" - S/N: ").append(this.getSerial()).append(") ");
      stringBuilder.append("size: ").append(this.getSize() > 0L ? FormatUtil.formatBytesDecimal(this.getSize()) : "?").append(", ");
      stringBuilder.append("reads: ").append(bl ? this.getReads() : "?");
      stringBuilder.append(" (").append(bl ? FormatUtil.formatBytes(this.getReadBytes()) : "?").append("), ");
      stringBuilder.append("writes: ").append(bl ? this.getWrites() : "?");
      stringBuilder.append(" (").append(bl ? FormatUtil.formatBytes(this.getWriteBytes()) : "?").append("), ");
      stringBuilder.append("xfer: ").append(bl ? this.getTransferTime() : "?");
      return stringBuilder.toString();
   }
}
