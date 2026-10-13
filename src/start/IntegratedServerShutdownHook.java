package start;

import net.minecraft.client.Minecraft;

public final class IntegratedServerShutdownHook extends Thread {
   public IntegratedServerShutdownHook(String var1) {
      super(var1);
   }

   @Override
   public void run() {
      Minecraft.stopIntegratedServer();
   }
}
