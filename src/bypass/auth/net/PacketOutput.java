package bypass.auth.net;

public interface PacketOutput {
   void attach(SocketWriter socketWriter);

   PacketSender getSender();
}
