package bypass.lua;

import bypass.ClientAccess;
import bypass.lua.modulemanager.IsModuleOn;
import bypass.lua.modulemanager.Option4;
import bypass.lua.modulemanager.RegisterBoolean3;
import bypass.lua.modulemanager.RegisterNumber;
import bypass.lua.modulemanager.SetOption;
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

public class ModuleManagerLibrary4 extends TwoArgFunction implements ClientAccess {
   private Module ownerModule;
   public final TwoArgFunction registerFunction = new ModuleManagerLibrary4.r4e();
   public static final OneArgFunction getSettingFunction = new GetSettingFunction4();
   public static final VarArgFunction optionFunction = new Option4();
   public static final VarArgFunction setOptionFunction = new SetOption();
   public static final OneArgFunction isModuleOnFunction = new IsModuleOn();
   public static final ThreeArgFunction registerBooleanFunction = new RegisterBoolean3();
   public static final VarArgFunction registerNumberFunction = new RegisterNumber();

   public ModuleManagerLibrary4(Module module) {
      this.ownerModule = module;
   }

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("register", this.registerFunction);
      luaTable.set("register_boolean", registerBooleanFunction);
      luaTable.set("register_number", registerNumberFunction);
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
            EnumSetting enumSetting = (EnumSetting)setting;
            Enum[] enums = (Enum[])enumSetting.getValue().getClass().getEnumConstants();

            try {
               enumSetting.setValue(enums[luaValue.toint() + 1]);
            } catch (Exception exception) {
               enumSetting.setValue(enums[0]);
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

   public static Module getOwnerModule(ModuleManagerLibrary4 moduleManagerLibrary4) {
      return moduleManagerLibrary4.ownerModule;
   }

   public static void accessApplyLuaValue(LuaValue luaValue, Setting setting) {
      applyLuaValue(luaValue, setting);
   }

   public class r4e extends TwoArgFunction {
      @Override
      public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
         String string = null;
         LuaTable luaTable = null;
         if (luaValue.isstring()) {
            LuaString luaString = (LuaString)luaValue;
            string = luaString.checkjstring();
         }

         if (luaValue2.isfunction()) {
            LuaValue luaValue3 = luaValue2.call();
            if (luaValue3.istable()) {
               luaTable = (LuaTable)luaValue3.call();
            }
         } else if (luaValue2.istable()) {
            luaTable = (LuaTable)luaValue2;
         }

         if (string != null && luaTable != null) {
            ModuleManagerLibrary4.getOwnerModule(ModuleManagerLibrary4.this).script = luaTable;
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      }
   }
}
