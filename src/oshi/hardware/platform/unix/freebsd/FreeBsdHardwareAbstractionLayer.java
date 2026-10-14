package oshi.hardware.platform.unix.freebsd;

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
public final class FreeBsdHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
   @Override
   public ComputerSystem createComputerSystem() {
      return new FreeBsdComputerSystem();
   }

   @Override
   public GlobalMemory createMemory() {
      return new FreeBsdGlobalMemory();
   }

   @Override
   public CentralProcessor createProcessor() {
      return new FreeBsdCentralProcessor();
   }

   @Override
   public Sensors createSensors() {
      return new FreeBsdSensors();
   }

   @Override
   public PowerSource[] getPowerSources() {
      return FreeBsdPowerSource.getPowerSources();
   }

   @Override
   public HWDiskStore[] getDiskStores() {
      return FreeBsdDisks.getDisks();
   }

   @Override
   public Display[] getDisplays() {
      return FreeBsdDisplay.getDisplays();
   }

   @Override
   public NetworkIF[] getNetworkIFs() {
      return new FreeBsdNetworks().getNetworks();
   }

   @Override
   public UsbDevice[] getUsbDevices(boolean tree) {
      return FreeBsdUsbDevice.getUsbDevices(tree);
   }

   @Override
   public SoundCard[] getSoundCards() {
      return FreeBsdSoundCard.getSoundCards().toArray(new SoundCard[0]);
   }

   @Override
   public GraphicsCard[] getGraphicsCards() {
      return FreeBsdGraphicsCard.getGraphicsCards().toArray(new GraphicsCard[0]);
   }
}
