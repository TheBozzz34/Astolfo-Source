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

   private static void handleDisconnect(NioConnection nioConnection) {
      try {
         nioConnection.close();
      } catch (IOException ioException) {
      }

      mc.mouseHelper.Y().getEndpoint().setConnection(null);
      mc.mouseHelper.Y().getEndpoint().setConnection(new AuthConnection());
      if (!(mc.currentScreen instanceof LoginScreen)) {
         disconnected = true;
         Minecraft.xG = true;
         Astolfo.INSTANCE.getUserManager().getUsers().clear();
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
