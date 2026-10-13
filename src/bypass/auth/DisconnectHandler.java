package bypass.auth;

import bypass.Astolfo;
import bypass.ClientAccess;
import bypass.auth.net.ConnectionListener;
import bypass.auth.net.NioConnection;
import bypass.gui.screen.LoginScreen;
import java.io.IOException;
import net.minecraft.client.Minecraft;

public class DisconnectHandler implements ConnectionListener, ClientAccess {
   public static boolean disconnected;
   private static final long b = 20256675810443L;

   private static void handleDisconnect(NioConnection var0) {
      long var1 = b ^ 21073478651130L;
      long var3 = var1 ^ 113367983521931L;

      try {
         var0.close();
      } catch (IOException var7) {
      }

      mc.mouseHelper.Y().getEndpoint().E(null);
      mc.mouseHelper.Y().getEndpoint().E(new AuthConnection());
      if (!(mc.currentScreen instanceof LoginScreen)) {
         disconnected = true;
         Minecraft.xG = true;
         Astolfo.INSTANCE.getUserManager().r().clear();
      }
   }

   @Override
   public void onClose(NioConnection var1, String var2) {
      handleDisconnect(var1);
   }

   @Override
   public void onError(NioConnection var1, String var2, Exception var3) {
      handleDisconnect(var1);
   }
}
