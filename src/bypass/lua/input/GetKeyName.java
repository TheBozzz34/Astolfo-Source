package bypass.lua.input;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.lwjgl.input.Keyboard;

public final class GetKeyName extends OneArgFunction {
   private static final long a = 84730923363937L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      return !luaValue.isnumber() ? valueOf(false) : valueOf(Keyboard.getKeyName(luaValue.checkint()));
   }
}
