package bypass.lua;

import bypass.ClientAccess;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class ClassLibrary extends TwoArgFunction implements ClientAccess {
   public Globals R;
   private static final long b = 25971575124369L;

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      this.R = luaValue2.checkglobals();
      LuaTable luaTable = new LuaTable();

      try {
         this.R.loadfile("/assets/minecraft/client/class.lua");
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      return luaTable;
   }
}
