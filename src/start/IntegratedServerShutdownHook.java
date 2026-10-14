package start;

import net.minecraft.client.Minecraft;

public final class IntegratedServerShutdownHook extends Thread {
   public IntegratedServerShutdownHook(String string) {
      super(string);
   }

   @Override
   public void run() {
      Minecraft.stopIntegratedServer();
   }
}
