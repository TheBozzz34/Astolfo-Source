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

   public AbstractUsbDevice(
      String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, UsbDevice[] connectedDevices
   ) {
      this.name = name;
      this.vendor = vendor;
      this.vendorId = vendorId;
      this.productId = productId;
      this.serialNumber = serialNumber;
      this.uniqueDeviceId = uniqueDeviceId;
      this.connectedDevices = Arrays.copyOf(connectedDevices, connectedDevices.length);
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

   public int compareTo(UsbDevice usb) {
      return this.getName().compareTo(usb.getName());
   }

   @Override
   public String toString() {
      return indentUsb(this, 1);
   }

   private static String indentUsb(UsbDevice usbDevice, int indent) {
      String string = indent > 2 ? String.format("%%%ds|-- ", indent - 4) : String.format("%%%ds", indent);
      StringBuilder stringBuilder = new StringBuilder(String.format(string, ""));
      stringBuilder.append(usbDevice.getName());
      if (usbDevice.getVendor().length() > 0) {
         stringBuilder.append(" (").append(usbDevice.getVendor()).append(')');
      }

      if (usbDevice.getSerialNumber().length() > 0) {
         stringBuilder.append(" [s/n: ").append(usbDevice.getSerialNumber()).append(']');
      }

      for (UsbDevice usbDevice2 : usbDevice.getConnectedDevices()) {
         stringBuilder.append('\n').append(indentUsb(usbDevice2, indent + 4));
      }

      return stringBuilder.toString();
   }
}
