package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class PrevPosition extends VarArgFunction {
   private static final long a = 66802772767264L;

   @Override
   public Varargs onInvoke(Varargs varargs) {
      LuaValue luaValue = varargs.arg1();
      if (luaValue != null && !luaValue.isnil() && luaValue.isnumber()) {
         Entity entity = WorldLibrary.X.get(luaValue.checkint());
         if (entity != null) {
            return LuaValue.varargsOf(
               new LuaValue[]{LuaValue.valueOf(entity.lastTickPosX), LuaValue.valueOf(entity.lastTickPosY), LuaValue.valueOf(entity.lastTickPosZ)}
            );
         }
      }

      return NIL;
   }
}
