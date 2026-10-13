package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.GraphicsCard;

@Immutable
public abstract class AbstractGraphicsCard implements GraphicsCard {
   private final String name;
   private final String deviceId;
   private final String vendor;
   private final String versionInfo;
   private long vram;
   private static final long a = 82002803959390L;

   public AbstractGraphicsCard(String var1, String var2, String var3, String var4, long var5) {
      this.name = var1;
      this.deviceId = var2;
      this.vendor = var3;
      this.versionInfo = var4;
      this.vram = var5;
   }

   @Override
   public String E() {
      return this.name;
   }

   @Override
   public String S() {
      return this.deviceId;
   }

   @Override
   public String L() {
      return this.vendor;
   }

   @Override
   public String e() {
      return this.versionInfo;
   }

   @Override
   public long getVRam() {
      return this.vram;
   }

   @Override
   public String toString() {
      StringBuilder var3 = new StringBuilder();
      var3.append("GraphicsCard@");
      var3.append(Integer.toHexString(this.hashCode()));
      var3.append(" [name=");
      var3.append(this.name);
      var3.append(", deviceId=");
      var3.append(this.deviceId);
      var3.append(", vendor=");
      var3.append(this.vendor);
      var3.append(", vRam=");
      var3.append(this.vram);
      var3.append(", versionInfo=[");
      var3.append(this.versionInfo);
      var3.append("]]");
      return var3.toString();
   }
}
