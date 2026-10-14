package bypass.lua.input;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.lwjgl.input.Keyboard;

public final class GetKeyNumber extends OneArgFunction {
   private static final long a = 100975100287976L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      return !luaValue.isstring() ? valueOf(false) : valueOf(Keyboard.getKeyIndex(luaValue.checkjstring()));
   }
}
