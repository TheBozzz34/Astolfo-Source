package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.module.Module;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsModuleOn2 extends OneArgFunction {
   private static final long a = 129472386065276L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      long l = a ^ 106876814656876L;
      long m = l ^ 39044790590371L;
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
