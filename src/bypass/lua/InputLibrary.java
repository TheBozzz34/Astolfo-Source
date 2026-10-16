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
   public static final OneArgFunction isKeyDownFunction = new IsKeyDown();
   public static final OneArgFunction getKeyNameFunction = new GetKeyName();
   public static final OneArgFunction getKeyNumberFunction = new GetKeyNumber();
   public static final ZeroArgFunction getScrollFunction = new GetScroll();
   public static final OneArgFunction isMouseDownFunction = new IsMouseDown();

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("is_key_down", isKeyDownFunction);
      luaTable.set("is_mouse_down", isMouseDownFunction);
      luaTable.set("get_key_name", getKeyNameFunction);
      luaTable.set("get_key_number", getKeyNumberFunction);
      luaTable.set("get_scroll", getScrollFunction);
      luaValue2.set("input", luaTable);
      return luaTable;
   }
}
