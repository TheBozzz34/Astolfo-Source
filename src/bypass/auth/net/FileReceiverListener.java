package bypass.auth.net;

public interface FileReceiverListener {
   void C(FileReceiver fileReceiver, String string, Exception exception);

   void b(FileReceiver fileReceiver, String string, FileReceiveSession fileReceiveSession);

   void q(FileReceiver fileReceiver, String string, Exception exception, FileReceiveSession fileReceiveSession);
}
