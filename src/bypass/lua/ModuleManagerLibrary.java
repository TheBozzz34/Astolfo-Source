package bypass.lua;

import bypass.ClientAccess;
import bypass.lua.modulemanager.IsModuleOn4;
import bypass.lua.modulemanager.Option;
import bypass.lua.modulemanager.RegisterBoolean;
import bypass.lua.modulemanager.RegisterNumber3;
import bypass.lua.modulemanager.SetOption2;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import bypass.setting.SettingType;
import bypass.setting.StringSetting;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class ModuleManagerLibrary extends TwoArgFunction implements ClientAccess {
   private Module p;
   public final TwoArgFunction J = new ModuleManagerLibrary.aoq();
   public static final OneArgFunction f = new GetSettingFunction();
   public static final VarArgFunction j = new Option();
   public static final VarArgFunction W = new SetOption2();
   public static final OneArgFunction G = new IsModuleOn4();
   public static final ThreeArgFunction c = new RegisterBoolean();
   public static final VarArgFunction A = new RegisterNumber3();
   private static final long b = 14387324638711L;

   public ModuleManagerLibrary(Module var1) {
      this.p = var1;
   }

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      LuaTable var5 = LuaValue.tableOf();
      var5.set("register", this.J);
      var5.set("register_boolean", c);
      var5.set("register_number", A);
      var5.set("option", j);
      var5.set("set_option", W);
      var5.set("is_module_on", G);
      var2.set("module_manager", var5);
      return var5;
   }

   private static void R(LuaValue var0, Setting<?> var1) {
      if (var0.isnumber()) {
         if (var1.getType() == SettingType.NUMBER) {
            NumberSetting var5 = (NumberSetting)var1;
            if (var5.valueClass == Integer.class) {
               var5.setValue(var0.toint());
            } else {
               var5.setValue(var0.tofloat());
            }
         } else if (var1.getType() == SettingType.ENUM) {
            EnumSetting var9 = (EnumSetting)var1;
            Enum[] var6 = (Enum[])var9.getValue().getClass().getEnumConstants();

            try {
               var9.setValue(var6[var0.toint() + 1]);
            } catch (Exception var8) {
               var9.setValue(var6[0]);
            }
         }
      } else if (var0.isboolean()) {
         if (var1.getType() == SettingType.BOOLEAN) {
            BooleanSetting var10 = (BooleanSetting)var1;
            var10.set(var0.checkboolean());
         }
      } else if (var0.isstring() && var1.getType() == SettingType.STRING) {
         StringSetting var11 = (StringSetting)var1;
         var11.setValue(var0.checkjstring());
      }
   }

   public static Module T(ModuleManagerLibrary var0) {
      return var0.p;
   }

   public static void c(LuaValue var0, Setting var1) {
      R(var0, var1);
   }

   public class aoq extends TwoArgFunction {
      private static final long a = 46936245130426L;

      @Override
      public LuaValue call(LuaValue var1, LuaValue var2) {
         String var6 = null;
         LuaTable var7 = null;
         if (var1.isstring()) {
            LuaString var8 = (LuaString)var1;
            var6 = var8.checkjstring();
         }

         if (var2.isfunction()) {
            LuaValue var9 = var2.call();
            if (var9.istable()) {
               var7 = (LuaTable)var9.call();
            }
         } else if (var2.istable()) {
            var7 = (LuaTable)var2;
         }

         if (var6 != null && var7 != null) {
            ModuleManagerLibrary.T(ModuleManagerLibrary.this).o = var7;
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      }
   }
}
