package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import java.util.function.Consumer;

public class DisablerScriptSender implements Consumer<String> {
   public final PacketOutput l;
   public final DisablerScriptHandler u;

   public DisablerScriptSender(DisablerScriptHandler disablerScriptHandler, PacketOutput packetOutput) {
      this.u = disablerScriptHandler;
      this.l = packetOutput;
   }

   public void accept(String string) {
      this.l.getSender().send((short)469, string);
   }
}
