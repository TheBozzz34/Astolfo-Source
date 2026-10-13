package bypass.script;

import bypass.lua.OsLibrary;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;

public class LuaGlobals {
   public static Globals globals;
   private static final long a = 47651154009291L;

   public static void init() {
      globals = new Globals();
      globals.load(new JseBaseLib());
      globals.load(new OsLibrary());
      globals.load(new StringLib());
      globals.load(new JseMathLib());
      globals.load(new CoroutineLib());
      LoadState.install(globals);
      LuaC.install(globals);
      LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);
   }
}
