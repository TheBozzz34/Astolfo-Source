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
      if (!ScriptManager.unsafeFunctions.isEnabled()) {
         return valueOf(false);
      }

      if (!luaValue.isstring()) {
         return valueOf(false);
      }

      HttpRequest httpRequest = new HttpRequest().setUrl(luaValue.checkjstring());
      httpRequest.setHeaderKeys("Astolfo-User");
      httpRequest.setHeaderValues(AuthUtil.getHwid());
      httpRequest.setUserAgent("Astolfo-Client");
      httpRequest.setAccept("");
      httpRequest.setContentType("");
      String string = HttpUtil.get(httpRequest);
      return valueOf(string);
   }
}
