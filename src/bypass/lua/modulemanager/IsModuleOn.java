package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.module.Module;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsModuleOn extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      if (luaValue.isstring()) {
         LuaString luaString = (LuaString)luaValue;
         Module module = ClientAccess.client.getModuleManager().getModule(luaString.checkjstring());
         if (module != null) {
            return valueOf(module.isEnabled());
         }
      }

      return valueOf(false);
   }
}
