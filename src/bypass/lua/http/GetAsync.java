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
   public LuaValue call(LuaValue var1, LuaValue var2) {
      if (!ScriptManager.N.t()) {
         return valueOf(false);
      }

      if (!var1.isstring()) {
         return valueOf(false);
      }

      if (!var2.istable()) {
         return valueOf(false);
      }

      HttpRequest var6 = new HttpRequest().g(var1.checkjstring());
      var6.W("Astolfo-User");
      var6.V(AuthUtil.getHwid());
      var6.T("Astolfo-Client");
      var6.i("");
      var6.S("");
      HttpLibrary.R.submit(() -> {
         String var4 = HttpUtil.get(var6);
         LuaValue var5 = var2.get("run");
         var5.call(var4);
      });
      return FALSE;
   }
}
