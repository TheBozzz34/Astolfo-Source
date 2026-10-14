package bypass.auth.net;

public interface ConnectionListener {
   void onClose(NioConnection nioConnection, String string);

   void onError(NioConnection nioConnection, String string, Exception exception);
}
