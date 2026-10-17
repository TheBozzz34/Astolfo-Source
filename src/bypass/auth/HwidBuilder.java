package bypass.auth;

import com.sun.jna.Platform;
import java.util.Arrays;
import java.util.List;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.util.EdidUtil;
import oshi.util.tuples.Pair;

public class HwidBuilder {
   private final StringBuilder hwid = new StringBuilder();

   public HwidBuilder(HwidOptions hwidOptions) {
      SystemInfo systemInfo = new SystemInfo();
      OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
      HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
      ComputerSystem computerSystem = hardwareAbstractionLayer.getComputerSystem();
      CentralProcessor centralProcessor = hardwareAbstractionLayer.getProcessor();
      if (HwidOptions.isUsernameEnabled(hwidOptions)) {
         this.hwid.append(System.getProperty("user.name")).append("$");
      }

      if (HwidOptions.isBaseboardEnabled(hwidOptions)) {
         String string = String.format("%08x", computerSystem.getBaseboard().getSerialNumber().hashCode());
         this.hwid.append(string).append("$");
      }

      if (HwidOptions.isCpuEnabled(hwidOptions)) {
         String string2;
         try {
            string2 = centralProcessor.getProcessorIdentifier().getIdentifier();
         } catch (Exception exception) {
            string2 = "unk";
         }

         this.hwid.append(string2.hashCode()).append("$");
      }

      if (HwidOptions.isGpuEnabled(hwidOptions)) {
         GraphicsCard[] graphicsCards = hardwareAbstractionLayer.getGraphicsCards();
         if (graphicsCards.length - 1 >= 0) {
            this.hwid.append(graphicsCards[graphicsCards.length - 1].E().hashCode()).append("$");
         } else {
            this.hwid.append("unk").append("$");
         }
      }

      if (HwidOptions.isMemoryEnabled(hwidOptions)) {
         this.hwid.append((int)(hardwareAbstractionLayer.getMemory().getTotal() / 1.0E9)).append("$");
      }

      if (HwidOptions.isDiskEnabled(hwidOptions)) {
         if (Platform.isWindows()) {
            Pair pair = findPartitionIndex(System.getProperty("user.home"), Arrays.asList(hardwareAbstractionLayer.getDiskStores()));
            int i = (Integer)pair.getA();
            if (i != -1) {
               this.hwid.append(hardwareAbstractionLayer.getDiskStores()[i].getSerial().hashCode());
            }
         } else {
            this.hwid.append(computerSystem.getSerialNumber().hashCode());
         }

         this.hwid.append("$");
      }

      if (HwidOptions.isDisplaysEnabled(hwidOptions)) {
         Display[] displays = hardwareAbstractionLayer.getDisplays();

         for (int j = 0; j < displays.length; j++) {
            byte[] bytes = displays[j].getEdid();
            byte[][] bytess = EdidUtil.getDescriptors(bytes);
            String string3 = "Display " + j;

            for (byte[] bytes2 : bytess) {
               if (EdidUtil.getDescriptorType(bytes2) == 252) {
                  string3 = EdidUtil.getDescriptorText(bytes2);
               }
            }

            this.hwid.append(string3).append("-");
         }

         this.hwid.append("$");
      }
   }

   private static Pair<Integer, Integer> findPartitionIndex(String string, List<HWDiskStore> list) {
      for (int i = 0; i < list.size(); i++) {
         HWDiskStore hwDiskStore = (HWDiskStore)list.get(i);
         List list2 = Arrays.asList(hwDiskStore.getPartitions());

         for (int j = 0; j < list2.size(); j++) {
            String string2 = ((HWPartition)list2.get(j)).getMountPoint();
            if (!string2.isEmpty() && string.substring(0, string2.length()).equalsIgnoreCase(string2)) {
               return new Pair<>(i, j);
            }
         }
      }

      return new Pair<>(-1, -1);
   }

   public String getRawHwid() {
      return this.hwid.toString();
   }
}
