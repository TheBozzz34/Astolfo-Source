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
         WorldLibrary.entitiesById.clear();
         return NIL;
      }

      WorldLibrary.getEntityIdList().clear();
      WorldLibrary.entitiesById.clear();

      for (Entity entity : ClientAccess.mc.theWorld.loadedEntityList) {
         WorldLibrary.getEntityIdList().add(valueOf(entity.getEntityId()));
         WorldLibrary.entitiesById.put(entity.getEntityId(), entity);
      }

      LuaValue[] luaValues = (org.luaj.vm2.LuaValue[])(WorldLibrary.getEntityIdList().toArray(new LuaValue[0]));
      return LuaValue.varargsOf(new LuaValue[]{new LuaTable(LuaValue.varargsOf(luaValues))});
   }
}
