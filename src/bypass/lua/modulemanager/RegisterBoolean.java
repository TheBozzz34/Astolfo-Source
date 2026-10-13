package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.lua.LuaApi;
import bypass.setting.BooleanSetting;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class RegisterBoolean extends ThreeArgFunction {
   private static final long a = 80749154130833L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2, LuaValue var3) {
      long var4 = a ^ 48682990277566L;
      long var6 = var4 ^ 90442467849628L;
      String var9 = null;
      String var10 = null;
      boolean var11 = false;
      if (var1.isstring()) {
         LuaString var12 = (LuaString)var1;
         var9 = var12.checkjstring();
      }

      if (var2.isstring()) {
         LuaString var13 = (LuaString)var2;
         var10 = var13.checkjstring();
      }

      if (var3.isboolean()) {
         LuaBoolean var14 = (LuaBoolean)var3;
         var11 = var14.toboolean();
      }

      if (var9 != null && var10 != null) {
         BooleanSetting var15 = new BooleanSetting(var10, "script option", var11);
         LuaApi.f.put(var10, var15);
         ClientAccess.client.getModuleManager().getModule(var9).addSettings(var15);
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
