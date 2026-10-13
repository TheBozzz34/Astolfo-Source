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
   public Varargs onInvoke(Varargs var1) {
      LuaValue var5 = var1.arg1();
      if (var5 != null && !var5.isnil() && var5.isnumber()) {
         Entity var6 = WorldLibrary.X.get(var5.checkint());
         if (var6 != null) {
            LuaValue[] var10000 = new LuaValue[(int)b];
            var10000[0] = LuaValue.valueOf(var6.getEntityBoundingBox().minX);
            var10000[1] = LuaValue.valueOf(var6.getEntityBoundingBox().minY);
            var10000[2] = LuaValue.valueOf(var6.getEntityBoundingBox().minZ);
            var10000[3] = LuaValue.valueOf(var6.getEntityBoundingBox().maxX);
            var10000[4] = LuaValue.valueOf(var6.getEntityBoundingBox().maxY);
            var10000[5] = LuaValue.valueOf(var6.getEntityBoundingBox().maxZ);
            return LuaValue.varargsOf(var10000);
         }
      }

      return NIL;
   }
}
