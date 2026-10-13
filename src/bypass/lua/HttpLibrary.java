package bypass.lua;

import bypass.ClientAccess;
import bypass.auth.AuthUtil;
import bypass.lua.http.Get;
import bypass.lua.http.GetAsync;
import bypass.lua.http.Post;
import bypass.lua.http.PostAsync;
import bypass.script.ScriptManager;
import bypass.util.HttpRequest;
import bypass.util.HttpUtil;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class HttpLibrary extends TwoArgFunction implements ClientAccess {
   public Globals Z;
   public static final ExecutorService R = Executors.newFixedThreadPool(25);
   public static final OneArgFunction S = new Get();
   public static final OneArgFunction w = new Post();
   public static final TwoArgFunction e = new PostAsync();
   public static final TwoArgFunction B = new GetAsync();
   private final OneArgFunction L = new HttpLibrary.z8();
   private static final long b = 123751960180539L;

   public HttpLibrary(Globals var1) {
      this.Z = var1;
   }

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      this.Z = var2.checkglobals();
      LuaTable var7 = LuaValue.tableOf();
      var7.set("get", S);
      var7.set("get_async", B);
      var7.set("post", w);
      var7.set("post_async", e);
      var7.set("load", this.L);
      var2.set("http", var7);
      return var7;
   }

   public class z8 extends OneArgFunction {
      private static final long a = 56435362779393L;

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
         LuaValue var7 = HttpLibrary.this.Z.load(var6, "", HttpLibrary.this.Z);
         var7.call();
         return var7;
      }
   }
}
