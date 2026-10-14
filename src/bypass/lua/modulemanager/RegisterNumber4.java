package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.lua.LuaApi;
import bypass.lua.ModuleManagerLibrary2;
import bypass.setting.NumberSetting;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class RegisterNumber4 extends VarArgFunction {
   private static final long a = 20027655455395L;

   @Override
   public Varargs invoke(Varargs varargs) {
      long l = a ^ 109449437869489L;
      long m = l ^ 77841455516321L;
      String string = null;
      String string2 = null;
      Number number = null;
      Number number2 = null;
      Number number3 = null;
      int i = 1;

      for (int j = varargs.narg(); i <= j; i++) {
         LuaValue luaValue = varargs.checkvalue(i);
         switch (i) {
            case 1:
               if (luaValue.isstring()) {
                  string = luaValue.checkjstring();
               }
               break;
            case 2:
               if (luaValue.isstring()) {
                  string2 = luaValue.checkjstring();
               }
               break;
            case 3:
               if (luaValue.isnumber()) {
                  Double double2 = null;
                  Integer integer = null;
                  boolean bl = false;
                  if (luaValue.isint()) {
                     try {
                        integer = luaValue.checkint();
                        bl = true;
                     } catch (LuaError luaError) {
                     }
                  }

                  if (integer == null) {
                     try {
                        double2 = luaValue.checkdouble();
                     } catch (LuaError luaError2) {
                     }
                  }

                  if (double2 != null) {
                     number = double2;
                  } else if (integer != null && bl) {
                     number = integer;
                  }
               }
               break;
            case 4:
               if (luaValue.isnumber()) {
                  Double double3 = null;
                  Integer integer2 = null;
                  boolean bl2 = false;
                  if (luaValue.isint()) {
                     try {
                        integer2 = luaValue.checkint();
                        bl2 = true;
                     } catch (LuaError luaError3) {
                     }
                  }

                  if (integer2 == null) {
                     try {
                        double3 = luaValue.checkdouble();
                     } catch (LuaError luaError4) {
                     }
                  }

                  if (double3 != null) {
                     number2 = double3;
                  } else if (integer2 != null && bl2) {
                     number2 = integer2;
                  }
               }
               break;
            case 5:
               if (luaValue.isnumber()) {
                  Double double4 = null;
                  Integer integer3 = null;
                  boolean bl3 = false;
                  if (luaValue.isint()) {
                     try {
                        integer3 = luaValue.checkint();
                        bl3 = true;
                     } catch (LuaError luaError5) {
                     }
                  }

                  if (integer3 == null) {
                     try {
                        double4 = luaValue.checkdouble();
                     } catch (LuaError luaError6) {
                     }
                  }

                  if (double4 != null) {
                     number3 = double4;
                  } else if (integer3 != null && bl3) {
                     number3 = integer3;
                  }
               }
         }
      }

      if (string != null && string2 != null && number != null && number2 != null && number3 != null) {
         NumberSetting numberSetting = new NumberSetting<>(string2, "script option", number3, number, number2);
         LuaApi.f.put(string2, numberSetting);
         ClientAccess.client.getModuleManager().getModule(string).addSettings(numberSetting);
         return ModuleManagerLibrary2.varargsOf(new LuaValue[]{valueOf(true)});
      } else {
         return ModuleManagerLibrary2.varargsOf(new LuaValue[]{valueOf(false)});
      }
   }
}
