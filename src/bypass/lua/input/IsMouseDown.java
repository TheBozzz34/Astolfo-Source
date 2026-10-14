package bypass.lua.input;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.lwjgl.input.Mouse;

public final class IsMouseDown extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      return !luaValue.isnumber() ? valueOf(false) : valueOf(Mouse.isButtonDown(luaValue.checkint()));
   }
}
