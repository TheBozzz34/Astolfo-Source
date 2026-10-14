package oshi.hardware;

import oshi.annotation.concurrent.NotThreadSafe;
import oshi.util.FormatUtil;

@NotThreadSafe
public class HWPartition implements Comparable<HWPartition> {
   private String identification;
   private String name;
   private String type;
   private String uuid;
   private long size;
   private int major;
   private int minor;
   private String mountPoint;

   public HWPartition(String identification, String name, String typeIn, String uuid, long size, int major, int minor, String mountPoint) {
      this.setIdentification(identification);
      this.setName(name);
      this.setType(typeIn);
      this.setUuid(uuid);
      this.setSize(size);
      this.setMajor(major);
      this.setMinor(minor);
      this.setMountPoint(mountPoint);
   }

   public HWPartition() {
      this("", "", "", "", 0L, 0, 0, "");
   }

   public String getIdentification() {
      return this.identification;
   }

   public String getName() {
      return this.name;
   }

   public String getType() {
      return this.type;
   }

   public String getUuid() {
      return this.uuid;
   }

   public long getSize() {
      return this.size;
   }

   public int getMajor() {
      return this.major;
   }

   public int getMinor() {
      return this.minor;
   }

   public String getMountPoint() {
      return this.mountPoint;
   }

   public void setIdentification(String identification) {
      this.identification = identification == null ? "" : identification;
   }

   public void setName(String name) {
      this.name = name == null ? "" : name;
   }

   public void setType(String typeIn) {
      this.type = typeIn == null ? "" : typeIn;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid == null ? "" : uuid;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public void setMajor(int major) {
      this.major = major;
   }

   public void setMinor(int minor) {
      this.minor = minor;
   }

   public void setMountPoint(String mountPoint) {
      this.mountPoint = mountPoint == null ? "" : mountPoint;
   }

   public int compareTo(HWPartition part) {
      return this.getIdentification().compareTo(part.getIdentification());
   }

   @Override
   public int hashCode() {
      int i = 1;
      i = 31 * i + (this.identification == null ? 0 : this.identification.hashCode());
      i = 31 * i + this.major;
      i = 31 * i + this.minor;
      i = 31 * i + (this.mountPoint == null ? 0 : this.mountPoint.hashCode());
      i = 31 * i + (this.name == null ? 0 : this.name.hashCode());
      i = 31 * i + (int)(this.size ^ this.size >>> 32);
      i = 31 * i + (this.type == null ? 0 : this.type.hashCode());
      return 31 * i + (this.uuid == null ? 0 : this.uuid.hashCode());
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

      HWPartition hwPartition = (HWPartition)obj;
      if (this.identification == null) {
         if (hwPartition.identification != null) {
            return false;
         }
      } else if (!this.identification.equals(hwPartition.identification)) {
         return false;
      }

      if (this.major != hwPartition.major) {
         return false;
      }

      if (this.minor != hwPartition.minor) {
         return false;
      }

      if (this.mountPoint == null) {
         if (hwPartition.mountPoint != null) {
            return false;
         }
      } else if (!this.mountPoint.equals(hwPartition.mountPoint)) {
         return false;
      }

      if (this.name == null) {
         if (hwPartition.name != null) {
            return false;
         }
      } else if (!this.name.equals(hwPartition.name)) {
         return false;
      }

      if (this.size != hwPartition.size) {
         return false;
      }

      if (this.type == null) {
         if (hwPartition.type != null) {
            return false;
         }
      } else if (!this.type.equals(hwPartition.type)) {
         return false;
      }

      if (this.uuid == null) {
         if (hwPartition.uuid != null) {
            return false;
         }
      } else if (!this.uuid.equals(hwPartition.uuid)) {
         return false;
      }

      return true;
   }

   @Override
   public String toString() {
      return this.getIdentification()
         + ": "
         + this.getName()
         + " ("
         + this.getType()
         + ") Maj:Min="
         + this.getMajor()
         + ":"
         + this.getMinor()
         + ", size: "
         + FormatUtil.formatBytesDecimal(this.getSize())
         + (this.getMountPoint().isEmpty() ? "" : " @ " + this.getMountPoint());
   }
}
