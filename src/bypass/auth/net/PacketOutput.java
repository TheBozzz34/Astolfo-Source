package bypass.auth.net;

public interface PacketOutput {
   void attach(SocketWriter var1);

   PacketSender getSender();
}
