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
   public String path;
   public String name;
   private final Globals globals;
   private boolean enabled;
   private boolean loadError;
   public boolean loaded;
   public String errorMessage;

   public Script(String string, String string2) {
      this.path = string;
      this.name = string2;
      this.globals = new Globals();
      installLibraries(this.globals);
      this.globals.load(new ModuleManagerLibrary2());
   }

   public static void installLibraries(Globals globals) {
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

   public boolean loadAndRun() {
      boolean bl = this.runFile();
      if (bl) {
         this.loaded = true;
         System.out.println("Ran " + this.path);
      } else {
         toasts.dangerMessage("Script error", this.errorMessage);
      }

      return bl;
   }

   public boolean enable() {
      if (this.enabled) {
         return false;
      }

      this.callFunction("on_enable");
      this.enabled = true;
      return true;
   }

   public boolean disable() {
      if (!this.enabled) {
         return false;
      }

      this.callFunction("on_disable");
      this.enabled = false;
      return true;
   }

   public void setEnabled(boolean bl) {
      this.enabled = bl;
   }

   public boolean runFile() {
      try {
         String string = FileUtils.readFileToString(new File(this.path));
         LuaValue luaValue = LuaGlobals.globals.load(string, this.path, this.globals);
         luaValue.call();
         return true;
      } catch (LuaError luaError) {
         this.errorMessage = luaError.getMessage();
         luaError.printStackTrace();
         return false;
      } catch (IOException ioException) {
         return false;
      }
   }

   public void callFunction(String string) {
      if (this.globals == null) {
         System.out.println("Error calling lua func " + string);
      } else {
         LuaValue luaValue = this.globals.get(string);
         if (!luaValue.isnil()) {
            luaValue.call();
         }
      }
   }

   public Globals getGlobals() {
      return this.globals;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean hasLoadError() {
      return this.loadError;
   }

   @Override
   public String getKey() {
      return this.name;
   }

   @Override
   public JsonElement toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("state", this.enabled);
      return jsonObject;
   }
}
