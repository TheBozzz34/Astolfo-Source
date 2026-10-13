package bypass.lua;

import bypass.ClientAccess;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class ClassLibrary extends TwoArgFunction implements ClientAccess {
   public Globals R;
   private static final long b = 25971575124369L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      this.R = var2.checkglobals();
      LuaTable var5 = new LuaTable();

      try {
         this.R.loadfile("/assets/minecraft/client/class.lua");
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return var5;
   }
}
