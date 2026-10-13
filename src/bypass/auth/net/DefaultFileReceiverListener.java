package bypass.auth.net;

import java.io.IOException;

public class DefaultFileReceiverListener implements FileReceiverListener {
   @Override
   public void C(FileReceiver var1, String var2, Exception var3) {
      var3.printStackTrace();

      try {
         var1.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }
   }

   @Override
   public void b(FileReceiver var1, String var2, FileReceiveSession var3) {
      try {
         var3.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }
   }

   @Override
   public void q(FileReceiver var1, String var2, Exception var3, FileReceiveSession var4) {
      var3.printStackTrace();

      try {
         var4.close();
      } catch (IOException var6) {
         var6.printStackTrace();
      }
   }
}
