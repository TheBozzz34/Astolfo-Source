package bypass.auth.net;

public interface ConnectionListener {
   void onClose(NioConnection var1, String var2);

   void onError(NioConnection var1, String var2, Exception var3);
}
