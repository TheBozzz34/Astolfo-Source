package oshi.hardware.platform.linux;

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
public final class LinuxHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
   @Override
   public ComputerSystem createComputerSystem() {
      return new LinuxComputerSystem();
   }

   @Override
   public GlobalMemory createMemory() {
      return new LinuxGlobalMemory();
   }

   @Override
   public CentralProcessor createProcessor() {
      return new LinuxCentralProcessor();
   }

   @Override
   public Sensors createSensors() {
      return new LinuxSensors();
   }

   @Override
   public PowerSource[] getPowerSources() {
      return LinuxPowerSource.x();
   }

   @Override
   public HWDiskStore[] getDiskStores() {
      return LinuxDisks.getDisks();
   }

   @Override
   public Display[] getDisplays() {
      return LinuxDisplay.getDisplays();
   }

   @Override
   public NetworkIF[] getNetworkIFs() {
      return new LinuxNetworks().getNetworks();
   }

   @Override
   public UsbDevice[] getUsbDevices(boolean tree) {
      return LinuxUsbDevice.getUsbDevices(tree);
   }

   @Override
   public SoundCard[] getSoundCards() {
      return LinuxSoundCard.getSoundCards().toArray(new SoundCard[0]);
   }

   @Override
   public GraphicsCard[] getGraphicsCards() {
      return LinuxGraphicsCard.getGraphicsCards().toArray(new GraphicsCard[0]);
   }
}
