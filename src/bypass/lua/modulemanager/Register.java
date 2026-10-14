package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.module.scripts.ScriptModule;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class Register extends TwoArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      String string = null;
      LuaTable luaTable = null;
      if (luaValue.isstring()) {
         LuaString luaString = (LuaString)luaValue;
         string = luaString.checkjstring();
      }

      if (luaValue2.isfunction()) {
         LuaValue luaValue3 = luaValue2.call();
         if (luaValue3.istable()) {
            luaTable = (LuaTable)luaValue3.call();
         }
      } else if (luaValue2.istable()) {
         luaTable = (LuaTable)luaValue2;
      }

      if (string != null && luaTable != null) {
         ClientAccess.client.getModuleManager().add(new ScriptModule(string, luaTable));
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
