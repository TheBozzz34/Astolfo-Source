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

   public OSFileStore(OSFileStore var1) {
      this.setName(var1.getName());
      this.setVolume(var1.getVolume());
      this.setLabel(var1.getLabel());
      this.setLogicalVolume(var1.getLogicalVolume());
      this.setMount(var1.getMount());
      this.setDescription(var1.getDescription());
      this.setType(var1.getType());
      this.setType(var1.getOptions());
      this.setUUID(var1.getUUID());
      this.setFreeSpace(var1.getFreeSpace());
      this.setUsableSpace(var1.getUsableSpace());
      this.setTotalSpace(var1.getTotalSpace());
      this.setFreeInodes(var1.getFreeInodes());
      this.setTotalInodes(var1.getTotalInodes());
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public String getVolume() {
      return this.volume;
   }

   public void setVolume(String var1) {
      this.volume = var1;
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String var1) {
      this.label = var1;
   }

   public String getLogicalVolume() {
      return this.logicalVolume;
   }

   public void setLogicalVolume(String var1) {
      this.logicalVolume = var1;
   }

   public String getMount() {
      return this.mount;
   }

   public void setMount(String var1) {
      this.mount = var1;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public String getType() {
      return this.fsType;
   }

   public void setType(String var1) {
      this.fsType = var1;
   }

   public String getOptions() {
      return this.options;
   }

   public void setOptions(String var1) {
      this.options = var1;
   }

   public String getUUID() {
      return this.uuid;
   }

   public void setUUID(String var1) {
      this.uuid = var1;
   }

   public long getFreeSpace() {
      return this.freeSpace;
   }

   public void setFreeSpace(long var1) {
      this.freeSpace = var1;
   }

   public long getUsableSpace() {
      return this.usableSpace;
   }

   public void setUsableSpace(long var1) {
      this.usableSpace = var1;
   }

   public long getTotalSpace() {
      return this.totalSpace;
   }

   public void setTotalSpace(long var1) {
      this.totalSpace = var1;
   }

   public long getFreeInodes() {
      return this.freeInodes;
   }

   public void setFreeInodes(long var1) {
      this.freeInodes = var1;
   }

   public long getTotalInodes() {
      return this.totalInodes;
   }

   public void setTotalInodes(long var1) {
      this.totalInodes = var1;
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
