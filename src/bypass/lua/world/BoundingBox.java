package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class BoundingBox extends VarArgFunction {
   @Override
   public Varargs onInvoke(Varargs varargs) {
      LuaValue luaValue = varargs.arg1();
      if (luaValue != null && !luaValue.isnil() && luaValue.isnumber()) {
         Entity entity = WorldLibrary.X.get(luaValue.checkint());
         if (entity != null) {
            return LuaValue.varargsOf(
               new LuaValue[]{
                  LuaValue.valueOf(entity.getEntityBoundingBox().minX),
                  LuaValue.valueOf(entity.getEntityBoundingBox().minY),
                  LuaValue.valueOf(entity.getEntityBoundingBox().minZ),
                  LuaValue.valueOf(entity.getEntityBoundingBox().maxX),
                  LuaValue.valueOf(entity.getEntityBoundingBox().maxY),
                  LuaValue.valueOf(entity.getEntityBoundingBox().maxZ)
               }
            );
         }
      }

      return NIL;
   }
}
