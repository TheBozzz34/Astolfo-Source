package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.module.Module;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsModuleOn4 extends OneArgFunction {
   private static final long a = 121785844082786L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      long l = a ^ 103293250774994L;
      long m = l ^ 4696272400899L;
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
