package bypass.lua.modulemanager;

import bypass.ClientAccess;
import bypass.lua.LuaApi;
import bypass.setting.BooleanSetting;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class RegisterBoolean3 extends ThreeArgFunction {
   private static final long a = 80212799512208L;

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2, LuaValue luaValue3) {
      long l = a ^ 35511673039155L;
      long m = l ^ 105312734054928L;
      String string = null;
      String string2 = null;
      boolean bl = false;
      if (luaValue.isstring()) {
         LuaString luaString = (LuaString)luaValue;
         string = luaString.checkjstring();
      }

      if (luaValue2.isstring()) {
         LuaString luaString2 = (LuaString)luaValue2;
         string2 = luaString2.checkjstring();
      }

      if (luaValue3.isboolean()) {
         LuaBoolean luaBoolean = (LuaBoolean)luaValue3;
         bl = luaBoolean.toboolean();
      }

      if (string != null && string2 != null) {
         BooleanSetting booleanSetting = new BooleanSetting(string2, "script option", bl);
         LuaApi.f.put(string2, booleanSetting);
         ClientAccess.client.getModuleManager().getModule(string).addSettings(booleanSetting);
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
