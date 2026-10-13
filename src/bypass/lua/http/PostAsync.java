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
   private static final long a = 5275094967960L;

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

      String var6 = var1.checkjstring();
      HttpRequest var7 = new HttpRequest().g(var6);
      var7.W("Astolfo-User");
      var7.V(AuthUtil.getHwid());
      var7.T("Astolfo-Client");
      var7.i("");
      var7.S("");
      ArrayList var8 = new ArrayList();
      ArrayList var9 = new ArrayList();
      String[] var10 = var6.split("\\?");
      if (var10.length > 1) {
         String[] var11 = var10[1].split("&");

         for (String var15 : var11) {
            String[] var16 = var15.split("=");
            if (var16.length > 0) {
               var8.add(var16[0]);
               var9.add(var16[1]);
            }
         }
      }

      String[] var17 = new String[var8.size()];
      String[] var18 = new String[var9.size()];
      var8.toArray(var17);
      var9.toArray(var18);
      var7.x(var17);
      var7.B(var18);
      HttpLibrary.R.submit(() -> {
         String var4 = HttpUtil.post(var7);
         LuaValue var5 = var2.get("run");
         var5.call(var4);
      });
      return FALSE;
   }
}
