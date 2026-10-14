package bypass.lua.render;

import bypass.module.hidden.PlayerListTracker;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class WorldToScreen extends VarArgFunction {
   @Override
   public Varargs invoke(Varargs varargs) {
      float f = 0.0F;
      float g = 0.0F;
      float h = 0.0F;
      if (varargs.narg() < 3) {
         return valueOf(false);
      }

      int i = 1;

      for (int j = varargs.narg(); i <= j; i++) {
         LuaValue luaValue = varargs.checkvalue(i);
         switch (i) {
            case 1:
               if (luaValue.isnumber()) {
                  f = luaValue.tofloat();
               }
               break;
            case 2:
               if (luaValue.isnumber()) {
                  g = luaValue.tofloat();
               }
               break;
            case 3:
               if (luaValue.isnumber()) {
                  h = luaValue.tofloat();
               }
         }
      }

      float[] floats = PlayerListTracker.project(f, g, h);
      return floats != null ? LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(floats[0]), LuaValue.valueOf(floats[1]), LuaValue.valueOf(floats[2])}) : NIL;
   }
}
