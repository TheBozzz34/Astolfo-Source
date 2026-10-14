package bypass.lua.http;

import bypass.auth.AuthUtil;
import bypass.lua.HttpLibrary;
import bypass.script.ScriptManager;
import bypass.util.HttpRequest;
import bypass.util.HttpUtil;
import java.util.ArrayList;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class PostAsync extends TwoArgFunction {
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

      String string = luaValue.checkjstring();
      HttpRequest httpRequest = new HttpRequest().g(string);
      httpRequest.W("Astolfo-User");
      httpRequest.V(AuthUtil.getHwid());
      httpRequest.T("Astolfo-Client");
      httpRequest.i("");
      httpRequest.S("");
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      String[] strings = string.split("\\?");
      if (strings.length > 1) {
         String[] strings2 = strings[1].split("&");

         for (String string2 : strings2) {
            String[] strings3 = string2.split("=");
            if (strings3.length > 0) {
               arrayList.add(strings3[0]);
               arrayList2.add(strings3[1]);
            }
         }
      }

      String[] strings4 = new String[arrayList.size()];
      String[] strings5 = new String[arrayList2.size()];
      arrayList.toArray(strings4);
      arrayList2.toArray(strings5);
      httpRequest.x(strings4);
      httpRequest.B(strings5);
      HttpLibrary.R.submit(() -> {
         String string3 = HttpUtil.post(httpRequest);
         LuaValue luaValue3 = luaValue2.get("run");
         luaValue3.call(string3);
      });
      return FALSE;
   }
}
