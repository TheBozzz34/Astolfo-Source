package oshi.hardware.common;

import java.util.Arrays;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;

@Immutable
public abstract class AbstractUsbDevice implements UsbDevice {
   public String name;
   public String vendor;
   public String vendorId;
   public String productId;
   public String serialNumber;
   public String uniqueDeviceId;
   public UsbDevice[] connectedDevices;
   private static final long b = 127111578330191L;

   public AbstractUsbDevice(String var1, String var2, String var3, String var4, String var5, String var6, UsbDevice[] var7) {
      this.name = var1;
      this.vendor = var2;
      this.vendorId = var3;
      this.productId = var4;
      this.serialNumber = var5;
      this.uniqueDeviceId = var6;
      this.connectedDevices = Arrays.copyOf(var7, var7.length);
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String getVendor() {
      return this.vendor;
   }

   @Override
   public String getVendorId() {
      return this.vendorId;
   }

   @Override
   public String getProductId() {
      return this.productId;
   }

   @Override
   public String getSerialNumber() {
      return this.serialNumber;
   }

   @Override
   public String getUniqueDeviceId() {
      return this.uniqueDeviceId;
   }

   @Override
   public UsbDevice[] getConnectedDevices() {
      return Arrays.copyOf(this.connectedDevices, this.connectedDevices.length);
   }

   public int compareTo(UsbDevice var1) {
      return this.getName().compareTo(var1.getName());
   }

   @Override
   public String toString() {
      return indentUsb(this, 1);
   }

   private static String indentUsb(UsbDevice var0, int var1) {
      String var5 = var1 > 2 ? String.format("%%%ds|-- ", var1 - 4) : String.format("%%%ds", var1);
      StringBuilder var6 = new StringBuilder(String.format(var5, ""));
      var6.append(var0.getName());
      if (var0.getVendor().length() > 0) {
         var6.append(" (").append(var0.getVendor()).append(')');
      }

      if (var0.getSerialNumber().length() > 0) {
         var6.append(" [s/n: ").append(var0.getSerialNumber()).append(']');
      }

      for (UsbDevice var10 : var0.getConnectedDevices()) {
         var6.append('\n').append(indentUsb(var10, var1 + 4));
      }

      return var6.toString();
   }
}
