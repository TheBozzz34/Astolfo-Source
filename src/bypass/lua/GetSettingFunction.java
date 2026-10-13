package bypass.lua;

import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public final class GetSettingFunction extends OneArgFunction {
   private static final long a = 73729772141821L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (var1.isstring()) {
         LuaString var5 = (LuaString)var1;
         Setting var6 = LuaApi.f.get(var5.checkjstring());
         if (var6 instanceof BooleanSetting) {
            return valueOf(var6.asBoolean());
         } else if (var6 instanceof NumberSetting) {
            NumberSetting var7 = (NumberSetting)var6;
            return CoerceJavaToLua.coerce(var7.getValue());
         } else {
            return valueOf(true);
         }
      } else {
         return valueOf(false);
      }
   }
}
