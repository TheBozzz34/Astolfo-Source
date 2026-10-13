package bypass.auth.net;

public interface PacketSender {
   default <T> void send(short var1, T var2) {
   }

   void f(short var1);
}
