package bypass.lua;

import bypass.ClientAccess;
import bypass.lua.input.GetKeyName;
import bypass.lua.input.GetKeyNumber;
import bypass.lua.input.GetScroll;
import bypass.lua.input.IsKeyDown;
import bypass.lua.input.IsMouseDown;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class InputLibrary extends TwoArgFunction implements ClientAccess {
   public static final OneArgFunction c = new IsKeyDown();
   public static final OneArgFunction w = new GetKeyName();
   public static final OneArgFunction o = new GetKeyNumber();
   public static final ZeroArgFunction n = new GetScroll();
   public static final OneArgFunction L = new IsMouseDown();
   private static final long b = 87716276116451L;

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("is_key_down", c);
      luaTable.set("is_mouse_down", L);
      luaTable.set("get_key_name", w);
      luaTable.set("get_key_number", o);
      luaTable.set("get_scroll", n);
      luaValue2.set("input", luaTable);
      return luaTable;
   }
}
