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
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("gui_name", u);
      luaTable.set("chest_name", r);
      luaTable.set("time", L);
      luaTable.set("config", O);
      luaTable.set("print", f);
      luaTable.set("title", N);
      luaTable.set("subtitle", T);
      luaTable.set("fps", C);
      luaValue2.set("client", luaTable);
      return luaTable;
   }
}
