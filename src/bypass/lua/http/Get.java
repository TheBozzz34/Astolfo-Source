package bypass.lua.http;

import bypass.auth.AuthUtil;
import bypass.script.ScriptManager;
import bypass.util.HttpRequest;
import bypass.util.HttpUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Get extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      if (!ScriptManager.N.t()) {
         return valueOf(false);
      }

      if (!luaValue.isstring()) {
         return valueOf(false);
      }

      HttpRequest httpRequest = new HttpRequest().g(luaValue.checkjstring());
      httpRequest.W("Astolfo-User");
      httpRequest.V(AuthUtil.getHwid());
      httpRequest.T("Astolfo-Client");
      httpRequest.i("");
      httpRequest.S("");
      String string = HttpUtil.get(httpRequest);
      return valueOf(string);
   }
}
