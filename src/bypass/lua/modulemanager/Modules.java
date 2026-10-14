package bypass.lua.modulemanager;

import bypass.Astolfo;
import bypass.lua.ModuleManagerLibrary2;
import bypass.module.ModuleInfo;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Modules extends VarArgFunction {
   private static final long a = 117859252108526L;

   @Override
   public Varargs invoke() {
      long l = a ^ 31943914266992L;
      long m = l ^ 71792402767917L;
      if (ModuleManagerLibrary2.i().isEmpty()) {
         for (ModuleInfo moduleInfo : Astolfo.INSTANCE.getModuleManager().getList()) {
            ModuleManagerLibrary2.i().add(valueOf(moduleInfo.getName()));
         }
      }

      LuaValue[] luaValues = (org.luaj.vm2.LuaValue[])(ModuleManagerLibrary2.i().toArray(new LuaValue[0]));
      return LuaValue.varargsOf(new LuaValue[]{new LuaTable(LuaValue.varargsOf(luaValues))});
   }
}
