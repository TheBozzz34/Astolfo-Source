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
   private Command ownerCommand;
   public final TwoArgFunction registerFunction = new ModuleManagerLibrary3.r5i();
   public static final OneArgFunction getSettingFunction = new GetSettingFunction3();
   public static final VarArgFunction optionFunction = new Option2();
   public static final VarArgFunction setOptionFunction = new SetOption4();
   public static final OneArgFunction isModuleOnFunction = new IsModuleOn2();
   public static final ThreeArgFunction registerBooleanFunction = new RegisterBoolean4();
   public static final VarArgFunction registerNumberFunction = new RegisterNumber4();

   public ModuleManagerLibrary3(Command command) {
      this.ownerCommand = command;
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

   public static Command getOwnerCommand(ModuleManagerLibrary3 moduleManagerLibrary3) {
      return moduleManagerLibrary3.ownerCommand;
   }

   public static void applyLuaValueToSetting(LuaValue luaValue, Setting setting) {
      applyLuaValue(luaValue, setting);
   }

   public class r5i extends TwoArgFunction {
      @Override
      public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
         String string = null;
         LuaTable luaTable2 = null;
         if (luaValue.isstring()) {
            LuaString luaString = (LuaString)luaValue;
            string = luaString.checkjstring();
         }

         if (luaValue2.isfunction()) {
            LuaValue luaValue3 = luaValue2.call();
            if (luaValue3.istable()) {
               luaTable2 = (LuaTable)luaValue3.call();
            }
         } else if (luaValue2.istable()) {
            luaTable2 = (LuaTable)luaValue2;
         }

         if (string != null && luaTable2 != null) {
            ModuleManagerLibrary3.getOwnerCommand(ModuleManagerLibrary3.this).luaTable = luaTable2;
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      }
   }
}
