package bypass.lua;

import bypass.ClientAccess;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class ClassLibrary extends TwoArgFunction implements ClientAccess {
   public Globals globals;

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      this.globals = luaValue2.checkglobals();
      LuaTable luaTable = new LuaTable();

      try {
         this.globals.loadfile("/assets/minecraft/client/class.lua");
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      return luaTable;
   }
}
