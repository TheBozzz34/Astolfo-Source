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

public final class Option2 extends VarArgFunction {
   private static final long a = 13531433516044L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      long var2 = a ^ 67178302348528L;
      long var4 = var2 ^ 6736140950863L;
      LuaValue var7 = var1.arg1();
      if (!var7.isstring()) {
         return NIL;
      }

      int var8 = var1.narg();
      LuaValue var9 = var1.arg(2);
      LuaValue var10 = var1.arg(3);
      switch (var8) {
         case 2:
            if (var9.isstring()) {
               String var11 = var7.checkjstring() + var9.checkjstring();
               Setting var12 = LuaApi.f.get(var11);
               if (var12 != null) {
                  return CoerceJavaToLua.coerce(var12.getValue());
               }

               Module var20 = ClientAccess.client.getModuleManager().getModule(var7.checkjstring());
               if (var20 != null) {
                  for (Setting var22 : var20.getSettings()) {
                     if (var22.getName().equals(var9.checkjstring())) {
                        LuaApi.f.put(var11, var22);
                        return CoerceJavaToLua.coerce(var22.getValue());
                     }
                  }
               }
            }
            break;
         case 3:
            if (var9.isstring() && var10.isstring()) {
               String var13 = var7.checkjstring() + var9.checkjstring() + var10.checkjstring();
               Setting var14 = LuaApi.f.get(var13);
               if (var14 != null) {
                  return CoerceJavaToLua.coerce(var14.getValue());
               }

               Module var15 = ClientAccess.client.getModuleManager().getModule(var7.checkjstring());
               if (var15 != null) {
                  for (Setting var17 : var15.getSettings()) {
                     if (var17 instanceof MapSetting) {
                        MapSetting var18 = (MapSetting)var17;
                        Setting var19 = var18.getSettings().get(var10.checkjstring());
                        if (var19 != null) {
                           LuaApi.f.put(var13, var19);
                           return CoerceJavaToLua.coerce(var19.getValue());
                        }
                     }
                  }
               }
            }
      }

      return NIL;
   }
}
