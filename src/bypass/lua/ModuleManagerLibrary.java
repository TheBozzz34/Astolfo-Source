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

   public ModuleManagerLibrary(Module module) {
      this.p = module;
   }

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("register", this.J);
      luaTable.set("register_boolean", c);
      luaTable.set("register_number", A);
      luaTable.set("option", j);
      luaTable.set("set_option", W);
      luaTable.set("is_module_on", G);
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

   public static Module T(ModuleManagerLibrary moduleManagerLibrary) {
      return moduleManagerLibrary.p;
   }

   public static void c(LuaValue luaValue, Setting setting) {
      R(luaValue, setting);
   }

   public class aoq extends TwoArgFunction {
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
            ModuleManagerLibrary.T(ModuleManagerLibrary.this).o = luaTable;
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      }
   }
}
