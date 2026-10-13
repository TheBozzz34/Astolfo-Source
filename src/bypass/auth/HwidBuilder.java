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

   public HwidBuilder(HwidOptions var1) {
      SystemInfo var4 = new SystemInfo();
      OperatingSystem var5 = var4.getOperatingSystem();
      HardwareAbstractionLayer var6 = var4.getHardware();
      ComputerSystem var7 = var6.getComputerSystem();
      CentralProcessor var8 = var6.getProcessor();
      if (HwidOptions.E(var1)) {
         this.l.append(System.getProperty("user.name")).append("$");
      }

      if (HwidOptions.o(var1)) {
         String var9 = String.format("%08x", var7.getBaseboard().A().hashCode());
         this.l.append(var9).append("$");
      }

      if (HwidOptions.k(var1)) {
         String var19;
         try {
            var19 = var8.getProcessorIdentifier().getIdentifier();
         } catch (Exception var18) {
            var19 = "unk";
         }

         this.l.append(var19.hashCode()).append("$");
      }

      if (HwidOptions.s(var1)) {
         GraphicsCard[] var20 = var6.getGraphicsCards();
         if (var20.length - 1 >= 0) {
            this.l.append(var20[var20.length - 1].E().hashCode()).append("$");
         } else {
            this.l.append("unk").append("$");
         }
      }

      if (HwidOptions.z(var1)) {
         this.l.append((int)(var6.getMemory().f() / 1.0E9)).append("$");
      }

      if (HwidOptions.D(var1)) {
         if (Platform.isWindows()) {
            Pair var21 = h(System.getProperty("user.home"), Arrays.asList(var6.getDiskStores()));
            int var10 = (Integer)var21.getA();
            if (var10 != -1) {
               this.l.append(var6.getDiskStores()[var10].getSerial().hashCode());
            }
         } else {
            this.l.append(var7.k().hashCode());
         }

         this.l.append("$");
      }

      if (HwidOptions.X(var1)) {
         Display[] var22 = var6.getDisplays();

         for (int var23 = 0; var23 < var22.length; var23++) {
            byte[] var11 = var22[var23].getEdid();
            byte[][] var12 = EdidUtil.getDescriptors(var11);
            String var13 = "Display " + var23;

            for (byte[] var17 : var12) {
               if (EdidUtil.getDescriptorType(var17) == (int)e) {
                  var13 = EdidUtil.getDescriptorText(var17);
               }
            }

            this.l.append(var13).append("-");
         }

         this.l.append("$");
      }
   }

   private static Pair<Integer, Integer> h(String var0, List<HWDiskStore> var1) {
      for (int var4 = 0; var4 < var1.size(); var4++) {
         HWDiskStore var5 = (HWDiskStore)var1.get(var4);
         List var6 = Arrays.asList(var5.getPartitions());

         for (int var7 = 0; var7 < var6.size(); var7++) {
            String var8 = ((HWPartition)var6.get(var7)).getMountPoint();
            if (!var8.isEmpty() && var0.substring(0, var8.length()).equalsIgnoreCase(var8)) {
               return new Pair<>(var4, var7);
            }
         }
      }

      return new Pair<>(-1, -1);
   }

   public String J() {
      return this.l.toString();
   }
}
