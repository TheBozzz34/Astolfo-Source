package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.lua.LuaApi;
import bypass.module.Module;
import bypass.setting.MapSetting;
import bypass.setting.Setting;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public final class Option extends VarArgFunction {
   private static final long a = 65437561292116L;

   @Override
   public Varargs onInvoke(Varargs varargs) {
      long l = a ^ 107965706393868L;
      long m = l ^ 121795152847339L;
      LuaValue luaValue = varargs.arg1();
      if (!luaValue.isstring()) {
         return NIL;
      }

      int i = varargs.narg();
      LuaValue luaValue2 = varargs.arg(2);
      LuaValue luaValue3 = varargs.arg(3);
      switch (i) {
         case 2:
            if (luaValue2.isstring()) {
               String string = luaValue.checkjstring() + luaValue2.checkjstring();
               Setting setting = LuaApi.f.get(string);
               if (setting != null) {
                  return CoerceJavaToLua.coerce(setting.getValue());
               }

               Module module = ClientAccess.client.getModuleManager().getModule(luaValue.checkjstring());
               if (module != null) {
                  for (Setting setting2 : module.getSettings()) {
                     if (setting2.getName().equals(luaValue2.checkjstring())) {
                        LuaApi.f.put(string, setting2);
                        return CoerceJavaToLua.coerce(setting2.getValue());
                     }
                  }
               }
            }
            break;
         case 3:
            if (luaValue2.isstring() && luaValue3.isstring()) {
               String string2 = luaValue.checkjstring() + luaValue2.checkjstring() + luaValue3.checkjstring();
               Setting setting3 = LuaApi.f.get(string2);
               if (setting3 != null) {
                  return CoerceJavaToLua.coerce(setting3.getValue());
               }

               Module module2 = ClientAccess.client.getModuleManager().getModule(luaValue.checkjstring());
               if (module2 != null) {
                  for (Setting setting4 : module2.getSettings()) {
                     if (setting4 instanceof MapSetting) {
                        MapSetting mapSetting = (MapSetting)setting4;
                        Setting setting5 = mapSetting.getSettings().get(luaValue3.checkjstring());
                        if (setting5 != null) {
                           LuaApi.f.put(string2, setting5);
                           return CoerceJavaToLua.coerce(setting5.getValue());
                        }
                     }
                  }
               }
            }
      }

      return NIL;
   }
}
