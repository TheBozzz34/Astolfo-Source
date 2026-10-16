package bypass.auth.net;

import java.io.IOException;

public class DefaultFileReceiverListener implements FileReceiverListener {
   @Override
   public void onReceiverError(FileReceiver fileReceiver, String string, Exception exception) {
      exception.printStackTrace();

      try {
         fileReceiver.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   @Override
   public void onSessionClosed(FileReceiver fileReceiver, String string, FileReceiveSession fileReceiveSession) {
      try {
         fileReceiveSession.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   @Override
   public void onSessionError(FileReceiver fileReceiver, String string, Exception exception, FileReceiveSession fileReceiveSession) {
      exception.printStackTrace();

      try {
         fileReceiveSession.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }
}
