package bypass.auth.net;

public interface FileReceiverListener {
   void onReceiverError(FileReceiver fileReceiver, String string, Exception exception);

   void onSessionClosed(FileReceiver fileReceiver, String string, FileReceiveSession fileReceiveSession);

   void onSessionError(FileReceiver fileReceiver, String string, Exception exception, FileReceiveSession fileReceiveSession);
}
