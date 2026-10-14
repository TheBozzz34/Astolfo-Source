package bypass.lua;

import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public final class GetSettingFunction4 extends OneArgFunction {
   private static final long a = 52162581759507L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      if (luaValue.isstring()) {
         LuaString luaString = (LuaString)luaValue;
         Setting setting = LuaApi.f.get(luaString.checkjstring());
         if (setting instanceof BooleanSetting) {
            return valueOf(setting.asBoolean());
         } else if (setting instanceof NumberSetting) {
            NumberSetting numberSetting = (NumberSetting)setting;
            return CoerceJavaToLua.coerce(numberSetting.getValue());
         } else {
            return valueOf(true);
         }
      } else {
         return valueOf(false);
      }
   }
}
