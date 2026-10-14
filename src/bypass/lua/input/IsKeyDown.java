package bypass.lua.input;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.lwjgl.input.Keyboard;

public final class IsKeyDown extends OneArgFunction {
   private static final long a = 138145460098398L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      return !luaValue.isnumber() ? valueOf(false) : valueOf(Keyboard.isKeyDown(luaValue.checkint()));
   }
}
