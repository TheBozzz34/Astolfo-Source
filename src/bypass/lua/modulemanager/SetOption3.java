package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.lua.LuaApi;
import bypass.lua.ModuleManagerLibrary2;
import bypass.module.Module;
import bypass.setting.MapSetting;
import bypass.setting.Setting;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class SetOption3 extends VarArgFunction {
   @Override
   public Varargs onInvoke(Varargs varargs) {
      LuaValue luaValue = varargs.arg1();
      if (!luaValue.isstring()) {
         return NIL;
      }

      int j = varargs.narg();
      LuaValue luaValue2 = varargs.arg(2);
      LuaValue luaValue3 = varargs.arg(3);
      LuaValue luaValue4 = varargs.arg(4);
      switch (j) {
         case 3:
            if (luaValue2.isstring()) {
               String string = luaValue.checkjstring() + luaValue2.checkjstring();
               Setting setting = LuaApi.f.get(string);
               if (setting == null) {
                  Module module = ClientAccess.client.getModuleManager().getModule(luaValue.checkjstring());
                  if (module != null) {
                     for (Setting setting2 : module.getSettings()) {
                        if (setting2.getName().equals(luaValue2.checkjstring())) {
                           LuaApi.f.put(string, setting2);
                           setting = setting2;
                        }
                     }
                  }
               }

               if (setting != null) {
                  ModuleManagerLibrary2.i(luaValue3, setting);
               }
            }
            break;
         case 4:
            if (luaValue2.isstring() && luaValue3.isstring()) {
               String string2 = luaValue.checkjstring() + luaValue2.checkjstring() + luaValue3.checkjstring();
               Setting setting3 = LuaApi.f.get(string2);
               if (setting3 == null) {
                  Module module2 = ClientAccess.client.getModuleManager().getModule(luaValue.checkjstring());
                  if (module2 != null) {
                     for (Setting setting4 : module2.getSettings()) {
                        if (setting4 instanceof MapSetting) {
                           MapSetting mapSetting = (MapSetting)setting4;
                           Setting setting5 = mapSetting.getSettings().get(luaValue3.checkjstring());
                           if (setting5 != null) {
                              LuaApi.f.put(string2, setting5);
                              setting3 = setting5;
                           }
                        }
                     }
                  }
               }

               if (setting3 != null) {
                  ModuleManagerLibrary2.i(luaValue4, setting3);
               }
            }
      }

      return NIL;
   }
}
