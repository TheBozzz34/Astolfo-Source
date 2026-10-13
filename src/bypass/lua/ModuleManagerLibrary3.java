package bypass.lua;

import bypass.ClientAccess;
import bypass.command.Command;
import bypass.lua.modulemanager.IsModuleOn2;
import bypass.lua.modulemanager.Option2;
import bypass.lua.modulemanager.RegisterBoolean4;
import bypass.lua.modulemanager.RegisterNumber4;
import bypass.lua.modulemanager.SetOption4;
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

public class ModuleManagerLibrary3 extends TwoArgFunction implements ClientAccess {
   private Command g;
   public final TwoArgFunction H = new ModuleManagerLibrary3.r5i();
   public static final OneArgFunction b = new GetSettingFunction3();
   public static final VarArgFunction X = new Option2();
   public static final VarArgFunction v = new SetOption4();
   public static final OneArgFunction l = new IsModuleOn2();
   public static final ThreeArgFunction B = new RegisterBoolean4();
   public static final VarArgFunction L = new RegisterNumber4();
   private static final long c = 77085563920682L;

   public ModuleManagerLibrary3(Command var1) {
      this.g = var1;
   }

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      LuaTable var6 = LuaValue.tableOf();
      var6.set("register", this.H);
      var6.set("register_boolean", B);
      var6.set("register_number", L);
      var6.set("option", X);
      var6.set("set_option", v);
      var6.set("is_module_on", l);
      var2.set("module_manager", var6);
      return var6;
   }

   private static void T(LuaValue var0, Setting<?> var1) {
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

   public static Command g(ModuleManagerLibrary3 var0) {
      return var0.g;
   }

   public static void U(LuaValue var0, Setting var1) {
      T(var0, var1);
   }

   public class r5i extends TwoArgFunction {
      private static final long a = 122804578530430L;

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
            ModuleManagerLibrary3.g(ModuleManagerLibrary3.this).w = var7;
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      }
   }
}
