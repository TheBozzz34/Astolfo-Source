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
   private final StringBuilder l = new StringBuilder();
   private static final long a = 72277192640955L;
   private static final long e = 5063730145215381756L;

   public HwidBuilder(HwidOptions hwidOptions) {
      SystemInfo systemInfo = new SystemInfo();
      OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
      HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
      ComputerSystem computerSystem = hardwareAbstractionLayer.getComputerSystem();
      CentralProcessor centralProcessor = hardwareAbstractionLayer.getProcessor();
      if (HwidOptions.E(hwidOptions)) {
         this.l.append(System.getProperty("user.name")).append("$");
      }

      if (HwidOptions.o(hwidOptions)) {
         String string = String.format("%08x", computerSystem.getBaseboard().A().hashCode());
         this.l.append(string).append("$");
      }

      if (HwidOptions.k(hwidOptions)) {
         String string2;
         try {
            string2 = centralProcessor.getProcessorIdentifier().getIdentifier();
         } catch (Exception exception) {
            string2 = "unk";
         }

         this.l.append(string2.hashCode()).append("$");
      }

      if (HwidOptions.s(hwidOptions)) {
         GraphicsCard[] graphicsCards = hardwareAbstractionLayer.getGraphicsCards();
         if (graphicsCards.length - 1 >= 0) {
            this.l.append(graphicsCards[graphicsCards.length - 1].E().hashCode()).append("$");
         } else {
            this.l.append("unk").append("$");
         }
      }

      if (HwidOptions.z(hwidOptions)) {
         this.l.append((int)(hardwareAbstractionLayer.getMemory().f() / 1.0E9)).append("$");
      }

      if (HwidOptions.D(hwidOptions)) {
         if (Platform.isWindows()) {
            Pair pair = h(System.getProperty("user.home"), Arrays.asList(hardwareAbstractionLayer.getDiskStores()));
            int i = (Integer)pair.getA();
            if (i != -1) {
               this.l.append(hardwareAbstractionLayer.getDiskStores()[i].getSerial().hashCode());
            }
         } else {
            this.l.append(computerSystem.k().hashCode());
         }

         this.l.append("$");
      }

      if (HwidOptions.X(hwidOptions)) {
         Display[] displays = hardwareAbstractionLayer.getDisplays();

         for (int j = 0; j < displays.length; j++) {
            byte[] bytes = displays[j].getEdid();
            byte[][] bytess = EdidUtil.getDescriptors(bytes);
            String string3 = "Display " + j;

            for (byte[] bytes2 : bytess) {
               if (EdidUtil.getDescriptorType(bytes2) == (int)e) {
                  string3 = EdidUtil.getDescriptorText(bytes2);
               }
            }

            this.l.append(string3).append("-");
         }

         this.l.append("$");
      }
   }

   private static Pair<Integer, Integer> h(String string, List<HWDiskStore> list) {
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

   public String J() {
      return this.l.toString();
   }
}
