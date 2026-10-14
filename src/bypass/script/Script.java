package bypass.script;

import bypass.ClientAccess;
import bypass.lua.ClientLibrary;
import bypass.lua.HttpLibrary;
import bypass.lua.InputLibrary;
import bypass.lua.ModuleManagerLibrary2;
import bypass.lua.OsLibrary;
import bypass.lua.PlayerLibrary;
import bypass.lua.RenderLibrary;
import bypass.lua.WorldLibrary;
import bypass.manager.JsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;

public class Script implements ClientAccess, JsonSerializable {
   public String P;
   public String C;
   private final Globals l;
   private boolean J;
   private boolean z;
   public boolean n;
   public String Q;

   public Script(String string, String string2) {
      this.P = string;
      this.C = string2;
      this.l = new Globals();
      w(this.l);
      this.l.load(new ModuleManagerLibrary2());
   }

   public static void w(Globals globals) {
      LuaC.install(globals);
      LoadState.install(globals);
      globals.load(new JseBaseLib());
      globals.load(new CoroutineLib());
      globals.load(new OsLibrary());
      globals.load(new Bit32Lib());
      globals.load(new TableLib());
      globals.load(new StringLib());
      globals.load(new JseMathLib());
      globals.load(new PlayerLibrary());
      globals.load(new HttpLibrary(globals));
      globals.load(new WorldLibrary());
      globals.load(new ClientLibrary());
      globals.load(new RenderLibrary());
      globals.load(new InputLibrary());
      globals.set("dofile", LuaValue.NIL);
      globals.set("load", LuaValue.NIL);
      globals.set("loadfile", LuaValue.NIL);
      globals.set("print", LuaValue.NIL);
      globals.set("_VERSION", LuaValue.NIL);
      globals.set("debug", LuaValue.NIL);
   }

   public boolean o() {
      boolean bl = this.r();
      if (bl) {
         this.n = true;
         System.out.println("Ran " + this.P);
      } else {
         toasts.k("Script error", this.Q);
      }

      return bl;
   }

   public boolean A() {
      if (this.J) {
         return false;
      }

      this.F("on_enable");
      this.J = true;
      return true;
   }

   public boolean x() {
      if (!this.J) {
         return false;
      }

      this.F("on_disable");
      this.J = false;
      return true;
   }

   public void j(boolean bl) {
      this.J = bl;
   }

   public boolean r() {
      try {
         String string = FileUtils.readFileToString(new File(this.P));
         LuaValue luaValue = LuaGlobals.globals.load(string, this.P, this.l);
         luaValue.call();
         return true;
      } catch (LuaError luaError) {
         this.Q = luaError.getMessage();
         luaError.printStackTrace();
         return false;
      } catch (IOException ioException) {
         return false;
      }
   }

   public void F(String string) {
      if (this.l == null) {
         System.out.println("Error calling lua func " + string);
      } else {
         LuaValue luaValue = this.l.get(string);
         if (!luaValue.isnil()) {
            luaValue.call();
         }
      }
   }

   public Globals l() {
      return this.l;
   }

   public boolean t() {
      return this.J;
   }

   public boolean K() {
      return this.z;
   }

   @Override
   public String c() {
      return this.C;
   }

   @Override
   public JsonElement V() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("state", this.J);
      return jsonObject;
   }
}
