package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import java.util.function.Consumer;

public class DisablerScriptSender implements Consumer<String> {
   public final PacketOutput packetOutput;
   public final DisablerScriptHandler handler;

   public DisablerScriptSender(DisablerScriptHandler disablerScriptHandler, PacketOutput packetOutput) {
      this.handler = disablerScriptHandler;
      this.packetOutput = packetOutput;
   }

   public void accept(String string) {
      this.packetOutput.getSender().send((short)469, string);
   }
}
