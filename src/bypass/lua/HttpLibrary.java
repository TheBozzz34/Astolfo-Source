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

   public HttpLibrary(Globals globals) {
      this.Z = globals;
   }

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      this.Z = luaValue2.checkglobals();
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("get", S);
      luaTable.set("get_async", B);
      luaTable.set("post", w);
      luaTable.set("post_async", e);
      luaTable.set("load", this.L);
      luaValue2.set("http", luaTable);
      return luaTable;
   }

   public class z8 extends OneArgFunction {
      private static final long a = 56435362779393L;

      @Override
      public LuaValue call(LuaValue luaValue) {
         if (!ScriptManager.N.t()) {
            return valueOf(false);
         }

         if (!luaValue.isstring()) {
            return valueOf(false);
         }

         HttpRequest httpRequest = new HttpRequest().g(luaValue.checkjstring());
         httpRequest.W("Astolfo-User");
         httpRequest.V(AuthUtil.getHwid());
         httpRequest.T("Astolfo-Client");
         httpRequest.i("");
         httpRequest.S("");
         String string = HttpUtil.get(httpRequest);
         LuaValue luaValue2 = HttpLibrary.this.Z.load(string, "", HttpLibrary.this.Z);
         luaValue2.call();
         return luaValue2;
      }
   }
}
