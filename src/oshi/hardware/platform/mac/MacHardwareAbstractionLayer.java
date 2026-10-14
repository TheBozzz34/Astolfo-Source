package oshi.hardware.platform.mac;

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
public final class MacHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
   @Override
   public ComputerSystem createComputerSystem() {
      return new MacComputerSystem();
   }

   @Override
   public GlobalMemory createMemory() {
      return new MacGlobalMemory();
   }

   @Override
   public CentralProcessor createProcessor() {
      return new MacCentralProcessor();
   }

   @Override
   public Sensors createSensors() {
      return new MacSensors();
   }

   @Override
   public PowerSource[] getPowerSources() {
      return MacPowerSource.Y();
   }

   @Override
   public HWDiskStore[] getDiskStores() {
      return MacDisks.getDisks();
   }

   @Override
   public Display[] getDisplays() {
      return MacDisplay.getDisplays();
   }

   @Override
   public NetworkIF[] getNetworkIFs() {
      return new MacNetworks().getNetworks();
   }

   @Override
   public UsbDevice[] getUsbDevices(boolean tree) {
      return MacUsbDevice.getUsbDevices(tree);
   }

   @Override
   public SoundCard[] getSoundCards() {
      return MacSoundCard.getSoundCards().toArray(new SoundCard[0]);
   }

   @Override
   public GraphicsCard[] getGraphicsCards() {
      return MacGraphicsCard.getGraphicsCards().toArray(new GraphicsCard[0]);
   }
}
