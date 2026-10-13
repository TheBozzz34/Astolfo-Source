package bypass.lua.world;

import bypass.ClientAccess;
import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Entities extends VarArgFunction {
   private static final long a = 82546753084627L;

   @Override
   public Varargs invoke(Varargs var1) {
      if (ClientAccess.mc.theWorld == null) {
         WorldLibrary.X.clear();
         return NIL;
      }

      WorldLibrary.M().clear();
      WorldLibrary.X.clear();

      for (Entity var6 : ClientAccess.mc.theWorld.loadedEntityList) {
         WorldLibrary.M().add(valueOf(var6.getEntityId()));
         WorldLibrary.X.put(var6.getEntityId(), var6);
      }

      LuaValue[] var7 = (org.luaj.vm2.LuaValue[])(WorldLibrary.M().toArray(new LuaValue[0]));
      return LuaValue.varargsOf(new LuaValue[]{new LuaTable(LuaValue.varargsOf(var7))});
   }
}
