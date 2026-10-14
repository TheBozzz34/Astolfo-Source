package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.module.Module;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsModuleOn3 extends OneArgFunction {
   private static final long a = 40519508856406L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      long l = a ^ 77445542052236L;
      long m = l ^ 94293309125225L;
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
