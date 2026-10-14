package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class BoundingBox extends VarArgFunction {
   private static final long a = 114460950178164L;
   private static final long b = -3713077318487375866L;

   @Override
   public Varargs onInvoke(Varargs varargs) {
      LuaValue luaValue = varargs.arg1();
      if (luaValue != null && !luaValue.isnil() && luaValue.isnumber()) {
         Entity entity = WorldLibrary.X.get(luaValue.checkint());
         if (entity != null) {
            LuaValue[] luaValues = new LuaValue[(int)b];
            luaValues[0] = LuaValue.valueOf(entity.getEntityBoundingBox().minX);
            luaValues[1] = LuaValue.valueOf(entity.getEntityBoundingBox().minY);
            luaValues[2] = LuaValue.valueOf(entity.getEntityBoundingBox().minZ);
            luaValues[3] = LuaValue.valueOf(entity.getEntityBoundingBox().maxX);
            luaValues[4] = LuaValue.valueOf(entity.getEntityBoundingBox().maxY);
            luaValues[5] = LuaValue.valueOf(entity.getEntityBoundingBox().maxZ);
            return LuaValue.varargsOf(luaValues);
         }
      }

      return NIL;
   }
}
