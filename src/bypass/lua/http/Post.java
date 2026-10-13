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
   public LuaValue call(LuaValue var1) {
      if (!ScriptManager.N.t()) {
         return valueOf(false);
      }

      if (!var1.isstring()) {
         return valueOf(false);
      }

      String var5 = var1.checkjstring();
      HttpRequest var6 = new HttpRequest().g(var5);
      var6.W("Astolfo-User");
      var6.V(AuthUtil.getHwid());
      var6.T("Astolfo-Client");
      var6.i("");
      var6.S("");
      ArrayList var7 = new ArrayList();
      ArrayList var8 = new ArrayList();
      String[] var9 = var5.split("\\?");
      if (var9.length > 1) {
         String[] var10 = var9[1].split("&");

         for (String var14 : var10) {
            String[] var15 = var14.split("=");
            if (var15.length > 0) {
               var7.add(var15[0]);
               var8.add(var15[1]);
            }
         }
      }

      String[] var16 = new String[var7.size()];
      String[] var17 = new String[var8.size()];
      var7.toArray(var16);
      var8.toArray(var17);
      var6.x(var16);
      var6.B(var17);
      String var18 = HttpUtil.post(var6);
      return valueOf(var18);
   }
}
