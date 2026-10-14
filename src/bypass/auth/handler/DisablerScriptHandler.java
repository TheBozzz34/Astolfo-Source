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

   public void handle(String string, PacketOutput packetOutput) {
      ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
      ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
      scriptEngine.put("disabler", new DisablerScriptSender(this, packetOutput));

      try {
         Disabler.jsHandler = scriptEngine.eval(string);
      } catch (ScriptException scriptException) {
         scriptException.printStackTrace();
      }
   }
}
