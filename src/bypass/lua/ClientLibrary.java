package bypass.lua;

import bypass.ClientAccess;
import bypass.lua.client.ChestName;
import bypass.lua.client.Config;
import bypass.lua.client.Fps;
import bypass.lua.client.GuiName;
import bypass.lua.client.Print;
import bypass.lua.client.Subtitle;
import bypass.lua.client.Time;
import bypass.lua.client.Title;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class ClientLibrary extends TwoArgFunction implements ClientAccess {
   public static final ZeroArgFunction u = new GuiName();
   public static final ZeroArgFunction r = new ChestName();
   public static final ZeroArgFunction L = new Time();
   public static final ZeroArgFunction O = new Config();
   public static final ZeroArgFunction C = new Fps();
   public static final ZeroArgFunction N = new Title();
   public static final ZeroArgFunction T = new Subtitle();
   public static final OneArgFunction f = new Print();
   private static final long b = 5556459501463L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      LuaTable var7 = LuaValue.tableOf();
      var7.set("gui_name", u);
      var7.set("chest_name", r);
      var7.set("time", L);
      var7.set("config", O);
      var7.set("print", f);
      var7.set("title", N);
      var7.set("subtitle", T);
      var7.set("fps", C);
      var2.set("client", var7);
      return var7;
   }
}
