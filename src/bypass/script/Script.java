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

   public Script(String var1, String var2) {
      this.P = var1;
      this.C = var2;
      this.l = new Globals();
      w(this.l);
      this.l.load(new ModuleManagerLibrary2());
   }

   public static void w(Globals var0) {
      LuaC.install(var0);
      LoadState.install(var0);
      var0.load(new JseBaseLib());
      var0.load(new CoroutineLib());
      var0.load(new OsLibrary());
      var0.load(new Bit32Lib());
      var0.load(new TableLib());
      var0.load(new StringLib());
      var0.load(new JseMathLib());
      var0.load(new PlayerLibrary());
      var0.load(new HttpLibrary(var0));
      var0.load(new WorldLibrary());
      var0.load(new ClientLibrary());
      var0.load(new RenderLibrary());
      var0.load(new InputLibrary());
      var0.set("dofile", LuaValue.NIL);
      var0.set("load", LuaValue.NIL);
      var0.set("loadfile", LuaValue.NIL);
      var0.set("print", LuaValue.NIL);
      var0.set("_VERSION", LuaValue.NIL);
      var0.set("debug", LuaValue.NIL);
   }

   public boolean o() {
      boolean var1 = this.r();
      if (var1) {
         this.n = true;
         System.out.println("Ran " + this.P);
      } else {
         toasts.k("Script error", this.Q);
      }

      return var1;
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

   public void j(boolean var1) {
      this.J = var1;
   }

   public boolean r() {
      try {
         String var1 = FileUtils.readFileToString(new File(this.P));
         LuaValue var2 = LuaGlobals.globals.load(var1, this.P, this.l);
         var2.call();
         return true;
      } catch (LuaError var3) {
         this.Q = var3.getMessage();
         var3.printStackTrace();
         return false;
      } catch (IOException var4) {
         return false;
      }
   }

   public void F(String var1) {
      if (this.l == null) {
         System.out.println("Error calling lua func " + var1);
      } else {
         LuaValue var2 = this.l.get(var1);
         if (!var2.isnil()) {
            var2.call();
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
      JsonObject var1 = new JsonObject();
      var1.addProperty("state", this.J);
      return var1;
   }
}
