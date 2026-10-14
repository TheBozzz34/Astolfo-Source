package bypass.auth.net;

public interface PacketSender {
   default <T> void send(short s, T object) {
   }

   void f(short s);
}
