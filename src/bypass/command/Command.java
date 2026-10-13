package bypass.command;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;

public abstract class Command extends AbstractCommand implements ClientAccess {
   public LuaValue w;

   public Command(String var1, String var2) {
      super(var1, var2);
   }
}
