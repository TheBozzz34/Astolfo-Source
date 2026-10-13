package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.lua.LuaApi;
import bypass.lua.ModuleManagerLibrary3;
import bypass.module.Module;
import bypass.setting.MapSetting;
import bypass.setting.Setting;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class SetOption4 extends VarArgFunction {
   private static final long a = 123109082897874L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      long var2 = a ^ 137710424449831L;
      long var4 = var2 ^ 41503981339462L;
      LuaValue var7 = var1.arg1();
      if (!var7.isstring()) {
         return NIL;
      }

      int var8 = var1.narg();
      LuaValue var9 = var1.arg(2);
      LuaValue var10 = var1.arg(3);
      LuaValue var11 = var1.arg(4);
      switch (var8) {
         case 3:
            if (var9.isstring()) {
               String var12 = var7.checkjstring() + var9.checkjstring();
               Setting var13 = LuaApi.f.get(var12);
               if (var13 == null) {
                  Module var21 = ClientAccess.client.getModuleManager().getModule(var7.checkjstring());
                  if (var21 != null) {
                     for (Setting var23 : var21.getSettings()) {
                        if (var23.getName().equals(var9.checkjstring())) {
                           LuaApi.f.put(var12, var23);
                           var13 = var23;
                        }
                     }
                  }
               }

               if (var13 != null) {
                  ModuleManagerLibrary3.U(var10, var13);
               }
            }
            break;
         case 4:
            if (var9.isstring() && var10.isstring()) {
               String var14 = var7.checkjstring() + var9.checkjstring() + var10.checkjstring();
               Setting var15 = LuaApi.f.get(var14);
               if (var15 == null) {
                  Module var16 = ClientAccess.client.getModuleManager().getModule(var7.checkjstring());
                  if (var16 != null) {
                     for (Setting var18 : var16.getSettings()) {
                        if (var18 instanceof MapSetting) {
                           MapSetting var19 = (MapSetting)var18;
                           Setting var20 = var19.getSettings().get(var10.checkjstring());
                           if (var20 != null) {
                              LuaApi.f.put(var14, var20);
                              var15 = var20;
                           }
                        }
                     }
                  }
               }

               if (var15 != null) {
                  ModuleManagerLibrary3.U(var11, var15);
               }
            }
      }

      return NIL;
   }
}
