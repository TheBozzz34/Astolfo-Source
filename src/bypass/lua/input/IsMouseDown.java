package bypass.lua.input;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.lwjgl.input.Mouse;

public final class IsMouseDown extends OneArgFunction {
   private static final long a = 98565084159257L;

   @Override
   public LuaValue call(LuaValue var1) {
      return !var1.isnumber() ? valueOf(false) : valueOf(Mouse.isButtonDown(var1.checkint()));
   }
}
