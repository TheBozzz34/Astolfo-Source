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
   private static final long a = 50212744401528L;

   public HWPartition(String var1, String var2, String var3, String var4, long var5, int var7, int var8, String var9) {
      this.setIdentification(var1);
      this.setName(var2);
      this.setType(var3);
      this.setUuid(var4);
      this.setSize(var5);
      this.setMajor(var7);
      this.setMinor(var8);
      this.setMountPoint(var9);
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

   public void setIdentification(String var1) {
      this.identification = var1 == null ? "" : var1;
   }

   public void setName(String var1) {
      this.name = var1 == null ? "" : var1;
   }

   public void setType(String var1) {
      this.type = var1 == null ? "" : var1;
   }

   public void setUuid(String var1) {
      this.uuid = var1 == null ? "" : var1;
   }

   public void setSize(long var1) {
      this.size = var1;
   }

   public void setMajor(int var1) {
      this.major = var1;
   }

   public void setMinor(int var1) {
      this.minor = var1;
   }

   public void setMountPoint(String var1) {
      this.mountPoint = var1 == null ? "" : var1;
   }

   public int compareTo(HWPartition var1) {
      return this.getIdentification().compareTo(var1.getIdentification());
   }

   @Override
   public int hashCode() {
      int var5 = 1;
      var5 = 31 * var5 + (this.identification == null ? 0 : this.identification.hashCode());
      var5 = 31 * var5 + this.major;
      var5 = 31 * var5 + this.minor;
      var5 = 31 * var5 + (this.mountPoint == null ? 0 : this.mountPoint.hashCode());
      var5 = 31 * var5 + (this.name == null ? 0 : this.name.hashCode());
      var5 = 31 * var5 + (int)(this.size ^ this.size >>> 32);
      var5 = 31 * var5 + (this.type == null ? 0 : this.type.hashCode());
      return 31 * var5 + (this.uuid == null ? 0 : this.uuid.hashCode());
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

      HWPartition var5 = (HWPartition)var1;
      if (this.identification == null) {
         if (var5.identification != null) {
            return false;
         }
      } else if (!this.identification.equals(var5.identification)) {
         return false;
      }

      if (this.major != var5.major) {
         return false;
      }

      if (this.minor != var5.minor) {
         return false;
      }

      if (this.mountPoint == null) {
         if (var5.mountPoint != null) {
            return false;
         }
      } else if (!this.mountPoint.equals(var5.mountPoint)) {
         return false;
      }

      if (this.name == null) {
         if (var5.name != null) {
            return false;
         }
      } else if (!this.name.equals(var5.name)) {
         return false;
      }

      if (this.size != var5.size) {
         return false;
      }

      if (this.type == null) {
         if (var5.type != null) {
            return false;
         }
      } else if (!this.type.equals(var5.type)) {
         return false;
      }

      if (this.uuid == null) {
         if (var5.uuid != null) {
            return false;
         }
      } else if (!this.uuid.equals(var5.uuid)) {
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
