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
   public Globals globals;
   public static final ExecutorService asyncExecutor = Executors.newFixedThreadPool(25);
   public static final OneArgFunction getFunction = new Get();
   public static final OneArgFunction postFunction = new Post();
   public static final TwoArgFunction postAsyncFunction = new PostAsync();
   public static final TwoArgFunction getAsyncFunction = new GetAsync();
   private final OneArgFunction loadFunction = new HttpLibrary.z8();

   public HttpLibrary(Globals globals) {
      this.globals = globals;
   }

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      this.globals = luaValue2.checkglobals();
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("get", getFunction);
      luaTable.set("get_async", getAsyncFunction);
      luaTable.set("post", postFunction);
      luaTable.set("post_async", postAsyncFunction);
      luaTable.set("load", this.loadFunction);
      luaValue2.set("http", luaTable);
      return luaTable;
   }

   public class z8 extends OneArgFunction {
      @Override
      public LuaValue call(LuaValue luaValue) {
         if (!ScriptManager.unsafeFunctions.isEnabled()) {
            return valueOf(false);
         }

         if (!luaValue.isstring()) {
            return valueOf(false);
         }

         HttpRequest httpRequest = new HttpRequest().setUrl(luaValue.checkjstring());
         httpRequest.setHeaderKeys("Astolfo-User");
         httpRequest.setHeaderValues(AuthUtil.getHwid());
         httpRequest.setUserAgent("Astolfo-Client");
         httpRequest.setAccept("");
         httpRequest.setContentType("");
         String string = HttpUtil.get(httpRequest);
         LuaValue luaValue2 = HttpLibrary.this.globals.load(string, "", HttpLibrary.this.globals);
         luaValue2.call();
         return luaValue2;
      }
   }
}
