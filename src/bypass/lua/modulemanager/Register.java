package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.module.scripts.ScriptModule;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class Register extends TwoArgFunction {
   private static final long a = 42808535312237L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      long var3 = a ^ 99408006610595L;
      long var5 = var3 ^ 83502128751741L;
      String var8 = null;
      LuaTable var9 = null;
      if (var1.isstring()) {
         LuaString var10 = (LuaString)var1;
         var8 = var10.checkjstring();
      }

      if (var2.isfunction()) {
         LuaValue var11 = var2.call();
         if (var11.istable()) {
            var9 = (LuaTable)var11.call();
         }
      } else if (var2.istable()) {
         var9 = (LuaTable)var2;
      }

      if (var8 != null && var9 != null) {
         ClientAccess.client.getModuleManager().add(new ScriptModule(var8, var9));
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
