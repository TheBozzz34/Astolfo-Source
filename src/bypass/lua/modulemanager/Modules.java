package bypass.lua.modulemanager;

import bypass.Astolfo;
import bypass.lua.ModuleManagerLibrary2;
import bypass.module.ModuleInfo;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Modules extends VarArgFunction {
   @Override
   public Varargs invoke() {
      if (ModuleManagerLibrary2.getModuleNames().isEmpty()) {
         for (ModuleInfo moduleInfo : Astolfo.INSTANCE.getModuleManager().getList()) {
            ModuleManagerLibrary2.getModuleNames().add(valueOf(moduleInfo.getName()));
         }
      }

      LuaValue[] luaValues = (org.luaj.vm2.LuaValue[])(ModuleManagerLibrary2.getModuleNames().toArray(new LuaValue[0]));
      return LuaValue.varargsOf(new LuaValue[]{new LuaTable(LuaValue.varargsOf(luaValues))});
   }
}
