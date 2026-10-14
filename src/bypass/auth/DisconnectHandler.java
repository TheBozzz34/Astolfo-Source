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

   private static void handleDisconnect(NioConnection nioConnection) {
      long l = b ^ 21073478651130L;
      long m = l ^ 113367983521931L;

      try {
         nioConnection.close();
      } catch (IOException ioException) {
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
   public void onClose(NioConnection nioConnection, String string) {
      handleDisconnect(nioConnection);
   }

   @Override
   public void onError(NioConnection nioConnection, String string, Exception exception) {
      handleDisconnect(nioConnection);
   }
}
