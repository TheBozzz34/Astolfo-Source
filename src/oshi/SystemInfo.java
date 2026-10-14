package oshi;

import com.sun.jna.Platform;
import java.util.function.Supplier;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.platform.linux.LinuxHardwareAbstractionLayer;
import oshi.hardware.platform.mac.MacHardwareAbstractionLayer;
import oshi.hardware.platform.unix.freebsd.FreeBsdHardwareAbstractionLayer;
import oshi.hardware.platform.unix.solaris.SolarisHardwareAbstractionLayer;
import oshi.hardware.platform.windows.WindowsHardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.software.os.mac.MacOperatingSystem;
import oshi.software.os.unix.freebsd.FreeBsdOperatingSystem;
import oshi.software.os.unix.solaris.SolarisOperatingSystem;
import oshi.software.os.windows.WindowsOperatingSystem;
import oshi.util.Memoizer;

public class SystemInfo {
   private static final PlatformEnum currentPlatformEnum;
   private final Supplier<OperatingSystem> os = Memoizer.memoize(this::createOperatingSystem);
   private final Supplier<HardwareAbstractionLayer> hardware = Memoizer.memoize(this::createHardware);

   public static PlatformEnum getCurrentPlatformEnum() {
      return currentPlatformEnum;
   }

   public OperatingSystem getOperatingSystem() {
      return this.os.get();
   }

   private OperatingSystem createOperatingSystem() {
      switch (currentPlatformEnum) {
         case WINDOWS:
            return new WindowsOperatingSystem();
         case LINUX:
            return new LinuxOperatingSystem();
         case MACOSX:
            return new MacOperatingSystem();
         case SOLARIS:
            return new SolarisOperatingSystem();
         case FREEBSD:
            return new FreeBsdOperatingSystem();
         default:
            throw new UnsupportedOperationException("Operating system not supported: " + Platform.getOSType());
      }
   }

   public HardwareAbstractionLayer getHardware() {
      return this.hardware.get();
   }

   private HardwareAbstractionLayer createHardware() {
      switch (currentPlatformEnum) {
         case WINDOWS:
            return new WindowsHardwareAbstractionLayer();
         case LINUX:
            return new LinuxHardwareAbstractionLayer();
         case MACOSX:
            return new MacHardwareAbstractionLayer();
         case SOLARIS:
            return new SolarisHardwareAbstractionLayer();
         case FREEBSD:
            return new FreeBsdHardwareAbstractionLayer();
         default:
            throw new UnsupportedOperationException("Operating system not supported: " + Platform.getOSType());
      }
   }

   static {
      if (Platform.isWindows()) {
         currentPlatformEnum = PlatformEnum.WINDOWS;
      } else if (Platform.isLinux()) {
         currentPlatformEnum = PlatformEnum.LINUX;
      } else if (Platform.isMac()) {
         currentPlatformEnum = PlatformEnum.MACOSX;
      } else if (Platform.isSolaris()) {
         currentPlatformEnum = PlatformEnum.SOLARIS;
      } else if (Platform.isFreeBSD()) {
         currentPlatformEnum = PlatformEnum.FREEBSD;
      } else {
         currentPlatformEnum = PlatformEnum.UNKNOWN;
      }
   }
}
