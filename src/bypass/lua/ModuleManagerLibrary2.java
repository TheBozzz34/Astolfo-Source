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
   public static final TwoArgFunction registerFunction = new Register();
   public static final OneArgFunction getSettingFunction = new GetSettingFunction2();
   public static final VarArgFunction optionFunction = new Option3();
   public static final VarArgFunction setOptionFunction = new SetOption3();
   public static final OneArgFunction isModuleOnFunction = new IsModuleOn3();
   private static final ArrayList<LuaValue> moduleNames = new ArrayList<>();
   public static final VarArgFunction modulesFunction = new Modules();
   public static final ThreeArgFunction registerBooleanFunction = new RegisterBoolean2();
   public static final VarArgFunction registerNumberFunction = new RegisterNumber2();

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("register", registerFunction);
      luaTable.set("register_boolean", registerBooleanFunction);
      luaTable.set("register_number", registerNumberFunction);
      luaTable.set("modules", modulesFunction);
      luaTable.set("option", optionFunction);
      luaTable.set("set_option", setOptionFunction);
      luaTable.set("is_module_on", isModuleOnFunction);
      luaValue2.set("module_manager", luaTable);
      return luaTable;
   }

   private static void applyLuaValue(LuaValue luaValue, Setting<?> setting) {
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

   public static void applyLuaValueToSetting(LuaValue luaValue, Setting setting) {
      applyLuaValue(luaValue, setting);
   }

   public static ArrayList getModuleNames() {
      return moduleNames;
   }
}
