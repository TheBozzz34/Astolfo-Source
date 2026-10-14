package bypass.via.platform;

import de.gerrygames.viarewind.api.ViaRewindConfig;
import de.gerrygames.viarewind.api.ViaRewindPlatform;
import de.gerrygames.viarewind.api.ViaRewindConfig.CooldownIndicator;
import de.gerrygames.viarewind.fabric.util.LoggerWrapper;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;

public class VRRewindLoader implements ViaRewindPlatform, Runnable {
   private final Logger b = new LoggerWrapper(LogManager.getLogger("ViaRewind"));

   @Override
   public void run() {
      this.init(new VRRewindLoader.d0());
   }

   @Override
   public Logger getLogger() {
      return this.b;
   }

   public class d0 implements ViaRewindConfig {
      @Override
      public CooldownIndicator getCooldownIndicator() {
         return CooldownIndicator.DISABLED;
      }

      @Override
      public boolean isReplaceAdventureMode() {
         return false;
      }

      @Override
      public boolean isReplaceParticles() {
         return false;
      }

      public int getMaxBookPages() {
         return 255;
      }

      public int getMaxBookPageSize() {
         return 500;
      }
   }
}
