package bypass.command;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;

public abstract class Command extends AbstractCommand implements ClientAccess {
   public LuaValue luaTable;

   public Command(String string, String string2) {
      super(string, string2);
   }
}
