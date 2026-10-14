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

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("register", e);
      luaTable.set("register_boolean", B);
      luaTable.set("register_number", Q);
      luaTable.set("modules", X);
      luaTable.set("option", L);
      luaTable.set("set_option", n);
      luaTable.set("is_module_on", J);
      luaValue2.set("module_manager", luaTable);
      return luaTable;
   }

   private static void R(LuaValue luaValue, Setting<?> setting) {
      if (luaValue.isnumber()) {
         if (setting.getType() == SettingType.NUMBER) {
            NumberSetting numberSetting = (NumberSetting)setting;
            if (numberSetting.valueClass == Integer.class) {
               numberSetting.setValue(luaValue.toint());
            } else {
               numberSetting.setValue(luaValue.tofloat());
            }
         } else if (setting.getType() == SettingType.ENUM) {
            if (luaValue.isnumber()) {
               EnumSetting enumSetting = (EnumSetting)setting;
               Enum[] enums = (Enum[])enumSetting.getValue().getClass().getEnumConstants();

               try {
                  enumSetting.setValue(enums[luaValue.toint() + 1]);
               } catch (Exception exception) {
                  enumSetting.setValue(enums[0]);
               }
            } else if (luaValue.isstring()) {
               setting.parse(luaValue.checkjstring());
            }
         }
      } else if (luaValue.isboolean()) {
         if (setting.getType() == SettingType.BOOLEAN) {
            BooleanSetting booleanSetting = (BooleanSetting)setting;
            booleanSetting.set(luaValue.checkboolean());
         }
      } else if (luaValue.isstring() && setting.getType() == SettingType.STRING) {
         StringSetting stringSetting = (StringSetting)setting;
         stringSetting.setValue(luaValue.checkjstring());
      }
   }

   public static void i(LuaValue luaValue, Setting setting) {
      R(luaValue, setting);
   }

   public static ArrayList i() {
      return A;
   }
}
