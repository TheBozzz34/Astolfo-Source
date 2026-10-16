package bypass.lua.http;

import bypass.auth.AuthUtil;
import bypass.script.ScriptManager;
import bypass.util.HttpRequest;
import bypass.util.HttpUtil;
import java.util.ArrayList;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Post extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      if (!ScriptManager.unsafeFunctions.isEnabled()) {
         return valueOf(false);
      }

      if (!luaValue.isstring()) {
         return valueOf(false);
      }

      String string = luaValue.checkjstring();
      HttpRequest httpRequest = new HttpRequest().setUrl(string);
      httpRequest.setHeaderKeys("Astolfo-User");
      httpRequest.setHeaderValues(AuthUtil.getHwid());
      httpRequest.setUserAgent("Astolfo-Client");
      httpRequest.setAccept("");
      httpRequest.setContentType("");
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
      httpRequest.setParamKeys(strings4);
      httpRequest.setParamValues(strings5);
      String string3 = HttpUtil.post(httpRequest);
      return valueOf(string3);
   }
}
