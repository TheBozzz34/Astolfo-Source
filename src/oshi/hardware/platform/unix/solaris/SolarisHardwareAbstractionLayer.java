package oshi.hardware.platform.unix.solaris;

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
public final class SolarisHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
   @Override
   public ComputerSystem createComputerSystem() {
      return new SolarisComputerSystem();
   }

   @Override
   public GlobalMemory createMemory() {
      return new SolarisGlobalMemory();
   }

   @Override
   public CentralProcessor createProcessor() {
      return new SolarisCentralProcessor();
   }

   @Override
   public Sensors createSensors() {
      return new SolarisSensors();
   }

   @Override
   public PowerSource[] getPowerSources() {
      return SolarisPowerSource.getPowerSources();
   }

   @Override
   public HWDiskStore[] getDiskStores() {
      return SolarisDisks.getDisks();
   }

   @Override
   public Display[] getDisplays() {
      return SolarisDisplay.getDisplays();
   }

   @Override
   public NetworkIF[] getNetworkIFs() {
      return new SolarisNetworks().getNetworks();
   }

   @Override
   public UsbDevice[] getUsbDevices(boolean tree) {
      return SolarisUsbDevice.getUsbDevices(tree);
   }

   @Override
   public SoundCard[] getSoundCards() {
      return SolarisSoundCard.getSoundCards().toArray(new SoundCard[0]);
   }

   @Override
   public GraphicsCard[] getGraphicsCards() {
      return SolarisGraphicsCard.getGraphicsCards().toArray(new GraphicsCard[0]);
   }
}
