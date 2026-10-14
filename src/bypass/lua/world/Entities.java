package bypass.lua.world;

import bypass.ClientAccess;
import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Entities extends VarArgFunction {
   @Override
   public Varargs invoke(Varargs varargs) {
      if (ClientAccess.mc.theWorld == null) {
         WorldLibrary.X.clear();
         return NIL;
      }

      WorldLibrary.M().clear();
      WorldLibrary.X.clear();

      for (Entity entity : ClientAccess.mc.theWorld.loadedEntityList) {
         WorldLibrary.M().add(valueOf(entity.getEntityId()));
         WorldLibrary.X.put(entity.getEntityId(), entity);
      }

      LuaValue[] luaValues = (org.luaj.vm2.LuaValue[])(WorldLibrary.M().toArray(new LuaValue[0]));
      return LuaValue.varargsOf(new LuaValue[]{new LuaTable(LuaValue.varargsOf(luaValues))});
   }
}
