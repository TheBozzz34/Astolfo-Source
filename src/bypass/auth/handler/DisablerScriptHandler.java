package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.auth.packet.PacketId;
import bypass.module.exploits.Disabler;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class DisablerScriptHandler extends PacketHandler<String> {
   private static final long b = 127786597021020L;

   public DisablerScriptHandler() {
      super(PacketId.DISABLER_SCRIPT);
   }

   public void handle(String var1, PacketOutput var2) {
      ScriptEngineManager var5 = new ScriptEngineManager();
      ScriptEngine var6 = var5.getEngineByName("nashorn");
      var6.put("disabler", new DisablerScriptSender(this, var2));

      try {
         Disabler.jsHandler = var6.eval(var1);
      } catch (ScriptException var8) {
         var8.printStackTrace();
      }
   }
}
