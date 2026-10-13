package oshi.hardware;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface HardwareAbstractionLayer {
   ComputerSystem getComputerSystem();

   CentralProcessor getProcessor();

   GlobalMemory getMemory();

   PowerSource[] getPowerSources();

   HWDiskStore[] getDiskStores();

   NetworkIF[] getNetworkIFs();

   Display[] getDisplays();

   Sensors getSensors();

   UsbDevice[] getUsbDevices(boolean var1);

   SoundCard[] getSoundCards();

   GraphicsCard[] getGraphicsCards();
}
