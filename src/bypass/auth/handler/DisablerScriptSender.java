package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import java.util.function.Consumer;

public class DisablerScriptSender implements Consumer<String> {
   public final PacketOutput l;
   public final DisablerScriptHandler u;
   private static final long a = 99435218102672L;
   private static final long b = 2626205594779910613L;

   public DisablerScriptSender(DisablerScriptHandler var1, PacketOutput var2) {
      this.u = var1;
      this.l = var2;
   }

   public void accept(String var1) {
      this.l.getSender().send((short)((int)b), var1);
   }
}
