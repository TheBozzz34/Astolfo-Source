package bypass.lua.input;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.lwjgl.input.Keyboard;

public final class IsKeyDown extends OneArgFunction {
   private static final long a = 138145460098398L;

   @Override
   public LuaValue call(LuaValue var1) {
      return !var1.isnumber() ? valueOf(false) : valueOf(Keyboard.isKeyDown(var1.checkint()));
   }
}
