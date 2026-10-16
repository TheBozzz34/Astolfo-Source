package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Angles extends VarArgFunction {
   @Override
   public Varargs onInvoke(Varargs varargs) {
      LuaValue luaValue = varargs.arg1();
      if (luaValue != null && !luaValue.isnil() && luaValue.isnumber()) {
         Entity entity = WorldLibrary.entitiesById.get(luaValue.checkint());
         if (entity != null) {
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(entity.rotationYaw), LuaValue.valueOf(entity.rotationPitch)});
         }
      }

      return NIL;
   }
}
