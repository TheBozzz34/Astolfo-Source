package oshi.hardware.platform.windows;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractHardwareAbstractionLayer;

@ThreadSafe
public final class WindowsHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
   @Override
   public ComputerSystem createComputerSystem() {
      return new WindowsComputerSystem();
   }

   @Override
   public GlobalMemory createMemory() {
      return new WindowsGlobalMemory();
   }

   @Override
   public CentralProcessor createProcessor() {
      return new WindowsCentralProcessor();
   }

   @Override
   public Sensors createSensors() {
      return new WindowsSensors();
   }

   @Override
   public PowerSource[] getPowerSources() {
      return WindowsPowerSource.getPowerSources();
   }

   @Override
   public HWDiskStore[] getDiskStores() {
      return WindowsDisks.getDisks();
   }

   @Override
   public Display[] getDisplays() {
      return WindowsDisplay.getDisplays();
   }

   @Override
   public NetworkIF[] getNetworkIFs() {
      return new WindowsNetworks().getNetworks();
   }

   @Override
   public UsbDevice[] getUsbDevices(boolean tree) {
      return WindowsUsbDevice.getUsbDevices(tree);
   }

   @Override
   public SoundCard[] getSoundCards() {
      return WindowsSoundCard.getSoundCards().toArray(new SoundCard[0]);
   }

   @Override
   public GraphicsCard[] getGraphicsCards() {
      return WindowsGraphicsCard.getGraphicsCards().toArray(new GraphicsCard[0]);
   }
}
