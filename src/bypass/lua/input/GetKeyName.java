package bypass.lua.input;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.lwjgl.input.Keyboard;

public final class GetKeyName extends OneArgFunction {
   private static final long a = 84730923363937L;

   @Override
   public LuaValue call(LuaValue var1) {
      return !var1.isnumber() ? valueOf(false) : valueOf(Keyboard.getKeyName(var1.checkint()));
   }
}
