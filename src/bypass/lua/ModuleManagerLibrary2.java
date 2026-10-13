package bypass.lua;

import bypass.ClientAccess;
import bypass.lua.modulemanager.IsModuleOn3;
import bypass.lua.modulemanager.Modules;
import bypass.lua.modulemanager.Option3;
import bypass.lua.modulemanager.Register;
import bypass.lua.modulemanager.RegisterBoolean2;
import bypass.lua.modulemanager.RegisterNumber2;
import bypass.lua.modulemanager.SetOption3;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import bypass.setting.SettingType;
import bypass.setting.StringSetting;
import java.util.ArrayList;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class ModuleManagerLibrary2 extends TwoArgFunction implements ClientAccess {
   public static final TwoArgFunction e = new Register();
   public static final OneArgFunction m = new GetSettingFunction2();
   public static final VarArgFunction L = new Option3();
   public static final VarArgFunction n = new SetOption3();
   public static final OneArgFunction J = new IsModuleOn3();
   private static final ArrayList<LuaValue> A = new ArrayList<>();
   public static final VarArgFunction X = new Modules();
   public static final ThreeArgFunction B = new RegisterBoolean2();
   public static final VarArgFunction Q = new RegisterNumber2();
   private static final long b = 7339663642732L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      LuaTable var7 = LuaValue.tableOf();
      var7.set("register", e);
      var7.set("register_boolean", B);
      var7.set("register_number", Q);
      var7.set("modules", X);
      var7.set("option", L);
      var7.set("set_option", n);
      var7.set("is_module_on", J);
      var2.set("module_manager", var7);
      return var7;
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
            if (var0.isnumber()) {
               EnumSetting var9 = (EnumSetting)var1;
               Enum[] var6 = (Enum[])var9.getValue().getClass().getEnumConstants();

               try {
                  var9.setValue(var6[var0.toint() + 1]);
               } catch (Exception var8) {
                  var9.setValue(var6[0]);
               }
            } else if (var0.isstring()) {
               var1.parse(var0.checkjstring());
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

   public static void i(LuaValue var0, Setting var1) {
      R(var0, var1);
   }

   public static ArrayList i() {
      return A;
   }
}
