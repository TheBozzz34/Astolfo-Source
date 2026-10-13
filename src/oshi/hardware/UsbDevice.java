package oshi.hardware;

import oshi.annotation.concurrent.Immutable;

@Immutable
public interface UsbDevice extends Comparable<UsbDevice> {
   String getName();

   String getVendor();

   String getVendorId();

   String getProductId();

   String getSerialNumber();

   String getUniqueDeviceId();

   UsbDevice[] getConnectedDevices();
}
