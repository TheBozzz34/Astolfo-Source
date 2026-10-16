package bypass.lua.http;

import bypass.auth.AuthUtil;
import bypass.lua.HttpLibrary;
import bypass.script.ScriptManager;
import bypass.util.HttpRequest;
import bypass.util.HttpUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class GetAsync extends TwoArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      if (!ScriptManager.unsafeFunctions.isEnabled()) {
         return valueOf(false);
      }

      if (!luaValue.isstring()) {
         return valueOf(false);
      }

      if (!luaValue2.istable()) {
         return valueOf(false);
      }

      HttpRequest httpRequest = new HttpRequest().setUrl(luaValue.checkjstring());
      httpRequest.setHeaderKeys("Astolfo-User");
      httpRequest.setHeaderValues(AuthUtil.getHwid());
      httpRequest.setUserAgent("Astolfo-Client");
      httpRequest.setAccept("");
      httpRequest.setContentType("");
      HttpLibrary.asyncExecutor.submit(() -> {
         String string = HttpUtil.get(httpRequest);
         LuaValue luaValue3 = luaValue2.get("run");
         luaValue3.call(string);
      });
      return FALSE;
   }
}
