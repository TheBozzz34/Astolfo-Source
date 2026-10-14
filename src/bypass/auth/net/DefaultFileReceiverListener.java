package bypass.auth.net;

import java.io.IOException;

public class DefaultFileReceiverListener implements FileReceiverListener {
   @Override
   public void C(FileReceiver fileReceiver, String string, Exception exception) {
      exception.printStackTrace();

      try {
         fileReceiver.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   @Override
   public void b(FileReceiver fileReceiver, String string, FileReceiveSession fileReceiveSession) {
      try {
         fileReceiveSession.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   @Override
   public void q(FileReceiver fileReceiver, String string, Exception exception, FileReceiveSession fileReceiveSession) {
      exception.printStackTrace();

      try {
         fileReceiveSession.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }
}
