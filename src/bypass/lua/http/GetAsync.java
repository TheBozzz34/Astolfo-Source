package bypass.lua.http;

import bypass.auth.AuthUtil;
import bypass.lua.HttpLibrary;
import bypass.script.ScriptManager;
import bypass.util.HttpRequest;
import bypass.util.HttpUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class GetAsync extends TwoArgFunction {
   private static final long a = 28639608301543L;

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      if (!ScriptManager.N.t()) {
         return valueOf(false);
      }

      if (!luaValue.isstring()) {
         return valueOf(false);
      }

      if (!luaValue2.istable()) {
         return valueOf(false);
      }

      HttpRequest httpRequest = new HttpRequest().g(luaValue.checkjstring());
      httpRequest.W("Astolfo-User");
      httpRequest.V(AuthUtil.getHwid());
      httpRequest.T("Astolfo-Client");
      httpRequest.i("");
      httpRequest.S("");
      HttpLibrary.R.submit(() -> {
         String string = HttpUtil.get(httpRequest);
         LuaValue luaValue3 = luaValue2.get("run");
         luaValue3.call(string);
      });
      return FALSE;
   }
}
