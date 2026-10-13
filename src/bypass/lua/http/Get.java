package bypass.lua.http;

import bypass.auth.AuthUtil;
import bypass.script.ScriptManager;
import bypass.util.HttpRequest;
import bypass.util.HttpUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Get extends OneArgFunction {
   private static final long a = 85561958818498L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (!ScriptManager.N.t()) {
         return valueOf(false);
      }

      if (!var1.isstring()) {
         return valueOf(false);
      }

      HttpRequest var5 = new HttpRequest().g(var1.checkjstring());
      var5.W("Astolfo-User");
      var5.V(AuthUtil.getHwid());
      var5.T("Astolfo-Client");
      var5.i("");
      var5.S("");
      String var6 = HttpUtil.get(var5);
      return valueOf(var6);
   }
}
