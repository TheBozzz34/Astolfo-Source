package oshi.software.os;

import oshi.SystemInfo;
import oshi.annotation.concurrent.NotThreadSafe;
import oshi.software.os.linux.LinuxFileSystem;
import oshi.software.os.mac.MacFileSystem;
import oshi.software.os.unix.freebsd.FreeBsdFileSystem;
import oshi.software.os.unix.solaris.SolarisFileSystem;
import oshi.software.os.windows.WindowsFileSystem;

@NotThreadSafe
public class OSFileStore {
   private String name;
   private String volume;
   private String label;
   private String logicalVolume = "";
   private String mount;
   private String description;
   private String fsType;
   private String options;
   private String uuid;
   private long freeSpace;
   private long usableSpace;
   private long totalSpace;
   private long freeInodes;
   private long totalInodes;
   private static final long a = 128296312367546L;

   public OSFileStore() {
   }

   public OSFileStore(OSFileStore fileStore) {
      this.setName(fileStore.getName());
      this.setVolume(fileStore.getVolume());
      this.setLabel(fileStore.getLabel());
      this.setLogicalVolume(fileStore.getLogicalVolume());
      this.setMount(fileStore.getMount());
      this.setDescription(fileStore.getDescription());
      this.setType(fileStore.getType());
      this.setType(fileStore.getOptions());
      this.setUUID(fileStore.getUUID());
      this.setFreeSpace(fileStore.getFreeSpace());
      this.setUsableSpace(fileStore.getUsableSpace());
      this.setTotalSpace(fileStore.getTotalSpace());
      this.setFreeInodes(fileStore.getFreeInodes());
      this.setTotalInodes(fileStore.getTotalInodes());
   }

   public String getName() {
      return this.name;
   }

   public void setName(String value) {
      this.name = value;
   }

   public String getVolume() {
      return this.volume;
   }

   public void setVolume(String value) {
      this.volume = value;
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String value) {
      this.label = value;
   }

   public String getLogicalVolume() {
      return this.logicalVolume;
   }

   public void setLogicalVolume(String value) {
      this.logicalVolume = value;
   }

   public String getMount() {
      return this.mount;
   }

   public void setMount(String value) {
      this.mount = value;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String value) {
      this.description = value;
   }

   public String getType() {
      return this.fsType;
   }

   public void setType(String value) {
      this.fsType = value;
   }

   public String getOptions() {
      return this.options;
   }

   public void setOptions(String value) {
      this.options = value;
   }

   public String getUUID() {
      return this.uuid;
   }

   public void setUUID(String value) {
      this.uuid = value;
   }

   public long getFreeSpace() {
      return this.freeSpace;
   }

   public void setFreeSpace(long value) {
      this.freeSpace = value;
   }

   public long getUsableSpace() {
      return this.usableSpace;
   }

   public void setUsableSpace(long value) {
      this.usableSpace = value;
   }

   public long getTotalSpace() {
      return this.totalSpace;
   }

   public void setTotalSpace(long value) {
      this.totalSpace = value;
   }

   public long getFreeInodes() {
      return this.freeInodes;
   }

   public void setFreeInodes(long value) {
      this.freeInodes = value;
   }

   public long getTotalInodes() {
      return this.totalInodes;
   }

   public void setTotalInodes(long value) {
      this.totalInodes = value;
   }

   public boolean updateAtrributes() {
      switch (SystemInfo.getCurrentPlatformEnum()) {
         case WINDOWS:
            return WindowsFileSystem.updateFileStoreStats(this);
         case LINUX:
            return LinuxFileSystem.updateFileStoreStats(this);
         case MACOSX:
            return MacFileSystem.updateFileStoreStats(this);
         case SOLARIS:
            return SolarisFileSystem.updateFileStoreStats(this);
         case FREEBSD:
            return FreeBsdFileSystem.updateFileStoreStats(this);
         default:
            return false;
      }
   }

   @Override
   public String toString() {
      return "OSFileStore [name="
         + this.name
         + ", volume="
         + this.volume
         + ", label="
         + this.label
         + ", logicalVolume="
         + this.logicalVolume
         + ", mount="
         + this.mount
         + ", description="
         + this.description
         + ", fsType="
         + this.fsType
         + ", options=\""
         + this.options
         + "\", uuid="
         + this.uuid
         + ", freeSpace="
         + this.freeSpace
         + ", usableSpace="
         + this.usableSpace
         + ", totalSpace="
         + this.totalSpace
         + ", freeInodes="
         + this.freeInodes
         + ", totalInodes="
         + this.totalInodes
         + "]";
   }
}
