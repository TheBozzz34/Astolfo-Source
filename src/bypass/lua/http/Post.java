package bypass.lua.http;

import bypass.auth.AuthUtil;
import bypass.script.ScriptManager;
import bypass.util.HttpRequest;
import bypass.util.HttpUtil;
import java.util.ArrayList;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Post extends OneArgFunction {
   private static final long a = 136866504753037L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      if (!ScriptManager.N.t()) {
         return valueOf(false);
      }

      if (!luaValue.isstring()) {
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
      String string3 = HttpUtil.post(httpRequest);
      return valueOf(string3);
   }
}
