package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.module.Module;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsModuleOn2 extends OneArgFunction {
   private static final long a = 129472386065276L;

   @Override
   public LuaValue call(LuaValue var1) {
      long var2 = a ^ 106876814656876L;
      long var4 = var2 ^ 39044790590371L;
      if (var1.isstring()) {
         LuaString var7 = (LuaString)var1;
         Module var8 = ClientAccess.client.getModuleManager().getModule(var7.checkjstring());
         if (var8 != null) {
            return valueOf(var8.isEnabled());
         }
      }

      return valueOf(false);
   }
}
