package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.lua.LuaApi;
import bypass.lua.ModuleManagerLibrary2;
import bypass.setting.NumberSetting;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class RegisterNumber2 extends VarArgFunction {
   private static final long a = 88837408547814L;

   @Override
   public Varargs invoke(Varargs var1) {
      long var2 = a ^ 77313823519900L;
      long var4 = var2 ^ 37292513700553L;
      String var7 = null;
      String var8 = null;
      Number var9 = null;
      Number var10 = null;
      Number var11 = null;
      int var12 = 1;

      for (int var13 = var1.narg(); var12 <= var13; var12++) {
         LuaValue var14 = var1.checkvalue(var12);
         switch (var12) {
            case 1:
               if (var14.isstring()) {
                  var7 = var14.checkjstring();
               }
               break;
            case 2:
               if (var14.isstring()) {
                  var8 = var14.checkjstring();
               }
               break;
            case 3:
               if (var14.isnumber()) {
                  Double var27 = null;
                  Integer var29 = null;
                  boolean var31 = false;
                  if (var14.isint()) {
                     try {
                        var29 = var14.checkint();
                        var31 = true;
                     } catch (LuaError var24) {
                     }
                  }

                  if (var29 == null) {
                     try {
                        var27 = var14.checkdouble();
                     } catch (LuaError var23) {
                     }
                  }

                  if (var27 != null) {
                     var9 = var27;
                  } else if (var29 != null && var31) {
                     var9 = var29;
                  }
               }
               break;
            case 4:
               if (var14.isnumber()) {
                  Double var26 = null;
                  Integer var28 = null;
                  boolean var30 = false;
                  if (var14.isint()) {
                     try {
                        var28 = var14.checkint();
                        var30 = true;
                     } catch (LuaError var22) {
                     }
                  }

                  if (var28 == null) {
                     try {
                        var26 = var14.checkdouble();
                     } catch (LuaError var21) {
                     }
                  }

                  if (var26 != null) {
                     var10 = var26;
                  } else if (var28 != null && var30) {
                     var10 = var28;
                  }
               }
               break;
            case 5:
               if (var14.isnumber()) {
                  Double var15 = null;
                  Integer var16 = null;
                  boolean var17 = false;
                  if (var14.isint()) {
                     try {
                        var16 = var14.checkint();
                        var17 = true;
                     } catch (LuaError var19) {
                     }
                  }

                  if (var16 == null) {
                     try {
                        var15 = var14.checkdouble();
                     } catch (LuaError var20) {
                     }
                  }

                  if (var15 != null) {
                     var11 = var15;
                  } else if (var16 != null && var17) {
                     var11 = var16;
                  }
               }
         }
      }

      if (var7 != null && var8 != null && var9 != null && var10 != null && var11 != null) {
         NumberSetting var25 = new NumberSetting<>(var8, "script option", var11, var9, var10);
         LuaApi.f.put(var8, var25);
         ClientAccess.client.getModuleManager().getModule(var7).addSettings(var25);
         return ModuleManagerLibrary2.varargsOf(new LuaValue[]{valueOf(true)});
      } else {
         return ModuleManagerLibrary2.varargsOf(new LuaValue[]{valueOf(false)});
      }
   }
}
